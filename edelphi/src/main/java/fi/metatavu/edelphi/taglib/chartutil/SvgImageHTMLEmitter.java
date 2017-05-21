/*******************************************************************************
 * Copyright (c) 2006 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package fi.metatavu.edelphi.taglib.chartutil;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.core.exception.BirtException;

import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartModelProvider;

public class SvgImageHTMLEmitter implements ImageHTMLEmitter {

  private Chart chartModel;
  private int width;
  private int height;
  private boolean lazy;

  public SvgImageHTMLEmitter(Chart chartModel, int width, int height) {
    super();
    this.chartModel = chartModel;
    this.width = width;
    this.height = height;
    this.lazy = false;
  }
  
  public void setLazy(boolean lazy) {
    this.lazy = lazy;
  }

  @Override
  public String generateHTML() throws IOException, BirtException {
    byte[] chartData = ChartModelProvider.getChartData(chartModel, "SVG");
    
    StringBuilder html = new StringBuilder();
    html.append("<object type=\"image/svg+xml\"");
    
    if (lazy) {
      String id = UUID.randomUUID().toString();
      ReportChartCache.put(id, chartData);
      html.append(String.format(" data-data=\"/queries/chartimage.binary?id=%s\"", id));
      html.append(" data=\"/_themes/default/gfx/ui/report-image-loader.svg\"");
    } else {
      StringBuilder dataUrlBuilder = new StringBuilder();
      dataUrlBuilder.append("data:image/svg+xml;charset=UTF-8;base64,");
      dataUrlBuilder.append(Base64.encodeBase64String(chartData));
      
      html.append(String.format(" data=\"%s\"", dataUrlBuilder.toString()));
    }
    
    html
      .append(" width=\"").append(width).append('"')
      .append(" height=\"").append(height).append('"')
      .append(" style=\"display: block\"")
      .append("></object>");
    
    return html.toString();
  }
  
}
