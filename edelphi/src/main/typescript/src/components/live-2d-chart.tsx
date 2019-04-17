import * as React from "react";
import * as actions from "../actions";
import { StoreState, AccessToken } from "../types";
import { connect } from "react-redux";
import { ScatterChart, Scatter, XAxis, YAxis, CartesianGrid, Cell, RechartsFunction } from 'recharts';
import Api, { QueryQuestionLive2dAnswerData } from "edelphi-client";

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

interface Answer {
  x: number,
  y: number
}

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
   * Add user scatter
   */
  private addUserScatter = async (data: any) => {
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

    const values: { [ answerId: string ]: Answer } = this.state.values;

    values[result.id!] = result.data;

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
  private convertToRange(value: any, fromLow: any, fromHigh: any, toLow: any, toHigh: any) {
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
   * Component render method
   */
  public render() {
    const answers: Answer[] = Object.values(this.state.values);

    const data: ChartValue[] = answers.map((answer) => {
      return {
        x: answer.x,
        y: answer.y,
        z: 500
      };
    });
    
    console.log("data", data);

    return (
      <div style={{margin:"auto"}}>
        <ScatterChart onMouseDown={(data: RechartsFunction) => { this.addUserScatter(data) }} width={400} height={400} margin={{ top: 20, right: 20, bottom: 20, left: 20 }}>
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