import * as _ from "lodash";
import * as React from "react";
import * as actions from "../../actions";
import QueryComment from "./query-comment";
import { StoreState, QueryQuestionCommentNotification } from "../../types";
import { connect } from "react-redux";
import { QueryQuestionComment, QueryQuestionCommentCategory } from "../../generated/client/models";
import { QueryQuestionCommentsApi } from "../../generated/client/apis";
import { Loader } from "semantic-ui-react";
import { mqttConnection, OnMessageCallback } from "../../mqtt";
import Api from "../../api";

/**
 * Interface representing component properties
 */
interface Props {
  queryId: number,
  panelId: number,
  pageId: number,
  parentId: number,
  queryReplyId: number,
  accessToken?: string,
  loggedUserId?: string,
  className: string,
  canManageComments: boolean,
  category: QueryQuestionCommentCategory | null,
  onCommentsChanged?: (comments: QueryQuestionComment[]) => void
}

/**
 * Interface representing component state
 */
interface State {
  comments?: QueryQuestionComment[]
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
    this.state = { };
    this.queryQuestionCommentsListener = this.onQueryQuestionCommentNotification.bind(this);
  }

  /**
   * Component will mount life-cycle event
   */
  public async componentWillMount() {
    mqttConnection.subscribe("queryquestioncomments", this.queryQuestionCommentsListener);
    await this.loadChildComments();
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
    await this.loadChildComments();

    if (this.props.onCommentsChanged && this.state.comments && !_.isEqual(this.state.comments, prevState.comments)) {
      this.props.onCommentsChanged(this.state.comments);
    }
  }

  /** 
   * Render comments container
   */
  public render() {
    if (!this.state.comments || !this.props.accessToken) {
      return <Loader/>;
    }

    return <div className={ this.props.className }>
      {
        this.state.comments.map((comment) => {
          return <QueryComment key={ comment.id } category={ this.props.category } canManageComments={ this.props.canManageComments } comment={ comment } queryReplyId={this.props.queryReplyId} pageId={ this.props.pageId } panelId={ this.props.panelId} queryId={ this.props.queryId }/>
        })
      } 
    </div>
  }

  /**
   * Handles query question comment notification MQTT message
   * 
   * @param message message
   */
  private async onQueryQuestionCommentNotification(message: QueryQuestionCommentNotification) {
    if (message.pageId != this.props.pageId || message.panelId != this.props.panelId || message.queryId != this.props.queryId) {
      return;
    }

    if (!this.state.comments || !this.props.accessToken) {
      return;
    }

    const parentCommentId = message.parentCommentId || 0;

    if (message.type == "CREATED" && parentCommentId == this.props.parentId) {
      const comment = await this.getQueryQuestionCommentsApi(this.props.accessToken).findQueryQuestionComment({
        panelId: this.props.panelId, 
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
            comments.push(await this.getQueryQuestionCommentsApi(this.props.accessToken).findQueryQuestionComment({
              panelId: this.props.panelId,
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
  private async loadChildComments() {
    if (!this.state.comments && this.props.accessToken) {
      const categoryId = this.props.category ? this.props.category.id : 0;
      
      const comments = await (this.getQueryQuestionCommentsApi(this.props.accessToken)).listQueryQuestionComments({
        panelId: this.props.panelId,
        queryId: this.props.queryId,
        pageId: this.props.pageId,
        parentId: this.props.parentId,
        categoryId: this.props.parentId == 0 ? categoryId : undefined
      });

      this.setState({
        comments: comments.sort(this.compareComments)
      });
    }
  } 

  /**
   * Returns query question comments API
   * 
   * @returns query question comments API
   */
  private getQueryQuestionCommentsApi(accessToken: string): QueryQuestionCommentsApi {
    return Api.getQueryQuestionCommentsApi(accessToken);
  }

}

export default QueryCommentContainer;
