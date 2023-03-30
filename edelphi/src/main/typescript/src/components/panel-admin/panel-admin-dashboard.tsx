import * as React from "react";
import * as actions from "../../actions";
import { StoreState, CommandEvent, AccessToken } from "../../types";
import { connect } from "react-redux";
import { Confirm, Dimmer, Loader, Modal } from "semantic-ui-react";
import strings from "../../localization/strings";
import Api from "../../api";
import LegacyUtils from "../../utils/legacy-utils";
import ErrorDialog from "../error-dialog";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken?: AccessToken;
  panelId: number;
}

/**
 * Interface representing component state
 */
interface State {
  panelName?: string;
  deletePanelDialogOpen: boolean;
  deletingPanel: boolean;
  error?: Error | unknown;
}

/**
 * React component for panel admin dashboard
 */
class PanelAdminDashboard extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      deletePanelDialogOpen: false,
      deletingPanel: false
    };
  }

  /**
   * Component will mount life-cycle event
   */
  public componentWillMount = () => {
    LegacyUtils.addCommandListener(this.onReactCommand);
  }

  /**
   * Component did mount life cycle handler
   */
  public componentDidMount = () => {
    this.loadPanel();
  }
  
  /**
   * Component did update life-cycle event
   */
  public async componentDidUpdate() {
    if (!this.state.panelName) {
      this.loadPanel();
    }
  }
  
  /**
   * Component will unmount life-cycle event
   */
  public componentWillUnmount = async () => {
    LegacyUtils.removeCommandListener(this.onReactCommand);
  }
  
  /** 
   * Component render method
   */
  public render() {
    const { error } = this.state;

    if (error) {
      return ( 
        <ErrorDialog 
          error={ error } 
          onClose={ () => this.setState({ error: undefined }) } 
        />
      ); 
    }

    return (
      <div>
        { this.renderDeletingPanelDimmer() }
        { this.renderDeletePanelDialog() }
      </div>
    );
  }

  /**
   * Renders remove query answer confirm dialog
   */
  private renderDeletePanelDialog = () => {
    const { panelName, deletePanelDialogOpen } = this.state;

    if (!panelName) {
      return null;
    }
    
    return (
      <Confirm 
        content={ 
          <Modal.Content>
            <p> { strings.formatString(strings.panelAdmin.dashboard.deletePanelDialogConfirmQuestion, panelName) } </p>
            <p> { strings.panelAdmin.dashboard.deletePanelDialogConfirmDetails } </p>
            <p> <b> { strings.panelAdmin.dashboard.deletePanelDialogConfirmDestructive } </b> </p>
          </Modal.Content>
        } 
        open={ deletePanelDialogOpen }
        onConfirm={ this.onDeletePanelConfirm }
        onCancel={ this.onDeletePanelCancel }
      />
    );
  }

  /**
   * Renders deleeting panel dimmer
   */
  private renderDeletingPanelDimmer = () => {
    const { deletingPanel } = this.state;

    if (!deletingPanel) {
      return null;
    }
    
    return (
      <Dimmer active>
        <Loader>
          { strings.panelAdmin.dashboard.deletingPanel }
        </Loader>
      </Dimmer>
    );
  }

  /**
   * Loads panel details
   */
  private loadPanel = async () => {
    const { panelName } = this.state;
    if (panelName) {
      return;
    }

    const { accessToken, panelId } = this.props;

    if (!accessToken) {
      return;
    }

    const panel = await Api.getPanelsApi(accessToken.token).findPanel({
      panelId: panelId
    });

    this.setState({
      panelName: panel.name
    });
  }

  /**
   * Deletes panel
   */
  private deletePanel = async () => {
    const { accessToken, panelId } = this.props;

    if (!accessToken) {
      return;
    }

    this.setState({
      deletingPanel: true,
      deletePanelDialogOpen: false
    });

    try {
      await Api.getPanelsApi(accessToken.token).deletePanel({
        panelId: panelId
      });

      this.setState({
        deletingPanel: false
      });
  
      window.location.href = '/';
    } catch (e) {
      this.setState({
        error: e
      });
    }
  }

  /**
   * Event handler for delete panel confirm. Method deletes the panel and redirects user to front page
   */
  private onDeletePanelConfirm = () => {
    this.deletePanel();
  }

  /**
   * Event handler for delete panel cancel
   */
  private onDeletePanelCancel = () => {
    this.setState({
      deletePanelDialogOpen: false
    });
  }

  /**
   * Event handler for react command events
   * 
   * @param event event
   */
  private onReactCommand = async (event: CommandEvent) => {
    switch (event.detail.command) {
      case "open-delete-panel-dialog":
        this.setState({
          deletePanelDialogOpen: true
        });
      break;
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
  return { };
}

export default connect(mapStateToProps, mapDispatchToProps)(PanelAdminDashboard);
