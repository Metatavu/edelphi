/*******************************************************************************
 * Copyright (c) 2007 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package fi.metatavu.edelphi.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.taglibs.standard.tag.common.core.ParamParent;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.core.exception.BirtException;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartContext;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageProvider;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.taglib.chartutil.ChartWebHelper;
import fi.metatavu.edelphi.taglib.chartutil.ImageHTMLEmitter;
import fi.metatavu.edelphi.taglib.chartutil.PngImageHTMLEmitter;
import fi.metatavu.edelphi.taglib.chartutil.ReportChartCache;
import fi.metatavu.edelphi.taglib.chartutil.SvgImageHTMLEmitter;
import fi.metatavu.edelphi.utils.ResourceUtils;

/**
 * 
 * Tag for generating chart image and HTML
 * 
 */
public class QueryPageChartTag extends BodyTagSupport implements ParamParent {

  private static final long serialVersionUID = 8922643273976526624L;

  private int width;

	private int height;

	private String renderURL;

	// TODO: output type
	private String output = "PNG";
	
	private boolean lazy = false;

	private Long queryPageId;
	
  private Map<String, String> parameters;
  
  private ReportContext reportContext;
  
  public ReportContext getReportContext() {
    return reportContext;
  }
  
  public void setReportContext(ReportContext reportContext) {
    this.reportContext = reportContext;
  }
	
  public int doEndTag() throws JspException {
    try {
      if (!ChartWebHelper.checkOutputType(output)) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.REPORTING_ERROR, "Unknown output format.");
      }
      
      if ("PNG".equals(output)) {
        generatePng();
      } else {
        generateSvg();
      }
      
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return EVAL_PAGE;
  }

  private ImageHTMLEmitter createEmitter(Chart chartModel) {
    ImageHTMLEmitter emitter;
    
    if ("SVG".equals(output)) {
      SvgImageHTMLEmitter svgEmitter = new SvgImageHTMLEmitter(chartModel, width, height);
      svgEmitter.setLazy(isLazy());
      emitter = svgEmitter;
    } else if ("PNG".equals(output)) {
      PngImageHTMLEmitter pngEmitter = new PngImageHTMLEmitter(chartModel, width, height);
      if ("true".equals(parameters.get("dynamicSize"))) {
        pngEmitter.setDynamicSize(true);
      }
      emitter = pngEmitter;
    } else {
      throw new RuntimeException("Could not find an Image emitter for " + output);
    }
    
    return emitter;
  }

  /**
   * @param width
   *          the width to set
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @param height
   *          the height to set
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * @param renderURL
   *          the renderURL to set
   */
  public void setRenderURL(String renderURL) {
    this.renderURL = renderURL;
  }

  /**
   * @return the renderURL
   */
  public String getRenderURL() {
    return renderURL;
  }

  /**
   * @param output
   *          the output to set
   */
  public void setOutput(String output) {
    this.output = output;
  }

  /**
   * @return the output
   */
  public String getOutput() {
    return output;
  }
  
  public boolean isLazy() {
    return lazy;
  }
  
  public void setLazy(boolean lazy) {
    this.lazy = lazy;
  }

  protected ServletContext getServletContext() {
    return this.pageContext.getServletContext();
  }

  public void setQueryPageId(Long queryPageId) {
    this.queryPageId = queryPageId;
  }

  public Long getQueryPageId() {
    return queryPageId;
  }

  @Override
  public int doStartTag() throws JspException {
    parameters = new HashMap<String, String>();
    return EVAL_BODY_INCLUDE;
  }

  @Override
  public void addParameter(String name, String value) {
    name = ResourceUtils.decodeUrlName(name);
    value = ResourceUtils.decodeUrlName(value);
    parameters.put(name, value);
  }

  private void generateSvg() throws IOException, BirtException {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryPage queryPage = queryPageDAO.findById(queryPageId);
    QueryReportPageController queryReportPageController = QueryReportPageProvider.getController(queryPage.getPageType());
    
    Map<String, String> chartParameters = new HashMap<>();
    chartParameters.putAll(parameters);
    ChartContext chartContext = new ChartContext(reportContext, chartParameters);
    
    Chart chartModel = queryReportPageController.constructChart(chartContext, queryPage);

    if (chartModel != null) {
      // Set size in chart model
      Bounds bounds = chartModel.getBlock().getBounds();
      bounds.setWidth(width);
      bounds.setHeight(height);
    } else {
      throw new SmvcRuntimeException(EdelfoiStatusCode.REPORTING_ERROR, "ChartModel was not found.");
    }
    
    pageContext.getOut().println(createEmitter(chartModel).generateHTML());
  }
  
  private void generatePng() throws IOException {
    boolean dynamicSize = "true".equals(parameters.get("dynamicSize"));
    
    Map<String, String> chartParameters = new HashMap<>();
    chartParameters.putAll(parameters);
    GenerateChartImageData chartImageData = new GenerateChartImageData(width, height, chartParameters, queryPageId, reportContext);
    
    String id = UUID.randomUUID().toString();
    ReportChartCache.put(id, SerializationUtils.serialize(chartImageData));
    String url = String.format("/queries/generatechartimage.binary?id=%s", id);
    
    StringBuilder html = new StringBuilder();
    if (dynamicSize) {
      html.append(String.format("<img src=\"%s\" width=\"100%%\"/>", url));
    } else {
      html.append(String.format("<img src=\"%s\" width=\"%s\" height=\"%s\"/>", url, width, height));
    }
    
    html.append("<br/>");
    
    pageContext.getOut().println(html.toString());
  }

}
