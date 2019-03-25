import * as React from "react";
import * as moment from "moment";
import * as actions from "../actions";
import QueryCommentContainer from "./query-comment-container";
import strings from "../localization/strings";
import { StoreState, QueryQuestionCommentNotification } from "../types";
import { connect } from "react-redux";
import Api, { QueryQuestionComment } from "edelphi-client";
import { QueryQuestionCommentsService } from "edelphi-client/dist/api/api";
import { mqttConnection } from "../mqtt";
import styles from "../constants/styles";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: string,
  locale: string,
  comment: QueryQuestionComment,
  panelId: number,
  queryId: number,
  pageId: number,
  queryReplyId: number
}

/**
 * Interface representing component state
 */
interface State {
  commentEditorOpen: boolean,
  commentEditorContents?: string
  replyEditorOpen: boolean,
  updating: boolean
}

/**
 * React component for comment editor
 */
class QueryCommentClass extends React.Component<Props, State> {

  private commentEditor: HTMLTextAreaElement | null;
  private replyEditor: HTMLTextAreaElement | null;

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = { 
      commentEditorOpen: false,
      replyEditorOpen: false,
      updating: false
    };

    mqttConnection.subscribe("queryquestioncomments", this.onQueryQuestionCommentNotification.bind(this));
  }
  
  /** 
   * Render edit pest view
   */
  public render() {
    return (
      <div key={ this.props.comment.id } className="queryComment">
        <a id={`comment.${this.props.comment.id}`}></a>
        <div className="queryCommentShowHideButton hideIcon"></div> 
        <div className="queryCommentHeader">
          <div className="queryCommentDate">{ strings.formatString(strings.panel.query.comments.commentDate, this.formatDateTime(this.props.comment.created)) } </div>
        </div>
        <div className="queryCommentContainerWrapper">
          {
            this.renderModified()
          }
          {
            this.renderContents()
          }
          { 
            this.renderLinks()
          }
          {
            this.renderChildComments()
          }
          {
            this.renderNewCommentEditor()
          }
        </div>
      </div>
    );
  }

  /**
   * Renders comment text or editor
   */
  private renderContents() {
    if (this.state.commentEditorOpen) {
      return (
        <div className="editCommentEditor">
          <textarea ref={ (textarea) => { this.commentEditor = textarea; }} onChange={ (event) => { this.setState({ commentEditorContents: event.target.value }); } } value={ this.state.commentEditorContents }></textarea>
          <input type="button" className="formButton" disabled={ !this.state.commentEditorContents || !this.state.commentEditorContents.trim() } onClick={ (event: React.MouseEvent<HTMLElement>) => this.onEditCommentSaveClick(event) } value={ strings.panel.query.comments.saveEdit }></input>
        </div>
      );
    }

    return (<div className="queryCommentText">{ this.props.comment.contents }</div>);
  }

  /**
   * Renders comment modified text (if comment has been modified)
   */
  private renderModified() {
    if (this.props.comment.created == this.props.comment.lastModified) {
      return null;
    }

    return <div className="queryCommentModified">{ strings.formatString(strings.panel.query.comments.commentModified, this.formatDateTime(this.props.comment.lastModified)) } </div>
  }

  /**
   * Renders new comment editor
   */
  private renderNewCommentEditor() {
    if (!this.state.replyEditorOpen) {
      return null;
    }

    return (
      <div className="newCommentEditor">
        <textarea ref={ (textarea) => { this.replyEditor = textarea; }}></textarea>
        <input type="button" className="formButton" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onNewCommentSaveClick(event) } value={ strings.panel.query.comments.saveReply }></input>
      </div>
    );
  }

  
  /**
   * Renders child comments
   */
  private renderChildComments() {
    return <QueryCommentContainer className="queryCommentChildren" parentId={ this.props.comment.id! } queryReplyId={this.props.queryReplyId} pageId={ this.props.pageId } panelId={ this.props.panelId } queryId={ this.props.queryId }/>
  }

  /**
   * Renders comment links
   */
  private renderLinks() {
    return (
      <div className="queryCommentMeta">
        <div className="queryCommentNewComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onNewCommentClick(event) }  className="queryCommentNewCommentLink">{ strings.panel.query.comments.reply }</a></div>
        {
          this.props.comment.hidden 
            ? <div className="queryCommentShowComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onShowClick(event) } className="queryCommentShowCommentLink">{ strings.panel.query.comments.show }</a></div>
            : <div className="queryCommentHideComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onHideClick(event) } className="queryCommentHideCommentLink">{ strings.panel.query.comments.hide }</a></div>
        }
        <div className="queryCommentEditComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onEditCommentClick(event) }   className="queryCommentEditCommentLink">{ strings.panel.query.comments.edit }</a></div>
        <div className="queryCommentDeleteComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" className="queryCommentDeleteCommentLink">{ strings.panel.query.comments.remove }</a></div>
      </div>  
    );
  }

  /**
   * Formats date time
   * 
   * @param dateTime date time
   * @return formatted date time
   */
  private formatDateTime(dateTime?: Date | string) {
    return moment(dateTime).locale(this.props.locale).format("LLL"); 
  }

  /**
   * Returns query question comments API
   * 
   * @returns query question comments API
   */
  private getQueryQuestionCommentsService(accessToken: string): QueryQuestionCommentsService {
    return Api.getQueryQuestionCommentsService(accessToken);
  }
    
  /**
   * Click handler for reply comment link
   * 
   * @param event event
   */
  private onNewCommentClick(event: React.MouseEvent<HTMLElement>) {
    event.preventDefault();

    this.setState({
      replyEditorOpen: true
    });
  }
    
  /**
   * Click handler for edit link
   * 
   * @param event event
   */
  private onEditCommentClick(event: React.MouseEvent<HTMLElement>) {
    event.preventDefault();

    this.setState({
      commentEditorOpen: true,
      commentEditorContents: this.props.comment.contents
    });
  }

  /**
   * Click handler edit comment save button 
   * 
   * @param event event
   */
  private onEditCommentSaveClick(event: React.MouseEvent<HTMLElement>) {
    event.preventDefault();

    if (!this.commentEditor || this.state.updating || !this.props.accessToken || !this.props.comment.id) {
      return;
    }

    const contents = this.state.commentEditorContents;

    this.setState({
      commentEditorOpen: false,
      updating: true
    });

    const queryQuestionCommentsService = this.getQueryQuestionCommentsService(this.props.accessToken);
    queryQuestionCommentsService.updateQueryQuestionComment({ ... this.props.comment, contents: contents }, this.props.panelId, this.props.comment.id);
  }
    
  /**
   * Click handler new save button 
   * 
   * @param event event
   */
  private onNewCommentSaveClick(event: React.MouseEvent<HTMLElement>) {
    event.preventDefault();

    if (!this.replyEditor || this.state.updating || !this.props.accessToken || !this.props.comment.id) {
      return;
    }

    const contents = this.replyEditor.value;

    this.setState({
      replyEditorOpen: false,
      updating: true
    });

    const queryQuestionCommentsService = this.getQueryQuestionCommentsService(this.props.accessToken);
    queryQuestionCommentsService.createQueryQuestionComment({
      contents: contents,
      hidden: false,
      parentId: this.props.comment.id,
      queryPageId: this.props.pageId,
      queryReplyId: this.props.queryReplyId,
    }, this.props.panelId);
  }
  
  /**
   * Click handler for show link
   * 
   * @param event event
   */
  private onShowClick(event: React.MouseEvent<HTMLElement>) {
    event.preventDefault();

    if (this.state.updating || !this.props.accessToken || !this.props.comment.id) {
      return;
    }

    this.setState({
      updating: true
    });

    const queryQuestionCommentsService = this.getQueryQuestionCommentsService(this.props.accessToken);
    queryQuestionCommentsService.updateQueryQuestionComment({ ... this.props.comment, hidden: false }, this.props.panelId, this.props.comment.id);
  }

  /**
   * Click handler for hide link
   * 
   * @param event event
   */
  private onHideClick(event: React.MouseEvent<HTMLElement>) {
    event.preventDefault();

    if (this.state.updating || !this.props.accessToken || !this.props.comment.id) {
      return;
    }

    this.setState({
      updating: true
    });

    const queryQuestionCommentsService = this.getQueryQuestionCommentsService(this.props.accessToken);
    queryQuestionCommentsService.updateQueryQuestionComment({ ... this.props.comment, hidden: true }, this.props.panelId, this.props.comment.id);
  }

  /**
   * Handles query question comment notification MQTT message
   * 
   * @param message message
   */
  private async onQueryQuestionCommentNotification(message: QueryQuestionCommentNotification) {
    switch (message.type) {
      case "UPDATED":
        if (message.commentId == this.props.comment.id) {
          this.setState({
            updating: false
          });
        }
      break;
      case "CREATED":
        if (message.parentCommentId == this.props.comment.id) {
          this.setState({
            updating: false
          });
        }
      break;
    }
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

const QueryComment = connect(mapStateToProps, mapDispatchToProps)(QueryCommentClass);
export default QueryComment;
