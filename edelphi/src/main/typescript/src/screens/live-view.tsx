import * as React from "react";
import * as actions from "../actions";
import * as _ from "lodash";
import { StoreState, AccessToken, QueryQuestionCommentNotification, QueryQuestionAnswerNotification, QueryPageStatistics, QueryLive2dAnswer } from "../types";
import { connect } from "react-redux";
import { Grid, DropdownItemProps, DropdownProps, Form, Container, Icon, Transition, SemanticShorthandCollection, BreadcrumbSectionProps, Button, Tab } from "semantic-ui-react";
import PanelAdminLayout from "../components/generic/panel-admin-layout";
import { Panel, QueryQuestionComment, Query, QueryPage, QueryQuestionAnswer, QueryQuestionCommentCategory, User } from "../generated/client/models";
import "../styles/live-view.scss";
import { mqttConnection, OnMessageCallback } from "../mqtt";
import * as queryString from "query-string";
import moment from "moment";
import getLanguage from "../localization/language";
import strings from "../localization/strings";
import StatisticsUtils from "../statistics/statistics-utils";
import Live2dQueryStatistics from "../components/generic/live2d-query-statistics";
import Live2dQueryChart from "../components/generic/live2d-query-chart";
import Api from "../api";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken?: AccessToken,
  location: any
}

/**
 * Interface representing component state
 */
interface State {
  panel?: Panel,
  loggedUser?: User,
  answers: QueryQuestionAnswer[],
  chartValues: QueryLive2dAnswer[],
  comments: QueryQuestionComment[],
  categories: QueryQuestionCommentCategory[],
  pages: QueryPage[],
  queries: Query[],
  queryId?: number,
  pageId?: number,
  categoryId?: number,
  loading: boolean,
  pageMaxX?: number,
  pageMaxY?: number,
  redirectTo?: string,
  rootMap:  { [ key: number ]: QueryQuestionComment[] },
  parentMap: { [ key: number ]: QueryQuestionComment[] },
  repliesOpen: number[],
  answersFullscreen: boolean,
  commentsFullscreen: boolean,
  statisticsX: QueryPageStatistics,
  statisticsY: QueryPageStatistics,
  chartSize: number | null
  commentCellExpanded: number | null
}

/**
 * Interface representing a answer and it's comment pair
 */
interface CommentAndAnswer {
  answer: QueryQuestionAnswer;
  comment: QueryQuestionComment;
}

/**
 * Amount of pixels to use a minimum window margin for graph
 */
const GRAPH_WINDOW_OFFSET = 40;

/**
 * React component for comment editor
 */
class LiveView extends React.Component<Props, State> {

  private chartContainerRef: HTMLDivElement | null;
  private queryQuestionCommentsListener: OnMessageCallback;
  private queryQuestionAnswersListener: OnMessageCallback;
  
  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      answers: [],
      chartValues: [],
      comments: [],
      queries: [],
      pages: [],
      categories: [],
      loading: false,
      parentMap: { },
      rootMap: { },
      pageMaxX: 6,
      pageMaxY: 6,
      repliesOpen: [],
      answersFullscreen: false,
      commentsFullscreen: false,
      statisticsX: StatisticsUtils.getStatistics([]),
      statisticsY: StatisticsUtils.getStatistics([]),
      chartSize: null,
      commentCellExpanded: null
    };

    this.chartContainerRef = null;
    this.queryQuestionCommentsListener = this.onQueryQuestionCommentNotification.bind(this);
    this.queryQuestionAnswersListener = this.onQueryQuestionAnswerNotification.bind(this);
  }

  /**
   * Component will mount life-cycle event
   */
  public componentDidMount = async () => {
    mqttConnection.subscribe("queryquestioncomments", this.queryQuestionCommentsListener);
    mqttConnection.subscribe("queryquestionanswers", this.queryQuestionAnswersListener);
    window.addEventListener("resize", this.onWindowResize);

    const queryParams = queryString.parse(this.props.location.search);    
    const panelId = parseInt(queryParams.panelId as string);

    const { accessToken } = this.props;
    if (!accessToken) {
      return;
    }

    this.setState({
      loading: true
    });

    const panel = await Api.getPanelsApi(accessToken.token).findPanel({ panelId: panelId });
    const queries = await Api.getQueriesApi(accessToken.token).listQueries({ panelId: panelId });
    const loggedUser = await Api.getUsersApi(accessToken.token).findUser({ userId: accessToken.userId });

    this.setState({
      loading: false,
      panel: panel,
      queries: queries,
      loggedUser: loggedUser
    });
  }

  /**
   * Component will unmount life-cycle event
   */
  public componentWillUnmount() {
    mqttConnection.unsubscribe("queryquestionanswers", this.queryQuestionAnswersListener);
    mqttConnection.unsubscribe("queryquestioncomments", this.queryQuestionCommentsListener);
    window.removeEventListener("resize", this.onWindowResize);
  }

  /**
   * Component did update life-cycle event
  */
  public async componentDidUpdate(prevProps: Props, prevState: State) {
    if (this.state.categoryId === undefined || !_.isEqual(this.state.categories, prevState.categories)) {
      this.setState({
        categoryId: this.state.categories.length ? this.state.categories[0].id : 0
      });
    }

    if (this.state.comments.length !== prevState.comments.length) {
      this.updateCommentMaps();
    }
  }

  /** 
   * Component render method
   */
  public render() {
    if (!this.state.panel || !this.state.loggedUser) {
      return null;
    }

    const breadcrumbs: SemanticShorthandCollection<BreadcrumbSectionProps> = [
      { key: "home", content: strings.generic.eDelphi, href: "/" },
      { key: "panel", content: this.state.panel.name, href: `/${this.state.panel.urlName}` },
      { key: "panel-admin", content: strings.generic.panelAdminBreadcrumb, href: `/panel/admin/dashboard.page?panelId=${this.state.panel.id}` },
      { key: "commentview", content: this.state.panel.name, active: true }      
    ];

    if (this.state.commentsFullscreen) {
      return this.renderCommentsView();
    }

    if (this.state.answersFullscreen) {
      return this.renderAnswersView();
    }

    return (
      <PanelAdminLayout loggedUser={ this.state.loggedUser } breadcrumbs={ breadcrumbs } loading={ this.state.loading } panel={ this.state.panel } redirectTo={ this.state.redirectTo }>
        <div style={{ width: "100%", height:"100%" }}>
          <Grid>
            { this.renderControls() }
            { this.renderView() }
          </Grid>
        </div>
      </PanelAdminLayout>
    );
  }

  /**
   * Renders a view
   */
  private renderView = () => {
    if (!this.state.queryId || !this.state.pageId) {
      return (
        <Container> 
          <p className="instructions">{ strings.panelAdmin.liveView.selectQueryAndPage }</p>
        </Container>
      );
    }

    return this.renderTabs();
  }

  /**
   * Renders tabs
   */
  private renderTabs = () => {
    const panes = [{
      menuItem: strings.panelAdmin.liveView.answersTab,
      render: () => <Tab.Pane> { this.renderAnswersView() } </Tab.Pane>
    }, {
      menuItem: strings.panelAdmin.liveView.commentsTab,
      render: () => <Tab.Pane> { this.renderCommentsView() } </Tab.Pane>
    }];

    return (
      <Grid.Row>
        <Grid.Column>
          <Container> 
            <Tab menu={{ color: "orange", pointing: true }} panes={panes}/>
          </Container>
        </Grid.Column>
      </Grid.Row>
    );
  }

  /**
   * Renders answers view
   */
  private renderAnswersView = () => {
    const page = this.state.pages.find((page) => {
      return page.id == this.state.pageId;
    });
    const title = page !== undefined ? page.title : "";

    return (
      <Grid style={{ marginTop: this.isFullscreen() ? "30px" : "0px" }}>
        <Grid.Row>
          <Grid.Column width={ 10 }>
            <h3 style={{ paddingLeft: this.isFullscreen() ? "30px" : "0px" }}>{ title }</h3>
          </Grid.Column>
        </Grid.Row>
        <Grid.Row>
          <Grid.Column width={ 10 }>
            <div style={{margin:"auto"}} ref={ (element) => this.setChartWrapperDiv(element) }>
              { this.renderChart() }
            </div>
          </Grid.Column>          
          <Grid.Column  width={ 6 }>
            { this.renderAnswersFullscreenButton() }
            { this.renderStatistics() }
          </Grid.Column>
        </Grid.Row>
      </Grid>
    );
  }

  /**
   * Renders statistics
   */
  private renderStatistics = () => {
    return (
      <Live2dQueryStatistics statisticsX={ this.state.statisticsX } statisticsY={ this.state.statisticsY } />
    );
  }
  
  /**
   * Renders chart component
   */
  private renderChart() {
    if (!this.state.chartSize) {
      return null;
    }

    const page = this.state.pages.find((page) => {
      return page.id == this.state.pageId;
    });

    return (
      <Live2dQueryChart values={ this.state.chartValues } page={ page } chartSize={ this.state.chartSize }/>
    );
  }

  /**
   * Renders comment view
   */
  private renderCommentsView = () => {
    const page = this.state.pages.find((page) => {
      return page.id == this.state.pageId;
    });

    const xLabel = page && page.queryOptions.axisX ? page.queryOptions.axisX.label : "";
    const yLabel = page && page.queryOptions.axisY ? page.queryOptions.axisY.label : "";
    
    return (
      <Container className="comments-container" style={{ marginTop: this.isFullscreen() ? "30px" : "0px" }}>
        <div className="comments-list-axis-x-container">
          { this.renderCommentsFullscreenButton() }
          <div className="comments-list-axis-x"> { xLabel } </div>
        </div>
        <div className="comments-list-axis-y-container">
          <div className="comments-list-axis-y"> { yLabel } </div>
        </div>
        { this.renderCommentGrid() }
      </Container>
    );
  }

  /**
   * Renders button for toggling answers fullscreen mode
   */
  private renderAnswersFullscreenButton = () => {
    if (!this.state.queryId) {
      return null;
    }

    return (
      <Button icon className="answers-fullscreen-button" onClick={ this.onAnswersFullscreenButtonClick }>
        <Icon name={ this.state.answersFullscreen ? "compress" : "expand" }/>
      </Button>
    );
  }

  /**
   * Renders button for toggling comments fullscreen mode
   */
  private renderCommentsFullscreenButton = () => {
    if (!this.state.queryId) {
      return null;
    }

    return (
      <Button icon className="comments-fullscreen-button" onClick={ this.onCommentsFullscreenButtonClick }>
        <Icon name={ this.state.commentsFullscreen ? "compress" : "expand" }/>
      </Button>
    );
  }

  /**
   * Renders controls for the view
   */
  private renderControls = () => {
    if (this.state.commentsFullscreen) {
      return null;
    }

    const queryOptions: DropdownItemProps[] = this.state.queries.map((query) => {
      return {
        key: query.id,
        value: query.id,
        text: query.name
      };
    });

    const pageOptions: DropdownItemProps[] = this.state.pages.map((page) => {
      return {
        key: page.id,
        value: page.id,
        text: page.title
      };
    });

    const categoryOptions: DropdownItemProps[] = !this.state.categories.length ? [{
      key: "default",
      value: 0,
      text: strings.panelAdmin.liveView.defaultCategory
    }] : this.state.categories.map((category) => {
      return {
        key: category.id,
        value: category.id,
        text: category.name
      };
    });
    
    return (
      <Grid.Row>
        <Grid.Column>
          <Container> 
            <Form className="controls">
              <Form.Group widths='equal'>
                <Form.Select fluid label={ strings.panelAdmin.liveView.querySelectLabel } value={ this.state.queryId } onChange={ this.onQueryChange } options={ queryOptions }/>
                <Form.Select fluid disabled={ !this.state.queryId } label={ strings.panelAdmin.liveView.pageSelectLabel } value={ this.state.pageId } onChange={ this.onPageChange } options={ pageOptions }/>
                <Form.Select fluid disabled={ !this.state.pageId } label={ strings.panelAdmin.liveView.commentCategorySelectLabel } value={ this.state.categoryId } onChange={ this.onCategoryChange } options={ categoryOptions }/>
              </Form.Group>
            </Form>
          </Container>
        </Grid.Column>
      </Grid.Row>
    );
  } 

  /**
   * Renders comments view grid
   */
  private renderCommentGrid = () => {
    const commentAnswers: CommentAndAnswer[] = this.state.comments
      .filter((comment) => {
        return !comment.parentId;
      })
      .map((comment) => {
        const answer = this.getCommentAnswer(comment);

        return {
          answer: answer,
          comment: comment 
        }
      })
      .filter((commentAnswer) => {
        return commentAnswer && commentAnswer.answer && commentAnswer.comment;
      }) as CommentAndAnswer[];

    if (this.state.commentCellExpanded !== null) {
      const y = Math.floor(this.state.commentCellExpanded / 2);
      const x = this.state.commentCellExpanded - (y * 2);
      return this.renderCommentCell(commentAnswers, x, y);
    }

    return (
      <Grid className="comments-grid">  
        {
          [1, 0].map((y) => {
            return (
              <Grid.Row className="comment-list-row" key={y}> 
                { [0, 1].map((x) => {
                  return this.renderCommentCell(commentAnswers, x, y);
                }) }
              </Grid.Row>
            );
          })
        }
      </Grid>
    );
  }

  /**
   * Renders single comments view cell
   * 
   * @param commentAnswers comments and answers
   * @param x cell x index
   * @param y cell y index
   */
  private renderCommentCell(commentAnswers: CommentAndAnswer[], x: number, y: number) {
    if (!this.state.pageMaxX || !this.state.pageMaxY) {
      return null;
    }

    const bounds = {
      x1: (x + 0) * (this.state.pageMaxX / 2),
      x2: (x + 1) * (this.state.pageMaxX / 2),
      y1: (y + 0) * (this.state.pageMaxY / 2),
      y2: (y + 1) * (this.state.pageMaxY / 2),
    };

    const cellCommentAnswers = commentAnswers.filter((commentAnswer) => {
      const answer = commentAnswer.answer;
      return answer.data.x >= bounds.x1 && answer.data.x <= bounds.x2 && answer.data.y >= bounds.y1 && answer.data.y <= bounds.y2;
    });

    const commentListClasses = ["comments-list"];
    if (this.state.commentCellExpanded !== null) {
      commentListClasses.push("comments-list-expanded");
    }

    return (
      <Grid.Column key={`cell-${x}-${y}`} className="comment-list-cell" width={ 8 }>
        <div className={ commentListClasses.join(" ") }>
          { this.renderExpandCommentCellButton(x, y) }
          { this.renderComments(cellCommentAnswers, x, y) }
        </div>
      </Grid.Column>
    );
  }

  /**
   * Renders expand comment cell button
   */
  private renderExpandCommentCellButton = (x: number, y: number) => {
    return (
      <Button icon className="expand-comments-button" onClick={ () => this.onExpandCommentCellButtonClick(x, y) }>
        <Icon name={ this.state.commentCellExpanded !== null ? "compress" : "expand" }/>
      </Button>
    ); 
  }

  /**
   * Renders cell's comments and answers
   * 
   * @param cellCommentAnswers comments and answers
   */
  private renderComments(cellCommentAnswers: CommentAndAnswer[], x: number, y: number) {
    return cellCommentAnswers.map((cellCommentAnswer) => {
      const comment = cellCommentAnswer.comment;

      return (
        <div key={`comment-${x}-${y}-${comment.id}`} className="comment">
          <div className="comment-created">{ this.formatDate(comment.created) }</div>
          <div className="comment-contents">{ this.renderCommentContents(comment) }</div>
          { this.renderCommentReplies(comment, x, y) }
        </div>
      );
    });
  }

  /**
   * Renders comment replies
   * 
   * @param comment comment
   * @param x cell x index
   * @param y cell y index
   */
  private renderCommentReplies = (comment: QueryQuestionComment, x: number, y: number) => {
    const childComments = this.state.rootMap[comment.id!] || [];

    if (!childComments.length) {
      return null;
    }

    const repliesOpen = this.state.repliesOpen.indexOf(comment.id!) != -1;
    const onClick = () => {
      if (repliesOpen) {
        this.setState({
          repliesOpen: this.state.repliesOpen.filter((id) => {
            return id != comment.id;
          })
        });
      } else {
        this.setState({
          repliesOpen: [ ... this.state.repliesOpen, comment.id! ]
        });
      }
    }

    return (
      <div>
        <div className="comment-replies-toggle" key={`replies-${x}-${y}-${comment.id}`} onClick={ onClick }>
          { repliesOpen ? <Icon name="minus" size="small" color="green"/> : <Icon name="plus" size="small" color="green"/> }
          { strings.formatString(strings.panelAdmin.liveView.replyCount, childComments.length) }
        </div>
        <div className="comment-replies">
          <Transition.Group animation="fade" duration={ 300 }>
            { repliesOpen && this.renderReplyComments(comment, x, y) }
          </Transition.Group>
        </div>
      </div>
    );
  }

  /**
   * Renders reply comments
   * 
   * @param parent parent comment
   * @param x cell x index
   * @param y cell y index
   */
  private renderReplyComments = (parent: QueryQuestionComment, x: number, y: number) => {
    const comments = this.state.parentMap[parent.id!] || [];

    return comments.map((comment) => {
      return (
        <div key={`${parent.id}-reply-${x}-${y}-${comment.id}`} >
          <div className="reply-created">{ this.formatDate(comment.created) }</div>
          <div className="reply-contents">{ this.renderCommentContents(comment) }</div>
          <div className="reply-children">{ this.renderReplyComments(comment, x, y) }</div>
        </div>
      );
    });
  }

  /**
   * Renders comment's contents
   * 
   * @param comment comment
   * @returns rendered contents
   */
  private renderCommentContents(comment: QueryQuestionComment) {
    if (!comment || !comment.contents) {
      return "";
    }

    return comment.contents.split("\n").map((item, index) => {
      return <span key={index}>{item}<br/></span>;
    });
  }

  /**
   * Loads view data
   */
  private async loadData() {
    const { accessToken } = this.props;
    const { panel, queryId, pageId, pages, categoryId } = this.state;

    if (!accessToken || !panel || !panel.id) {
      return;
    }

    const queryQuestionCommentsApi = Api.getQueryQuestionCommentsApi(accessToken.token);
    const queryPagesApi = Api.getQueryPagesApi(accessToken.token);
    const queryQuestionAnswersApi = Api.getQueryQuestionAnswersApi(accessToken.token);
    const queryQuestionCommentCategoriesApi = Api.getQueryQuestionCommentCategoriesApi(accessToken.token);

    const panelId = panel.id;

    this.setState({
      loading: true
    });

    if (queryId) {
      const pages = await queryPagesApi.listQueryPages({ panelId: panelId, queryId: queryId, includeHidden: false });

      await this.setStateAsync({
        pages: pages.filter((page) => {
          return page.type == "LIVE_2D";
        })
      });
    }

    if (pageId && queryId && panel && panel.id) {
      const page = pages.find((page) => {
        return page.id == pageId;
      });

      if (!page || !page.queryOptions.axisX || !page.queryOptions.axisY || !page.queryOptions.axisX.options || !page.queryOptions.axisY.options) {
        throw new Error("Could not lookup page axes");
      }

      const categories = await queryQuestionCommentCategoriesApi
        .listQueryQuestionCommentCategories({ panelId: panel.id, pageId: pageId });

      await this.setStateAsync({
        categories: categories,
        pageMaxX: page.queryOptions.axisX.options.length,
        pageMaxY: page.queryOptions.axisY.options.length
      });
    } else {
      await this.setStateAsync({
        categories: []
      });
    }

    if (pageId && queryId && panel && panel.id && categoryId !== undefined) {
      const comments = await queryQuestionCommentsApi.listQueryQuestionComments({
        panelId: panel.id,
        queryId: queryId,
        pageId: pageId,
        categoryId: categoryId,
        firstResult: 0,
      // TODO: Fix this later!!!
        maxResults: 10000,
        oldestFirst: false
      });
      
      const answers = await queryQuestionAnswersApi.listQueryQuestionAnswers({
        panelId: panel.id,
        pageId: pageId,
        queryId: queryId
      });

      await this.setStateAsync({
        comments: comments,
        answers: answers
      });

      this.recalculateChartValues();
    }

    this.setState({
      loading: false
    });
  }

  /**
   * Recalculates chart values
   */
  private recalculateChartValues = () => {
    const chartValues: QueryLive2dAnswer[] = this.state.answers.map((answer) => {
      return {
        x: answer.data.x,
        y: answer.data.y,
        z: 500,
        id: answer.id!
      };
    });
    
    this.setState({
      chartValues: chartValues,
      statisticsX: this.getStatisticsX(chartValues),
      statisticsY: this.getStatisticsY(chartValues)
    });
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
   * Returns whether component is currently in fullscreen mode
   * 
   * @returns whether component is currently in fullscreen mode
   */
  private isFullscreen = () => {
    return this.state.commentsFullscreen || this.state.answersFullscreen;
  }
  
  /**
   * Ref callback for chart wrapper div
   */
  private setChartWrapperDiv(element: HTMLDivElement | null) {
    this.chartContainerRef = element;

    if (this.chartContainerRef && !this.state.chartSize) {
      this.setState({
        chartSize: this.chartContainerRef.offsetWidth
      });
    }
  }

  /**
   * Returns answer for a comment
   * 
   * @param comment comment
   */
  private getCommentAnswer(comment: QueryQuestionComment): QueryQuestionAnswer | null {
    return this.state.answers.find((answer) => {
      return answer.queryReplyId == comment.queryReplyId && answer.queryPageId == comment.queryPageId;
    }) || null;
  }

  /**
   * Formats a date into a human readable string
   * 
   * @param date date
   * @return human readable string
   */
  private formatDate(date?: Date | string): string {
    if (date) {
      return moment(date).locale(getLanguage()).format("LLL");
    }

    return "";
  }

  /**
   * Updates an answer into state
   * 
   * @param answer answer
   */
  private updateAnswer = async (answer: QueryQuestionAnswer) => {
    if (this.state.pageId != answer.queryPageId) {
      return;
    }

    const answers = _.clone(this.state.answers);
    let updated = false;
    
    for (let i = 0; i < answers.length; i++) {
      if (answers[i].id == answer.id) {
        answers[i].data.x = answer.data.x;
        answers[i].data.y = answer.data.y;
        updated = true;
      }
    }

    if (!updated) {
      answers.push(answer);
    }

    await this.setStateAsync({
      answers: answers
    });

    this.recalculateChartValues();
  }

  /**
   * Updates a comment into state
   * 
   * @param comment comment
   */
  private updateComment(comment: QueryQuestionComment) {
    const categoryId = comment.categoryId || 0;
    if (this.state.categoryId != categoryId || this.state.pageId != comment.queryPageId) {
      return;
    }

    const comments = _.clone(this.state.comments);
    let updated = false;
    
    for (let i = 0; i < comments.length; i++) {
      if (comments[i].id == comment.id) {
        comments[i].contents = comment.contents;
        updated = true;
      }
    }

    if (!updated) {
      comments.push(comment);
    }
    
    this.setState({
      comments: comments
    });
  }

  /**
   * Returns parent comment for given comment
   * 
   * @param comment
   * @return parent comment or null if not found
   */
  private getParentComment = (comment: QueryQuestionComment) => {
    if (!comment.parentId) {
      return null;
    }

    return this.state.comments.find((parentComment) => {
      return parentComment.id == comment.parentId;
    }) || null;
  }

  /**
   * Returns root comment id for a comment
   * 
   * @param comment comment
   * @return root comment id
   */
  private getRootCommentId = (comment: QueryQuestionComment): number | null => {
    let current: QueryQuestionComment | null = comment;

    while (current) {
      const parent = this.getParentComment(current);
      if (!parent) {
        return current.id || null;
      } else {
        current = parent;
      }
    }

    return null;
  }

  /**
   * Updates parent and root comment maps
   */
  private updateCommentMaps = () => {
    const parentMap: { [key: number]: QueryQuestionComment[] } = {};
    const rootMap: { [key: number]: QueryQuestionComment[] } = {};

    this.state.comments.forEach((comment) => {
      const parentCommentId = comment.parentId;

      if (parentCommentId) {
        const rootCommentId = this.getRootCommentId(comment);
        
        if (rootCommentId) {
          rootMap[rootCommentId] = rootMap[rootCommentId] || [];
          rootMap[rootCommentId].push(comment);
        }

        parentMap[parentCommentId] = parentMap[parentCommentId] || [];
        parentMap[parentCommentId].push(comment);
      }
    });

    this.setState({
      parentMap: parentMap,
      rootMap: rootMap
    });
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
   * Sets state and returns a promise for the change
   * 
   * @param state state
   */
  private setStateAsync<K extends keyof State>(state: (Pick<State, K>)): Promise<void> {
    return new Promise((resolve) => {
      this.setState(state, resolve);  
    });
  }

  /**
   * Event handler for answers fullscreen button click
   */
  private onAnswersFullscreenButtonClick = async () => {
    await this.setStateAsync({
      answersFullscreen: !this.state.answersFullscreen
    });

    this.recalculateChartSize();
  }
  

  /**
   * Event handler for comments fullscreen button click
   */
  private onCommentsFullscreenButtonClick = async () => {
    await this.setState({
      commentsFullscreen: !this.state.commentsFullscreen
    });

    this.recalculateChartSize();
  }
  
  /**
   * Handles query question comment notification MQTT message
   * 
   * @param notification notification
   */
  private async onQueryQuestionCommentNotification(notification: QueryQuestionCommentNotification) {
    const { accessToken } = this.props;

    if (!accessToken || !this.state.panel || !this.state.panel.id) {
      return;
    }

    if (notification.panelId != this.state.panel.id || notification.pageId != this.state.pageId) {
      return;
    }

    const comment = await Api.getQueryQuestionCommentsApi(accessToken.token).findQueryQuestionComment({
      panelId: this.state.panel.id,
      commentId: notification.commentId
    });
      
    this.updateComment(comment);
  }

  /**
   * Handles query question comment notification MQTT message
   * 
   * @param notification notification
   */
  private async onQueryQuestionAnswerNotification(notification: QueryQuestionAnswerNotification) {
    const { accessToken } = this.props;

    if (!accessToken || !this.state.panel || !this.state.panel.id) {
      return;
    }

    if (notification.panelId != this.state.panel.id || notification.pageId != this.state.pageId) {
      return;
    }

    const answer = await Api.getQueryQuestionAnswersApi(accessToken.token).findQueryQuestionAnswer({
      panelId: this.state.panel.id,
      answerId: notification.answerId
    });
    
    this.updateAnswer(answer);
  }

  /**
   * Event handler for query change event
   * 
   * @param event event
   * @param data event data
   */
  private onQueryChange = async (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
    await this.setStateAsync({
      queryId: data.value as number
    });

    await this.loadData();
  }

  /**
   * Event handler for page change event
   * 
   * @param event event
   * @param data event data
   */
  private onPageChange = async (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
    await this.setStateAsync({
      pageId: data.value as number
    });

    await this.loadData();
  }

  /**
   * Event handler for category change event
   * 
   * @param event event
   * @param data event data
   */
  private onCategoryChange = async (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
    await this.setStateAsync({
      categoryId: data.value as number
    });

    await this.loadData();
  }

  /**
   * Event handler for comment cell expand button click
   * 
   * @param x cell x index
   * @param y cell y index
   */
  private onExpandCommentCellButtonClick = (x: number, y: number) => {
    this.setState({
      commentCellExpanded: this.state.commentCellExpanded !== null ? null : (y * 2) + x
    });
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
  return { };
}

export default connect(mapStateToProps, mapDispatchToProps)(LiveView);
