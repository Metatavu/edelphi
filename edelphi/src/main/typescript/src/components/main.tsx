import * as React from "react";
import * as actions from "../actions";
import { StoreState, AccessToken } from "../types";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { Route } from "react-router";
import LiveView from "../screens/live-view";
import Reports from "../screens/reports";
import InviteUsersScreen from "../screens/invite-users-screen";

/**
 * Interface for component props
 */
interface Props {
  accessToken?: AccessToken
}

/**
 * Interface for component state
 */
interface State {

}

/**
 * Class for main page component
 */
class MainPage extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props props
   */
  constructor(props: Props) {
    super(props);
    this.state = {};
  }

  /**
   * Render method
   */
  public render() {
    if (!this.props.accessToken) {
      return null;
    }

    return (
      <div>
        <Route exact path="/panel/admin/liveview.page" component={ LiveView }/>
        <Route exact path="/panel/admin/reports.page" component={ Reports }/>
        <Route exact path="/panel/admin/inviteusers.page" component={ InviteUsersScreen }/>
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
  }
}

/**
 * Redux mapper for mapping component dispatches 
 * 
 * @param dispatch dispatch method
 */
function mapDispatchToProps(dispatch: Dispatch<actions.AppAction>) {
  return {
  };
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(MainPage) as any);
