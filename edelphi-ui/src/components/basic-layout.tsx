import * as React from "react";
import { WithStyles, withStyles } from "@material-ui/core";

/**
 * Interface representing component properties
 */
interface Props extends WithStyles<typeof styles> {

}

/**
 * Interface representing component state
 */
interface State {
  loading: boolean
}

/**
 * React component for basic application layout
 */
class BasicLayout extends React.Component<Props, State> {

  /**
   * Constructor
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  public componentDidMount = async () => {
    this.setState({
      loading: true
    });
    
    this.setState({
      loading: false
    });
  }

  /**
   * Render basic layout
   */
  public render() {
    return (
      <div className="content">
        { this.props.children }
      </div>
    );
  }

}

export default withStyles(styles)(BasicLayout);