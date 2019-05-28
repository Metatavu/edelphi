import * as React from "react";
import { Modal, Header, Button } from "semantic-ui-react";
import strings from "../localization/strings";

/**
 * Interface representing component properties
 */
interface Props {
  error: Error,
  onClose: () => void
}

/**
 * Interface representing component state
 */
interface State {

}

/**
 * React component for live 2d chart
 */
export default class ErrorDialog extends React.Component<Props, State> {

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
   * Component render method
   */
  public render() {
    return (
      <Modal open={ true } basic size='small'>
        <Header content={ strings.errorDialog.header } />
        <Modal.Content>
          <p>
            { strings.errorDialog.errorOccurred } { this.props.error.message }
          </p>
        </Modal.Content>
        <Modal.Actions>
          <Button onClick={ this.props.onClose }>
            { strings.errorDialog.close }
          </Button>
        </Modal.Actions>
      </Modal>
    );
  }

}