import * as React from "react";
import { TextArea, TextAreaProps } from "semantic-ui-react";
import * as actions from "../actions";
import { StoreState, AccessToken, SaveQueryAnswersCommandEvent } from "../types";
import { connect } from "react-redux";
import { QueryQuestionCommentsService } from "edelphi-client/dist/api/api";
import Api, { QueryQuestionCommentCategory } from "edelphi-client";
import strings from "../localization/strings";

/**
 * Interface representing component properties
 */
interface Props {
  queryReplyId: number,
  accessToken?: AccessToken,
  pageId: number,
  panelId: number,
  queryId: number,
  category: QueryQuestionCommentCategory | null
}

/**
 * Interface representing component state
 */
interface State {
  contents?: string,
  updating: boolean,
  loaded: boolean,
  commentId?: number
}

/**
 * React component for comment editor
 */
class QueryCommentEditor extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      updating: true,
      loaded: false
    };
  }
  
  /**
   * Component did mount life-cycle event
   */
  public async componentDidMount() {
    document.addEventListener("react-command", this.onReactCommand);
    this.loadComment();
  }
  
  /**
   * Component will unmount life-cycle event
   */
  public async componentWillUnmount() {
    document.removeEventListener("react-command", this.onReactCommand);
  }
  
  
  /**
   * Component did update life-cycle event
   */
  public async componentDidUpdate() {
    this.loadComment();
  }

  /** 
   * Component render method
   */
  public render() {
    return (
      <div className="queryCommentEditor">
        <h2 className="querySubTitle">{ strings.panel.query.commentEditor.title }</h2>
        <div className="formFieldContainer formMemoFieldContainer">
          <TextArea className="formField formMemoField queryComment" value={ this.state.contents } disabled={ this.state.updating } onChange={ this.onContentChange } />
        </div>
        <div className="formFieldContainer formSubmitContainer">
          <input type="submit" className="formField formSubmit" value={ strings.panel.query.commentEditor.save } onClick={ this.onSaveButtonClick } disabled={ !this.state.contents || this.state.updating }/>
        </div>
      </div>
    );
  }

  /**
   * Loads a comment
   */
  private async loadComment() {
    if (!this.props.accessToken || this.state.loaded) {
      return;
    }

    const categoryId = this.props.category ? this.props.category.id : 0;
    const queryQuestionCommentsService = this.getQueryQuestionCommentsService(this.props.accessToken.token);
    const comments = await queryQuestionCommentsService.listQueryQuestionComments(this.props.panelId, this.props.queryId, this.props.pageId, this.props.accessToken.userId, undefined, 0, categoryId);
    
    if (comments.length === 1) {
      this.setState({
        commentId: comments[0].id,
        contents: comments[0].contents,
        updating: false,
        loaded: true
      });
    } else if (comments.length > 1) {
      throw new Error("Unexpected comment count");
    } else {
      this.setState({
        updating: false,
        loaded: true
      });
    }
  }

  /**
   * Returns query question comments API
   * 
   * @returns query question comments API
   */
  private getQueryQuestionCommentsService(accessToken: string): QueryQuestionCommentsService {
    return Api.getQueryQuestionCommentsService(accessToken);
  }

  /**
   * Saves editor contents
   */
  private save = async () => {
    if (!this.state.contents || this.state.updating || !this.props.accessToken) {
      return;
    }

    this.setState({
      updating: true
    });

    const queryQuestionCommentsService = this.getQueryQuestionCommentsService(this.props.accessToken.token);

    let comment = null;
    const categoryId = this.props.category ? this.props.category.id : 0;

    if (!this.state.commentId) {
      comment = await queryQuestionCommentsService.createQueryQuestionComment({
        contents: this.state.contents,
        hidden: false,
        queryPageId: this.props.pageId,
        queryReplyId: this.props.queryReplyId,
        categoryId: categoryId
      }, this.props.panelId);
    } else {
      comment = await queryQuestionCommentsService.updateQueryQuestionComment({
        contents: this.state.contents,
        hidden: false,
        queryPageId: this.props.pageId,
        queryReplyId: this.props.queryReplyId,
        categoryId: categoryId
      }, this.props.panelId, this.state.commentId);
    }

    this.setState({
      commentId: comment.id,
      contents: comment.contents,
      updating: false
    });
  }

  /**
   * Event handler for contents change
   */
  private onContentChange = (event: React.FormEvent<HTMLTextAreaElement>, data: TextAreaProps) => {
    this.setState({
      contents: data.value as string
    });
  }

  /**
   * Handler for save button click
   */
  private onSaveButtonClick = async (event: React.MouseEvent<HTMLInputElement, MouseEvent>) => {
    event.preventDefault();
    this.save();
  }

  /**
   * Event handler for react command events
   */
  private onReactCommand = async (event: SaveQueryAnswersCommandEvent) => {
    if (event.detail.command == "save-query-answers" && this.state.contents) {
      await this.save();
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
    accessToken: state.accessToken,
    locale: state.locale
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

export default connect(mapStateToProps, mapDispatchToProps)(QueryCommentEditor);