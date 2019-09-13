import * as React from "react";
import * as actions from "../../actions";
import { StoreState, CommandEvent, EditPageLegacyPageData } from "../../types";
import { connect } from "react-redux";
import PanelAdminQueryPageCommentOptionsEditor from "./panel-admin-query-page-comment-options-editor";
import PanelAdminQueryCommentOptionsEditor from "./panel-admin-query-comment-options-editor";
import PanelAdminQueryPageLive2dOptionsEditor from "./panel-admin-query-page-live2d-options-editor";
import { Confirm } from "semantic-ui-react";
import strings from "../../localization/strings";
import Api from "edelphi-client";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: string,
  panelId: number,
  queryId: number
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
  pageData?: EditPageLegacyPageData,
  pageId: number
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
      pageId: 0
    };
  }
  
  /**
   * Component did update life-cycle event
   */
  public async componentDidMount() {
  }

  /**
   * Component will mount life-cycle event
   */
  public componentWillMount() {
    document.addEventListener("react-command", this.onReactCommand);  
  }
  
  /**
   * Component will unmount life-cycle event
   */
  public async componentWillUnmount() {
    document.removeEventListener("react-command", this.onReactCommand);
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
    const queryQuestionAnswersService = this.getQueryQuestionAnswersService();
    await queryQuestionAnswersService.deleteQueryQuestionAnswers(this.props.panelId, this.props.queryId, undefined, undefined);
  }

  /**
   * Returns query question answers service
   * 
   * @returns query question answers service
   */
  private getQueryQuestionAnswersService = () => {
    return Api.getQueryQuestionAnswersService(this.props.accessToken);
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
    accessToken: state.accessToken ? state.accessToken.token : null,
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