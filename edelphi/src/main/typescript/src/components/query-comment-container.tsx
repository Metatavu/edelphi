import * as React from "react";
import * as actions from "../actions";
import QueryComment from "./query-comment";
import { StoreState, QueryQuestionCommentNotification } from "../types";
import { connect } from "react-redux";
import Api, { QueryQuestionComment } from "edelphi-client";
import { QueryQuestionCommentsService } from "edelphi-client/dist/api/api";
import { Loader } from "semantic-ui-react";
import { mqttConnection } from "../mqtt";

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
  locale: string,
  className: string
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

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = { };

    mqttConnection.subscribe("queryquestioncomments", this.onQueryQuestionCommentNotification.bind(this));
  }

  /**
   * Component did update life-cycle event
   */
  public async componentWillMount() {
    this.loadChildComments();
  }

  /**
   * Component did update life-cycle event
  */
  public async componentDidUpdate() {
    this.loadChildComments();
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
          return <QueryComment key={ comment.id } comment={ comment } queryReplyId={this.props.queryReplyId} pageId={ this.props.pageId } panelId={ this.props.panelId} queryId={ this.props.queryId }/>
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

    if (message.type == "CREATED" && message.pageId == this.props.pageId) {
      const comment = await this.getQueryQuestionCommentsService(this.props.accessToken).findQueryQuestionComment(this.props.panelId, message.commentId);
      this.setState({
        comments: this.state.comments.concat([comment])
      });      
    } else {
      const comments = [];

      for (let i = 0; i < this.state.comments.length; i++) {
        const comment = this.state.comments[i];
        if (message.commentId == comment.id) {
          if (message.type == "UPDATED") {
            comments.push(await this.getQueryQuestionCommentsService(this.props.accessToken).findQueryQuestionComment(this.props.panelId, message.commentId));
          }
        } else {
          comments.push(comment);
        }
      }

      this.setState({
        comments: comments
      });
    }
  }
  /**
   * Loads child comments
   */
  
  private async loadChildComments() {
    if (!this.state.comments && this.props.accessToken) {
      this.setState({
        comments: await (this.getQueryQuestionCommentsService(this.props.accessToken)).listQueryQuestionComments(this.props.panelId, this.props.parentId, this.props.queryId, this.props.pageId, undefined)
      });
    }
  } 

  /**
   * Returns query question comments API
   * 
   * @returns query question comments API
   */
  private getQueryQuestionCommentsService(accessToken: string): QueryQuestionCommentsService {
    return Api.getQueryQuestionCommentsService(accessToken);
  }

}

/**
 * Redux mapper for mapping store state to component props
 * 
 * @param state store state
 */
function mapStateToProps(state: StoreState) {
  return {
    accessToken: state.accessToken ? state.accessToken.token : null,
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

export default connect(mapStateToProps, mapDispatchToProps)(QueryCommentContainer);
