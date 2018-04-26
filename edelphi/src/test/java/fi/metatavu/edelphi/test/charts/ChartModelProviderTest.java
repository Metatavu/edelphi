package fi.metatavu.edelphi.test.charts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryFieldDataStatistics;

public class ChartModelProviderTest {
  
  /**
   * Explodes bubble value array (describes value count in position as value) 
   * into array of answer values
   * 
   * e.g. [2.0, 2.0, 1.0] into [3.0, 3.0, 2.0, 2.0, 1.0, 1.0]
   * 
   * @param bubbleValues bubble values
   * @return answer array
   */
  public List<Double> explodeBubbleValues(double[] bubbleValues) {
    List<Double> result = new ArrayList<>();
    
    for (int y = 0; y < bubbleValues.length; y++) {
      for (double c = 0; c < bubbleValues[y]; c++) {
        result.add((double) (bubbleValues.length - 1) - y);
      }
    }
    
    return result;
  }
  
  /**
   * Creates statistics for specified x values
   * 
   * @param values bubble values
   * @param x x index
   * @return statistics
   */
  public QueryFieldDataStatistics getBubbleXStatistics(Double[][] values, int x) {
    return new QueryFieldDataStatistics(Arrays.asList(values[x]));
  }
  
  @Test
  public void testBubbleQuartilesX() {
    double[] xValues = {
      0.0,
      3.0,
      1.0,
      2.0,
      4.0,
      1.0,
      0.0
    };
    
    List<Double> values = new ArrayList<>();
    
    for (int y = 0; y < xValues.length; y++) {
      for (double c = 0; c < xValues[y]; c++) {
        values.add((double) (xValues.length - 1) - y);
      }
    }
    
    QueryFieldDataStatistics statistics = new QueryFieldDataStatistics(values);
    
    System.out.println(StringUtils.join(values, ','));

    System.out.println(statistics.getQ1());
    System.out.println(statistics.getQ3());
    
  }
  
}
