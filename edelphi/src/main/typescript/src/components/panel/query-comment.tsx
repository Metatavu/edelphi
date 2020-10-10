import * as React from "react";
import moment from "moment";
import * as actions from "../../actions";
import * as _ from "lodash";
import QueryCommentContainer from "./query-comment-container";
import strings from "../../localization/strings";
import { StoreState, QueryQuestionCommentNotification } from "../../types";
import { connect } from "react-redux";
import { QueryQuestionComment, QueryQuestionCommentCategory } from "../../generated/client/models";
import { QueryQuestionCommentsApi } from "../../generated/client/apis";
import { mqttConnection, OnMessageCallback } from "../../mqtt";
import styles from "../../constants/styles";
import { Confirm } from "semantic-ui-react";
import Api from "../../api";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: string,
  loggedUserId: string,
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
  folded: boolean,
  hasChildren?: boolean
}

/**
 * React component for comment editor
 */
class QueryCommentClass extends React.Component<Props, State> {

  private queryQuestionCommentsListener: OnMessageCallback;
  private commentEditor: HTMLTextAreaElement | null = null;
  private replyEditor: HTMLTextAreaElement | null = null;

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
        <div className="queryCommentHeader" style={{ clear: "both" }}>
          <div className="queryCommentDate">{ strings.formatString(strings.panel.query.comments.commentDate, this.formatDateTime(this.props.comment.created)) } </div>
          { this.props.comment.creatorId == this.props.loggedUserId ? <p style={{ fontStyle: "italic", fontWeight: "bold" }}> { strings.panel.query.comments.yourComment } </p> : null }
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

    return (<div className="queryCommentText">{ this.renderCommentContents() }</div>);
  }

  /**
   * Renders comment's contents
   * 
   * @returns rendered contents
   */
  private renderCommentContents() {
    const comment = this.props.comment;

    if (!comment || !comment.contents) {
      return "";
    }

    return comment.contents.split("\n").map((item, index) => {
      return <span key={index}>{item}<br/></span>;
    });
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
    const { accessToken, loggedUserId } = this.props;

    return <QueryCommentContainer 
      accessToken={ accessToken }
      loggedUserId={ loggedUserId }
      onCommentsChanged={ this.onCommentsChanged } 
      category={ this.props.category } 
      className="queryCommentChildren" 
      canManageComments={ this.props.canManageComments } 
      parentId={ this.props.comment.id! } 
      queryReplyId={this.props.queryReplyId} 
      pageId={ this.props.pageId } 
      panelId={ this.props.panelId } 
      queryId={ this.props.queryId }/>
  }

  /**
   * Renders comment links
   */
  private renderLinks() {
    return (
      <div className="queryCommentMeta">
        <div className="queryCommentNewComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onNewCommentClick(event) }  className="queryCommentNewCommentLink">{ this.props.comment.creatorId == this.props.loggedUserId ? strings.panel.query.comments.elaborate : strings.panel.query.comments.reply }</a></div>
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
    if (!this.canEditComment()) {
      return null
    }

    return <div className="queryCommentEditComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onEditCommentClick(event) }   className="queryCommentEditCommentLink">{ strings.panel.query.comments.edit }</a></div>
  }

  /**
   * Returns whether user may edit a comment or not
   * 
   * @returns whether user may edit a comment or not
   */
  private canEditComment = () => {
    return this.state.hasChildren == false && (this.props.canManageComments || this.props.loggedUserId == this.props.comment.creatorId);
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
    return moment(dateTime).locale(strings.getLanguage()).format("LLL"); 
  }

  /**
   * Returns query question comments API
   * 
   * @returns query question comments API
   */
  private getQueryQuestionCommentsApi(accessToken: string): QueryQuestionCommentsApi {
    return Api.getQueryQuestionCommentsApi(accessToken);
  }

  /**
   * Event called when container comments array have changed
   * 
   * @param comments comments
   */
  private onCommentsChanged = (comments: QueryQuestionComment[]) => {
    this.setState({
      hasChildren: comments.length > 0
    });
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

    const queryQuestionCommentsApi = this.getQueryQuestionCommentsApi(this.props.accessToken);

    queryQuestionCommentsApi.deleteQueryQuestionComment({
      panelId: this.props.panelId,
      commentId: this.props.comment.id
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

    const queryQuestionCommentsApi = this.getQueryQuestionCommentsApi(this.props.accessToken);

    queryQuestionCommentsApi.updateQueryQuestionComment({
      commentId: this.props.comment.id,
      panelId: this.props.panelId,
      queryQuestionComment: { ... this.props.comment, contents: contents }
    });
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

    const categoryId = this.props.category ? this.props.category.id : 0;

    const queryQuestionCommentsApi = this.getQueryQuestionCommentsApi(this.props.accessToken);

    queryQuestionCommentsApi.createQueryQuestionComment({
      panelId: this.props.panelId,
      queryQuestionComment: {
        contents: contents,
        hidden: false,
        parentId: this.props.comment.id,
        queryPageId: this.props.pageId,
        queryReplyId: this.props.queryReplyId,
        categoryId: categoryId
      }
    });
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

    const queryQuestionCommentsApi = this.getQueryQuestionCommentsApi(this.props.accessToken);
    
    queryQuestionCommentsApi.updateQueryQuestionComment({
      commentId: this.props.comment.id,
      panelId: this.props.panelId,
      queryQuestionComment: { ... this.props.comment, hidden: false }
    });
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

    const queryQuestionCommentsApi = this.getQueryQuestionCommentsApi(this.props.accessToken);

    queryQuestionCommentsApi.updateQueryQuestionComment({
      commentId: this.props.comment.id,
      panelId: this.props.panelId,
      queryQuestionComment: { ... this.props.comment, hidden: true }
    });
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

export default QueryCommentClass;
