import * as React from "react";
import { connect } from "react-redux";
import { StoreState, AccessToken } from "../types";
import * as actions from "../actions";
import ErrorDialog from "./error-dialog";
import { KeycloakInstance } from "keycloak-js";
import Keycloak from "keycloak-js";

const config: any = {
  url: process.env.REACT_APP_KEYCLOAK_URL,
  realm: process.env.REACT_APP_KEYCLOAK_REALM,
  clientId: process.env.REACT_APP_KEYCLOAK_CLIENT_ID
};

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

  private keycloak: KeycloakInstance;
  private timer?: any;

  /**
   * Constructor
   * 
   * @param props props
   */
  constructor(props: Props) {
    super(props);
    this.keycloak = Keycloak(config);
    this.state = { };
  }

  /**
   * Component did mount life-cycle event
   */
  public componentDidMount = async () => {
    const auth = await this.keycloakInit();

    if (!auth) {
      window.location.reload();
    } else {
      const { token, tokenParsed } = this.keycloak;

      if (tokenParsed && tokenParsed.sub && token) {
        this.props.onAccessTokenUpdate({
          token: token,
          userId: tokenParsed.sub
        });
      }
      
      this.refreshAccessToken();

      this.timer = setInterval(() => {
        this.refreshAccessToken();
      }, 1000 * 60);
    };
  }

  /**
   * Initializes Keycloak client
   */
  private keycloakInit = () => {
    return new Promise((resolve) => {
      this.keycloak.init({ onLoad: "login-required" }).success((auth) => {
        resolve(auth);
      });
    });
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

    return this.props.accessToken ? this.props.children : null;
  }

  /**
   * Refreshes access token
   */
  private async refreshAccessToken() {
    try {
      const refreshed = await this.keycloak.updateToken(70);
      if (refreshed) {
        const { token, tokenParsed } = this.keycloak;

        if (tokenParsed && tokenParsed.sub && token) {
          this.props.onAccessTokenUpdate({
            token: token,
            userId: tokenParsed.sub
          });
        }
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