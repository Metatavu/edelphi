import * as _ from "lodash";
import * as React from "react";
import QueryComment from "./query-comment";
import { QueryQuestionCommentNotification } from "../../types";
import { QueryQuestionComment, QueryQuestionCommentCategory } from "../../generated/client/models";
import { Dimmer, Loader, Segment } from "semantic-ui-react";
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
}

/**
 * Interface representing component state
 */
interface State {
  comments?: QueryQuestionComment[];
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
      loading: false
    };
    this.queryQuestionCommentsListener = this.onQueryQuestionCommentNotification.bind(this);
  }

  /**
   * Component will mount life-cycle event
   */
  public componentWillMount = async () => {
    mqttConnection.subscribe("queryquestioncomments", this.queryQuestionCommentsListener);
    await this.loadComments();
  }

  /**
   * Component will unmount life-cycle event
   */
  public componentWillUnmount = () => {
    mqttConnection.unsubscribe("queryquestioncomments", this.queryQuestionCommentsListener);
  }

  /**
   * Component did update life-cycle event
  */
  public async componentDidUpdate(prevProps: Props, prevState: State) {
    const { onCommentsChanged } = this.props;
    const { comments } = this.state;

    // if (onCommentsChanged && comments && !_.isEqual(comments, prevState.comments)) {
    //   await this.loadComments();
    //   onCommentsChanged(comments);
    // }
  }

  /** 
   * Render comments container
   */
  public render = () => {
    const { className, accessToken, loggedUserId, category, canManageComments, queryReplyId, pageId, panelId, queryId,  parentId } = this.props;
    const { comments, loading } = this.state;

    if (!comments || !accessToken || loading) {
      return (
        <Segment style={{ minHeight: "100px" }}>
          <Dimmer inverted active>
            <Loader>{ strings.generic.loading }</Loader>
          </Dimmer>
        </Segment>
      );
    }

    return (
      <div className={ className }>
        {
          comments.map(comment => {
            return <QueryComment
              key={ comment.id } 
              accessToken={ accessToken }
              loggedUserId={ loggedUserId }
              category={ category } 
              canManageComments={ canManageComments } 
              comment={ comment } 
              queryReplyId={ queryReplyId }
              pageId={ pageId } 
              panelId={ panelId} 
              queryId={ queryId }
            />
          })
        }
      </div>
    );
  }

  /**
   * Handles query question comment notification MQTT message
   * 
   * @param message message
   */
  private async onQueryQuestionCommentNotification(message: QueryQuestionCommentNotification) {
    const { pageId, panelId, queryId, accessToken, parentId } = this.props;
    const { comments } = this.state;

    console.log("NEW MESSAGE!!!!!!!!!!!!!");

    // if (message.pageId != pageId || message.panelId != panelId || message.queryId != queryId) {
    //   return;
    // }

    // if (!comments || !accessToken) {
    //   return;
    // }

    // const parentCommentId = message.parentCommentId || 0;

    // if (message.type == "CREATED" && parentCommentId == parentId) {
    //   const comment = await Api.getQueryQuestionCommentsApi(accessToken).findQueryQuestionComment({
    //     panelId: panelId, 
    //     commentId: message.commentId
    //   });

    //   this.setState({
    //     comments: [ ...comments, comment ].sort(this.compareComments)
    //   });      
    // } else {
    //   const commentList = [];

    //   for (let i = 0; i < comments.length; i++) {
    //     const comment = comments[i];
    //     if (message.commentId == comment.id) {
    //       if (message.type == "UPDATED") {
    //         commentList.push(await Api.getQueryQuestionCommentsApi(accessToken).findQueryQuestionComment({
    //           panelId: panelId,
    //           commentId: message.commentId
    //         }));
    //       }
    //     } else {
    //       if (!(message.type == "DELETED" && message.commentId == comment.id)) {
    //         commentList.push(comment);
    //       }
    //     }
    //   }

    //   this.setState({
    //     comments: commentList.sort(this.compareComments)
    //   });
    // }
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
    const { pageId, panelId, queryId, accessToken, parentId, category } = this.props;

    if (!accessToken) {
      return;
    }

    this.setState({ loading: true });

    const categoryId = category ? category.id : 0;
    try {
      const response = await Api.getQueryQuestionCommentsApi(accessToken).listQueryQuestionCommentsRaw({
        panelId: panelId,
        queryId: queryId,
        pageId: pageId,
        parentId: parentId,
        categoryId: categoryId,
        firstResult: 0,
        maxResults: 1000,
        oldestFirst: false
      });
  
      const comments = await response.value();

      this.setState({
        comments: comments.sort(this.compareComments),
        loading: false
      });
    } catch (error) {
      console.error(error);
    }
  }

}

export default QueryCommentContainer;
