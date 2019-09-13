import * as React from "react";
import { TextArea, TextAreaProps } from "semantic-ui-react";
import * as actions from "../../actions";
import { StoreState, AccessToken, PageChangeEvent } from "../../types";
import { connect } from "react-redux";
import { QueryQuestionCommentsService } from "edelphi-client/dist/api/api";
import Api, { QueryQuestionCommentCategory } from "edelphi-client";
import strings from "../../localization/strings";
import ErrorDialog from "../error-dialog";

/**
 * Interface representing component properties
 */
interface Props {
  queryReplyId: number,
  accessToken?: AccessToken,
  pageId: number,
  panelId: number,
  queryId: number,
  category: QueryQuestionCommentCategory | null,
  setPageChangeListener: (listener: (event: PageChangeEvent) => void) => void
}

/**
 * Interface representing component state
 */
interface State {
  contents?: string,
  saving: boolean,
  loading: boolean,
  commentId?: number,
  changed: boolean,
  loaded: boolean,
  error?: Error
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
      saving: false,
      loading: false,
      changed: false,
      loaded: false
    };

    this.props.setPageChangeListener(this.onPageChange);
  }
  
  /**
   * Component did mount life-cycle event
   */
  public async componentDidMount() {
    this.loadComment();
  }
  
  /**
   * Component did mount life-cycle event
   */
  public async componentDidUpdate() {
    if (!this.state.loaded) {
      this.loadComment();
    }
  }

  /** 
   * Component render method
   */
  public render() {
    if (this.state.error) {
      return <ErrorDialog error={ this.state.error } onClose={ () => this.setState({ error: undefined }) } /> 
    }
    
    return (
      <div className="queryCommentEditor">
        <h2 className="querySubTitle">{ this.props.category ? this.props.category.name : strings.panel.query.commentEditor.title }</h2>
        <div className="formFieldContainer formMemoFieldContainer">
          <TextArea className="formField formMemoField queryComment" value={ this.state.contents } disabled={ this.state.saving || this.state.loading } onChange={ this.onContentChange } />
        </div>
        <div className="formFieldContainer formSubmitContainer">
          <input type="submit" className="formField formSubmit" value={ this.state.commentId ? strings.panel.query.commentEditor.modify : strings.panel.query.commentEditor.save } onClick={ this.onSaveButtonClick } disabled={ !this.state.contents || this.state.saving || this.state.loading }/>
        </div>
      </div>
    );
  }

  /**
   * Event handler for a page change
   */
  private onPageChange = async (event: PageChangeEvent) => {
    await this.save();
  }

  /**
   * Loads a comment
   */
  private async loadComment() {
    if (!this.props.accessToken || this.state.loading || this.state.saving) {
      return;
    }

    this.setState({
      loading: true
    });

    const categoryId = this.props.category ? this.props.category.id : 0;
    const queryQuestionCommentsService = this.getQueryQuestionCommentsService(this.props.accessToken.token);
    const comments = await queryQuestionCommentsService.listQueryQuestionComments(this.props.panelId, this.props.queryId, this.props.pageId, this.props.accessToken.userId, undefined, 0, categoryId);
    
    if (comments.length > 1) {
      console.error("Unexpected comment count");
    }

    this.setState({
      loading: false,
      commentId: comments.length ? comments[0].id : undefined,
      contents: comments.length ? comments[0].contents : undefined,
      loaded: true
    });
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
    if (!this.state.changed || !this.state.contents || this.state.saving || !this.props.accessToken) {
      return;
    }

    this.setState({
      saving: true
    });

    try {
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
        saving: false
      });
    } catch (e) {
      this.setState({
        error: e,
        saving: false
      });
    }
  }

  /**
   * Event handler for contents change
   */
  private onContentChange = (event: React.FormEvent<HTMLTextAreaElement>, data: TextAreaProps) => {
    this.setState({
      contents: data.value as string,
      changed: true
    });
  }

  /**
   * Handler for save button click
   */
  private onSaveButtonClick = async (event: React.MouseEvent<HTMLInputElement, MouseEvent>) => {
    event.preventDefault();
    await this.save();

    this.setState({
      changed: false
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