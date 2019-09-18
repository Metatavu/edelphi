import { QueryPageStatistics } from "../types";

/**
 * Utilities for calculating statistics
 */
export default class StatisticsUtils {

  /**
   * Calculates statistics for array of values
   * 
   * @param values values
   * @returns statistics
   */
  public static getStatistics(values: number[]): QueryPageStatistics {
    const sorted = (values || []).sort((a, b) => a - b);

    return {
      answerCount: values.length,
      q1: this.getQuantile(sorted, 1),
      q2: this.getQuantile(sorted, 2),
      q3: this.getQuantile(sorted, 3)
    };
  }

  /**
   * Returns quantile
   * 
   * @param sorted sorted array of values
   * @param quantile quantile index
   * @return quantile
   */
  private static getQuantile(sorted: number[], quantile: number) {
    if (sorted.length == 0) {
      return 0;
    }

    const index = Math.round((quantile / 4) * (sorted.length - 1));
    const base = Math.floor(index);

    const rest = index - base;
    if ((sorted[base + 1] !== undefined)) {
      return sorted[base] + rest * (sorted[base + 1] - sorted[base]);
    } else {
      return sorted[base];
    }
  }
  
}