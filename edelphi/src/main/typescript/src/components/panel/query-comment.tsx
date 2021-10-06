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
import { Button, Confirm } from "semantic-ui-react";
import Api from "../../api";
import QueryComment from "./query-comment";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: string;
  loggedUserId: string;
  comment: QueryQuestionComment;
  panelId: number;
  queryId: number;
  pageId: number;
  queryReplyId: number;
  canManageComments: boolean;
  category: QueryQuestionCommentCategory | null;
  isRootComment?: boolean;
  childComments?: QueryQuestionComment[];
  loadChildComments?: (parentId: number) => void;
}

/**
 * Interface representing component state
 */
interface State {
  commentEditorOpen: boolean;
  commentEditorContents?: string;
  commentDeleteOpen: boolean;
  replyEditorOpen: boolean;
  updating: boolean;
  folded: boolean;
  hasChildren?: boolean;
  showChildComments: boolean;
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
      folded: false,
      showChildComments: false
    };

    this.queryQuestionCommentsListener = this.onQueryQuestionCommentNotification.bind(this);
  }

  /**
   * Component will mount life-cycle event
   */
  public componentWillMount = () => {
    mqttConnection.subscribe("queryquestioncomments", this.queryQuestionCommentsListener);
  }

  /**
   * Component will unmount life-cycle event
   */
  public componentWillUnmount = () => {
    mqttConnection.unsubscribe("queryquestioncomments", this.onQueryQuestionCommentNotification.bind(this));
  }

  /**
   * Render edit pest view
   */
  public render = () => {
    const { comment, loggedUserId } = this.props;
    const { folded } = this.state;

    return (
      <div key={ comment.id } className="queryComment">
        <a id={`comment.${comment.id}`}></a>
        <div
          className={ folded ? "queryCommentShowHideButton hideIcon" : "queryCommentShowHideButton showIcon" }
          onClick={ () => this.setState({ folded: !folded }) }
        />
        <div className="queryCommentHeader" style={{ clear: "both" }}>
          <div className="queryCommentDate">
            { strings.formatString(strings.panel.query.comments.commentDate, this.formatDateTime(comment.created)) }
          </div>
          { comment.creatorId == loggedUserId ? <p style={{ fontStyle: "italic", fontWeight: "bold" }}> { strings.panel.query.comments.yourComment } </p> : null }
        </div>
        { this.renderFoldableContent() }        
      </div>
    );
  }

  /**
   * Renders foldable content
   */
  private renderFoldableContent = () => {
    const { isRootComment, childComments } = this.props;
    const { folded, showChildComments } = this.state;

    if (folded) {
      return null;
    }

    console.log({
      folded,
      isRootComment
    });

    return (
      <div className="queryCommentContainerWrapper">
        { this.renderCommentDeleteConfirm() }
        { this.renderModified() }
        { this.renderContents() }
        { this.renderLinks() }
        
        { showChildComments && this.renderChildComments() }
        { this.renderNewCommentEditor() }
      </div>
    );
  }

  /**
   * Renders comment delete confirm
   */
  private renderCommentDeleteConfirm = () => {
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
  private renderContents = () => {
    if (this.state.commentEditorOpen) {
      return (
        <div className="editCommentEditor">
          <textarea ref={ (textarea) => { this.commentEditor = textarea; }} onChange={ (event) => { this.setState({ commentEditorContents: event.target.value }); } } value={ this.state.commentEditorContents }></textarea>
          <input type="button" className="formButton" disabled={ !this.state.commentEditorContents || !this.state.commentEditorContents.trim() } onClick={ this.onEditCommentSaveClick } value={ strings.panel.query.comments.saveEdit }></input>
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
  private renderCommentContents = () => {
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
  private renderModified = () => {
    if (this.props.comment.created == this.props.comment.lastModified) {
      return null;
    }

    return <div className="queryCommentModified">{ strings.formatString(strings.panel.query.comments.commentModified, this.formatDateTime(this.props.comment.lastModified)) } </div>
  }

  /**
   * Renders new comment editor
   */
  private renderNewCommentEditor = () => {
    if (!this.state.replyEditorOpen) {
      return null;
    }

    return (
      <div className="newCommentEditor">
        <textarea ref={ textarea => this.replyEditor = textarea }></textarea>
        <input type="button" className="formButton" onClick={ this.onNewCommentSaveClick } value={ strings.panel.query.comments.saveReply }></input>
      </div>
    );
  }

  private renderShowComments = () => {
    const { comment } = this.props;

    if (!comment.childCount) {
      return null;
    }

    return (
      <div className="queryCommentEditComment">
        <a
          style={ this.state.updating ? styles.disabledLink : {} }
          href="#"
          onClick={ this.onShowMoreClick(comment) }
          className="queryCommentEditCommentLink"
        >
          { strings.formatString(strings.panel.query.comments.showReplies, comment.childCount) }
        </a>
      </div>
    );
  }

  /**
   * Renders child comments
   */
  private renderChildComments = () => {
    const { accessToken, loggedUserId, category, comment, canManageComments, queryReplyId, pageId, panelId, queryId, childComments, loadChildComments } = this.props;

    console.log("QUERY COMMENT COMPONENT FOR COMMENT");
    console.log({
      "this.props": this.props
    });

    if (!childComments) {
      return null;
    }

    const comments = childComments.filter(_comment => _comment.parentId === comment.id);
    const filteredChildComments = childComments.filter(_comment => _comment.parentId !== comment.id);
    console.log("COMMENTS: ", comments);
    console.log("ALL CHILD COMMENTS: ", childComments);
    console.log("FILTERED CHILD COMMENTS: ", filteredChildComments);

    return comments.map(childComment => (
      <QueryComment
        key={ childComment.id } 
        accessToken={ accessToken }
        loggedUserId={ loggedUserId }
        category={ category } 
        canManageComments={ canManageComments } 
        comment={ childComment } 
        queryReplyId={ queryReplyId }
        pageId={ pageId } 
        panelId={ panelId} 
        queryId={ queryId }
        childComments={ filteredChildComments }
        loadChildComments={ loadChildComments }
      />
    ));
  }

  /**
   * Renders comment links
   */
  private renderLinks = () => {
    return (
      <div className="queryCommentMeta">
        <div className="queryCommentNewComment">
          <a
            style={ this.state.updating ? styles.disabledLink : {} }
            href="#"
            onClick={ this.onNewCommentClick }
            className="queryCommentNewCommentLink"
          >
            {
              this.props.comment.creatorId == this.props.loggedUserId ?
              strings.panel.query.comments.elaborate :
              strings.panel.query.comments.reply
            }
          </a>
        </div>
        { this.renderShowComments() }
        { this.renderShowHideComment() }
        { this.renderEditComment() }
        { this.renderDeleteComment() }
      </div>
    );
  }

  /**
   * Renders show / hide comment link
   */
  private renderShowHideComment = () => {
    const { canManageComments, comment } = this.props;

    if (!canManageComments) {
      return null;
    }

    return comment.hidden
      ? <div className="queryCommentShowComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ this.onShowClick } className="queryCommentShowCommentLink">{ strings.panel.query.comments.show }</a></div>
      : <div className="queryCommentHideComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ this.onHideClick } className="queryCommentHideCommentLink">{ strings.panel.query.comments.hide }</a></div>
  }

  /**
   * Renders edit comment link
   */
  private renderEditComment = () => {
    if (!this.canEditComment()) {
      return null;
    }

    return <div className="queryCommentEditComment"><a style={ this.state.updating ? styles.disabledLink : {} } href="#" onClick={ this.onEditCommentClick }   className="queryCommentEditCommentLink">{ strings.panel.query.comments.edit }</a></div>
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
  private renderDeleteComment = () => {
    const { canManageComments, comment } = this.props;

    if (!canManageComments || comment.childCount) {
      return null;
    }

    return (
      <div className="queryCommentDeleteComment">
        <a
          style={ this.state.updating ? styles.disabledLink : {} }
          href="#"
          onClick={ this.onDeleteClick }
          className="queryCommentDeleteCommentLink"
        >
          { strings.panel.query.comments.remove }
        </a>
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
    return moment(dateTime).locale(strings.getLanguage()).format("LLL");
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
  private onNewCommentClick = (event: React.MouseEvent<HTMLElement>) => {
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
  private onEditCommentClick = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();

    this.setState({
      commentEditorOpen: true,
      commentEditorContents: this.props.comment.contents
    });
  }

  /**
   * Event handler for comment delete dialog confirm click
   */
  private onCommentDeleteConfirm = () => {
    const { accessToken, comment, panelId } = this.props;

    if (!accessToken || !comment.id) {
      return;
    }

    if (this.state.updating || !accessToken || !comment.id) {
      return;
    }

    this.setState({
      commentDeleteOpen: false,
      updating: true
    });

    const queryQuestionCommentsApi = Api.getQueryQuestionCommentsApi(accessToken);

    queryQuestionCommentsApi.deleteQueryQuestionComment({
      panelId: panelId,
      commentId: comment.id
    });
  }

  /**
   * Click handler edit comment save button
   *
   * @param event event
   */
  private onEditCommentSaveClick = async (event: React.MouseEvent<HTMLElement>) => {
    const { accessToken, comment, panelId } = this.props;

    event.preventDefault();

    if (!this.commentEditor || this.state.updating || !accessToken || !comment.id) {
      return;
    }

    const contents = this.state.commentEditorContents;

    this.setState({
      commentEditorOpen: false,
      updating: true
    });

    const queryQuestionCommentsApi = Api.getQueryQuestionCommentsApi(accessToken);

    await queryQuestionCommentsApi.updateQueryQuestionComment({
      commentId: comment.id,
      panelId: panelId,
      queryQuestionComment: { ... comment, contents: contents }
    });
  }

  /**
   * Click handler new save button
   *
   * @param event event
   */
  private onNewCommentSaveClick = async (event: React.MouseEvent<HTMLElement>) => {
    const { accessToken, panelId, comment, pageId, queryReplyId, category } = this.props;
    const { updating } = this.state;

    event.preventDefault();

    if (!this.replyEditor ||updating || !accessToken || !comment.id) {
      return;
    }

    const contents = this.replyEditor.value;

    this.setState({
      replyEditorOpen: false,
      updating: true
    });

    const categoryId = category ? category.id : 0;

    const queryQuestionCommentsApi = Api.getQueryQuestionCommentsApi(accessToken);

    await queryQuestionCommentsApi.createQueryQuestionComment({
      panelId: panelId,
      queryQuestionComment: {
        contents: contents,
        hidden: false,
        parentId: comment.id,
        queryPageId: pageId,
        queryReplyId: queryReplyId,
        categoryId: categoryId
      }
    });
  }

  /**
   * Event handler for on show more click
   */
  private onShowMoreClick = (comment: QueryQuestionComment) => (event: React.MouseEvent<HTMLElement>) => {
    const { loadChildComments } = this.props;
    const { showChildComments } = this.state;

    this.setState({ showChildComments: !showChildComments });
    loadChildComments && comment.id && loadChildComments(comment.id);

    event.stopPropagation();
    event.preventDefault();
  }

  /**
   * Click handler for delete link
   *
   * @param event event
   */
  private onDeleteClick = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();

    this.setState({ commentDeleteOpen: true });
  }

  /**
   * Click handler for show link
   *
   * @param event event
   */
  private onShowClick = (event: React.MouseEvent<HTMLElement>) => {
    const { accessToken, comment, panelId } = this.props;

    event.preventDefault();

    if (this.state.updating || !accessToken || !comment.id) {
      return;
    }

    this.setState({
      updating: true
    });

    const queryQuestionCommentsApi = Api.getQueryQuestionCommentsApi(accessToken);

    queryQuestionCommentsApi.updateQueryQuestionComment({
      commentId: comment.id,
      panelId: panelId,
      queryQuestionComment: { ... comment, hidden: false }
    });
  }

  /**
   * Click handler for hide link
   *
   * @param event event
   */
  private onHideClick = (event: React.MouseEvent<HTMLElement>) => {
    const { accessToken, comment, panelId } = this.props;

    event.preventDefault();

    if (this.state.updating || !accessToken || !comment.id) {
      return;
    }

    this.setState({
      updating: true
    });

    const queryQuestionCommentsApi = Api.getQueryQuestionCommentsApi(accessToken);

    queryQuestionCommentsApi.updateQueryQuestionComment({
      commentId: comment.id,
      panelId: panelId,
      queryQuestionComment: { ... comment, hidden: true }
    });
  }

  private onFoldClick = () => {
  }

  /**
   * Handles query question comment notification MQTT message
   *
   * @param message message
   */
  private onQueryQuestionCommentNotification(message: QueryQuestionCommentNotification) {
    // switch (message.type) {
    //   case "UPDATED":
    //     if (message.commentId == this.props.comment.id) {
    //       this.setState({
    //         updating: false
    //       });
    //     }
    //   break;
    //   case "CREATED":
    //     if (message.parentCommentId == this.props.comment.id) {
    //       this.setState({
    //         updating: false
    //       });
    //     }
    //   break;
    // }
  }
}

export default QueryCommentClass;
