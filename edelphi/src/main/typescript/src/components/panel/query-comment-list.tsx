import * as React from "react";
import strings from "../../localization/strings";
import QueryComment from "./query-comment";
import { QueryQuestionCommentCategory, QueryQuestionComment } from "../../generated/client/models";
import { Checkbox, Dimmer, DropdownItemProps, DropdownProps, Loader, Pagination, Segment, Select } from "semantic-ui-react";
import Api from "../../api";
import { CommentUtils } from "../../utils/comments";
import { QueryQuestionCommentNotification } from "../../types";
import { mqttConnection, OnMessageCallback } from "../../mqtt";
import ErrorDialog from "../error-dialog";


/**
 * Interface representing component properties
 */
interface Props {
  accessToken: string;
  loggedUserId: string;
  queryId: number;
  panelId: number;
  pageId: number;
  queryReplyId: number;
  canManageComments: boolean;
  category: QueryQuestionCommentCategory | null;
}

/**
 * Interface representing component state
 */
interface State {
  empty: boolean;
  commentsPerPage: number;
  activePage: number;
  pageCount: number;
  oldestFirst: boolean;
  loading: boolean;
  comments: QueryQuestionComment[];
  error?: Error | unknown;
}

/**
 * React component for comment editor
 */
class QueryCommentList extends React.Component<Props, State> {

  private queryQuestionCommentsListener: OnMessageCallback;

  /**
   * Constructor
   *
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      empty: false,
      commentsPerPage: 10,
      activePage: 1,
      pageCount: 1,
      oldestFirst: false,
      loading: true,
      comments: []
    };

    this.queryQuestionCommentsListener = this.onQueryQuestionCommentNotification.bind(this);
  }

  /**
   * Dropdown amount options
   */
  private amountOptions: DropdownItemProps[] = [
    { key: 10, value: 10, text: "10" },
    { key: 25, value: 25, text: "25" },
    { key: 50, value: 50, text: "50" },
  ];

    /**
   * Component will mount life-cycle event
   */
  public componentWillMount = () => {
    mqttConnection.subscribe("queryquestioncomments", this.queryQuestionCommentsListener);
  }

  /**
   * Component did mount life cycle handler
   */
  public componentDidMount = async () => {
    await this.loadRootComments();
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
    const { error } = this.state;

    if (error) {
      return <ErrorDialog error={ error } onClose={ () => this.setState({ error: undefined }) } /> 
    }

    return (
      <div className="queryCommentList">
        { this.renderTitleSection() }
        { this.renderContent() }
        { this.renderPagination() }
      </div>
    );
  }

  /**
   * Renders title section
   */
  private renderTitleSection = () => {
    return (
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", borderBottom: "1px solid #DDE7ED", paddingBottom: "8px" }}>
        { this.renderTitle() }
        { this.renderCommentAmount() }
        { this.renderSort() }
      </div>
    );      
  }

  /**
   * Renders comment title
   */
  private renderTitle = () => {
    return (
      <h2 className="querySubTitle queryCommentListSubTitle" style={{ display: "flex", padding: 0, borderBottom: 0 }}>
        { strings.panel.query.comments.title }
      </h2>
    );
  }

  /**
   * Renders comment amount input
   */
  private renderCommentAmount = () => {
    const { commentsPerPage } = this.state;
    
    return (
      <div style={{ display: "flex", alignItems: "center" }}>
        <p style={{ margin: 0, marginRight: "10px" }}>{ strings.panel.query.comments.selectAmount }</p>
        <Select
          placeholder={ strings.generic.select }
          onChange={ this.onDropdownChange }
          value={ commentsPerPage }
          options={ this.amountOptions }
          compact
        />
      </div>
    );
  }

  /**
   * Renders sort input
   */
  private renderSort = () => {
    const { oldestFirst } = this.state;

    return (
      <div style={{ display: "flex" }}>
        <p style={{ marginTop: 10, marginRight: 10 }}>
          { strings.panel.query.comments.newestFirst }
        </p>
        <div style={{ marginTop: 10 }}>
          <Checkbox
            onChange={ this.onToggleChange }
            toggle
            checked={ oldestFirst }
          />
        </div>
        <p style={{ marginTop: 10, marginLeft: 10 }}>
          { strings.panel.query.comments.oldestFirst }
        </p>
      </div>
    );
  }

  /**
   * Renders content
   */
  private renderContent = () => {
    const { accessToken, loggedUserId, category, canManageComments, queryReplyId, pageId, panelId, queryId } = this.props;
    const { empty, comments, loading } = this.state;

    if (empty) {
      return <p> { strings.panel.query.comments.noComments } </p>;
    }

    if (loading) {
      return (
        <Segment style={{ minHeight: "100px" }}>
          <Dimmer inverted active>
            <Loader>{ strings.generic.loading }</Loader>
          </Dimmer>
        </Segment>
      );
    }

    const rootComments = comments.filter(comment => !comment.parentId);
    const childComments = comments.filter(_comment => _comment.parentId);

    return rootComments.map(comment => (
      <QueryComment
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
        isRootComment
        loadChildComments={ this.loadComments }
        childComments={ childComments }
      />
    ));

  }

  /**
   * Renders pagination
   */
  private renderPagination = () => {
    const { pageCount, activePage, commentsPerPage } = this.state;

    return (
      <div style={{ display: "flex", justifyContent: "center" }}>
        <Pagination
          siblingRange={ commentsPerPage }
          activePage={ activePage }
          totalPages={ pageCount }
          boundaryRange={ 0 }
          size="mini"
          onPageChange={ async (event, data) => {
            const activePage = data.activePage as number;
            this.setState({ activePage: activePage, loading: true }, () => this.loadRootComments());
          }}

        />
      </div>
    );
  }

  /**
   * Event handler for on toggle change
   */
  private onToggleChange = () => {
    const { oldestFirst } = this.state;

    this.setState({ oldestFirst: !oldestFirst, loading: true }, () => this.loadRootComments());
  }

  /**
   * Event handler for on drop down change
   *
   * @param event React event
   * @param data dropdown data
   */
  private onDropdownChange = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
    const { value } = data;

    event.preventDefault();

    if (!value) {
      return;
    }

    this.setState({
      commentsPerPage: Number(value),
      activePage: 1,
      loading: true
    }, () => this.loadRootComments());
  }

  /**
   * Fetches new root comments
   */
  private loadRootComments = async () => {
    const { pageId, panelId, queryId, accessToken, category } = this.props;
    const { commentsPerPage, activePage, oldestFirst } = this.state;

    if (!accessToken) {
      return;
    }

    try {
      const categoryId = category ? category.id : 0;

      const response = await Api.getQueryQuestionCommentsApi(accessToken).listQueryQuestionCommentsRaw({
        panelId: panelId,
        queryId: queryId,
        pageId: pageId,
        parentId: 0,
        categoryId: categoryId,
        firstResult: (activePage - 1) * commentsPerPage,
        maxResults: commentsPerPage,
        oldestFirst: oldestFirst
      });
  
      const totalCount = parseInt(response.raw.headers.get("X-Total-Count") || "0") || 0;
      const comments = await response.value();
  
      this.setState({
        pageCount: Math.ceil(totalCount / commentsPerPage),
        comments: comments.sort(CommentUtils.compareComments),
        loading: false
      });
    } catch (e) {
      this.setState({
        error: e
      });
    }
  }

  /**
   * Loads child comments with parent ID
   *
   * @param parentId parent ID
   */
  private loadComments = async (parentId: number) => {
    const { pageId, panelId, queryId, accessToken, category } = this.props;
    const { comments } = this.state;

    if (!accessToken) {
      return;
    }

    const categoryId = category ? category.id : 0;
    try {
      const childComments = await Api.getQueryQuestionCommentsApi(accessToken).listQueryQuestionComments({
        panelId: panelId,
        queryId: queryId,
        pageId: pageId,
        parentId: parentId,
        categoryId: categoryId,
        firstResult: 0,
        maxResults: 1000,
        oldestFirst: false
      });

      const combinedComments = [ ...comments ];
      childComments.forEach(
        childComment => {
          const childIndex = combinedComments.findIndex(comment => comment.id === childComment.id);
          if (childIndex > -1) {
            combinedComments.splice(childIndex, 1, childComment);
          } else {
            combinedComments.push(childComment);
          }
        }
      )

      this.setState({
        comments: combinedComments
      });
    } catch (error) {
      this.setState({
        error: error
      });
    }
  }

  /**
   * Handles query question comment notification MQTT message
   *
   * @param message message
   */
  private onQueryQuestionCommentNotification = async (message: QueryQuestionCommentNotification) => {
    const { pageId, panelId } = this.props;

    if (message.pageId !== pageId || message.panelId !== panelId) {
      return;
    }

    const { commentId, parentCommentId, type } = message;

    switch (type) {
      case "CREATED":
        this.onCreate(commentId);
        break;
      case "UPDATED":
        this.onUpdate(commentId);
        break;
      case "DELETED":
        this.onDelete(commentId, parentCommentId);
        break;
    }
  }

  /**
   * Event handler for CREATED MQTT message
   *
   * @param commentId comment ID
   */
  private onCreate = async (commentId: number) => {
    const { panelId, accessToken } = this.props;
    const { comments } = this.state;

    const commentList = [ ...comments ];

    try {
      const newComment = await Api.getQueryQuestionCommentsApi(accessToken).findQueryQuestionComment({
        panelId: panelId,
        commentId: commentId
      });

      if (newComment.parentId) {
        const parentIndex = commentList.findIndex(comment => comment.id === newComment.parentId);

        if (parentIndex > -1) {
          const parent = commentList[parentIndex];
          commentList.splice(parentIndex, 1, { ...parent, childCount: (parent.childCount || 0) + 1 });
        }

        this.setState({ comments: [ newComment, ...commentList ] }, () => this.loadComments(newComment.parentId!));

      } else {
        this.setState({ comments: [ newComment, ...commentList ] });
      }
    } catch (error) {
      this.setState({
        error: error
      });
    }
  }

  /**
   * Event handler for UPDATED MQTT message
   *
   * @param commentId comment ID
   */
  private onUpdate = async (commentId: number) => {
    const { panelId, accessToken } = this.props;
    const { comments } = this.state;

    const commentList = [ ...comments ];
    try {
      const updatedComment = await Api.getQueryQuestionCommentsApi(accessToken).findQueryQuestionComment({
        panelId: panelId,
        commentId: commentId
      });

      const index = commentList.findIndex(comment => comment.id === updatedComment.id);

      if (index < 0) {
        commentList.push(updatedComment);
      } else {
        commentList.splice(index, 1, updatedComment);
      }

      this.setState({ comments: commentList });
    } catch (error) {
      this.setState({
        error: error
      });
    }
  }

  /**
   * Handler for DELETED MQTT message
   *
   * @param commentId comment ID
   * @param parentCommentId parent comment ID or null
   */
  private onDelete = async (commentId: number, parentCommentId: number | null) => {
    const { comments } = this.state;

    const commentList = [ ...comments ];
    const index = commentList.findIndex(comment => comment.id === parentCommentId);

    if (index > -1) {
      const parent = commentList[index];
      commentList.splice(index, 1, { ...parent, childCount: parent.childCount ? parent.childCount - 1 : undefined });
    }

    this.setState({ 
      comments: commentList.filter(comment => comment.id !== commentId)
    });
  }

}

export default QueryCommentList;