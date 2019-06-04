import * as React from "react";
import * as actions from "../../actions";
import strings from "../../localization/strings";
import QueryCommentContainer from "./query-comment-container";
import { StoreState } from "../../types";
import { connect } from "react-redux";
import { QueryQuestionCommentCategory, QueryQuestionComment } from "edelphi-client";

/**
 * Interface representing component properties
 */
interface Props {
  queryId: number,
  panelId: number,
  pageId: number,
  queryReplyId: number,
  accessToken?: string,
  locale: string,
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

export default connect(mapStateToProps, mapDispatchToProps)(QueryCommentList);
