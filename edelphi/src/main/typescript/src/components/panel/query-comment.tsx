import * as React from "react";
import moment from "moment";
import * as _ from "lodash";
import strings from "../../localization/strings";
import { QueryQuestionComment, QueryQuestionCommentCategory } from "../../generated/client/models";
import { Confirm } from "semantic-ui-react";
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
  folded: boolean;
  hasChildren?: boolean;
  showChildComments: boolean;
}

/**
 * React component for comment editor
 */
class QueryCommentClass extends React.Component<Props, State> {

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
      folded: false,
      showChildComments: false
    };
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
    const { folded, showChildComments } = this.state;

    if (folded) {
      return null;
    }

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

    return (
      <Confirm
        centered={ true }
        confirmButton={ strings.panel.query.comments.confirmRemoveConfirm }
        cancelButton={ strings.panel.query.comments.confirmRemoveCancel }
        content={ strings.formatString(strings.panel.query.comments.confirmRemoveText, message) }
        open={ this.state.commentDeleteOpen }
        onCancel={ () => this.setState({ commentDeleteOpen: false}) }
        onConfirm={() => { this.onCommentDeleteConfirm(); }}
      />
    );
  }

  /**
   * Renders comment text or editor
   */
  private renderContents = () => {
    const { commentEditorOpen, commentEditorContents } = this.state;

    if (commentEditorOpen) {
      return (
        <div className="editCommentEditor">
          <textarea
            ref={ textarea => this.commentEditor = textarea }
            onChange={ event => this.setState({ commentEditorContents: event.target.value }) }
            value={ commentEditorContents }
          />
          <input
            type="button"
            className="formButton"
            disabled={ !commentEditorContents || !commentEditorContents.trim() }
            onClick={ this.onEditCommentSaveClick }
            value={ strings.panel.query.comments.saveEdit }
          />
        </div>
      );
    }

    return (
      <div className="queryCommentText">
        { this.renderCommentContents() }
      </div>
    );
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
    const { comment } = this.props;
    const { created, lastModified } = comment;

    if (created === lastModified) {
      return null;
    }

    return (
      <div className="queryCommentModified">
        { strings.formatString(strings.panel.query.comments.commentModified, this.formatDateTime(lastModified)) }
      </div>
    );
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
        <textarea ref={ textarea => this.replyEditor = textarea }/>
        <input
          type="button"
          className="formButton"
          onClick={ this.onNewCommentSaveClick }
          value={ strings.panel.query.comments.saveReply }
        />
      </div>
    );
  }

  /**
   * Renders show comments button
   */
  private renderShowComments = () => {
    const { comment } = this.props;
    const { showChildComments } = this.state;

    if (!comment.childCount) {
      return null;
    }

    const buttonText = strings.formatString(
      showChildComments ?
        strings.panel.query.comments.hideReplies :
        strings.panel.query.comments.showReplies,
      comment.childCount
    );

    return (
      <div className="queryCommentEditComment">
        <a
          href="#"
          onClick={ this.onShowMoreClick(comment) }
          className="queryCommentEditCommentLink"
        >
          { buttonText }
        </a>
      </div>
    );
  }

  /**
   * Renders child comments
   */
  private renderChildComments = () => {
    const {
      accessToken,
      loggedUserId,
      category,
      comment,
      canManageComments,
      queryReplyId,
      pageId,
      panelId,
      queryId,
      childComments,
      loadChildComments
    } = this.props;

    if (!childComments) {
      return null;
    }

    const comments = childComments.filter(_comment => _comment.parentId === comment.id);
    const filteredChildComments = childComments.filter(_comment => _comment.parentId !== comment.id);

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
    const { comment, loggedUserId } = this.props;

    return (
      <div className="queryCommentMeta">
        <div className="queryCommentNewComment">
          <a
            href="#"
            onClick={ this.onNewCommentClick }
            className="queryCommentNewCommentLink"
          >
            {
              comment.creatorId == loggedUserId ?
                strings.panel.query.comments.elaborate :
                strings.panel.query.comments.reply
            }
          </a>
        </div>
        { this.renderShowComments() }
        {/* TODO: Add this if this feature is requested from the customer */}
        {/* { this.renderShowHideComment() } */}
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
      ? <div className="queryCommentShowComment"><a href="#" onClick={ () => this.onShowOrHideClick(false) } className="queryCommentShowCommentLink">{ strings.panel.query.comments.show }</a></div>
      : <div className="queryCommentHideComment"><a href="#" onClick={ () => this.onShowOrHideClick(true) } className="queryCommentHideCommentLink">{ strings.panel.query.comments.hide }</a></div>
  }

  /**
   * Renders edit comment link
   */
  private renderEditComment = () => {
    if (!this.canEditComment()) {
      return null;
    }

    return (
      <div className="queryCommentEditComment">
        <a
          href="#"
          onClick={ this.onEditCommentClick }
          className="queryCommentEditCommentLink"
        >
          { strings.panel.query.comments.edit }
        </a>
      </div>
    );
  }

  /**
   * Returns whether user may edit a comment or not
   *
   * @returns whether user may edit a comment or not
   */
  private canEditComment = () => {
    const { canManageComments, loggedUserId, comment } = this.props;

    return canManageComments || loggedUserId == comment.creatorId;
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
   * Click handler for reply comment link
   *
   * @param event event
   */
  private onNewCommentClick = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();

    this.setState({
      replyEditorOpen: true,
      showChildComments: true
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

    if (!accessToken || !comment.id) {
      return;
    }

    this.setState({ commentDeleteOpen: false });

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

    if (!this.commentEditor || !accessToken || !comment.id) {
      return;
    }

    const contents = this.state.commentEditorContents;

    this.setState({ commentEditorOpen: false });

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

    event.preventDefault();

    if (!this.replyEditor || !accessToken || !comment.id) {
      return;
    }

    const contents = this.replyEditor.value;

    this.setState({ replyEditorOpen: false });

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

    event.stopPropagation();
    event.preventDefault();

    this.setState({ showChildComments: !this.state.showChildComments }, () => loadChildComments && !showChildComments && comment.id && loadChildComments(comment.id));
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
  private onShowOrHideClick = (hidden: boolean) => (event: React.MouseEvent<HTMLElement>) => {
    const { accessToken, comment, panelId } = this.props;

    event.preventDefault();

    if (!accessToken || !comment.id) {
      return;
    }

    const queryQuestionCommentsApi = Api.getQueryQuestionCommentsApi(accessToken);

    queryQuestionCommentsApi.updateQueryQuestionComment({
      commentId: comment.id,
      panelId: panelId,
      queryQuestionComment: { ...comment, hidden: hidden }
    });
  }
}

export default QueryCommentClass;