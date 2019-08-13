import * as React from "react";
import * as actions from "../actions";
import * as _ from "lodash";
import { StoreState, AccessToken, QueryQuestionCommentNotification, QueryQuestionAnswerNotification } from "../types";
import { connect } from "react-redux";
import { Grid, DropdownItemProps, DropdownProps, Form, Container, Icon, Transition, SemanticShorthandCollection, BreadcrumbSectionProps } from "semantic-ui-react";
import PanelAdminLayout from "../components/generic/panel-admin-layout";
import Api, { Panel, QueryQuestionComment, Query, QueryPage, QueryQuestionAnswer, QueryQuestionCommentCategory, User } from "edelphi-client";
import "../styles/comment-view.scss";
import { mqttConnection, OnMessageCallback } from "../mqtt";
import { QueryQuestionCommentsService, QueriesService, PanelsService, QueryPagesService, QueryQuestionAnswersService, QueryQuestionCommentCategoriesService, UsersService } from "edelphi-client/dist/api/api";
import * as queryString from "query-string";
import * as moment from "moment";
import getLanguage from "../localization/language";
import strings from "../localization/strings";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: AccessToken,
  location: any
}

/**
 * Interface representing component state
 */
interface State {
  panel?: Panel,
  loggedUser?: User,
  answers: QueryQuestionAnswer[],
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
  repliesOpen: number[] 
}

interface CommentAndAnswer {
  answer: QueryQuestionAnswer;
  comment: QueryQuestionComment;
}

/**
 * React component for comment editor
 */
class CommentView extends React.Component<Props, State> {

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
      comments: [],
      queries: [],
      pages: [],
      categories: [],
      loading: false,
      parentMap: { },
      rootMap: { },
      pageMaxX: 6,
      pageMaxY: 6,
      repliesOpen: []
    };

    this.queryQuestionCommentsListener = this.onQueryQuestionCommentNotification.bind(this);
    this.queryQuestionAnswersListener = this.onQueryQuestionAnswerNotification.bind(this);
  }

  /**
   * Component will mount life-cycle event
   */
  public async componentDidMount() {
    mqttConnection.subscribe("queryquestioncomments", this.queryQuestionCommentsListener);
    mqttConnection.subscribe("queryquestionanswers", this.queryQuestionAnswersListener);

    const queryParams = queryString.parse(this.props.location.search);
    
    const panelId = parseInt(queryParams.panelId as string);

    this.setState({
      loading: true
    });

    const panel = await this.getPanelsService().findPanel(panelId);
    const queries = await this.getQueriesService().listQueries(panelId);
    const loggedUser = await this.getUsersService().findUser(this.props.accessToken.userId);

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
      { key: "home", content: strings.generic.eDelphi, link: true, href: "/" },
      { key: "panel", content: this.state.panel.name, link: true, href: `/${this.state.panel.urlName}` },
      { key: "commentview", content: this.state.panel.name, active: true }      
    ];

    return (
      <PanelAdminLayout loggedUser={ this.state.loggedUser } breadcrumbs={ breadcrumbs } loading={ this.state.loading } panel={ this.state.panel } redirectTo={ this.state.redirectTo }>
        <div style={{ width: "100%", height:"100%" }}>
          <Grid>
            { this.renderControls() }
            { this.renderCommentView() }
          </Grid>
        </div>
      </PanelAdminLayout>
    );
  }

  /**
   * Renders comment view
   */
  private renderCommentView = () => {
    const page = this.state.pages.find((page) => {
      return page.id == this.state.pageId;
    });

    const xLabel = page && page.queryOptions.axisX ? page.queryOptions.axisX.label : "";
    const yLabel = page && page.queryOptions.axisY ? page.queryOptions.axisY.label : "";

    return (
      <Container className="comments-container">
        <div className="comments-list-axis-x-container">
          <div className="comments-list-axis-x"> { xLabel } </div>
        </div>
        <div className="comments-list-axis-y-container">
          <div className="comments-list-axis-y"> { yLabel } </div>
        </div>
        { this.renderGrid() }
      </Container>
    );
  }

  /**
   * Renders controls for the view
   */
  private renderControls = () => {
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
      text: strings.panelAdmin.commentView.defaultCategory
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
                <Form.Select fluid label={ strings.panelAdmin.commentView.querySelectLabel } value={ this.state.queryId } onChange={ this.onQueryChange } options={ queryOptions }/>
                <Form.Select fluid disabled={ !this.state.queryId } label={ strings.panelAdmin.commentView.pageSelectLabel } value={ this.state.pageId } onChange={ this.onPageChange } options={ pageOptions }/>
                <Form.Select fluid disabled={ !this.state.pageId } label={ strings.panelAdmin.commentView.categorySelectLabel } value={ this.state.categoryId } onChange={ this.onCategoryChange } options={ categoryOptions }/>
              </Form.Group>
            </Form>
          </Container>
        </Grid.Column>
      </Grid.Row>
    );
  } 

  /**
   * Renders comment grid
   */
  private renderGrid = () => {
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

    return (
      <Grid className="comments-grid">  
        {
          [1, 0].map((y) => {
            return (
              <Grid.Row className="comment-list-row" key={y}> 
                { [0, 1].map((x) => {
                  return this.renderCell(commentAnswers, x, y);
                }) }
              </Grid.Row>
            );
          })
        }
      </Grid>
    );
  }

  /**
   * Renders single cell
   * 
   * @param commentAnswers comments and answers
   * @param x cell x index
   * @param y cell y index
   */
  private renderCell(commentAnswers: CommentAndAnswer[], x: number, y: number) {
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

    return (
      <Grid.Column key={`cell-${x}-${y}`} className="comment-list-cell" width={ 8 }>
        <div className="comments-list">
          { this.renderComments(cellCommentAnswers, x, y) }
        </div>
      </Grid.Column>
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
          { this.renderReplies(comment, x, y) }
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
  private renderReplies = (comment: QueryQuestionComment, x: number, y: number) => {
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
          { strings.formatString(strings.panelAdmin.commentView.replyCount, childComments.length) }
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
    if (!this.state.panel || !this.state.panel.id) {
      return;
    }

    const panelId = this.state.panel.id;

    this.setState({
      loading: true
    });

    if (this.state.queryId) {
      const pages = await this.getQueryPagesService().listQueryPages(panelId, this.state.queryId, false);

      await this.setStateAsync({
        pages: pages.filter((page) => {
          return page.type == "LIVE_2D";
        })
      });
    }

    if (this.state.pageId && this.state.queryId && this.state.panel && this.state.panel.id) {
      const page = this.state.pages.find((page) => {
        return page.id == this.state.pageId;
      });

      if (!page || !page.queryOptions.axisX || !page.queryOptions.axisY || !page.queryOptions.axisX.options || !page.queryOptions.axisY.options) {
        throw new Error("Could not lookup page axes");
      }

      await this.setStateAsync({
        categories: await this.getQueryQuestionCommentCategoriesService(this.props.accessToken.token).listQueryQuestionCommentCategories(this.state.panel.id, this.state.pageId),
        pageMaxX: page.queryOptions.axisX.options.length,
        pageMaxY: page.queryOptions.axisY.options.length
      });
    } else {
      await this.setStateAsync({
        categories: []
      });
    }

    if (this.state.pageId && this.state.queryId && this.state.panel && this.state.panel.id && this.state.categoryId !== undefined) {
      const comments = await this.getQueryQuestionCommentsService().listQueryQuestionComments(this.state.panel.id, this.state.queryId, this.state.pageId, undefined, undefined, undefined, this.state.categoryId);
      const answers = await this.getQueryQuestionAnswersService().listQueryQuestionAnswers(this.state.panel.id, this.state.queryId, this.state.pageId, undefined, undefined);

      await this.setStateAsync({
        comments: comments,
        answers: answers
      });
    }

    this.setState({
      loading: false
    });
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
   * Returns query question comments API
   * 
   * @returns query question comments API
   */
  private getQueryQuestionCommentsService(): QueryQuestionCommentsService {
    return Api.getQueryQuestionCommentsService(this.props.accessToken.token);
  }

  /**
   * Returns queries API
   * 
   * @returns queries API
   */
  private getQueriesService(): QueriesService {
    return Api.getQueriesService(this.props.accessToken.token);
  }

  /**
   * Returns users API
   * 
   * @returns users API
   */
  private getUsersService(): UsersService {
    return Api.getUsersService(this.props.accessToken.token);
  }

  /**
   * Returns panels API
   * 
   * @returns panels API
   */
  private getPanelsService(): PanelsService {
    return Api.getPanelsService(this.props.accessToken.token);
  }

  /**
   * Returns query pages API
   * 
   * @returns query pages API
   */
  private getQueryPagesService(): QueryPagesService {
    return Api.getQueryPagesService(this.props.accessToken.token);
  }

  /**
   * Returns query question answers API
   * 
   * @returns query question answers API
   */
  private getQueryQuestionAnswersService(): QueryQuestionAnswersService {
    return Api.getQueryQuestionAnswersService(this.props.accessToken.token);
  }

  /**
   * Returns query question comments API
   * 
   * @returns query question comments API
   */
  private getQueryQuestionCommentCategoriesService(accessToken: string): QueryQuestionCommentCategoriesService {
    return Api.getQueryQuestionCommentCategoriesService(accessToken);
  }

  /**
   * 
   * @param date 
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
   * @param answer 
   */
  private updateAnswer(answer: QueryQuestionAnswer) {
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

    this.setState({
      answers: answers
    });
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
    const parentMap = {};
    const rootMap = {};

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
   * Handles query question comment notification MQTT message
   * 
   * @param notification notification
   */
  private async onQueryQuestionCommentNotification(notification: QueryQuestionCommentNotification) {
    if (!this.state.panel || !this.state.panel.id) {
      return;
    }

    if (notification.panelId != this.state.panel.id || notification.pageId != this.state.pageId) {
      return;
    }

    const comment = await this.getQueryQuestionCommentsService().findQueryQuestionComment(this.state.panel.id, notification.commentId);
    this.updateComment(comment);
  }

  /**
   * Handles query question comment notification MQTT message
   * 
   * @param notification notification
   */
  private async onQueryQuestionAnswerNotification(notification: QueryQuestionAnswerNotification) {
    if (!this.state.panel || !this.state.panel.id) {
      return;
    }

    if (notification.panelId != this.state.panel.id || notification.pageId != this.state.pageId) {
      return;
    }

    const answer = await this.getQueryQuestionAnswersService().findQueryQuestionAnswer(this.state.panel.id, notification.answerId);
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

export default connect(mapStateToProps, mapDispatchToProps)(CommentView);