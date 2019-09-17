import { QueryPageStatistics } from "../types";

export default class StatisticsUtils {

  /**
   * Calculates statistics for array of values
   * 
   * @param values values
   * @returns statistics
   */
  public static getStatistics(values: number[]): QueryPageStatistics {
    return {
      answerCount: values.length,
      q1: this.getQuantile(values, 1),
      q2: this.getQuantile(values, 2),
      q3: this.getQuantile(values, 3)
    };
  }

  /**
   * Returns quantile over base value.
   * 
   * @param quantile quantile index
   * @param base quantile base
   * @return quantile over base value.
   */
  private static getQuantile(values: number[], quantile: number) {
    if (!values || values.length == 0) {
      return null;
    }

    const index = Math.round((quantile / 4) * (values.length - 1));
    return values[index];
  }
  
}