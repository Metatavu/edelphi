import * as React from "react";
import * as actions from "../../actions";
import { StoreState, AccessToken, Command, EditPageCommentOptionsCommand, EditPageLegacyCommandPageData, EditPageLive2dOptionsCommand } from "../../types";
import { connect } from "react-redux";
import PanelAdminQueryPageCommentOptionsEditor from "./panel-admin-query-page-comment-options-editor";
import PanelAdminQueryPageLive2dOptionsEditor from "./panel-admin-query-page-live2d-options-editor";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken?: AccessToken,
  panelId: number,
  queryId: number
}

/**
 * Interface representing component state
 */
interface State {
  pageCommentOptionsOpen: boolean,
  pageLive2dOptionsOpen: boolean,
  pageData?: EditPageLegacyCommandPageData,
  pageId: number
}

const COMMANDS: Command[] = [ "edit-page-comment-options", "edit-page-live2d-options" ];

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
   * Component render method
   */
  public render() {
    return (
      <div>
        { this.renderCommentOptionsEditor() }
        { this.renderLive2dOptionsEditor() }
        { this.renderCommandLinks() }
      </div>
    );
  }

  /**
   * Renders comment options editor
   */
  private renderCommentOptionsEditor() {
    if (!this.state.pageData) {
      return null;
    }

    return <PanelAdminQueryPageCommentOptionsEditor pageData={ this.state.pageData } open={ this.state.pageCommentOptionsOpen } pageId={ this.state.pageId } panelId={ this.props.panelId} queryId={ this.props.queryId } onClose={ this.onCommentOptionsEditorClose }/>
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
   * Renders legacy "command" links
   */
  private renderCommandLinks = () => {
    return (<div style={{ display: "none" }}> 
      {
        COMMANDS.map((command: Command) => {
          return <a key={ command } id={ "panel-admin-query-editor-" + command } onClick={ this.onCommandTriggerClick }></a>
        })
      }
    </div>);
  }

  /**
   * Event handler for comment option close event
   */
  private onCommentOptionsEditorClose = () => {
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

  /**
   * Event handler for handling "commands" triggered from legacy UI
   * 
   * @param event
   */
  private onCommandTriggerClick = (event: React.MouseEvent<HTMLAnchorElement, MouseEvent>) => {
    const target: Element = event.target as Element;
    const data = target.getAttribute("data-data");
    const command: Command = target.id.substring("panel-admin-query-editor-".length) as Command;
    this.onCommandTrigger(command, data ? JSON.parse(data) : {});
  }

  /**
   * Event handler for handling command trigger
   * 
   * @param command command
   * @param data data
   */
  private onCommandTrigger(command: Command, data: any) {
    switch (command) {
      case "edit-page-comment-options":
        this.onPageCommentOptionsCommandTrigger({
          type: command,
          pageData: data.pageData
        });
      break;
      case "edit-page-live2d-options":
        this.onPageLive2dOptionsCommandTrigger({
          type: command,
          pageData: data.pageData
        });
      break;
    }
  }

  /**
   * Event handler for handling EditPageCommentOptionsCommand -command
   * 
   * @param command command
   */
  private onPageCommentOptionsCommandTrigger(command: EditPageCommentOptionsCommand) {
    this.setState({
      pageId: command.pageData.id,
      pageData: command.pageData,
      pageCommentOptionsOpen: true
    });
  }

  /**
   * Event handler for handling EditPageLive2dOptionsCommand -command
   * 
   * @param command command
   */
  private onPageLive2dOptionsCommandTrigger(command: EditPageLive2dOptionsCommand) {
    this.setState({
      pageId: command.pageData.id,
      pageData: command.pageData,
      pageLive2dOptionsOpen: true
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

export default connect(mapStateToProps, mapDispatchToProps)(PanelAdminQueryEditor);