import * as React from "react";
import * as actions from "../actions";
import { StoreState, AccessToken } from "../types";
import { connect } from "react-redux";
import Api from "edelphi-client";
import { QueryQuestionCommentsService } from "edelphi-client/dist/api/api";

/**
 * Interface representing component properties
 */
interface Props {
  queryId: number,
  accessToken?: AccessToken
}

/**
 * Interface representing component state
 */
interface State {
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

  public componentDidMount() {
  }

  /** 
   * Render edit pest view
   */
  public render() {
    if (!this.props.accessToken) {
      return null;
    }

    return (
      <div className="queryCommentList">
        <h2 className="querySubTitle queryCommentListSubTitle">Kommentit</h2>
        <div className="queryCommentsContainer">
          <div className="queryComment">
          </div>
        </div>
      </div>
    );
  }

  /**
   * Returns query question comments API
   * 
   * @returns query question comments API
   */
  private getQueryQuestionCommentsService(): QueryQuestionCommentsService | null {
    if (this.props.accessToken) {
      return Api.getQueryQuestionCommentsService(this.props.accessToken.token);
    }

    return null;
  }

}

/**
 * Redux mapper for mapping store state to component props
 * 
 * @param state store state
 */
function mapStateToProps(state: StoreState) {
  return {
    accessToken: state.accessToken
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
