import * as React from "react";
import { connect } from "react-redux";
import { StoreState, AccessToken } from "../types";
import * as actions from "../actions";
import ErrorDialog from "./error-dialog";

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
      const init: RequestInit = {
        credentials: "same-origin"
      };
      
      const response: AccessToken = await (await fetch("/system/accesstoken.json", init)).json();

      if (response && response.expires && response.token && response.userId) {
        this.props.onAccessTokenUpdate(response);
      } 
    } catch (e) {
      this.setState({
        error: e
      });
    }
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