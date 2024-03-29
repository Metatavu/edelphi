import * as React from "react";
import { Modal, Button, Icon } from "semantic-ui-react";
import strings from "../localization/strings";
import moment from "moment";

/**
 * Interface representing component properties
 */
interface Props {
  error: Error | unknown;
  onClose: () => void
}

/**
 * Interface representing component state
 */
interface State {

}

/**
 * React component for generic error dialog
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
      <Modal size="large" open={ true }>
          <Modal.Header><Icon name="exclamation" color="red" /> { strings.errorDialog.header }</Modal.Header>
          <Modal.Content>
            <p> { strings.errorDialog.reloadPage } </p>
            <p> { strings.errorDialog.unsavedContents } </p>
            <p> { strings.errorDialog.reportIssue } </p>
            <p>
              { strings.errorDialog.technicalDetails }<br/>
              <br/>
              { strings.formatString(strings.errorDialog.time, this.getTime()) }<br/>
              { strings.formatString(strings.errorDialog.url, this.getURL()) }<br/>
              { strings.errorDialog.errorMessage }<br/>
              <br/>
              <pre style={{ fontSize: "10px" }}>{ this.getErrorMessage() }</pre>
            </p>
          </Modal.Content>
          <Modal.Actions>
            <Button
              positive
              icon='redo alternate'
              labelPosition='right'
              onClick={ this.onReloadClick }
              content={ strings.errorDialog.reload }
            />
            <Button
              secondary
              icon='checkmark'
              labelPosition='right'
              onClick={ this.props.onClose }
              content={ strings.errorDialog.close }
            />
          </Modal.Actions>
        </Modal>
    );
  }

  /**
   * Returns current time
   * 
   * @returns current time
   */
  private getTime = () => {
    return moment().format();
  }

  /**
   * Returns current window URL
   * 
   * @returns current window URL
   */
  private getURL = () => {
    return window.location.href;
  }

  /**
   * Returns an error message
   * 
   * @returns an error message
   */
  private getErrorMessage = () => {
    const { error } = this.props;

    if (error instanceof Error) {
      return error.message || "";
    }

    return "Unknown error";
  }

  /**
   * Reload button click event handler
   */
  private onReloadClick = () => {
    window.location.reload();
  }

}