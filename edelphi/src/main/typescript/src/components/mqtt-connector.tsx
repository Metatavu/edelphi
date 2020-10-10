import * as React from "react";
import * as actions from "../actions";
import { MqttConfig, MqttConnection, mqttConnection } from "../mqtt";
import { StoreState } from "../types";
import { Dispatch } from "redux";
import { connect } from "react-redux";

/**
 * Component props
 */
interface Props {
  accessToken?: string,
  locale: string
}

/**
 * Component state
 */
interface State {
  options?: MqttConfig
}

/**
 * MQTT connector component
 */
class MqttConnector extends React.Component<Props, State> {

  private connection: MqttConnection;
  
  /**
   * Constructor
   * 
   * @param props props
   */
  constructor(props: Props) {
    super(props);
    this.connection = mqttConnection;
    this.state = { };
  }

  /**
   * Component did update life-cycle event
   * 
   * @param prevProps previous props
   * @param prevState previous state
   */
  public async componentDidUpdate(prevProps: Props, prevState: State) {
    if (this.props.accessToken !== prevProps.accessToken) {
      this.setState({
        options: await this.getConnectionOptions()
      });
    }

    if (this.state.options && !this.connection.isConnected() && !this.connection.isConnecting()) {
      if (this.state.options) {
        this.connection.connect(this.state.options);
      }
    }
  }

  /**
   * Component will unmount life-cycle event
   */
  public componentWillUnmount() {
    this.connection.disconnect();
  }

  /**
   * Component did mount life-cycle event
   */
  public async componentDidMount() {
    this.setState({
      options: await this.getConnectionOptions()
    });
  }

  /**
   * Component render method
   * 
   * @return returns child components
   */
  public render() {
    return this.props.children;
  }

  /**
   * Loads MQTT connection options from the server
   */
  private async getConnectionOptions(): Promise<MqttConfig | undefined> {
    if (!this.props.accessToken) {
      return undefined;
    }

    return (await fetch("/api/v1/system/mqttSettings", {
      headers: {
        "Authorization": `Bearer ${this.props.accessToken}`
      }
    })).json();
  }
}

/**
 * Redux mapper for mapping store state to component props
 * 
 * @param state store state
 */
function mapStateToProps(state: StoreState) {
  return {
    accessToken: state.accessToken?.token,
    locale: state.locale
  };
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

export default connect(mapStateToProps, mapDispatchToProps)(MqttConnector);