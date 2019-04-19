import * as React from "react";
import * as actions from "../actions";
import { StoreState, AccessToken, QueryQuestionAnswerNotification } from "../types";
import { connect } from "react-redux";
import { ScatterChart, Scatter, XAxis, YAxis, CartesianGrid, Cell, RechartsFunction, AxisDomain, ZAxis, Label } from 'recharts';
import Api, { QueryQuestionLive2dAnswerData, QueryPageLive2DColor, QueryPage, QueryPageLive2DOptions } from "edelphi-client";
import { mqttConnection, OnMessageCallback } from "../mqtt";
import { Loader } from "semantic-ui-react";

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

/**
 * Interface representing component state
 */
interface State {
  contents?: string,
  updating: boolean,
  loaded: boolean,
  commentId?: number
  values: Answer[],
  page?: QueryPage
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

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      updating: true,
      loaded: false,
      values: []
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
    await this.load();

    setInterval(() => {
      this.pulse();
    }, 500);
  }

  /**
   * Component did update life-cycle event
   */
  public async componentDidUpdate(prevProps: Props) {
    if (!this.state.loaded) {
      this.load();
    }
  }

  /** 
   * Component render method
   */
  public render() {
    return (
      <div style={{margin:"auto"}} ref={ (element) => this.setWrapperDiv(element) }>
        { this.renderChart() }
      </div>
    );
  }

  /**
   * Renders chart component
   */
  private renderChart() {
    if (!this.state.loaded || !this.state.page || !this.wrapperDiv) {
      return <Loader/>;
    }

    const pageOptions = this.state.page.options as QueryPageLive2DOptions;
    if (!pageOptions.axisX || !pageOptions.axisY) {
      return <Loader/>;
    }

    const optionsX = pageOptions.axisX.options || [];
    const optionsY = pageOptions.axisY.options || [];

    const domainX: [ AxisDomain, AxisDomain ] = [ 0, optionsX.length - 1 ];
    const domainY: [ AxisDomain, AxisDomain ] = [ 0, optionsY.length - 1 ];
    const colorY = ( pageOptions.axisY ? pageOptions.axisY.color : undefined ) || "RED";
    const size = this.wrapperDiv.offsetWidth;
    const colorX = ( pageOptions.axisX ? pageOptions.axisX.color : undefined ) || "GREEN";

    return (
      <ScatterChart onMouseDown={(data: RechartsFunction) => { this.onScatterMouseDown(data) }} width={ size } height={ size } margin={{ top: 20, right: 20, bottom: 20, left: 20 }}>
        <XAxis type="number" domain={ domainX } dataKey={'x'} tickCount={ optionsX.length } tickFormatter={ (value) => this.formatTick(value, optionsX) }>
          <Label content={ this.renderAxisXLabelContents }/>
        </XAxis>
        <YAxis type="number" domain={ domainY } dataKey={'y'} tickCount={ optionsY.length } tickFormatter={ (value) => this.formatTick(value, optionsY) }>
          <Label content={ this.renderAxisYLabelContents }/>
        </YAxis>
        <ZAxis type="number" range={[500, 1000]} dataKey={'z'} />
        <CartesianGrid />
        <Scatter data={ this.state.values } fill={'#fff'}>
          {
            this.state.values.map((entry, index) => {
              return <Cell key={`cell-${index}`} fill={this.getColor(colorX, colorY, entry.x || 0, entry.y || 0, optionsX.length, optionsY.length)} />
            })
          }
        </Scatter>
      </ScatterChart>
    );
  }

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

    const pageOptions = this.state.page.options as QueryPageLive2DOptions;
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

    const pageOptions = this.state.page.options as QueryPageLive2DOptions;
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
      values: values
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
   * Event handler for handling scatter mouse down events
   */
  private onScatterMouseDown = async (data: any) => {
    if (!this.props.accessToken) {
      return;
    }

    const { xValue, yValue } = data;

    const queryQuestionAnswersService = Api.getQueryQuestionAnswersService(this.props.accessToken.token);
    const answerData: QueryQuestionLive2dAnswerData = {
      x: xValue,
      y: yValue
    };

    const answerId = `${this.props.pageId}-${this.props.queryReplyId}`;

    await queryQuestionAnswersService.upsertQueryQuestionAnswer({
      queryReplyId: this.props.queryReplyId,
      queryPageId: this.props.pageId,
      data: answerData
    }, this.props.panelId, answerId);

    // this.updateAnswer(result.id!, result.data.x, result.data.y);
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
        
        const queryQuestionAnswersService = Api.getQueryQuestionAnswersService(this.props.accessToken.token); 
        const answer = await queryQuestionAnswersService.findQueryQuestionAnswer(this.props.panelId, notification.answerId);
        this.updateAnswer(answer.id!, answer.data.x, answer.data.y);
      break;
    }
  }

  private pulse = () => {
    const values = this.state.values;
    const id = `${this.props.pageId}-${this.props.queryReplyId}`;
    
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
      values: values
    });
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