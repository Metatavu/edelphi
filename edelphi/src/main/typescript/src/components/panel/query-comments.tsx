import * as React from "react";
import * as actions from "../../actions";
import { StoreState, AccessToken, PageChangeEvent } from "../../types";
import { connect } from "react-redux";
import { QueryQuestionCommentCategoriesApi } from "../../generated/client/apis";
import { QueryQuestionCommentCategory } from "../../generated/client/models";
import QueryCommentEditor from "./query-comment-editor";
import QueryCommentList from "./query-comment-list";
import { Tab, Menu, Confirm } from 'semantic-ui-react'
import strings from "../../localization/strings";
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
  viewDiscussion: boolean,
  commentable: boolean,
  canManageComments: boolean,
  setPageChangeListener: (listener: (event: PageChangeEvent) => void) => void
}

/**
 * Interface representing component state
 */
interface State {
  categories: QueryQuestionCommentCategory[];
  loading: boolean;
  commentChanged: boolean;
  confirmOpen: boolean;
  activeTabIndex: number;
  clickedTabIndex?: number;
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
      loading: false,
      commentChanged: false,
      confirmOpen: false,
      activeTabIndex: 0,
    };
  }
  
  /**
   * Component did mount life-cycle event
   */
  public async componentDidMount() {
    await this.loadData();
  }

  /**
   * Component did update life-cycle event
   * @param oldProps old props
   */
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
    const panes = this.state.categories.map((category, index) => {
      return {
        menuItem: (
          <Menu.Item
            key={ category.name }
            onClick={ () => this.onTabClick(index) }
          >
            { category.name }
          </Menu.Item>
        ),
        render: () => <Tab.Pane>{ this.renderCategory(category) }</Tab.Pane>
      }
    });
    
    return (<>
      <Tab activeIndex={ this.state.activeTabIndex } menu={{ color: "orange", pointing: true }} panes={panes}/>
      <Confirm
        header={ strings.confirmationDialog.title }
        content={ strings.confirmationDialog.content }
        confirmButton={ strings.confirmationDialog.confirm }
        cancelButton={ strings.confirmationDialog.cancel }
        open={ this.state.confirmOpen }
        onCancel={ this.onModalCancel }
        onConfirm={ this.onModalConfirm }
      />
    </>);
  }

  /**
   * Renders comment category
   * 
   * @param category category
   */
  private renderCategory = (category: QueryQuestionCommentCategory | null) => {      
    const { accessToken } = this.props;
    if (!accessToken) {
      return null;
    }

    return (
      <div key={ category ? category.id : "ROOT" }>
        { this.props.commentable ? this.renderCommentEditor(category) : null }
        { this.props.viewDiscussion ?
          <QueryCommentList
            accessToken={ accessToken.token }
            loggedUserId={ accessToken.userId }
            category={ category }
            canManageComments={ this.props.canManageComments }
            panelId={ this.props.panelId } queryId={ this.props.queryId }
            pageId={ this.props.pageId }
            queryReplyId={ this.props.queryReplyId }
          />
          : null 
        }
      </div>
    );
  }

  /**
   * Render comment editor
   * @param category comment category
   */
  private renderCommentEditor = (category: QueryQuestionCommentCategory | null) => {
    return (
      <QueryCommentEditor
      onCommentChange={ this.onCommentChange }
      setPageChangeListener={ this.props.setPageChangeListener }
      category={ category }
      pageId={ this.props.pageId }
      panelId={ this.props.panelId }
      queryId={ this.props.queryId }
      queryReplyId={ this.props.queryReplyId }
      />
    );
  }

  /**
   * On comment change handler
   * @param commentChanged has user changed comment
   */
  private onCommentChange = (commentChanged: boolean) => {
    this.setState({ commentChanged });
  }

  /**
   * Modal cancel handler
   */
  private onModalCancel = () => {
    this.setState({
      confirmOpen: false
    });
  }

  /**
   * Modal confirm handler
   */
  private onModalConfirm = () => {
    const { clickedTabIndex } = this.state;
    const indexToSet = clickedTabIndex || 0;
    this.setState({
      activeTabIndex: indexToSet,
      commentChanged: false,
      confirmOpen: false
    });
  }

  /**
   * On tab click handler
   * @param tabIndex clicked tab index
   */
  private onTabClick = (tabIndex : number) => {
    const { activeTabIndex, commentChanged } = this.state;
    if (activeTabIndex === tabIndex) {
      return;
    }
    if (commentChanged) {
      this.setState({
        confirmOpen: true,
        clickedTabIndex: tabIndex
      });
      return;
    }

    this.setState({
      activeTabIndex: tabIndex
    });
  }

  /**
   * Loads a comment
   */
  private loadData = async () => {
    const { accessToken, panelId, pageId, queryId } = this.props;

    if (!accessToken) {
      return;
    }

    this.setState({
      loading: true
    });

    const queryQuestionCommentCategoriesApi = Api.getQueryQuestionCommentCategoriesApi(accessToken.token);

    const pageCategories = await queryQuestionCommentCategoriesApi.listQueryQuestionCommentCategories({
      panelId: panelId,
      pageId: pageId,
      queryId: queryId
    });
    
    const queryCategories = await queryQuestionCommentCategoriesApi.listQueryQuestionCommentCategories({
      panelId: panelId,
      queryId: queryId
    });

    this.setState({
      categories: pageCategories.concat(queryCategories),
      loading: false
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

export default connect(mapStateToProps, mapDispatchToProps)(QueryComments);