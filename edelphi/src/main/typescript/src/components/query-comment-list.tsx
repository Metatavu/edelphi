import * as React from "react";
import * as actions from "../actions";
import { StoreState, AccessToken } from "../types";
import { connect } from "react-redux";

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
