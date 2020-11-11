import * as React from "react";
import * as _ from "lodash";
import { AccessToken, EditPageLegacyPageData } from "../../types";
import { Modal, Button, Input, InputOnChangeData, Grid, Loader, Dimmer, Confirm } from "semantic-ui-react";
import { QueryQuestionCommentCategory } from "../../generated/client/models";
import { QueryQuestionCommentCategoriesApi } from "../../generated/client/apis";
import strings from "../../localization/strings";
import Api from "../../api";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: AccessToken,
  pageId: number,
  panelId: number,
  queryId: number,
  open: boolean,
  pageData: EditPageLegacyPageData,
  onClose: () => void
}

/**
 * Interface representing component state
 */
interface State {
  deleteConfirmOpen: boolean,
  updating: boolean,
  loading: boolean,
  categories: QueryQuestionCommentCategory[]
}

/**
 * React component for comment editor
 */
export default class PanelAdminQueryPageCommentOptionsEditor extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      deleteConfirmOpen: false,
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
    if ((!oldProps.accessToken && !!this.props.accessToken) || (this.props.panelId != oldProps.panelId) || (this.props.pageId != oldProps.pageId)) {
      await this.loadData();
    }
  }

  /** 
   * Component render method
   */
  public render() {
    return (
      <Modal open={this.props.open} onClose={this.onModalClose}>
        <Modal.Header>{ strings.panelAdmin.queryEditor.pageCommentOptions.title }</Modal.Header>
        <Modal.Content>  { this.renderModalContent() } </Modal.Content>
        <Modal.Actions>
          <Button onClick={ this.onSaveClick } positive> { strings.panelAdmin.queryEditor.pageCommentOptions.save } </Button>
          <Button onClick={ this.onCloseClick }> { strings.panelAdmin.queryEditor.pageCommentOptions.close } </Button>
        </Modal.Actions>
      </Modal>
    );
  }

  /**
   * Renders modal's content
   */
  private renderModalContent = () => {
    if (this.state.updating || this.state.loading) {
      return (
        <div style={{ minHeight: "200px" }}>
          <Dimmer active inverted>
            <Loader inverted/>
          </Dimmer>
        </div>
      );
    }

    return (
      <Grid>
        <Grid.Row>
          <Grid.Column>
             <h2> { strings.panelAdmin.queryEditor.pageCommentOptions.categories } </h2>
          </Grid.Column>
        </Grid.Row>
        <Grid.Row>
          <Grid.Column width={ 10 }>
            { this.renderCategoryList() }
          </Grid.Column>
          <Grid.Column width={ 6 } style={{ textAlign: "right" }}>
            <Button disabled={ this.props.pageData.hasAnswers == "true" } onClick={ this.onCategoryAddButtonClick }>{ strings.panelAdmin.queryEditor.pageCommentOptions.addCategory }</Button>
          </Grid.Column>
        </Grid.Row>
        <Grid.Row>
          <Grid.Column>
          </Grid.Column>
        </Grid.Row>
      </Grid>        
    );
  }

  /**
   * Renders category list
   */
  private renderCategoryList = () => {
    return (
      <Grid>
        {
          this.state.categories.map((category, index) => {
            return (
              <Grid.Row key={ category.id }>
                <Grid.Column width={ 12 }>
                  <Input style={{ width: "100%" }} value={ category.name } onChange={ (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => this.onCategoryListNameChange(index, data.value) }/>
                </Grid.Column>
                <Grid.Column width={ 4 }>
                  <Button onClick={ () => this.setState({ deleteConfirmOpen: true }) } disabled={ this.props.pageData.hasAnswers == "true" } negative> { strings.panelAdmin.queryEditor.pageCommentOptions.deleteCategory } </Button>
                  <Confirm 
                    content={ strings.panelAdmin.queryEditor.queryCommentOptions.deleteCategoryConfirm } 
                    open={ this.state.deleteConfirmOpen }
                    onConfirm={ () => { this.setState({ deleteConfirmOpen: false }); this.deleteCategory(index) } } 
                    onCancel={ () => { this.setState({ deleteConfirmOpen: false }); } }/>                    
                </Grid.Column>
              </Grid.Row>
            )
          })
        }
      </Grid>
    );
  }

  /**
   * Loads a comment
   */
  private loadData = async () => {
    const { accessToken, pageId, panelId, queryId } = this.props;

    if (!pageId) {
      return;
    }

    this.setState({
      loading: true
    });

    const categories = await Api.getQueryQuestionCommentCategoriesApi(accessToken.token).listQueryQuestionCommentCategories({
      panelId: panelId,
      pageId: pageId,
      queryId: queryId
    });

    this.setState({
      categories: categories,
      loading: false
    });
  }

  /**
   * Deletes a category
   * 
   * @param category category
   */
  private deleteCategory = async (index: number) => {
    const { accessToken } = this.props;

    const category: QueryQuestionCommentCategory = this.state.categories[index];
    const categories = _.clone(this.state.categories);
    categories.splice(index, 1); 

    if (category.id) {
      await Api.getQueryQuestionCommentCategoriesApi(accessToken.token).deleteQueryQuestionCommentCategory({
        panelId: this.props.panelId,
        categoryId: category.id
      });
    } 

    this.setState({
      categories: categories
    });
  }

  /**
   * Event handler for modal close
   */
  private onModalClose = () => {
    this.props.onClose();
  }

  /**
   * Event handler for save click
   */
  private onSaveClick = async () => {
    const { accessToken } = this.props;

    this.setState({
      updating: true
    });

    const categories = [];

    for (let i = 0; i < this.state.categories.length; i++) {
      const category = this.state.categories[i];
      
      if (category.id) {
        categories.push(await Api.getQueryQuestionCommentCategoriesApi(accessToken.token).updateQueryQuestionCommentCategory({
          categoryId: category.id,
          panelId: this.props.panelId,
          queryQuestionCommentCategory: category
        }));
      } else {
        categories.push(await Api.getQueryQuestionCommentCategoriesApi(accessToken.token).createQueryQuestionCommentCategory({
          panelId:this.props.panelId,
          queryQuestionCommentCategory: category
        }));
      }
    }

    this.setState({
      updating: false,
      categories: categories
    });

    this.props.onClose();
  }
  
  /**
   * Event handler for close click
   */
  private onCloseClick = async () => {
    this.props.onClose();
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
        queryId: this.props.queryId,
        queryPageId: this.props.pageId
      })
    });
  }
}