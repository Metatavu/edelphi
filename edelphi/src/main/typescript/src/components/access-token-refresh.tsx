import * as React from "react";
import { connect } from "react-redux";
import { StoreState, AccessToken } from "../types";
import * as actions from "../actions";
import ErrorDialog from "./error-dialog";
import * as moment from "moment";

/**
 * Component props
 */
interface Props {
  accessToken?: AccessToken,
  onAccessTokenUpdate: (accessToken: AccessToken) => void
};

/**
 * Component state
 */
interface State {
  error?: Error
}

const EXPIRE_SLACK = 60 * 1000;

interface AccessTokenResponse {
  token?: string
  expires?: Date,
  userId?: string
  unauthorized?: "true"
}

/**
 * Component for keeping authentication token fresh
 */
class AccessTokenRefresh extends React.Component<Props, State> {

  private timer?: any;

  /**
   * Constructor
   * 
   * @param props props
   */
  constructor(props: Props) {
    super(props);
    this.state = {
    };
  }

  /**
   * Component did mount life-cycle event
   */
  public componentDidMount() {
    this.refreshAccessToken();

    this.timer = setInterval(() => {
      this.refreshAccessToken();
    }, 5000);
  }

  /**
   * Component will unmount life-cycle event
   */
  public componentWillUnmount() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  /**
   * Component render method
   */
  public render() {
    if (this.state.error) {
      return <ErrorDialog error={ this.state.error } onClose={ () => this.setState({ error: undefined }) } /> 
    }

    return null;
  }

  /**
   * Refreshes access token
   */
  private async refreshAccessToken() {
    try {
      if (!this.getNeedsRefresh()) {
        return;
      }

      const init: RequestInit = {
        credentials: "same-origin"
      };
      
      const response: AccessTokenResponse = await (await fetch("/system/accesstoken.json", init)).json();
      if (response && response.expires && response.token && response.userId) {
        this.props.onAccessTokenUpdate({
          expires: response.expires,
          token: response.token,
          userId: response.userId
        });
      } else if (response.unauthorized == "true" && this.isLegacyUILoggedIn()) {
        window.location.href = "/logout.page";
      }
    } catch (e) {
      this.setState({
        error: e
      });
    }
  }

  /**
   * Returns whether the legacy UI is logged in or not
   * 
   * @returns whether the legacy UI is logged in or not
   */
  private isLegacyUILoggedIn = () => {
    return true === (window as any).isLoggedIn;
  }

  /**
   * Returns true if access token needs refreshing
   * 
   * @returns true if access token needs refreshing
   */
  private getNeedsRefresh = (): boolean => { 
    if (!this.props.accessToken || !this.props.accessToken.token) {
      return true;
    } 

    return moment(this.props.accessToken.expires).isAfter(moment().subtract(EXPIRE_SLACK, "seconds"));
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
  return {
    onAccessTokenUpdate: (accessToken: AccessToken) => dispatch(actions.accessTokenUpdate(accessToken))
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(AccessTokenRefresh);