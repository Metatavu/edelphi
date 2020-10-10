import * as React from "react";
import * as actions from "../actions";
import * as _ from "lodash";
import { StoreState, AccessToken } from "../types";
import { connect } from "react-redux";
import * as queryString from "query-string";
import InviteUsers from "../components/invite-users/invite-users";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken?: AccessToken,
  location: any
}

/**
 * Interface representing component state
 */
interface State {
}

/**
 * React component for invite users screen
 */
class InviteUsersScreen extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
    };
  }

  /** 
   * Component render method
   */
  public render() {
    const { accessToken } = this.props;
    if (!accessToken) {
      return null;
    }

    const queryParams = queryString.parse(this.props.location.search);    
    const panelId = parseInt(queryParams.panelId as string);
    
    return ( 
      <InviteUsers 
        accessToken={ accessToken }
        panelId={ panelId }      
      />
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
    accessToken: state.accessToken,
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

export default connect(mapStateToProps, mapDispatchToProps)(InviteUsersScreen);