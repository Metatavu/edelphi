import * as React from "react";
import { AxisDomain, ScatterChart, XAxis, YAxis, ZAxis, CartesianGrid, Scatter, Cell, Label } from "recharts";
import { QueryLive2dAnswer } from "src/types";
import { QueryPage, QueryPageLive2DOptions, QueryPageLive2DColor } from "edelphi-client";

/**
 * Interface representing component properties
 */
interface Props {
  chartSize: number,
  page?: QueryPage,
  values: QueryLive2dAnswer[],
  onScatterChartClick?: (data: any) => Promise<void>
  onScatterChartMouseDown?: (data: any) => Promise<void>
}

/**
 * Interface representing component state
 */
interface State {
}

const LABEL_BOX_WIDTH = 20;
const LABEL_BOX_HEIGHT = 20;
const LABEL_MARGIN = 17;

/**
 * React component for live 2d chart
 */
export default class Live2dQueryChart extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
    };
  }

  /**
   * Component render method
   */
  public render() {
    if (!this.props.page) {
      return null;
    }

    const pageOptions = this.props.page.queryOptions as QueryPageLive2DOptions;
    if (!pageOptions.axisX || !pageOptions.axisY) {
      return null;
    }

    const optionsX = pageOptions.axisX.options || [];
    const optionsY = pageOptions.axisY.options || [];

    const domainX: [ AxisDomain, AxisDomain ] = [ 0, optionsX.length - 1 ];
    const domainY: [ AxisDomain, AxisDomain ] = [ 0, optionsY.length - 1 ];
    const colorY = ( pageOptions.axisY ? pageOptions.axisY.color : undefined ) || "RED";
    const colorX = ( pageOptions.axisX ? pageOptions.axisX.color : undefined ) || "GREEN";
    const data = this.props.values;

    return (
      <ScatterChart onClick={ this.props.onScatterChartClick } onMouseDown={ this.props.onScatterChartMouseDown } width={ this.props.chartSize } height={ this.props.chartSize } margin={{ top: 20, right: 20, bottom: 20, left: 20 }}>
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
  private renderAxisXLabelContents = (props: any): React.ReactNode => {
    if (!this.props.page) {
      return null;
    }

    const pageOptions = this.props.page.queryOptions as QueryPageLive2DOptions;
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
  private renderAxisYLabelContents = (props: any): React.ReactNode => {
    if (!this.props.page) {
      return null;
    }

    const pageOptions = this.props.page.queryOptions as QueryPageLive2DOptions;
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

  
}