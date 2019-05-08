import * as React from "react";
import * as actions from "../../actions";
import * as _ from "lodash";
import { StoreState, AccessToken } from "../../types";
import { connect } from "react-redux";
import { Modal, List, Button, Input, InputOnChangeData, Grid, Loader } from "semantic-ui-react";
import Api, { QueryQuestionCommentCategory } from "edelphi-client";
import { QueryQuestionCommentCategoriesService } from "edelphi-client/dist/api/api";
import strings from "../../localization/strings";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken?: AccessToken,
  pageId: number,
  panelId: number,
  queryId: number,
  open: boolean
}

/**
 * Interface representing component state
 */
interface State {
  updating: boolean,
  loading: boolean,
  categories: QueryQuestionCommentCategory[]
}

/**
 * React component for comment editor
 */
class PanelAdminQueryPageCommentOptionsEditor extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      updating: false,
      loading: false,
      categories: []
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
   * 
   * @param oldProps old props
   */
  public async componentDidUpdate(oldProps: Props) {
    if ((this.props.accessToken != oldProps.accessToken) || (this.props.panelId != oldProps.panelId) || (this.props.pageId != oldProps.pageId)) {
      await this.loadData();
    }
  }

  /** 
   * Component render method
   */
  public render() {
    if (this.state.updating || this.state.loading) {
      return <Loader/>
    }

    return (
      <Modal open={this.props.open}>
        <Modal.Header>{ strings.panelAdmin.queryEditor.pageCommentOptions.title }</Modal.Header>
        <Modal.Content> 
          <Grid>
            <Grid.Row>
              <Grid.Column width={ 10 }>
                { this.renderCategoryList() }
              </Grid.Column>
              <Grid.Column width={ 6 } style={{ textAlign: "right" }}>
                <Button onClick={ this.onCategoryAddButtonClick }>{ strings.panelAdmin.queryEditor.pageCommentOptions.addCategory }</Button>
              </Grid.Column>
            </Grid.Row>
            <Grid.Row>
              <Grid.Column>
                <Button onClick={ this.onSaveClick }> { strings.panelAdmin.queryEditor.pageCommentOptions.save } </Button>
              </Grid.Column>
            </Grid.Row>
          </Grid>          
          
        </Modal.Content>
      </Modal>
    );
  }

  /**
   * Renders category list
   */
  private renderCategoryList = () => {
    console.log("s", this.state.categories);

    return (
      <List>
        {
          this.state.categories.map((category, index) => {
            return (
            <List.Item>
              <Input key={ category.id } value={ category.name } onChange={ (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => this.onCategoryListNameChange(index, data.value) }/>
            </List.Item>
            )
          })
        }
      </List>  
    );
  }

  /**
   * Loads a comment
   */
  private loadData = async () => {
    if (!this.props.accessToken || !this.props.pageId) {
      return;
    }

    this.setState({
      loading: true
    });

    const queryQuestionCommentCategoriesService = await this.getQueryQuestionCommentCategoriesService(this.props.accessToken.token);
    const categories = await queryQuestionCommentCategoriesService.listQueryQuestionCommentCategories(this.props.panelId, this.props.pageId);

    this.setState({
      categories: categories,
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

  /**
   * Event handler for save click
   */
  private onSaveClick = async () => {
    if (!this.props.accessToken) {
      return;
    }
    
    this.setState({
      updating: true
    });

    const queryQuestionCommentCategoriesService = await this.getQueryQuestionCommentCategoriesService(this.props.accessToken.token);
    const categories = [];

    for (let i = 0; i < this.state.categories.length; i++) {
      const category = this.state.categories[i];
      
      if (category.id) {
        categories.push(await queryQuestionCommentCategoriesService.updateQueryQuestionCommentCategory(category, this.props.panelId, category.id));
      } else {
        categories.push(await queryQuestionCommentCategoriesService.createQueryQuestionCommentCategory(category, this.props.panelId));
      }
    }

    this.setState({
      updating: false,
      categories: categories
    });
  }

  /**
   * Event handler for category name change
   */
  private onCategoryListNameChange = (index: number, name: string) => {
    const categories = _.clone(this.state.categories);
    categories[index] = { ... categories[index], name };
    
    this.setState({
      categories: categories
    });
  }

  /**
   * Event handler for category add button click
   */
  private onCategoryAddButtonClick = () => {
    this.setState({
      categories: this.state.categories.concat({
        name: "",
        queryPageId: this.props.pageId
      })
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

export default connect(mapStateToProps, mapDispatchToProps)(PanelAdminQueryPageCommentOptionsEditor);