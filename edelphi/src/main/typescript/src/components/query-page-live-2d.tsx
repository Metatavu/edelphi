import * as React from "react";
import * as actions from "../actions";
import { StoreState, AccessToken, QueryQuestionAnswerNotification } from "../types";
import { connect } from "react-redux";
import { ScatterChart, Scatter, XAxis, YAxis, CartesianGrid, Cell, AxisDomain, ZAxis, Label } from 'recharts';
import Api, { QueryQuestionLive2dAnswerData, QueryPageLive2DColor, QueryPage, QueryPageLive2DOptions } from "edelphi-client";
import { mqttConnection, OnMessageCallback } from "../mqtt";
import { Loader, Dimmer, Segment, Grid } from "semantic-ui-react";
import strings from "../localization/strings";
import ErrorDialog from "./error-dialog";

/**
 * Interface representing component properties
 */
interface Props {
  queryReplyId: number,
  accessToken?: AccessToken,
  pageId: number,
  panelId: number,
  queryId: number
}

/**
 * Interface representing a single user answer 
 */
interface Answer {
  x: number,
  y: number,
  z: number,
  id: string
}

interface Statistics {
  answerCount: number,
  q1: number | null,
  q2: number | null,
  q3: number | null
}

/**
 * Interface representing component state
 */
interface State {
  contents?: string,
  loaded: boolean,
  commentId?: number
  values: Answer[],
  page?: QueryPage,
  error?: Error,
  statisticsX: Statistics,
  statisticsY: Statistics
}

const LABEL_BOX_WIDTH = 20;
const LABEL_BOX_HEIGHT = 20;
const LABEL_MARGIN = 17;

/**
 * React component for live 2d chart
 */
class Live2dChart extends React.Component<Props, State> {

  private wrapperDiv: HTMLDivElement | null;
  private queryQuestionAnswersListener: OnMessageCallback;
  private savedAt: number = 0;
  private saving: boolean = false;

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      loaded: false,
      values: [],
      statisticsX: {
        answerCount: 0,
        q1: null,
        q2: null,
        q3: null
      },
      statisticsY: {
        answerCount: 0,
        q1: null,
        q2: null,
        q3: null
      }
    };

    this.wrapperDiv = null;
    this.queryQuestionAnswersListener = this.onQueryQuestionAnswerNotification.bind(this);
  }
  
  /**
   * Component will mount life-cycle event
   */
  public componentWillMount() {
    mqttConnection.subscribe("queryquestionanswers", this.queryQuestionAnswersListener);
  }

  /**
   * Component will unmount life-cycle event
   */
  public componentWillUnmount() {
    mqttConnection.unsubscribe("queryquestionanswers", this.queryQuestionAnswersListener);
  }
  
  /**
   * Component did update life-cycle event
   */
  public async componentDidMount() {
    try {
      await this.load();

      setInterval(() => {
        this.pulse();
      }, 500);
    } catch (e) {
      this.setState({
        error: e
      });
    }
  }

  /**
   * Component did update life-cycle event
   */
  public async componentDidUpdate(prevProps: Props) {
    try {
      if (!this.state.loaded) {
        this.load();
      }
    } catch (e) {
      this.setState({
        error: e
      });
    }
  }

  /** 
   * Component render method
   */
  public render() {
    if (this.state.error) {
      return <ErrorDialog error={ this.state.error } onClose={ () => this.setState({ error: undefined }) } /> 
    }

    if (!this.state.loaded) {
      return (
        <Segment style={{ minHeight: "400px" }}>
          <Dimmer inverted active>
            <Loader>{ strings.generic.loading }</Loader>
          </Dimmer>
        </Segment>
      );
    }

    return (
      <Grid>
        <Grid.Row>
          <Grid.Column width={ 10 }>
            <div style={{margin:"auto"}} ref={ (element) => this.setWrapperDiv(element) }>
              { this.renderChart() }
            </div>
          </Grid.Column>          
          <Grid.Column  width={ 6 }>
            { this.renderStatistics() }
          </Grid.Column>
        </Grid.Row>
      </Grid>
      );
  }

  /**
   * Component did catch method
   */
  public componentDidCatch = (error: Error) => {
    this.setState({
      error: error
    });
  }

  /**
   * Renders statistics
   */
  private renderStatistics = () => {
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

        { this.renderStatistic(strings.panel.query.live2d.statistics.answerCount, this.state.statisticsX.answerCount ) }
        { this.renderStatistic(strings.panel.query.live2d.statistics.lowerQuartile, this.state.statisticsX.q1 ) }
        { this.renderStatistic(strings.panel.query.live2d.statistics.median, this.state.statisticsX.q2 ) }
        { this.renderStatistic(strings.panel.query.live2d.statistics.upperQuartile, this.state.statisticsX.q3 ) }

        <Grid.Row>
          <Grid.Column>
            <h4> { strings.panel.query.live2d.statistics.axisY } </h4>
          </Grid.Column>
        </Grid.Row>

        { this.renderStatistic(strings.panel.query.live2d.statistics.answerCount, this.state.statisticsY.answerCount ) }
        { this.renderStatistic(strings.panel.query.live2d.statistics.lowerQuartile, this.state.statisticsY.q1 ) }
        { this.renderStatistic(strings.panel.query.live2d.statistics.median, this.state.statisticsY.q2 ) }
        { this.renderStatistic(strings.panel.query.live2d.statistics.upperQuartile, this.state.statisticsY.q3 ) }
      </Grid>
    );
  }

  /**
   * Calculates statistics for x-axis
   * 
   * @param values answers
   * @returns statistics for x-axis
   */
  private getStatisticsX(values: Answer[]): Statistics {
    return this.getStatistics(values.map((value: Answer) => {
      return value.x;
    }));
  }

  /**
   * Calculates statistics for y-axis
   * 
   * @param values answers
   * @returns statistics for y-axis
   */
  private getStatisticsY(values: Answer[]): Statistics {
    return this.getStatistics(values.map((value: Answer) => {
      return value.x;
    }));
  }

  /**
   * Calculates statistics for array of values
   * 
   * @param values values
   * @returns statistics
   */
  private getStatistics(values: number[]): Statistics {
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
  private getQuantile(values: number[], quantile: number) {
    if (!values || values.length == 0) {
      return null;
    }

    const index = Math.round((quantile / 4) * (values.length - 1));
    return values[index];
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
        <Grid.Column width={ 8 }>
          <b> { value === null ? "NA" : value.toFixed(2) } </b>
        </Grid.Column>
      </Grid.Row>
    );
  }

  /**
   * Renders chart component
   */
  private renderChart() {
    if (!this.state.loaded || !this.state.page || !this.wrapperDiv) {
      return null;
    }

    const pageOptions = this.state.page.queryOptions as QueryPageLive2DOptions;
    if (!pageOptions.axisX || !pageOptions.axisY) {
      return null;
    }

    const optionsX = pageOptions.axisX.options || [];
    const optionsY = pageOptions.axisY.options || [];

    const domainX: [ AxisDomain, AxisDomain ] = [ 0, optionsX.length - 1 ];
    const domainY: [ AxisDomain, AxisDomain ] = [ 0, optionsY.length - 1 ];
    const colorY = ( pageOptions.axisY ? pageOptions.axisY.color : undefined ) || "RED";
    const size = this.wrapperDiv.offsetWidth;
    const colorX = ( pageOptions.axisX ? pageOptions.axisX.color : undefined ) || "GREEN";
    const data = this.getValuesVisible() ? this.state.values : [];

    return (
      <ScatterChart onClick={ this.onScatterChartClick } onMouseDown={ this.onScatterChartMouseDown } width={ size } height={ size } margin={{ top: 20, right: 20, bottom: 20, left: 20 }}>
        <XAxis type="number" domain={ domainX } dataKey={'x'} tickCount={ optionsX.length } tickFormatter={ (value) => this.formatTick(value, optionsX) }>
          <Label content={ this.renderAxisXLabelContents }/>
        </XAxis>
        <YAxis type="number" domain={ domainY } dataKey={'y'} tickCount={ optionsY.length } tickFormatter={ (value) => this.formatTick(value, optionsY) }>
          <Label content={ this.renderAxisYLabelContents }/>
        </YAxis>
        <ZAxis type="number" range={[500, 1000]} dataKey={'z'} />
        <CartesianGrid />
        <Scatter data={ data } fill={'#fff'}>
          {
            data.map((entry, index) => {
              return <Cell key={`cell-${index}`} fill={this.getColor(colorX, colorY, entry.x || 0, entry.y || 0, optionsX.length, optionsY.length)} />
            })
          }
        </Scatter>
      </ScatterChart>
    );
  }

  /**
   * Formats tick value
   * 
   * @param value value
   * @param options options
   * @return formatted tick
   */
  private formatTick = (value: any, options: string[]) => {
    const index: number = value;
    return options[index];
  }

  /**
   * Renders x-axis label contents
   */
  private renderAxisXLabelContents = (props: any) => {
    if (!this.state.page) {
      return null;
    }

    const pageOptions = this.state.page.queryOptions as QueryPageLive2DOptions;
    const colorX = ( pageOptions.axisX ? pageOptions.axisX.color : undefined ) || "GREEN";
    const labelX = pageOptions.axisX ? pageOptions.axisX.label : undefined;

    const { viewBox } = props;

    const offsetTop = LABEL_BOX_HEIGHT * 1.5;
    const x = ((viewBox.width / 2) + viewBox.x) - (LABEL_BOX_WIDTH / 2);

    return (
      <g transform={ `translate(${x} ${ viewBox.y + offsetTop })` }>
        <rect height={ LABEL_BOX_HEIGHT } width={ LABEL_BOX_WIDTH } style={{ fill: colorX }} />
        <text className="recharts-text recharts-label" textAnchor="start"><tspan dx={ LABEL_BOX_WIDTH + LABEL_MARGIN } dy={ offsetTop / 2 }>{ labelX }</tspan></text>
      </g>
    );
  }

  /**
   * Renders y-axis label contents
   */
  private renderAxisYLabelContents = (props: any) => {
    if (!this.state.page) {
      return null;
    }

    const pageOptions = this.state.page.queryOptions as QueryPageLive2DOptions;
    const colorY = ( pageOptions.axisY ? pageOptions.axisY.color : undefined ) || "RED";
    const label = pageOptions.axisY ? pageOptions.axisY.label : undefined;

    const { viewBox } = props;

    const offsetTop = LABEL_BOX_HEIGHT * 1.5;
    const x = viewBox.x;
    const y = ((viewBox.height / 2) + viewBox.y) - (LABEL_BOX_HEIGHT / 2);

    return (
      <g transform={ `translate(${x} ${y})` }>
        <rect height={ LABEL_BOX_HEIGHT } width={ LABEL_BOX_WIDTH } style={{ fill: colorY }} />
        <text transform={ "rotate(-90, 0, 0)" } className="recharts-text recharts-label" textAnchor="start"><tspan dx={ LABEL_MARGIN } dy={ offsetTop / 2 }>{ label }</tspan></text>
      </g>
    );
  }
  
  /**
   * Ref callback for wrapper div
   */
  private setWrapperDiv(element: HTMLDivElement | null) {
    if (element) {
      this.wrapperDiv = element;
    }
  }

  /**
   * Loads initial datas
   */
  private async load() {
    if (!this.props.accessToken) {
      return;
    }

    await this.loadSettings();
    await this.loadValues();

    this.setState({
      loaded: true
    });
  }

  /**
   * Loads settings
   */
  private async loadSettings() {
    if (!this.props.accessToken) {
      return;
    }

    const getQueryPagesService= Api.getQueryPagesService(this.props.accessToken.token);

    this.setState({
      page: await getQueryPagesService.findQueryPage(this.props.panelId, this.props.pageId)
    });
  }

  /**
   * Loads values from server
   */
  private async loadValues() {
    if (!this.props.accessToken) {
      return;
    }

    const queryQuestionAnswersService = Api.getQueryQuestionAnswersService(this.props.accessToken.token);
    const answers = await queryQuestionAnswersService.listQueryQuestionAnswers(this.props.panelId, this.props.queryId, this.props.pageId);

    const values: Answer[] = answers.map((answer) => {
      return {
        x: answer.data.x,
        y: answer.data.y,
        z: 500,
        id: answer.id!
      };
    });
    
    this.setState({
      values: values,
      statisticsX: this.getStatisticsX(values),
      statisticsY: this.getStatisticsY(values)
    });
  }

  /**
   * Converts value from range to new range
   * 
   * @param {Number} value value
   * @param {Number} fromLow from low
   * @param {Number} fromHigh from high
   * @param {Number} toLow to low
   * @param {Number} toHigh to high
   * @return new value
   */
  private convertToRange(value: number, fromLow: number, fromHigh: number, toLow: number, toHigh: number): number {
    const fromLength = fromHigh - fromLow;
    const toRange = toHigh - toLow;
    const newValue = toRange / (fromLength / value);

    if (newValue < toLow) {
      return toLow;
    } else if (newValue > toHigh) {
      return toHigh;
    }

    return newValue;
  }

  /**
   * Returns color for given point in coordinates
   * 
   * @param colorX color for x-axis
   * @param colorY color for y-axis
   * @param x x
   * @param y y
   * @param maxX max x 
   * @param maxY max y
   */
  private getColor(colorX: QueryPageLive2DColor, colorY: QueryPageLive2DColor, x: number, y: number, maxX: number, maxY: number) {
    const cX = colorX || 'RED';
    const cY = colorY || 'BLUE';
    const bColor = 100;
    const xColor = Math.floor(this.convertToRange(x, 0, maxX, 0, 255));
    const yColor = Math.floor(this.convertToRange(y, 0, maxY, 0, 255));

    const r = cX === 'RED' ? xColor : cY === 'RED' ? yColor : bColor;
    const g = cX === 'GREEN' ? xColor : cY === 'GREEN' ? yColor : bColor;
    const b = cX === 'BLUE' ? xColor : cY === 'BLUE' ? yColor : bColor;

    return `rgb(${r}, ${g}, ${b})`;
  }

  /**
   * Returns whether values should be visible or not
   * 
   * @returns whether values should be visible or not
   */
  private getValuesVisible = () => {
    if (!this.state.page) {
      return false;
    }

    if (this.state.page.queryOptions.answersVisible == "AFTER_OWN_ANSWER") {
      return this.getHasOwnAnswer();
    }

    return true;
  }

  /**
   * Returns whether user has already answered or not
   * 
   * @return whether user has already answered or not
   */
  private getHasOwnAnswer = () => {
    const values = this.state.values;
    const id = this.getLoggedUserAnswerId();

    for (let i = 0; i < values.length; i++) {
      if (values[i].id == id) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns logged user answer id
   * 
   * @return logged user answer id
   */
  private getLoggedUserAnswerId = () => {
    return `${this.props.pageId}-${this.props.queryReplyId}`;
  }

  /**
   * Pulsates own answer
   */
  private pulse = () => {
    const values = this.state.values;
    const id = this.getLoggedUserAnswerId();
    
    for (let i = 0; i < values.length; i++) {
      if (values[i].id == id) {
        values[i].z = values[i].z > 500 ? 500 : 1000;

        this.setState({
          values: values
        });

        return;
      }
    }
  }

  /**
   * Updates single answer
   * 
   * @param id answer id
   * @param x x
   * @param y y
   * @param own whether answer is own or not
   */
  private updateAnswer(id: string, x: number, y: number) {
    const values = this.state.values;
    let updated = false;

    for (let i = 0; i < values.length; i++) {
      if (values[i].id == id) {
        values[i].x = x;
        values[i].y = y;
        updated = true;
        break;
      }
    }

    if (!updated) {
      values.push({
        x: x,
        y: y,
        z: 500,
        id: id
      });
    }
    
    this.setState({
      values: values,
      statisticsX: this.getStatisticsX(values),
      statisticsY: this.getStatisticsY(values)
    });
  }

  /**
   * Saves user answer
   * 
   * @param x answer x
   * @param y answer y
   */
  private saveAnswer = async (x: number, y: number) => {
    if (!this.props.accessToken || this.saving) {
      return;
    }

    this.saving = true;

    const queryQuestionAnswersService = Api.getQueryQuestionAnswersService(this.props.accessToken.token);
    const answerData: QueryQuestionLive2dAnswerData = {
      x: x,
      y: y
    };

    const answerId = this.getLoggedUserAnswerId();

    const updatedAnswer = await queryQuestionAnswersService.upsertQueryQuestionAnswer({
      queryReplyId: this.props.queryReplyId,
      queryPageId: this.props.pageId,
      data: answerData
    }, this.props.panelId, answerId);

    if (updatedAnswer && updatedAnswer.id) {
      this.updateAnswer(updatedAnswer.id, updatedAnswer.data.x, updatedAnswer.data.y);
    }

    this.saving = false;
    this.savedAt = new Date().getTime();
  }

  /**
   * Event handler for handling scatter click events
   * 
   * @param data event data
   */
  private onScatterChartClick = async (data: any) => {
    if (!data) {
      return;
    }

    const { xValue, yValue } = data;
    await this.saveAnswer(xValue, yValue);
  }

  /**
   * Event handler for handling scatter mouse down events
   * 
   * @param data event data
   */
  private onScatterChartMouseDown = async (data: any) => {
    if (!data) {
      return;
    }

    const { xValue, yValue } = data;
    await this.saveAnswer(xValue, yValue);
  }

  /**
   * Handles query question comment notification MQTT message
   * 
   * @param notification notification
   */
  private async onQueryQuestionAnswerNotification(notification: QueryQuestionAnswerNotification) {
    switch (notification.type) {
      case "UPDATED":
        if (!this.props.accessToken) {
          return;
        }

        const loggedUserAnswerId = this.getLoggedUserAnswerId();
        const now = new Date().getTime();

        if (loggedUserAnswerId == notification.answerId && (now - this.savedAt < 10000)) {
          return;
        }

        const queryQuestionAnswersService = Api.getQueryQuestionAnswersService(this.props.accessToken.token); 
        const answer = await queryQuestionAnswersService.findQueryQuestionAnswer(this.props.panelId, notification.answerId);
        this.updateAnswer(answer.id!, answer.data.x, answer.data.y);
      break;
    }
  }
}

/**
 * Redux mapper for mapping store state to component props
 * 
 * @param state store state
 */
function mapStateToProps(state: StoreState) {
  return {
    accessToken: state.accessToken,
    locale: state.locale
  };
}

/**
 * Redux mapper for mapping component dispatches 
 * 
 * @param dispatch dispatch method
 */
function mapDispatchToProps(dispatch: React.Dispatch<actions.AppAction>) {
  return {};
}

export default connect(mapStateToProps, mapDispatchToProps)(Live2dChart);