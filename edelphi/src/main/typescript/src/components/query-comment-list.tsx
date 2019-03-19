import * as React from "react";
import * as actions from "../actions";
import strings from "../localization/strings";
import QueryCommentContainer from "./query-comment-container";
import { StoreState } from "../types";
import { connect } from "react-redux";
import { QueryQuestionComment } from "edelphi-client";

/**
 * Interface representing component properties
 */
interface Props {
  queryId: number,
  panelId: number,
  pageId: number,
  accessToken?: string,
  locale: string
}

/**
 * Interface representing component state
 */
interface State {
  comments?: QueryQuestionComment[]
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
    this.state = { };
  }

  /** 
   * Render edit pest view
   */
  public render() {
    return (
      <div className="queryCommentList">
        <h2 className="querySubTitle queryCommentListSubTitle">{ strings.panel.query.comments.title }</h2>
        <QueryCommentContainer className="queryCommentsContainer" parentId={ 0 } pageId={ this.props.pageId } panelId={ this.props.panelId } queryId={ this.props.queryId }/>
      </div>
    );
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
