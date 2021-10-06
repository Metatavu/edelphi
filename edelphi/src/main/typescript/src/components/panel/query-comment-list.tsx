import * as React from "react";
import strings from "../../localization/strings";
import QueryComment from "./query-comment";
import { QueryQuestionCommentCategory, QueryQuestionComment } from "../../generated/client/models";
import { Button, Checkbox, Dimmer, DropdownItemProps, DropdownProps, Loader, Pagination, Segment, Select } from "semantic-ui-react";
import Api from "../../api";
import { CommentUtils } from "../../utils/comments";
import { QueryQuestionCommentNotification } from "../../types";
import { mqttConnection, OnMessageCallback } from "../../mqtt";


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
  newComments: QueryQuestionComment[];
  loadedChildCommentIds: number[];
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
      comments: [],
      newComments: [],
      loadedChildCommentIds: []
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
   * Component did mount life cycle handler
   */
  public componentDidMount = async () => {
    await this.fetchComments();
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
    const { commentsPerPage, oldestFirst, newComments } = this.state;

    return (
      <div className="queryCommentList">
        <h2 className="querySubTitle queryCommentListSubTitle">{ strings.panel.query.comments.title }</h2>
        { newComments &&
          <Button color="blue" onClick={ this.onLoadNewCommentsClick }>
            { strings.formatString(strings.panel.query.comments.newCommentCount, newComments.length) }
          </Button>
        }
        <p> { strings.panel.query.comments.selectAmount }</p>
        <Select
          placeholder={ strings.generic.select }
          onChange={ this.onDropdownChange }
          value={ commentsPerPage }
          options={ this.getAmountOptions() }
        />
        <div style={{ display: "flex" }}>
          <p> { strings.panel.query.comments.newestFirst }</p>
          <Checkbox
            onChange={ this.onToggleChange }
            toggle
            checked={ oldestFirst }
          />
          <p> { strings.panel.query.comments.oldestFirst }</p>
        </div>
        { this.renderContent() }
        { this.renderPagination() }
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

    console.log("ALL COMMENTS: ", comments);
    console.log("ROOT COMMENTS: ", rootComments);
    console.log("CHILD COMMENTS FOR ROOT COMMENTS: ", childComments);

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
      <Pagination
        siblingRange={ commentsPerPage }
        activePage={ activePage }
        totalPages={ pageCount }
        boundaryRange={ 0 }
        size="mini"
        onPageChange={ async (event, data) => {
          const activePage = data.activePage as number;
          this.setState({ activePage: activePage, loading: true }, () => this.fetchComments());
        }}
      />
    );
  }

  /**
   * Gets comment amount options
   */
  private getAmountOptions = (): DropdownItemProps[] => {
    return [
      { key: 10, value: 10, text: "10" },
      { key: 25, value: 25, text: "25" },
      { key: 50, value: 50, text: "50" },
    ];
  }

  private onLoadNewCommentsClick = async (event: React.MouseEvent<HTMLElement>) => {
    const { comments, newComments } = this.state;
    event.preventDefault();

    console.log({
      title: "onLoadNewCommentsClick",
      comments,
      newComments
    });

    this.setState({
      comments: [ ...comments, ...newComments ],
      newComments: []
    });
  }

  private onToggleChange = () => {
    const { oldestFirst } = this.state;
    this.setState({ oldestFirst: !oldestFirst, loading: true }, () => this.fetchComments());
  }

  private onDropdownChange = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
    const { value } = data;

    event.preventDefault();

    if (!value) {
      return;
    }

    this.setState({ commentsPerPage: Number(value) }, () => this.fetchComments());
  }

  /**
   * Event called when container comments array have changed
   *
   * @param comments comments
   */
  private onCommentsChanged = (comments: QueryQuestionComment[]) => {
    this.setState({
      empty: comments.length == 0
    });
  }

  private fetchComments = async () => {
    const { pageId, panelId, queryId, accessToken, category } = this.props;
    const { commentsPerPage, activePage, oldestFirst } = this.state;

    if (!accessToken) {
      return;
    }

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

    console.log({
      "ROOT TOTAL COUNT": totalCount,
      "ROOT TOTAL COMMENTS": comments
    });

    this.setState({
      pageCount: Math.ceil(totalCount / commentsPerPage),
      comments: comments.sort(CommentUtils.compareComments),
      loading: false
    });
  }

  /**
   * Loads child comments
   */
  private loadComments = async (parentId: number) => {
    const { pageId, panelId, queryId, accessToken, category } = this.props;
    const { comments, loadedChildCommentIds } = this.state;
    const childCommentIds = [ ...loadedChildCommentIds ];

    console.log("loadComments");

    if (!accessToken || loadedChildCommentIds.includes(parentId)) {
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

      console.log("LOADED CHILD COMMENTS FROM API: ", childComments);
      childCommentIds.push(parentId);

      this.setState({
        comments: [ ...comments, ...childComments ],
        loadedChildCommentIds: childCommentIds
      });
    } catch (error) {
      console.error(error);
    }
  }

  /**
   * Handles query question comment notification MQTT message
   *
   * @param message message
   */
  private onQueryQuestionCommentNotification = async (message: QueryQuestionCommentNotification) => {
    const { pageId, panelId, accessToken } = this.props;
    const { newComments, comments } = this.state;

    const newCommentList = [ ...newComments ];
    const commentList = [ ...comments ];

    console.log("NEW MESSAGE TO QUERY-COMMENT-LIST!!!", message);
    switch (message.type) {
      case "UPDATED":
        if (message.pageId === pageId && message.panelId ===panelId) {
          try {
            const updatedComment = await Api.getQueryQuestionCommentsApi(accessToken).findQueryQuestionComment({
              panelId: panelId,
              commentId: message.commentId
            });
  
            const index = commentList.findIndex(comment => comment.id === updatedComment.id);
  
            if (index < 0) {
              commentList.push(updatedComment);
            } else {
              commentList.splice(index, 1, updatedComment);
            }

            this.setState({ comments: commentList });
          } catch (error) {
            console.error(error);
          }
        }
        break;
      case "CREATED":
        if (message.pageId === pageId && message.panelId ===panelId) {
          console.log(`GOT NEW MESSAGE FOR PANEL ${panelId} PAGE ${pageId}`);
          try {
            const newComment = await Api.getQueryQuestionCommentsApi(accessToken).findQueryQuestionComment({
              panelId: panelId,
              commentId: message.commentId
            });

            const index = commentList.findIndex(comment => comment.id === newComment.parentId);

            if (index > -1) {
              const parent = commentList[index];
              commentList.splice(index, 1, { ...parent, childCount: (parent.childCount || 0) + 1 });
            }

            newCommentList.push(newComment);

            this.setState({ newComments: newCommentList, comments: commentList });
          } catch (error) {
            console.error(error);
          }
        }
        break;
      case "DELETED":
        const index = commentList.findIndex(comment => comment.id === message.parentCommentId);

        if (index > -1) {
          const parent = commentList[index];
          commentList.splice(index, 1, { ...parent, childCount: parent.childCount ? parent.childCount - 1 : undefined });
        }

        this.setState({ comments: commentList.filter(comment => comment.id !== message.commentId) });
        break;
    }
  }

}

export default QueryCommentList;