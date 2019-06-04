import * as React from "react";
import * as moment from "moment";
import * as actions from "../../actions";
import * as _ from "lodash";
import QueryCommentContainer from "./query-comment-container";
import strings from "../../localization/strings";
import { StoreState, QueryQuestionCommentNotification } from "../../types";
import { connect } from "react-redux";
import Api, { QueryQuestionComment, QueryQuestionCommentCategory } from "edelphi-client";
import { QueryQuestionCommentsService } from "edelphi-client/dist/api/api";
import { mqttConnection, OnMessageCallback } from "../../mqtt";
import styles from "../../constants/styles";
import { Confirm } from "semantic-ui-react";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken?: string,
  logggedUserId?: string,
  locale: string,
  comment: QueryQuestionComment,
  panelId: number,
  queryId: number,
  pageId: number,
  queryReplyId: number,
  canManageComments: boolean,
  category: QueryQuestionCommentCategory | null
}

/**
 * Interface representing component state
 */
interface State {
  commentEditorOpen: boolean,
  commentEditorContents?: string,
  commentDeleteOpen: boolean,
  replyEditorOpen: boolean,
  updating: boolean,
  folded: boolean
}

/**
 * React component for comment editor
 */
class QueryCommentClass extends React.Component<Props, State> {

  private queryQuestionCommentsListener: OnMessageCallback;
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
      commentDeleteOpen: false,
      updating: false,
      folded: false
    };

    this.queryQuestionCommentsListener = this.onQueryQuestionCommentNotification.bind(this);
  }
  
  /**
   * Component will mount life-cycle event
   */
  public componentWillMount() {
    mqttConnection.subscribe("queryquestioncomments", this.queryQuestionCommentsListener);
  }

  /**
   * Component will unmount life-cycle event
   */
  public componentWillUnmount() {
    mqttConnection.unsubscribe("queryquestioncomments", this.onQueryQuestionCommentNotification.bind(this));
  }

  /** 
   * Render edit pest view
   */
  public render() {
    return (
      <div key={ this.props.comment.id } className="queryComment">
        <a id={`comment.${this.props.comment.id}`}></a>
        <div className={ this.state.folded ? "queryCommentShowHideButton hideIcon" : "queryCommentShowHideButton showIcon" } onClick={ () => this.onHoldClick() }></div>
        <div className="queryCommentHeader">
          <div className="queryCommentDate">{ strings.formatString(strings.panel.query.comments.commentDate, this.formatDateTime(this.props.comment.created)) } </div>
          { this.props.comment.creatorId == this.props.logggedUserId ? <p style={{ fontStyle: "italic", fontWeight: "bold" }}> { strings.panel.query.comments.yourComment } </p> : null }
        </div>
        {
          this.renderFoldableContent()
        }        
      </div>
    );
  }
  
  /**
   * Renders foldable content
   */
  private renderFoldableContent() {
    if (this.state.folded) {
      return null;
    }

    return (
      <div className="queryCommentContainerWrapper">
        {
          this.renderCommentDeleteConfirm()
        }
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
    );
  }

  private renderCommentDeleteConfirm() {
    const message = _.truncate(this.props.comment.contents, {
      length: 30
    });

    return <Confirm 
      centered={ true }
      confirmButton={ strings.panel.query.comments.confirmRemoveConfirm }
      cancelButton={ strings.panel.query.comments.confirmRemoveCancel }
      content={ strings.formatString(strings.panel.query.comments.confirmRemoveText, message) } 
      open={this.state.commentDeleteOpen} 
      onCancel={() => { this.setState({ commentDeleteOpen: false}); }} 
      onConfirm={() => { this.onCommentDeleteConfirm(); }} />
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
    return <QueryCommentContainer category={ this.props.category } className="queryCommentChildren" canManageComments={ this.props.canManageComments } parentId={ this.props.comment.id! } queryReplyId={this.props.queryReplyId} pageId={ this.props.pageId } panelId={ this.props.panelId } queryId={ this.props.queryId }/>
  }

  /**
   * Renders comment links
   */
  private renderLinks() {
    return (
      <div className="queryCommentMeta">
        <div className="queryCommentNewComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onNewCommentClick(event) }  className="queryCommentNewCommentLink">{ this.props.comment.creatorId == this.props.logggedUserId ? strings.panel.query.comments.ellaborate : strings.panel.query.comments.reply }</a></div>
        {
          this.renderShowHideComment()
        }
        {
          this.renderEditComment()
        }
        {
          this.renderDeleteComment()
        }
      </div>  
    );
  }

  /**
   * Renders show / hide comment link
   */
  private renderShowHideComment() {
    if (!this.props.canManageComments) {
      return null
    }

    return this.props.comment.hidden 
      ? <div className="queryCommentShowComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onShowClick(event) } className="queryCommentShowCommentLink">{ strings.panel.query.comments.show }</a></div>
      : <div className="queryCommentHideComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onHideClick(event) } className="queryCommentHideCommentLink">{ strings.panel.query.comments.hide }</a></div>
  }

  /**
   * Renders edit comment link
   */
  private renderEditComment() {
    if (!this.props.canManageComments) {
      return null
    }

    return <div className="queryCommentEditComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onEditCommentClick(event) }   className="queryCommentEditCommentLink">{ strings.panel.query.comments.edit }</a></div>
  }

  /**
   * Renders delete comment link
   */
  private renderDeleteComment() {
    if (!this.props.canManageComments) {
      return null
    }

    return <div className="queryCommentDeleteComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onDeleteClick(event) }  className="queryCommentDeleteCommentLink">{ strings.panel.query.comments.remove }</a></div>
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
   * Event handler for comment delete dialog confirm click
   */
  private onCommentDeleteConfirm() {
    if (!this.props.accessToken || !this.props.comment.id) {
      return;
    }

    if (this.state.updating || !this.props.accessToken || !this.props.comment.id) {
      return;
    }

    this.setState({
      commentDeleteOpen: false,
      updating: true
    });

    const queryQuestionCommentsService = this.getQueryQuestionCommentsService(this.props.accessToken);
    queryQuestionCommentsService.deleteQueryQuestionComment(this.props.panelId, this.props.comment.id);
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
   * Click handler for delete link
   * 
   * @param event event
   */
  private onDeleteClick(event: React.MouseEvent<HTMLElement>) {
    event.preventDefault();

    this.setState({
      commentDeleteOpen: true
    });
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

  private onHoldClick() {
    this.setState({
      folded: !this.state.folded
    });
  }

  /**
   * Handles query question comment notification MQTT message
   * 
   * @param message message
   */
  private onQueryQuestionCommentNotification(message: QueryQuestionCommentNotification) {
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
    logggedUserId: state.accessToken ? state.accessToken.userId : null,
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
