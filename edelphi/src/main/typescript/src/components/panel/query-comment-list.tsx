import * as React from "react";
import * as actions from "../../actions";
import strings from "../../localization/strings";
import QueryCommentContainer from "./query-comment-container";
import { QueryQuestionCommentCategory, QueryQuestionComment } from "../../generated/client/models";

/**
 * Interface representing component properties
 */
interface Props {
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
    return (
      <div className="queryCommentList">
        <h2 className="querySubTitle queryCommentListSubTitle">{ strings.panel.query.comments.title }</h2>
        { this.state.empty ? <p> { strings.panel.query.comments.noComments } </p> : null }
        <QueryCommentContainer onCommentsChanged={ this.onCommentsChanged } category={ this.props.category } className="queryCommentsContainer" canManageComments={ this.props.canManageComments } queryReplyId={ this.props.queryReplyId } parentId={ 0 } pageId={ this.props.pageId } panelId={ this.props.panelId } queryId={ this.props.queryId }/>
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
