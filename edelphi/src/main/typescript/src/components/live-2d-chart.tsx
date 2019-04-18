import * as React from "react";
import * as actions from "../actions";
import { StoreState, AccessToken, QueryQuestionAnswerNotification } from "../types";
import { connect } from "react-redux";
import { ScatterChart, Scatter, XAxis, YAxis, CartesianGrid, Cell, RechartsFunction } from 'recharts';
import Api, { QueryQuestionLive2dAnswerData } from "edelphi-client";
import { mqttConnection, OnMessageCallback } from "../mqtt";

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
  y: number
}

/**
 * Interface representing a single value in chart
 */
interface ChartValue {
  x: number,
  y: number,
  z: number
}

/**
 * Interface representing component state
 */
interface State {
  contents?: string,
  updating: boolean,
  loaded: boolean,
  commentId?: number
  values?: { [ answerId: string ]: Answer }
}

/**
 * React component for live 2d chart
 */
class Live2dChart extends React.Component<Props, State> {

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
      loaded: false
    };

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
    this.loadValues();
  }

  /**
   * Component did update life-cycle event
   */
  public async componentDidUpdate(prevProps: Props) {
    if (this.props.accessToken && !this.state.values) {
      this.loadValues();
    }
  }

  /** 
   * Component render method
   */
  public render() {
    const answers: Answer[] = this.state.values ? Object.values(this.state.values) : [];

    const data: ChartValue[] = answers.map((answer) => {
      return {
        x: answer.x,
        y: answer.y,
        z: 500
      };
    });
    
    return (
      <div style={{margin:"auto"}}>
        <ScatterChart onMouseDown={(data: RechartsFunction) => { this.onScatterMouseDown(data) }} width={400} height={400} margin={{ top: 20, right: 20, bottom: 20, left: 20 }}>
          <XAxis type="number" domain={[0, 100]} dataKey={'x'} unit='%' />
          <YAxis type="number" domain={[0, 100]} dataKey={'y'} unit='%' />
          <CartesianGrid />
          <Scatter isAnimationActive={false} name='A school' data={data} fill={'#fff'}>
            {
              data.map((entry, index) => {
                return <Cell key={`cell-${index}`} fill={this.getColor('GREEN', 'RED', entry.x || 0, entry.y || 0, 100, 100)} />
              })
            }
          </Scatter>
        </ScatterChart>
      </div>
    );
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
    const values: { [ answerId: string ]: Answer } = this.state.values || {};

    answers.forEach((answer) => {
      const data: QueryQuestionLive2dAnswerData = answer.data;
      values[answer.id!] = data;
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

  private getColor(colorX: any, colorY: any, x: number, y: number, maxX: number, maxY: number) {
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

    const result = await queryQuestionAnswersService.upsertQueryQuestionAnswer({
      queryReplyId: this.props.queryReplyId,
      queryPageId: this.props.pageId,
      data: answerData
    }, this.props.panelId, answerId);

    this.updateAnswer(result.id!, result.data.x, result.data.y);
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

  /**
   * Updates single answer
   * 
   * @param id answer id
   * @param x x
   * @param y y
   */
  private updateAnswer(id: string, x: number, y: number) {
    const values: { [ answerId: string ]: Answer } = this.state.values || {};

    values[id] = {
      x, y
    };

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