import * as React from "react";
import * as actions from "../../actions";
import * as _ from "lodash";
import { StoreState, CommandEvent, PageChangeEvent } from "../../types";
import { connect } from "react-redux";
import { Grid, Button, Popup, List, Segment, Dimmer, Loader, Icon } from "semantic-ui-react";
import Api, { QueryPage, Panel, QueryState } from "edelphi-client";
import { QueryPagesService, PanelsService } from "edelphi-client/dist/api/api";
import strings from "../../localization/strings";

declare const JSONUtils: any;

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: string,
  panelId: number,
  queryId: number,
  pageId: number,
  queryState: QueryState,
  queryValidationMessage: string | null,
  onPageChange: (event: PageChangeEvent) => void,
  queryValidationMessageUpdate: (queryValidationMessage: string | null) => void
}

/**
 * Interface representing component state
 */
interface State {
  loading: boolean,
  nextSaving: boolean,
  previousSaving: boolean,
  pagesOpen: boolean,
  pages: QueryPage[],
  panel?: Panel
}

/**
 * React component for query navigation
 */
class QueryNavigation extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = { 
      loading: true,
      nextSaving: false,
      previousSaving: false,
      pages: [],
      pagesOpen: false
    };
  }
  
  /**
   * Component will mount life-cycle event
   */
  public componentWillMount() {
    document.addEventListener("react-command", this.onReactCommand);  
  }
  
  /**
   * Component will unmount life-cycle event
   */
  public async componentWillUnmount() {
    document.removeEventListener("react-command", this.onReactCommand);
  }
  
  /**
   * Component did mount life-cycle event
   */
  public async componentDidMount() {
    await this.loadData();
  }

  /**
   * Component did update life-cycle event
   */
  public async componentDidUpdate(oldProps: Props) {
    if ((this.props.accessToken != oldProps.accessToken)) {
      await this.loadData();
    }
  }

  /** 
   * Render edit pest view
   */
  public render() {
    if (this.state.loading) {
      return (
        <Segment style={{ minHeight: "200px" }}>
          <Dimmer inverted active>
            <Loader>{ strings.generic.loading }</Loader>
          </Dimmer>
        </Segment>
      );
    }

    const currentPage = this.getCurrentPage();
    const nextPage = this.getNextPage();
    const previousPage = this.getPreviousPage();

    if (!currentPage) {
      return null;
    }

    const nextDisabled = this.props.queryState !== "ACTIVE" || !!this.props.queryValidationMessage || this.state.previousSaving || this.state.nextSaving;
    const previousDisabled = !previousPage || this.state.previousSaving || this.state.nextSaving;
    const skipDisabled = !nextPage || this.state.previousSaving || this.state.nextSaving;
    
    return (
      <Grid style={{ marginTop: "10px", borderTop: "1px solid #000" }}>
        { this.renderDisabledMessage() }
        <Grid.Row>
          <Grid.Column width={ 5 } only={"computer"}></Grid.Column>
          <Grid.Column computer={ 6 } mobile={ 16 }>
            <Grid>
              <Grid.Row>
                <Grid.Column width={ 6 } style={{ textAlign: "center" }}> 
                  <Button disabled={ previousDisabled } color={ "blue" } onClick={ this.onPreviousClick }> 
                    { strings.panel.query.previous }
                    { this.state.previousSaving && <Loader style={{ marginLeft: "10px" }} active={ true } inline size="mini" inverted/> }
                  </Button> 
                </Grid.Column>
                <Grid.Column  width={ 4 } style={{ textAlign: "center" }}>
                  <Popup position="top center" size="large" closeOnDocumentClick={ true } trigger={ <div style={{ textAlign: "center", marginBottom: "5px" }}> <Icon size="large" color="blue" name="triangle up"/> <div> </div> { `${ currentPage.pageNumber + 1 } / ${ this.state.pages.length }` } </div> } on="click" open={ this.state.pagesOpen } onClose={ () => this.setState({ pagesOpen: false }) } onOpen={ () => this.setState({ pagesOpen: true }) }>
                    <p> { strings.panel.query.quickNavigationTitle } </p>
                    { this.renderPages() }
                  </Popup>
                </Grid.Column>
                <Grid.Column width={ 6 } style={{ textAlign: "center" }}>
                  <Button disabled={ nextDisabled } color={ "blue" } onClick={ this.onNextClick }>
                    { nextPage ? strings.panel.query.next : strings.panel.query.save }
                    { this.state.nextSaving && <Loader style={{ marginLeft: "10px" }} active={ true } inline size="mini" inverted/> }
                  </Button>
                </Grid.Column>
              </Grid.Row>
              <Grid.Row>
                <Grid.Column width={ 16 } style={{ textAlign: "center" }}> 
                  <Button disabled={ skipDisabled } color={ "blue" } onClick={ this.onSkipClick }>{ strings.panel.query.skip }</Button>
                </Grid.Column>
              </Grid.Row>
            </Grid>
            </Grid.Column>
          <Grid.Column width={ 5 } only={"computer"}></Grid.Column>
        </Grid.Row>
      </Grid>
    );
  }

  /**
   * Renders disabled message
   */
  private renderDisabledMessage = () => {
    if (!this.props.queryValidationMessage) {
      return null;
    }

    return (
      <Grid.Row>
        <Grid.Column width={ 16 }>
          <p style={{ color: "red", textAlign: "center", fontSize: "16px", fontWeight: "bold" }}>
            { this.props.queryValidationMessage }
          </p>
        </Grid.Column>
      </Grid.Row>
    );
  }

  /**
   * Renders pages list
   */
  private renderPages = () => {
    return (
      <List as='ol' style={{ marginBottom: "10px" }}>
        {
          this.state.pages.map((page: QueryPage) => {
            return (
              <List.Item as='li' key={ `list-${page.id}` }>
                <a  key={ `link-${page.id}` } style={{ minWidth: "400px", display: "inline-block", marginLeft: "10px" }} href={ `?page=${page.pageNumber}` }>{ page.title }</a>
              </List.Item>
            )
          })
        }    
      </List>
    );
  }

  /**
   * Locates legacy form
   */
  private getLegacyForm = () => {
    const queryBlock = document.getElementById("panelQueryBlock");
    if (queryBlock) {
      const forms = queryBlock.getElementsByTagName("form");
      if (forms.length == 1) {
        return forms[0];
      }
    }

    return null;
  }

  /**
   * Saves legacy form
   */
  private saveLegacyForm = (finish: boolean) => {
    const form  = this.getLegacyForm();
    if (!form) {
      throw new Error("Failed to locate legacy form");
    }

    return new Promise((resolve, reject) => {
      JSONUtils.sendForm(form, {
        onSuccess : function(jsonResponse: any) {
          if (finish) {
            JSONUtils.request('/queries/finishquery.json', {
              parameters : {
                replyId : form.queryReplyId.value
              },
              onSuccess : function(jsonResponse: any) {
                resolve(jsonResponse);
              },
              onFailure : function(jsonResponse: any) {
                reject(jsonResponse);
              }
            });
          } else {
            resolve(jsonResponse);
          }
        },
        onFailure : function(jsonResponse: any) {
          reject(jsonResponse);
        }
      });
    });
  }

  /**
   * Handles page change click event
   */
  private changePage = async (page: QueryPage | null, save: boolean, finish: boolean) => {
    await this.props.onPageChange({ });
    
    if (save) {
      await this.saveLegacyForm(finish);
    }

    await this.promiseAwait(500);
    
    if (page) {
      window.location.href = `?page=${page.pageNumber}`;
    } else if (finish && this.state.panel) {
      window.location.href = `/${this.state.panel.urlName}`;
    }
  }

  /**
   * Returns current page
   * 
   * @returns current page or null if not found
   */
  private getCurrentPage = () => {
    return this.state.pages.find((page) => {
      return page.id == this.props.pageId;
    });
  }

  /**
   * Returns previous page
   * 
   * @returns previous page or null if not available
   */
  private getPreviousPage = () => {
    const currentIndex = this.state.pages.findIndex((page) => {
      return page.id == this.props.pageId;
    });

    if (currentIndex > 0) {
      return this.state.pages[currentIndex - 1];
    }

    return null;
  }

  /**
   * Returns next page
   * 
   * @returns next page or null if not available
   */
  private getNextPage = () => {
    const currentIndex = this.state.pages.findIndex((page) => {
      return page.id == this.props.pageId;
    });

    if (currentIndex < this.state.pages.length - 1) {
      return this.state.pages[currentIndex + 1];
    }

    return null;
  }
  
  /**
   * Loads component data
   */
  private loadData = async () => {
    if (!this.props.accessToken) {
      return;
    }

    this.setState({
      loading: true
    });
    
    const pages = await this.getQueryPagesService().listQueryPages(this.props.panelId, this.props.queryId, false);
    const panel = await this.getPanelsService().findPanel(this.props.panelId);

    this.setState({
      loading: false,
      pages: pages,
      panel: panel
    });
  }

  /**
   * Resolves promise after given  time
   */
  private promiseAwait = (timeout: number) => {
    return new Promise((resolve) => {
      setTimeout(resolve, timeout);
    });
  }

  /**
   * Returns query pages API
   * 
   * @returns query pages API
   */
  private getQueryPagesService(): QueryPagesService {
    return Api.getQueryPagesService(this.props.accessToken);
  }

  /**
   * Returns panels API
   * 
   * @returns panels API
   */
  private getPanelsService(): PanelsService {
    return Api.getPanelsService(this.props.accessToken);
  }

  /**
   * Event handler for previous button click
   * 
   * @param event event
   */
  private onPreviousClick = async (event: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    event.preventDefault();
    event.stopPropagation();

    this.setState({
      previousSaving: true
    });

    const previousPage = this.getPreviousPage();

    await this.changePage(previousPage, true, false);
  }

  /**
   * Event handler for next button click
   * 
   * @param event event
   */
  private onNextClick = async (event: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    event.preventDefault();
    event.stopPropagation();

    this.setState({
      nextSaving: true
    });

    const nextPage = this.getNextPage();

    await this.changePage(nextPage, true, !nextPage);
  }

  /**
   * Event handler for skip button click
   * 
   * @param event event
   */
  private onSkipClick = async (event: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    event.preventDefault();
    event.stopPropagation();

    const nextPage = this.getNextPage();

    await this.changePage(nextPage, false, false);
  }

  /**
   * Event handler for react command events
   * 
   * @param event event
   */
  private onReactCommand = async (event: CommandEvent) => {
    if (event.detail.command == "disable-query-next") {
      this.props.queryValidationMessageUpdate(event.detail.data.reason || strings.panel.query.noAnswer);
    } else if (event.detail.command == "enable-query-next") {
      this.props.queryValidationMessageUpdate(null);
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
    queryValidationMessage: state.queryValidationMessage,
    accessToken: state.accessToken ? state.accessToken.token : null,
    locale: state.locale
  };
}

/**
 * Redux mapper for mapping component dispatches 
 * 
 * @param dispatch dispatch method
 */
function mapDispatchToProps(dispatch: React.Dispatch<actions.AppAction>) {
  return {
    queryValidationMessageUpdate: (queryValidationMessage: string | null) => dispatch(actions.queryValidationMessageUpdate(queryValidationMessage))
  };
}

const QueryComment = connect(mapStateToProps, mapDispatchToProps)(QueryNavigation);
export default QueryComment;
