import * as React from "react";
import * as _ from "lodash";
import { Modal, Button, Loader, Dimmer, Icon, Input, InputOnChangeData, DropdownItemProps, Select, DropdownProps } from "semantic-ui-react";
import { Panel } from "../../generated/client/models";
import strings from "../../localization/strings";
import Api from "../../api";
import ErrorDialog from "../../components/error-dialog";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: string;
  panelId: number;
  queryId: number;
  open: boolean;
  onClose: () => void;
}

/**
 * Interface representing component state
 */
interface State {
  loading: boolean;
  panels: Panel[];
  name: string;
  targetPanelId: number;
  copyData: boolean;
  emailDialogVisible: boolean;
  validationError?: string;
  error?: Error;
}

/**
 * React component for copy query dialog
 */
export default class PanelAdminQueryCopyDialog extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false,
      panels: [],
      name: strings.panelAdmin.queryEditor.copyQueryDialog.newName,
      targetPanelId: props.panelId,
      copyData: false,
      emailDialogVisible: false
    };
  }
  
  /**
   * Component did mount life-cycle event
   */
  public async componentDidMount() {
    const { accessToken } = this.props;

    this.setState({
      loading: true
    });

    const panels = await Api.getPanelsApi(accessToken).listPanels({
      managedOnly: true
    });
    
    this.setState({
      panels: panels,
      loading: false
    });
  }

  /** 
   * Component render method
   */
  public render() {
    const { error, validationError, emailDialogVisible } = this.state;

    if (error) {
      return this.renderErrorDialog();
    }

    if (validationError) {
      return this.renderValidationErrorModal();
    }
    
    if (emailDialogVisible) {
      return this.renderEmailModal();
    }

    return this.renderCopyModal();
  }

  /**
   * Renders copy modal
   */
  private renderCopyModal = () => {
    return (
      <Modal open={this.props.open} onClose={this.onModalClose}>
        <Modal.Header>{ strings.panelAdmin.queryEditor.copyQueryDialog.title }</Modal.Header>
        <Modal.Content>
          { this.renderCopyModalContent() }
        </Modal.Content>
        <Modal.Actions>
          <Button color="green" onClick={ this.onCopyButtonClick } inverted>
            <Icon name="checkmark" /> { strings.panelAdmin.queryEditor.copyQueryDialog.okButton }
          </Button>
          <Button color="red" onClick={ this.onCancelButtonClick } inverted>
            { strings.panelAdmin.queryEditor.copyQueryDialog.cancelButton }
          </Button>
        </Modal.Actions>
      </Modal>
    );
  }

  /**
   * Renders an email modal
   */
  private renderEmailModal = () => {
    return (
      <Modal open={this.state.emailDialogVisible} >
        <Modal.Header> { strings.panelAdmin.queryEditor.copyQueryDialog.emailDialogTitle } </Modal.Header>
        <Modal.Content> 
          <p>{ strings.panelAdmin.queryEditor.copyQueryDialog.emailDialogMessage }</p>
        </Modal.Content>
        <Modal.Actions>
          <Button color='green' onClick={ this.onEmailDialogClose } inverted>
            <Icon name='checkmark' /> 
            { strings.generic.ok }
          </Button>
        </Modal.Actions>
      </Modal>
    );
  }

  /**
   * Renders validation error modal
   */
  private renderValidationErrorModal = () => {
    const { validationError } = this.state;

    return (
      <Modal open={ true } >
        <Modal.Header>
          <Icon name="exclamation" color="yellow" /> { strings.panelAdmin.queryEditor.copyQueryDialog.validationErrorDialogTitle }
        </Modal.Header>
        <Modal.Content> 
          <p><b>{ validationError }</b></p>
        </Modal.Content>
        <Modal.Actions>
          <Button
            color="green"
            onClick={ this.onValidationErrorDialogClose }
            inverted
          >
            <Icon name='checkmark' /> 
            { strings.generic.ok }
          </Button>
        </Modal.Actions>
      </Modal>
    );
  }

  /**
   * Renders modal's content
   */
  private renderCopyModalContent = () => {
    if (this.state.loading) {
      return (
        <div style={{ minHeight: "200px" }}>
          <Dimmer active inverted>
            <Loader inverted/>
          </Dimmer>
        </div>
      );
    }

    const panelOptions: DropdownItemProps[] = this.state.panels.map((panel) => {
      return {
        key: panel.id,
        value: panel.id,
        text: panel.name
      }
    });

    const dataOptions: DropdownItemProps[] = [
      {
        key: "withdata",
        value: true,
        text: strings.panelAdmin.queryEditor.copyQueryDialog.withDataOption
      },
      {
        key: "withoutdata",
        value: false,
        text: strings.panelAdmin.queryEditor.copyQueryDialog.withoutDataOption
      }
    ];

    return (
      <div>
        <p>{ strings.panelAdmin.queryEditor.copyQueryDialog.helpText }</p>
        <Select value={ this.state.copyData } options={ dataOptions } onChange={ this.onDataChange }/>
        <p><label>{ strings.panelAdmin.queryEditor.copyQueryDialog.nameLabel }</label></p>
        <Input style={{ width: "100%" }} value={ this.state.name } onChange={ this.onNameChange }/>
        <p><label>{ strings.panelAdmin.queryEditor.copyQueryDialog.targetPanelLabel }</label></p>
        <Select value={ this.state.targetPanelId } options={ panelOptions } onChange={ this.onTargetPanelChange }/>
      </div>
    );
  }

  /**
   * Renders error dialog
   */
  private renderErrorDialog = () => {
    const { error } = this.state;

    if (!error) {
      return null;
    }

    return (
      <ErrorDialog
          error={ error }
          onClose={ this.onErrorDialogClose }
      />
    )
  }

  /**
   * Event handler for email dialog close
   */
  private onEmailDialogClose = () => {
    this.setState({      
      emailDialogVisible: false 
    });

    this.props.onClose();
  }

  /**
   * Event handler for name change event
   * 
   * @param event event
   * @param data data
   */
  private onNameChange = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
    this.setState({
      name: data.value
    });
  }

  /**
   * Event for visible dropdown change
   * 
   * @param event event
   * @param data data
   */
  private onTargetPanelChange = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
    this.setState({
      targetPanelId: data.value as number
    });
  }

  /**
   * Event for visible dropdown change
   * 
   * @param event event
   * @param data data
   */
  private onDataChange = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
    this.setState({
      copyData: data.value as boolean
    });
  }

  /**
   * Event handler for modal close
   */
  private onModalClose = () => {
    this.props.onClose();
  }

  /**
   * Event handler for save click
   */
  private onCopyButtonClick  = async () => {
    const { accessToken, panelId, queryId } = this.props;
    const { copyData, name, targetPanelId } = this.state;

    this.setState({
      loading: true
    });

    try {
        const queriesApi = Api.getQueriesApi(accessToken);
        await queriesApi.copyQuery({
          copyData: copyData,
          newName: name,
          panelId: panelId,
          queryId: queryId,
          targetPanelId: targetPanelId
        });

        this.setState({
          emailDialogVisible: true
        });
    } catch (e) {
      if (e instanceof Response && e.status == 400) {
        const body = await e.json();
        const validationError = body?.message || "Unknonwn error";
        this.setState({
          validationError: validationError
        });
      } else {
        this.setState({
          error: e
        });
      }
    }

    this.setState({
      loading: false
    });
  }
  
  /**
   * Event handler for close click
   */
  private onCancelButtonClick  = async () => {
    this.props.onClose();
  }

  /**
   * Event handler for error dialog close
   */
  private onErrorDialogClose = () => {
    this.setState({ 
      error: undefined 
    });
  }

  /**
   * Event handler for error dialog close
   */
  private onValidationErrorDialogClose = () => {
    this.setState({ 
      validationError: undefined
    });
  }

}
