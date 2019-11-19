import * as React from "react";
import { Grid } from "semantic-ui-react";
import strings from "../../localization/strings";
import { QueryPageStatistics } from "../../types";

/**
 * Interface representing component properties
 */
interface Props {
  statisticsX: QueryPageStatistics,
  statisticsY: QueryPageStatistics
}

/**
 * Interface representing component state
 */
interface State {
}

/**
 * React component for live 2d statistis
 */
export default class Live2dQueryStatistics extends React.Component<Props, State> {

  /**
   * Component render method
   */
  public render = () => {
    return (
      <Grid>
        <Grid.Row>
          <Grid.Column>
            <h3> { strings.panel.query.live2d.statistics.title } </h3>
          </Grid.Column>
        </Grid.Row>

        <Grid.Row>
          <Grid.Column>
            <h4> { strings.panel.query.live2d.statistics.axisX } </h4>
          </Grid.Column>
        </Grid.Row>

        { this.renderStatistic(strings.panel.query.live2d.statistics.answerCount, this.props.statisticsX.answerCount ) }
        { this.renderStatistic(strings.panel.query.live2d.statistics.lowerQuartile, this.props.statisticsX.q1 ) }
        { this.renderStatistic(strings.panel.query.live2d.statistics.median, this.props.statisticsX.q2 ) }
        { this.renderStatistic(strings.panel.query.live2d.statistics.upperQuartile, this.props.statisticsX.q3 ) }

        <Grid.Row>
          <Grid.Column>
            <h4> { strings.panel.query.live2d.statistics.axisY } </h4>
          </Grid.Column>
        </Grid.Row>

        { this.renderStatistic(strings.panel.query.live2d.statistics.answerCount, this.props.statisticsY.answerCount ) }
        { this.renderStatistic(strings.panel.query.live2d.statistics.lowerQuartile, this.props.statisticsY.q1 ) }
        { this.renderStatistic(strings.panel.query.live2d.statistics.median, this.props.statisticsY.q2 ) }
        { this.renderStatistic(strings.panel.query.live2d.statistics.upperQuartile, this.props.statisticsY.q3 ) }
      </Grid>
    );
  }

  /**
   * Renders a statistics row
   */
  private renderStatistic = (label: string, value: number | null) => {
    return (
      <Grid.Row>
        <Grid.Column width={ 8 }>
          <label> { label } </label>
        </Grid.Column>
        <Grid.Column width={ 8 } className="text-right">
          <b> { value === null ? "NA" : value.toFixed(2) } </b>
        </Grid.Column>
      </Grid.Row>
    );
  }
}