import * as React from "react";
import * as actions from "../../actions";
import * as _ from "lodash";
import { StoreState, AccessToken, EditPageLegacyPageData } from "../../types";
import { connect } from "react-redux";
import { Modal, Button, Loader, Dimmer, DropdownItemProps, Form, Select, DropdownProps } from "semantic-ui-react";
import { QueryPageLive2DAnswersVisibleOption } from "../../generated/client/models";
import strings from "../../localization/strings";
import { QueryPagesApi } from "../../generated/client/apis";
import Api from "../../api";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken?: AccessToken,
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
  updating: boolean,
  loading: boolean,
  visible: QueryPageLive2DAnswersVisibleOption
}

/**
 * React component for comment editor
 */
class PanelAdminQueryPageLive2dOptionsEditor extends React.Component<Props, State> {

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
      visible: QueryPageLive2DAnswersVisibleOption.IMMEDIATELY
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
        <Modal.Header>{ strings.panelAdmin.queryEditor.pageLive2dOptions.title }</Modal.Header>
        <Modal.Content>  { this.renderModalContent() } </Modal.Content>
        <Modal.Actions>
          <Button onClick={ this.onSaveClick } positive> { strings.panelAdmin.queryEditor.pageLive2dOptions.save } </Button>
          <Button onClick={ this.onCloseClick }> { strings.panelAdmin.queryEditor.pageLive2dOptions.close } </Button>
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
    
    const options: DropdownItemProps[] = [{
      key: QueryPageLive2DAnswersVisibleOption.IMMEDIATELY,
      value: QueryPageLive2DAnswersVisibleOption.IMMEDIATELY,
      text: strings.panelAdmin.queryEditor.pageLive2dOptions.visibleOptions.IMMEDIATELY
    }, {
      key: QueryPageLive2DAnswersVisibleOption.AFTEROWNANSWER,
      value: QueryPageLive2DAnswersVisibleOption.AFTEROWNANSWER,
      text: strings.panelAdmin.queryEditor.pageLive2dOptions.visibleOptions.AFTEROWNANSWER
    }];

    return (
      <Form>
        <Form.Field>
          <label>{ strings.panelAdmin.queryEditor.pageLive2dOptions.visible }</label>
          <Select value={ this.state.visible } options={ options } onChange={ this.onVisibileChange }/>
        </Form.Field>
      </Form>
    );
  }

  /**
   * Loads a comment
   */
  private loadData = async () => {
    const  { accessToken, pageId, panelId } = this.props;

    if (!accessToken || !pageId) {
      return;
    }

    this.setState({
      loading: true
    });

    const queryPage = await Api.getQueryPagesApi(accessToken.token).findQueryPage({
      panelId: panelId,
      queryPageId: pageId
    });

    if (!queryPage) {
      throw new Error("Failed to find query page");
    }

    this.setState({
      loading: false,
      visible: queryPage.queryOptions.answersVisible || QueryPageLive2DAnswersVisibleOption.IMMEDIATELY
    });
  }

  /**
   * Event for visible dropdown change
   */
  private onVisibileChange = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
    this.setState({
      visible: data.value as QueryPageLive2DAnswersVisibleOption
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
    const { accessToken, panelId, pageId, onClose } = this.props;

    if (!accessToken) {
      return;
    }

    this.setState({
      updating: true
    });

    const queryPagesApi = Api.getQueryPagesApi(accessToken.token);
    const queryPage = await queryPagesApi.findQueryPage({
      panelId: panelId,
      queryPageId: pageId
    });

    if (!queryPage) {
      throw new Error("Failed to find query page");
    }

    const updatePage = {... queryPage, queryOptions: { ... queryPage.queryOptions, answersVisible: this.state.visible } };

    await queryPagesApi.updateQueryPage({
      panelId: panelId,
      queryPage: updatePage,
      queryPageId: pageId
    });
    
    this.setState({
      updating: false
    });

    onClose();
  }
  
  /**
   * Event handler for close click
   */
  private onCloseClick = async () => {
    this.props.onClose();
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

export default connect(mapStateToProps, mapDispatchToProps)(PanelAdminQueryPageLive2dOptionsEditor);