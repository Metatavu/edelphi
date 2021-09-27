import * as React from "react";
import { TextArea, TextAreaProps } from "semantic-ui-react";
import * as actions from "../../actions";
import { StoreState, AccessToken } from "../../types";
import { connect } from "react-redux";
import { QueryQuestionCommentsApi } from "../../generated/client/apis";
import { QueryQuestionCommentCategory } from "../../generated/client/models";
import strings from "../../localization/strings";
import ErrorDialog from "../error-dialog";
import Api from "../../api";

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

  /**
   * On comment change handler from parent
   */
  onCommentChange: (changed: boolean) => void;
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

    (document as any).addEventListener("before-page-save", async () => {
      await this.save();
    });
  }
  
  /**
   * Component did mount life-cycle event
   */
  public async componentDidMount() {
    this.loadComment();
  }
  
  /**
   * Component did update life-cycle event
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
   * Loads a comment
   */
  private async loadComment() {
    const { accessToken, category, panelId, queryId, pageId } = this.props;
    const { loading, saving } = this.state;

    if (!accessToken || loading || saving) {
      return;
    }

    this.setState({
      loading: true
    });

    const categoryId = category ? category.id : 0;
    const comments = await Api.getQueryQuestionCommentsApi(accessToken.token).listQueryQuestionComments({
      panelId: panelId,
      queryId: queryId,
      pageId: pageId,
      userId: accessToken.userId,
      parentId: 0,
      categoryId: categoryId,
      firstResult: 0,
      // TODO: Fix this later!!!
      maxResults: 10000,
      oldestFirst: false
    });

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
   * Saves editor contents
   */
  private save = async () => {
    const { accessToken, category, panelId, pageId, queryReplyId } = this.props;
    const { changed, contents, saving, commentId } = this.state;

    if (!changed || !contents || saving || !accessToken) {
      return;
    }

    this.setState({
      saving: true
    });

    try {
      const queryQuestionCommentsApi = Api.getQueryQuestionCommentsApi(accessToken.token)

      let comment = null;
      const categoryId = category ? category.id : 0;

      if (!commentId) {
        comment = await queryQuestionCommentsApi.createQueryQuestionComment({
          panelId: panelId,
          queryQuestionComment: {
            contents: contents,
            hidden: false,
            queryPageId: pageId,
            queryReplyId: queryReplyId,
            categoryId: categoryId
          }
        });
      } else {
        comment = await queryQuestionCommentsApi.updateQueryQuestionComment({
          commentId: commentId,
          panelId: panelId,
          queryQuestionComment: {
            contents: contents,
            hidden: false,
            queryPageId: pageId,
            queryReplyId: queryReplyId,
            categoryId: categoryId
          }
        });
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

    this.props.onCommentChange(true);
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
    this.props.onCommentChange(false);
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