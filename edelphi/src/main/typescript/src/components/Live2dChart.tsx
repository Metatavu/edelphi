import * as React from "react";
import * as actions from "../actions";
import { StoreState, AccessToken } from "../types";
import { connect } from "react-redux";
import { ScatterChart, Scatter, XAxis, YAxis, CartesianGrid, Cell, RechartsFunction } from 'recharts';


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
 * Interface representing component state
 */
interface State {
  contents?: string,
  updating: boolean,
  loaded: boolean,
  commentId?: number
  xValue?: number
  yValue?: number
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

    console.log(this.props);

  }

  /**
   * Component did update life-cycle event
   */
  public async componentDidUpdate() {
    console.log(this.props);
  }

  /**
   * Add user scatter
   */
  private addUserScatter = async (data: any) => {
    const { xValue, yValue } = data;
    await this.setState({ xValue, yValue });
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
    const data = [
      { x: 34, y: 23, z: 200 },
      { x: 50, y: 50, z: 260 },
      { x: 80, y: 30, z: 400 },
      { x: 25, y: 25, z: 280 },
      { x: 50, y: 10, z: 500 },
      { x: 75, y: 75, z: 200 },
      { x: this.state.xValue, y: this.state.yValue, z: 1000 }
    ];
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