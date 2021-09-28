import * as _ from "lodash";
import * as React from "react";
import QueryComment from "./query-comment";
import { QueryQuestionCommentNotification } from "../../types";
import { QueryQuestionComment, QueryQuestionCommentCategory } from "../../generated/client/models";
import { Dimmer, Loader, Pagination, Segment } from "semantic-ui-react";
import { mqttConnection, OnMessageCallback } from "../../mqtt";
import Api from "../../api";
import strings from "../../localization/strings";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: string;
  loggedUserId: string;
  queryId: number;
  panelId: number;
  pageId: number;
  parentId: number;
  queryReplyId: number;
  className: string;
  canManageComments: boolean;
  category: QueryQuestionCommentCategory | null;
  onCommentsChanged?: (comments: QueryQuestionComment[]) => void;
  renderPagination?: boolean;
}

/**
 * Interface representing component state
 */
interface State {
  comments?: QueryQuestionComment[];
  activePage: number;
  commentsPerPage: number;
  pageCount: number;
  loading: boolean;
}

/**
 * React component for comment editor
 */
class QueryCommentContainer extends React.Component<Props, State> {

  private queryQuestionCommentsListener: OnMessageCallback;

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      activePage: 1,
      commentsPerPage: 10,
      pageCount: 1,
      loading: true
    };
    this.queryQuestionCommentsListener = this.onQueryQuestionCommentNotification.bind(this);
  }

  /**
   * Component will mount life-cycle event
   */
  public async componentWillMount() {
    mqttConnection.subscribe("queryquestioncomments", this.queryQuestionCommentsListener);
    await this.loadComments();
  }

  /**
   * Component will unmount life-cycle event
   */
  public componentWillUnmount() {
    mqttConnection.unsubscribe("queryquestioncomments", this.queryQuestionCommentsListener);
  }

  /**
   * Component did update life-cycle event
  */
  public async componentDidUpdate(prevProps: Props, prevState: State) {
    const { onCommentsChanged } = this.props;
    const { comments } = this.state;

    if (onCommentsChanged && comments && !_.isEqual(comments, prevState.comments)) {
      await this.loadComments();
      onCommentsChanged(comments);
    }
  }

  /** 
   * Render comments container
   */
  public render() {
    const { className, accessToken, loggedUserId, category, canManageComments, queryReplyId, pageId, panelId, queryId, renderPagination } = this.props;
    const { comments, loading } = this.state;

    if (!comments || !accessToken || loading) {
      return (
        <Segment style={{ minHeight: "200px" }}>
          <Dimmer inverted active>
            <Loader>{ strings.generic.loading }</Loader>
          </Dimmer>
        </Segment>
      );
    }

    return <div className={ className }>
      {
        comments.map((comment) => {
          return <QueryComment key={ comment.id } 
            accessToken={ accessToken }
            loggedUserId={ loggedUserId }
            category={ category } 
            canManageComments={ canManageComments } 
            comment={ comment } 
            queryReplyId={ queryReplyId }
            pageId={ pageId } 
            panelId={ panelId} 
            queryId={ queryId }/>
        })
      }
      { renderPagination && this.renderPagination() }
    </div>
  }

  /**
   * Renders pagination
   */
  private renderPagination = () => {
    const { pageCount, activePage, commentsPerPage } = this.state;

    return (
      <Pagination
        siblingRange={ commentsPerPage }
        activePage={ activePage }
        totalPages={ pageCount }
        boundaryRange={ 0 }
        size="mini"
        onPageChange={ async (event, data) => {
          const activePage = data.activePage as number;
          this.setState({ activePage: activePage }, () => this.loadComments());
        }}
      />
    );
  }

  /**
   * Handles query question comment notification MQTT message
   * 
   * @param message message
   */
  private async onQueryQuestionCommentNotification(message: QueryQuestionCommentNotification) {
    const { pageId, panelId, queryId, accessToken, parentId } = this.props;

    if (message.pageId != pageId || message.panelId != panelId || message.queryId != queryId) {
      return;
    }

    if (!this.state.comments || !accessToken) {
      return;
    }

    const parentCommentId = message.parentCommentId || 0;

    if (message.type == "CREATED" && parentCommentId == parentId) {
      const comment = await Api.getQueryQuestionCommentsApi(accessToken).findQueryQuestionComment({
        panelId: panelId, 
        commentId: message.commentId
      });

      this.setState({
        comments: this.state.comments.concat([comment]).sort(this.compareComments)
      });      
    } else {
      const comments = [];

      for (let i = 0; i < this.state.comments.length; i++) {
        const comment = this.state.comments[i];
        if (message.commentId == comment.id) {
          if (message.type == "UPDATED") {
            comments.push(await Api.getQueryQuestionCommentsApi(accessToken).findQueryQuestionComment({
              panelId: panelId,
              commentId: message.commentId
            }));
          }
        } else {
          if (!(message.type == "DELETED" && message.commentId == comment.id)) {
            comments.push(comment);
          }
        }
      }

      this.setState({
        comments: comments.sort(this.compareComments)
      });
    }
  }

  /**
   * Compares two comments. Prefers logged user's comment if comment is a root comment
   * 
   * @param a comment a
   * @param b comment b
   * @return compare result
   */
  private compareComments = (a: QueryQuestionComment, b: QueryQuestionComment): number => {
    if (a.parentId || b.parentId || !this.props.loggedUserId || a.creatorId == b.creatorId) {
      return 0;
    }
    
    const loggedUserId = this.props.loggedUserId;
    if (a.creatorId == loggedUserId) {
      return -1;
    }

    if (b.creatorId == loggedUserId) {
      return 1;
    }

    return 0;
  }

  /**
   * Loads child comments
   */
  private loadComments = async () => {
    const { pageId, panelId, queryId, accessToken, parentId, category, renderPagination } = this.props;
    const { commentsPerPage, activePage } = this.state;

    if (!accessToken) {
      return;
    }

    this.setState({ loading: true });

    const categoryId = category ? category.id : 0;
    
    const response = await Api.getQueryQuestionCommentsApi(accessToken).listQueryQuestionCommentsRaw({
      panelId: panelId,
      queryId: queryId,
      pageId: pageId,
      parentId: parentId,
      categoryId: parentId == 0 ? categoryId : undefined,
      firstResult: renderPagination ? ((activePage - 1) * commentsPerPage) : 0,
      maxResults: renderPagination ? commentsPerPage : 10,
      oldestFirst: false
    });

    const totalCount = parseInt(response.raw.headers.get("X-Total-Count") || "0") || 0;
    const comments = await response.value();

    this.setState({
      pageCount: Math.ceil(totalCount / commentsPerPage),
      comments: comments.sort(this.compareComments),
      loading: false
    });
  }

}

export default QueryCommentContainer;
