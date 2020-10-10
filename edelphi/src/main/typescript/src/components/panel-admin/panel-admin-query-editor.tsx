import * as React from "react";
import * as actions from "../../actions";
import { StoreState, CommandEvent, EditPageLegacyPageData } from "../../types";
import { connect } from "react-redux";
import PanelAdminQueryPageCommentOptionsEditor from "./panel-admin-query-page-comment-options-editor";
import PanelAdminQueryCommentOptionsEditor from "./panel-admin-query-comment-options-editor";
import PanelAdminQueryPageLive2dOptionsEditor from "./panel-admin-query-page-live2d-options-editor";
import { Confirm, Modal, Header, Button, Icon } from "semantic-ui-react";
import strings from "../../localization/strings";
import * as QRCode from "qrcode";
import PanelAdminQueryCopyDialog from "./panel-admin-query-copy-dialog";
import Api from "../../api";
import LegacyUtils from "../../utils/legacy-utils";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: string,
  panelId: number,
  queryId: number,
  openCopyDialog: boolean;
}

/**
 * Interface representing component state
 */
interface State {
  queryCommentOptionsOpen: boolean,
  queryCommentOptionsHasAnswers: boolean,
  removeQueryAnswersOpen: boolean,
  pageCommentOptionsOpen: boolean,
  pageLive2dOptionsOpen: boolean,
  anonymousLoginDialogOpen: boolean,
  copyQueryDialogOpen: boolean,
  pageData?: EditPageLegacyPageData,
  pageId: number,
  anonymousLoginQrCode?: string
  anonymousLoginQrCodePrintable?: string
}

/**
 * React component for comment editor
 */
class PanelAdminQueryEditor extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      queryCommentOptionsOpen: false,
      queryCommentOptionsHasAnswers: false,
      removeQueryAnswersOpen: false,
      pageCommentOptionsOpen: false,
      pageLive2dOptionsOpen: false,
      anonymousLoginDialogOpen: false,
      copyQueryDialogOpen: this.props.openCopyDialog,
      pageId: 0
    };
  }
  
  /**
   * Component did update life-cycle event
   */
  public async componentDidMount() {
    const [ anonymousLoginQrCode, anonymousLoginQrCodePrintable ] = await Promise.all([
      QRCode.toDataURL(this.getAnonymousLoginUrl(), {
        margin: 0
      }),
      QRCode.toDataURL(this.getAnonymousLoginUrl(), {
        margin: 0,
        scale: 12
      })
    ]);

    this.setState({
      anonymousLoginQrCode: anonymousLoginQrCode, 
      anonymousLoginQrCodePrintable: anonymousLoginQrCodePrintable
    });
  }

  /**
   * Component will mount life-cycle event
   */
  public componentWillMount() {
    LegacyUtils.addCommandListener(this.onReactCommand);  
  }
  
  /**
   * Component will unmount life-cycle event
   */
  public async componentWillUnmount() {
    LegacyUtils.removeCommandListener(this.onReactCommand);
  }
  
  /** 
   * Component render method
   */
  public render() {
    return (
      <div>
        { this.renderQueryCommentOptionsEditor() }
        { this.renderPageCommentOptionsEditor() }
        { this.renderLive2dOptionsEditor() }
        { this.renderRemoveQueryAnswersDialog() }
        { this.renderAnonymousLoginDialog() }
        { this.renderCopyQueryDialog() }
      </div>
    );
  }

  /**
   * Renders remove query answer confirm dialog
   */
  private renderRemoveQueryAnswersDialog = () => {
    return (
      <Confirm 
        content={ strings.panelAdmin.queryEditor.removeQueryAnswersConfirm } 
        open={ this.state.removeQueryAnswersOpen }
        onConfirm={ this.onRemoveQueryAnswersDialogConfirm }
        onCancel={ this.onRemoveQueryAnswersDialogCancel }/>
    );
  }

  /**
   * Renders anonymous login link dialog
   */
  private renderAnonymousLoginDialog = () => {
    return (
      <Modal
        open={this.state.anonymousLoginDialogOpen }
        onClose={ this.onAnonymousLoginDialogClose }>
        <Header icon="user secret" content={ strings.panelAdmin.queryEditor.anonymousLoginDialog.title } />
        <Modal.Content>
          <h3>{ strings.panelAdmin.queryEditor.anonymousLoginDialog.helpText }</h3>
          <p>{ strings.panelAdmin.queryEditor.anonymousLoginDialog.hintText }</p>
          { this.renderAnonymousLoginDialogContents() }
        </Modal.Content>
        <Modal.Actions>
          <Button color="green" onClick={ this.onAnonymousLoginDialogCloseButtonClick } inverted>
            <Icon name="checkmark" /> { strings.panelAdmin.queryEditor.anonymousLoginDialog.okButton }
          </Button>
        </Modal.Actions>
      </Modal>
    );
  }

  /**
   * Renders anonymous login QR code 
   */
  private renderAnonymousLoginDialogContents = () => {
    return (
      <div>
        { this.renderAnonymousLoginQrImage() }
        <div style={{ float: "right" }} >
          { this.renderAnonymousLoginDownloadLink() }
          { this.renderAnonymousLoginDownloadPrintableLink() }
        </div>
        <label style={{ paddingTop: "5px", paddingBottom: "5px", display: "block" }}>{ strings.panelAdmin.queryEditor.anonymousLoginDialog.linkLabel }</label>
        <input style={{ width: "100%"}} type="url" readOnly value={ this.getAnonymousLoginUrl() } />
      </div>
    );
  }

  /**
   * Renders anonymous login QR image
   */
  private renderAnonymousLoginQrImage = () => {
    if (!this.state.anonymousLoginQrCode) {
      return null;
    }

    return (
      <img src={ this.state.anonymousLoginQrCode } />
    );
  }

  /**
   * Renders anonymous login download QR link
   */
  private renderAnonymousLoginDownloadLink = () => {
    if (!this.state.anonymousLoginQrCode) {
      return null;
    }

    return (
      <a href={ this.state.anonymousLoginQrCode } target="_blank" download="qrcode.png">{ strings.panelAdmin.queryEditor.anonymousLoginDialog.downloadImage }</a>
    );
  }

  /**
   * Renders anonymous login download prinable QR link
   */
  private renderAnonymousLoginDownloadPrintableLink = () => {
    if (!this.state.anonymousLoginQrCodePrintable) {
      return null;
    }
    
    return (
      <a href={ this.state.anonymousLoginQrCodePrintable } target="_blank" style={{ paddingLeft: "5px" }} download="qrcodeprintable.png">{ strings.panelAdmin.queryEditor.anonymousLoginDialog.downloadPrintableImage }</a>
    );
  }

  /**
   * Renders copy query dialog
   */
  private renderCopyQueryDialog = () => {
    if (!this.props.accessToken) {
      return null;
    }

    return (
      <PanelAdminQueryCopyDialog 
        queryId={ this.props.queryId } 
        panelId={ this.props.panelId} 
        accessToken={ this.props.accessToken } 
        open={ this.state.copyQueryDialogOpen } 
        onClose={ this.onCopyQueryDialogClose }/>
    );
  }

  /**
   * Event handler for react command events
   * 
   * @param event event
   */
  private onReactCommand = async (event: CommandEvent) => {
    switch (event.detail.command) {
      case "edit-query-comment-options":
        const pageDatas = event.detail.data.pageDatas;
        const hasAnswers = !!pageDatas.find(pageData => {
          return "true" == pageData.hasAnswers;
        });

        this.setState({
          queryCommentOptionsOpen: true,
          queryCommentOptionsHasAnswers: hasAnswers
        });
      break;
      case "edit-page-comment-options":
        this.setState({
          pageId: event.detail.data.pageData.id,
          pageData: event.detail.data.pageData,
          pageCommentOptionsOpen: true
        });
      break;
      case "edit-page-live2d-options":
        this.setState({
          pageId: event.detail.data.pageData.id,
          pageData: event.detail.data.pageData,
          pageLive2dOptionsOpen: true
        });
      break;
      case "remove-query-answers":
        this.setState({
          removeQueryAnswersOpen: true
        });
      break;
      case "open-anonymous-login-dialog":
        this.setState({
          anonymousLoginDialogOpen: true
        });
      break;
      case "open-copy-query-dialog":
        this.setState({
          copyQueryDialogOpen: true
        });
      break;
    }
  }

  /**
   * Renders query comment options editor
   */
  private renderQueryCommentOptionsEditor() {
    return (
      <PanelAdminQueryCommentOptionsEditor 
        panelId={ this.props.panelId} 
        open={ this.state.queryCommentOptionsOpen } 
        queryId={ this.props.queryId } 
        hasAnswers={ this.state.queryCommentOptionsHasAnswers }
        onClose={ this.onQueryCommentOptionsEditorClose }/>
    );
  }

  /**
   * Renders page comment options editor
   */
  private renderPageCommentOptionsEditor() {
    if (!this.state.pageData) {
      return null;
    }

    return <PanelAdminQueryPageCommentOptionsEditor pageData={ this.state.pageData } open={ this.state.pageCommentOptionsOpen } pageId={ this.state.pageId } panelId={ this.props.panelId} queryId={ this.props.queryId } onClose={ this.onPageCommentOptionsEditorClose }/>
  }

  /**
   * Renders Live2d options editor
   */
  private renderLive2dOptionsEditor() {
    if (!this.state.pageData) {
      return null;
    }

    return <PanelAdminQueryPageLive2dOptionsEditor pageData={ this.state.pageData } open={ this.state.pageLive2dOptionsOpen } pageId={ this.state.pageId } panelId={ this.props.panelId} queryId={ this.props.queryId } onClose={ this.onLive2dOptionsEditorClose }/>
  }

  /**
   * Removes query answers
   */
  private removeQueryAnswers = async () => {
    const queryQuestionAnswersApi = this.getQueryQuestionAnswersApi();
    await queryQuestionAnswersApi.deleteQueryQuestionAnswers({
      panelId: this.props.panelId,
      queryId: this.props.queryId
    });
  }

  /**
   * Returns URL for anonymous login
   * 
   * @returns URL for anonymous login
   */
  private getAnonymousLoginUrl = () => {
    const location = window.location;
    return `${location.protocol}//${location.host}/panel/anonymouslogin.page?panelId=${this.props.panelId}&queryId=${this.props.queryId}`;
  }

  /**
   * Returns query question answers service
   * 
   * @returns query question answers service
   */
  private getQueryQuestionAnswersApi = () => {
    return Api.getQueryQuestionAnswersApi(this.props.accessToken);
  }

  /**
   * Event handler for anonymous login dialog close event
   */
  private onAnonymousLoginDialogClose = () => {
    this.setState({
      anonymousLoginDialogOpen: false
    });
  }

  /**
   * Event handler for anonymous login dialog close button click
   */
  private onAnonymousLoginDialogCloseButtonClick = () => {
    this.setState({
      anonymousLoginDialogOpen: false
    });
  }

  /**
   * Event handler for copy query dialog close event
   */
  private onCopyQueryDialogClose = () => {
    this.setState({
      copyQueryDialogOpen: false
    });
  }

  /**
   * Event handler for remove query answers dialog confirm
   */
  private onRemoveQueryAnswersDialogConfirm = async () => {
    this.setState({ removeQueryAnswersOpen: false }); 
    await this.removeQueryAnswers();
  }

  /**
   * Event handler for remove query answers dialog cancel
   */
  private onRemoveQueryAnswersDialogCancel = () => {
    this.setState({ removeQueryAnswersOpen: false }); 
  }

  /**
   * Event handler for comment option close event
   */
  private onQueryCommentOptionsEditorClose = () => {
    this.setState({
      queryCommentOptionsOpen: false
    });
  }

  /**
   * Event handler for comment option close event
   */
  private onPageCommentOptionsEditorClose = () => {
    this.setState({
      pageCommentOptionsOpen: false
    });
  }

  /**
   * Event handler for live2d option close event
   */
  private onLive2dOptionsEditorClose = () => {
    this.setState({
      pageLive2dOptionsOpen: false
    });
  }

}

/**
 * Redux mapper for mapping store state to component props
 * 
 * @param state store state
 */
function mapStateToProps(state: StoreState) {
  return {
    accessToken: state.accessToken!.token
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

export default connect(mapStateToProps, mapDispatchToProps)(PanelAdminQueryEditor);