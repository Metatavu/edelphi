/*******************************************************************************
 * Copyright (c) 2006, 2007 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package fi.metatavu.edelphi.taglib.chartutil;

import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.util.ChartUtil;
import org.eclipse.emf.common.util.EList;

/**
 * Utility class for web component
 */

public class ChartWebHelper {

  /**
   * Checks if the output type is supported
   * 
   * @param type
   *          output type
   * @return supported or not
   */
  public static boolean checkOutputType(String type) {
    try {
      return ChartUtil.isOutputFormatSupport(type);
    } catch (ChartException e) {
      return false;
    }
  }

  /**
   * Checks if current chart has runtime data sets.
   * 
   * @param cm
   *          chart model
   * @return has runtime data or not
   */
  public static boolean isChartInRuntime(Chart cm) {
    if (cm instanceof ChartWithAxes) {
      Axis bAxis = ((ChartWithAxes) cm).getAxes().get(0);
      EList<Axis> oAxes = bAxis.getAssociatedAxes();
      for (int i = 0; i < oAxes.size(); i++) {
        Axis oAxis = oAxes.get(i);
        EList<SeriesDefinition> oSeries = oAxis.getSeriesDefinitions();
        for (int j = 0; j < oSeries.size(); j++) {
          SeriesDefinition sd = oSeries.get(j);
          if (sd.getRunTimeSeries().size() > 0) {
            return true;
          }
        }
      }
    } else if (cm instanceof ChartWithoutAxes) {
      SeriesDefinition bsd = ((ChartWithoutAxes) cm).getSeriesDefinitions().get(0);
      EList<SeriesDefinition> osds = bsd.getSeriesDefinitions();
      for (int i = 0; i < osds.size(); i++) {
        SeriesDefinition osd = osds.get(i);
        if (osd.getRunTimeSeries().size() > 0) {
          return true;
        }
      }
    }
    return false;
  }

}
