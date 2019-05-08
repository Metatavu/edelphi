import * as React from "react";
import * as actions from "../../actions";
import { StoreState, AccessToken, Command, EditPageCommentOptionsCommand } from "../../types";
import { connect } from "react-redux";
import PanelAdminQueryPageCommentOptionsEditor from "./panel-admin-query-page-comment-options-editor";

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
  pageCommentOptionsEditable: boolean,
  pageId: number
}

const COMMANDS: Command[] = [ "edit-page-comment-options" ];

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
      pageCommentOptionsEditable: false,
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
        <PanelAdminQueryPageCommentOptionsEditor editable={ this.state.pageCommentOptionsEditable } open={ this.state.pageCommentOptionsOpen } pageId={ this.state.pageId } panelId={ this.props.panelId} queryId={ this.props.queryId } onClose={ this.onCommentOptionsEditorClose }/>
        { this.renderCommandLinks() }
      </div>
    );
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

  private onCommandTrigger(command: Command, data: any) {
    switch (command) {
      case "edit-page-comment-options":
        this.onPageCommentOptionsCommandTrigger({
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
    const editable = command.pageData.hasAnswers == "false";

    this.setState({
      pageId: command.pageData.id,
      pageCommentOptionsEditable: editable, 
      pageCommentOptionsOpen: true
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