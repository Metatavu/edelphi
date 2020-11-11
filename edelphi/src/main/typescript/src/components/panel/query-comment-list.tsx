import * as React from "react";
import * as actions from "../../actions";
import strings from "../../localization/strings";
import QueryCommentContainer from "./query-comment-container";
import { QueryQuestionCommentCategory, QueryQuestionComment } from "../../generated/client/models";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: string;
  loggedUserId: string;
  queryId: number,
  panelId: number,
  pageId: number,
  queryReplyId: number,  
  canManageComments: boolean,
  category: QueryQuestionCommentCategory | null
}

/**
 * Interface representing component state
 */
interface State {
  empty: boolean
}

/**
 * React component for comment editor
 */
class QueryCommentList extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      empty: false
    };
  }

  /** 
   * Render edit pest view
   */
  public render() {
    const { accessToken, loggedUserId, category, canManageComments, queryReplyId, pageId, panelId, queryId } = this.props;

    return (
      <div className="queryCommentList">
        <h2 className="querySubTitle queryCommentListSubTitle">{ strings.panel.query.comments.title }</h2>
        { this.state.empty ? <p> { strings.panel.query.comments.noComments } </p> : null }
        <QueryCommentContainer 
          accessToken={ accessToken }
          loggedUserId={ loggedUserId }
          onCommentsChanged={ this.onCommentsChanged } 
          category={ category } 
          className="queryCommentsContainer" 
          canManageComments={ canManageComments } 
          queryReplyId={ queryReplyId } 
          parentId={ 0 } 
          pageId={ pageId } 
          panelId={ panelId } 
          queryId={ queryId }/>
      </div>
    );
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
  
}

export default QueryCommentList;
