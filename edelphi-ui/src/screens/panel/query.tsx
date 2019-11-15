import * as React from "react";
import BasicLayout from "../../components/basic-layout";

/**
 * Interface representing component properties
 */
interface Props {
  panelSlug: string,
  querySlug: string
}

/**
 * Interface representing component state
 */
interface State {
}

/**
 * WelcomePage component
 */
class WelcomePage extends React.Component<Props, State> {

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
   * Component did mount life-cycle handler
   */
  public componentDidMount = async () => {
    this.setState({
      loading: true
    });

    this.setState({
      loading: false
    });
  }

  /**
   * Component render method
   */
  public render() {
    return (
      <BasicLayout>
      </BasicLayout>
    );
  }
}

export default WelcomePage;