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
  accessToken: string,
  loggedUserId: string,
  queryId: number,
  panelId: number,
  pageId: number,
  parentId: number,
  queryReplyId: number,
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
    const { className, accessToken, loggedUserId, category, canManageComments, queryReplyId, pageId, panelId, queryId } = this.props;
    const { comments } = this.state;

    if (!comments || !accessToken) {
      return <Loader/>;
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
    </div>
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
  private async loadChildComments() {
    const { pageId, panelId, queryId, accessToken, parentId, category } = this.props;

    if (!this.state.comments && accessToken) {
      const categoryId = category ? category.id : 0;
      
      const comments = await Api.getQueryQuestionCommentsApi(accessToken).listQueryQuestionComments({
        panelId: panelId,
        queryId: queryId,
        pageId: pageId,
        parentId: parentId,
        categoryId: parentId == 0 ? categoryId : undefined
      });

      this.setState({
        comments: comments.sort(this.compareComments)
      });
    }
  }

}

export default QueryCommentContainer;
