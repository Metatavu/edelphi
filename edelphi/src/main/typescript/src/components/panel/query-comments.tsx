import * as React from "react";
import * as actions from "../../actions";
import { StoreState, AccessToken, PageChangeEvent } from "../../types";
import { connect } from "react-redux";
import { QueryQuestionCommentCategoriesService } from "edelphi-client/dist/api/api";
import Api, { QueryQuestionCommentCategory } from "edelphi-client";
import QueryCommentEditor from "./query-comment-editor";
import QueryCommentList from "./query-comment-list";
import { Tab } from 'semantic-ui-react'

/**
 * Interface representing component properties
 */
interface Props {
  queryReplyId: number,
  accessToken?: AccessToken,
  pageId: number,
  panelId: number,
  queryId: number,
  viewDiscussion: boolean,
  commentable: boolean,
  canManageComments: boolean,
  setPageChangeListener: (listener: (event: PageChangeEvent) => void) => void
}

/**
 * Interface representing component state
 */
interface State {
  categories: QueryQuestionCommentCategory[],
  loading: boolean
}

/**
 * React component for comment editor
 */
class QueryComments extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      categories: [],
      loading: false
    };
  }
  
  /**
   * Component did update life-cycle event
   */
  public async componentDidMount() {
    await this.loadData();
  }

  public async componentDidUpdate(oldProps: Props) {
    if ((this.props.accessToken != oldProps.accessToken)) {
      await this.loadData();
    }
  }

  /** 
   * Component render method
   */
  public render() {
    if (!this.props.commentable && !this.props.viewDiscussion) {
      return null;
    }

    if (this.state.categories.length == 0) {
      return this.renderCategory(null);
    }

    return this.renderCategorized();
  }

  /**
   * Renders categorized comments
   */
  private renderCategorized() {
    const panes = this.state.categories.map((category) => {
      return {
        menuItem: category.name,
        render: () => <Tab.Pane> { this.renderCategory(category) } </Tab.Pane>
      }
    });
    
    return (
      <Tab menu={{ color: "orange", pointing: true }} panes={panes}/>
    );
  }

  /**
   * Renders comment category
   * 
   * @param category category
   */
  private renderCategory = (category: QueryQuestionCommentCategory | null) => {      
    return (
      <div key={ category ? category.id : "ROOT" }>
        {this.props.commentable ? this.renderCommentEditor(category) : null}
        {this.props.viewDiscussion ? <QueryCommentList category={ category } canManageComments={this.props.canManageComments} panelId={this.props.panelId} queryId={this.props.queryId} pageId={this.props.pageId} queryReplyId={this.props.queryReplyId} /> : null}
      </div>
    );
  }

  private renderCommentEditor = (category: QueryQuestionCommentCategory | null) => {
    return <QueryCommentEditor setPageChangeListener={ this.props.setPageChangeListener } category={ category } pageId={this.props.pageId} panelId={this.props.panelId} queryId={this.props.queryId} queryReplyId={this.props.queryReplyId} />;
  }

  /**
   * Loads a comment
   */
  private loadData = async () => {
    if (!this.props.accessToken) {
      return;
    }

    this.setState({
      loading: true
    });

    const queryQuestionCommentCategoriesService = await this.getQueryQuestionCommentCategoriesService(this.props.accessToken.token);
    const pageCategories = await queryQuestionCommentCategoriesService.listQueryQuestionCommentCategories(this.props.panelId, this.props.pageId, this.props.queryId);
    const queryCategories = await queryQuestionCommentCategoriesService.listQueryQuestionCommentCategories(this.props.panelId, undefined, this.props.queryId);

    this.setState({
      categories: pageCategories.concat(queryCategories),
      loading: false
    });
  }

  /**
   * Returns query question comments API
   * 
   * @returns query question comments API
   */
  private getQueryQuestionCommentCategoriesService(accessToken: string): QueryQuestionCommentCategoriesService {
    return Api.getQueryQuestionCommentCategoriesService(accessToken);
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

export default connect(mapStateToProps, mapDispatchToProps)(QueryComments);