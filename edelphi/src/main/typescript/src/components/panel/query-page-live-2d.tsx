import * as React from "react";
import * as actions from "../../actions";
import { StoreState, AccessToken, QueryQuestionAnswerNotification, QueryPageStatistics, QueryLive2dAnswer } from "../../types";
import { connect } from "react-redux";
import { QueryQuestionLive2dAnswerData, QueryPage, QueryQuestionAnswer } from "../../generated/client/models";
import { mqttConnection, OnMessageCallback } from "../../mqtt";
import { Loader, Dimmer, Segment, Grid } from "semantic-ui-react";
import strings from "../../localization/strings";
import ErrorDialog from "../error-dialog";
import Live2dQueryStatistics from "../generic/live2d-query-statistics";
import StatisticsUtils from "../../statistics/statistics-utils";
import Live2dQueryChart from "../generic/live2d-query-chart";
import Api from "../../api";

/**
 * Interface representing component properties
 */
interface Props {
  queryReplyId: number | null,
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
  loaded: boolean,
  commentId?: number
  values: QueryLive2dAnswer[],
  page?: QueryPage,
  error?: Error,
  statisticsX: QueryPageStatistics,
  statisticsY: QueryPageStatistics,
  chartSize: number | null
}

/**
 * Amount of pixels to use a minimum window margin for graph
 */
const GRAPH_WINDOW_OFFSET = 40;

/**
 * React component for live 2d chart
 */
class QueryPageLive2d extends React.Component<Props, State> {

  private chartContainerRef: HTMLDivElement | null = null;
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
      statisticsX: StatisticsUtils.getStatistics([]),
      statisticsY: StatisticsUtils.getStatistics([]),
      chartSize: null
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
    window.removeEventListener("resize", this.onWindowResize);
  }
  
  /**
   * Component did update life-cycle event
   */
  public async componentDidMount() {
    window.addEventListener("resize", this.onWindowResize);

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
          <Grid.Column mobile={ 16 } computer={ 10 }>
            <div style={{margin:"auto"}} ref={ (element) => this.setChartWrapperDiv(element) }>
              { this.renderChart() }
            </div>
          </Grid.Column>          
          <Grid.Column mobile={ 16 } computer={ 6 }>
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
   * Ref callback for chart wrapper div
   */
  private setChartWrapperDiv(element: HTMLDivElement | null) {
    this.chartContainerRef = element;

    if (this.chartContainerRef && !this.state.chartSize) {
      const maxSize = Math.min(window.innerWidth, window.innerHeight) - GRAPH_WINDOW_OFFSET;

      this.setState({
        chartSize: Math.min(this.chartContainerRef.offsetWidth, maxSize)
      });
    }
  }

  /**
   * Renders statistics
   */
  private renderStatistics = () => {
    return (
      <Live2dQueryStatistics statisticsX={ this.state.statisticsX } statisticsY={ this.state.statisticsY } />
    );
  }

  private renderChart() {
    if (!this.state.loaded || !this.state.page || !this.state.chartSize) {
      return null;
    }

    const values = this.getValuesVisible() ? this.state.values : [];

    return <Live2dQueryChart 
      page={ this.state.page } 
      values={ values } 
      chartSize={ this.state.chartSize }
      onScatterChartClick={ this.onScatterChartClick } 
      onScatterChartMouseDown={ this.onScatterChartMouseDown }/>
  }

  /**
   * Calculates statistics for x-axis
   * 
   * @param values answers
   * @returns statistics for x-axis
   */
  private getStatisticsX(values: QueryLive2dAnswer[]): QueryPageStatistics {
    return StatisticsUtils.getStatistics(values.map((value: QueryLive2dAnswer) => {
      return value.x;
    }));
  }

  /**
   * Calculates statistics for y-axis
   * 
   * @param values answers
   * @returns statistics for y-axis
   */
  private getStatisticsY(values: QueryLive2dAnswer[]): QueryPageStatistics {
    return StatisticsUtils.getStatistics(values.map((value: QueryLive2dAnswer) => {
      return value.y;
    }));
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
    const { accessToken, pageId, panelId } = this.props;

    if (!accessToken) {
      return;
    }

    this.setState({
      page: await Api.getQueryPagesApi(accessToken.token).findQueryPage({
        panelId: panelId,
        queryPageId: pageId
      })
    });
  }

  /**
   * Loads values from server
   */
  private async loadValues() {
    const { accessToken, pageId, queryId, panelId } = this.props;

    if (!accessToken) {
      return;
    }

    const queryQuestionAnswersApi = Api.getQueryQuestionAnswersApi(accessToken.token);
    const answers = await queryQuestionAnswersApi.listQueryQuestionAnswers({
      panelId: panelId,
      queryId: queryId,
      pageId: pageId
    });

    const values: QueryLive2dAnswer[] = answers.map((answer: QueryQuestionAnswer) => {
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
    const { accessToken, pageId, queryReplyId, panelId } = this.props;

    if (!accessToken || this.saving || !queryReplyId) {
      return;
    }

    this.saving = true;

    const queryQuestionAnswersApi = Api.getQueryQuestionAnswersApi(accessToken.token);
    const answerData: QueryQuestionLive2dAnswerData = {
      x: x,
      y: y
    };

    const answerId = this.getLoggedUserAnswerId();

    const updatedAnswer = await queryQuestionAnswersApi.upsertQueryQuestionAnswer({
      answerId: answerId,
      panelId: panelId,
      queryQuestionAnswer: {
        queryReplyId: queryReplyId,
        queryPageId: pageId,
        data: answerData
      }
    });

    if (updatedAnswer && updatedAnswer.id) {
      this.updateAnswer(updatedAnswer.id, updatedAnswer.data.x, updatedAnswer.data.y);
    }

    this.saving = false;
    this.savedAt = new Date().getTime();
  }

  /**
   * Recalculates a chart size
   */
  private recalculateChartSize = () => {
    if (this.chartContainerRef) {
      const maxSize = Math.min(window.innerWidth, window.innerHeight) - GRAPH_WINDOW_OFFSET;

      this.setState({
        chartSize: Math.min(this.chartContainerRef.offsetWidth, maxSize)
      });
    }
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

        const queryQuestionAnswersApi = Api.getQueryQuestionAnswersApi(this.props.accessToken.token); 
        const answer = await queryQuestionAnswersApi.findQueryQuestionAnswer({
          panelId: this.props.panelId,
          answerId: notification.answerId
        });
        
        this.updateAnswer(answer.id!, answer.data.x, answer.data.y);
      break;
    }
  }

  /**
   * Event handler for window resize events
   */
  private onWindowResize = () => {
    this.recalculateChartSize();
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

export default connect(mapStateToProps, mapDispatchToProps)(QueryPageLive2d);