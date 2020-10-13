import * as React from "react";
import * as actions from "../actions";
import * as _ from "lodash";
import { StoreState, AccessToken } from "../types";
import { connect } from "react-redux";
import PanelAdminLayout from "../components/generic/panel-admin-layout";
import { Panel, Query, QueryPage, ReportFormat, ReportType, User } from "../generated/client/models";
import "../styles/reports.scss";
import { PanelsApi, QueriesApi, ReportsApi, UsersApi } from "../generated/client/apis";
import * as queryString from "query-string";
import { Grid, Container, List, Modal, Button, Icon, SemanticShorthandCollection, BreadcrumbSectionProps } from "semantic-ui-react";
import strings from "../localization/strings";
import PanelAdminReportsQueryListItem from "../components/panel-admin/panel-admin-reports-query-list-item";
import PanelAdminReportsOptions from "../components/panel-admin/panel-admin-reports-options";
import ErrorDialog from "../components/error-dialog";
import Api from "../api";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken?: AccessToken,
  location: any
}

/**
 * Interface representing component state
 */
interface State {
  error?: Error,
  loggedUser?: User,
  panel?: Panel,
  queries: Query[],
  loading: boolean,
  redirectTo?: string,
  selectedQueryId?: number
  queryPages: QueryPage[],
  reportToEmailDialogVisible: boolean,
  filterQueryPageId: number | "ALL",
  expertiseGroupIds: number[] | "ALL",
  panelUserGroupIds: number[] | "ALL",
  commentCategoryIds: number[] | "ALL"
}

/**
 * React component for comment editor
 */
class Reports extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false,
      queries: [],
      reportToEmailDialogVisible: false,
      queryPages: [],
      filterQueryPageId: "ALL",
      expertiseGroupIds: "ALL",
      panelUserGroupIds: "ALL",
      commentCategoryIds: "ALL"
    };
  }

  /**
   * Component will mount life-cycle event
   */
  public componentDidMount = async () => {
    const { accessToken } = this.props;
    if (!accessToken) {
      return;
    }

    const queryParams = queryString.parse(this.props.location.search);
    
    const panelId = parseInt(queryParams.panelId as string);

    this.setState({
      loading: true
    });

    const panel = await Api.getPanelsApi(accessToken.token).findPanel({ panelId: panelId });
    const queries = await Api.getQueriesApi(accessToken.token).listQueries({ panelId: panelId });
    const loggedUser = await Api.getUsersApi(accessToken.token).findUser({ userId: accessToken.userId });

    this.setState({
      loading: false,
      panel: panel,
      queries: queries,
      loggedUser: loggedUser
    });
  }

  /** 
   * Component render method
   */
  public render() {
    if (!this.state.panel || !this.state.loggedUser) {
      return null;
    }

    if (this.state.error) {
      return <ErrorDialog error={ this.state.error } onClose={ () => this.setState({ error: undefined }) } /> 
    }

    const breadcrumbs: SemanticShorthandCollection<BreadcrumbSectionProps> = [
      { key: "home", content: strings.generic.eDelphi, href: "/" },
      { key: "panel", content: this.state.panel.name, href: `/${this.state.panel.urlName}` },
      { key: "panel-admin", content: strings.generic.panelAdminBreadcrumb, href: `/panel/admin/dashboard.page?panelId=${this.state.panel.id}` },
      { key: "reports", content: this.state.panel.name, active: true }      
    ];

    return (
      <PanelAdminLayout loggedUser={ this.state.loggedUser } breadcrumbs={ breadcrumbs } loading={ this.state.loading } panel={ this.state.panel } redirectTo={ this.state.redirectTo }>
        <Modal open={this.state.reportToEmailDialogVisible} >
          <Modal.Header> { strings.panelAdmin.reports.reportToEmailTitle } </Modal.Header>
          <Modal.Content> 
            <p>{ strings.panelAdmin.reports.reportToEmailMessageDelivery }. { strings.panelAdmin.reports.reportToEmailMessageLeave }</p>
            <br/>
            <p><i>{ strings.panelAdmin.reports.reportToEmailMessageDeliveryTime }</i></p>
          </Modal.Content>
          <Modal.Actions>
            <Button color='green' onClick={ () => { this.setState({reportToEmailDialogVisible: false })}} inverted>
              <Icon name='checkmark' /> 
              { strings.generic.ok }
            </Button>
          </Modal.Actions>
        </Modal>
        <Container className="reports-screen-container">
          <Grid>
            <Grid.Row>
              <Grid.Column width={ 6 }>
                <h1>{ strings.panelAdmin.reports.title }</h1>
              </Grid.Column>
            </Grid.Row>
            <Grid.Row>
              <Grid.Column width={ 6 } className="query-list-container">
                { this.renderQueriesList() }
              </Grid.Column>
              <Grid.Column width={ 10 } className="report-options-container">
                { this.renderReportOptions() }
              </Grid.Column>
            </Grid.Row>
          </Grid>
        </Container>
      </PanelAdminLayout>
    );
  }

  /**
   * Renders queries list
   */
  private renderQueriesList = () => {
    if (!this.state.panel || !this.state.panel.id) {
      return null;
    }

    const panelId: number = this.state.panel.id;

    return (
      <div className="query-list block">
        <h2>{ strings.panelAdmin.reports.queriesListTitle }</h2>
        <List divided relaxed>
          {
            this.state.queries.map((query) => {
              return (
                <PanelAdminReportsQueryListItem key={ query.id} query={ query } panelId={ panelId } selected={ this.state.selectedQueryId == query.id } onClick={ () => { this.setState({ selectedQueryId: query.id }); }}/>
              );
            })
          }
        </List>
      </div>
    );
  }

  /**
   * Renders report options
   */
  private renderReportOptions = () => {
    if (!this.state.panel || !this.state.panel.id || !this.state.selectedQueryId) {
      return null;
    }

    const { accessToken } = this.props;

    if (!accessToken) {
      return null;
    }

    return (
      <PanelAdminReportsOptions 
        accessToken={ accessToken.token }
        panelId={ this.state.panel.id } 
        queryId={ this.state.selectedQueryId } 
        queryPageId={ this.state.filterQueryPageId }
        expertiseGroupIds={ this.state.expertiseGroupIds }
        panelUserGroupIds={ this.state.panelUserGroupIds }
        commentCategoryIds={ this.state.commentCategoryIds }
        onQueryPageChange={ this.onQueryPageFilterChange }
        onExpertiseGroupsChanged={ this.onExpertiseGroupsChanged }
        onPanelUserGroupsChanged={ this.onPanelUserGroupsChanged }
        onCommentCategoriesChanged={ this.onCommentCategoriesChanged }
        onExportReportContentsPdfClick={ this.onExportReportContentsPdfClick } 
        onExportReportSpreadsheetCsvClick={ this.onExportReportSpreadsheetCsvClick }
        onExportReportSpreadsheetGoogleSheetsClick={ this.onExportReportSpreadsheetGoogleSheetClick }
        onExportReportContentsGoogleDocumentClick={ this.onExportReportContentsGoogleDocumentClick }
        onExportReportImagesPngClick={ this.onExportReportImagesPngClick }
      />
    );
  }

  /**
   * Does a request for a report
   * 
   * @param type report type
   * @param format report format
   */
  private requestReport = async (type: ReportType, format: ReportFormat) => {
    const { accessToken } = this.props;
    if (!accessToken) {
      return;
    }

    try {
      if (!this.state.panel || !this.state.panel.id) {
        throw new Error("Could not load panel");
      }

      if (!this.state.selectedQueryId) {
        throw new Error("Query not selected");
      }

      await Api.getReportsApi(accessToken.token).createReportRequest({
        reportRequest: {
          format: format,
          type: type,
          panelId: this.state.panel.id,
          queryId: this.state.selectedQueryId,
          options: {
            expertiseGroupIds: this.state.expertiseGroupIds == "ALL" ? undefined : this.state.expertiseGroupIds,
            queryPageIds: this.state.filterQueryPageId == "ALL" ? undefined : [ this.state.filterQueryPageId ],
            panelUserGroupIds: this.state.panelUserGroupIds == "ALL" ? undefined : this.state.panelUserGroupIds,
            commentCategoryIds: this.state.commentCategoryIds == "ALL" ? undefined : this.state.commentCategoryIds
          }
        }
      });

      this.setState({
        reportToEmailDialogVisible: true
      });
    } catch (e) {
      this.setState({
        error: e
      });
    }
  }

  /**
   * Event handler for query page filter change
   * 
   * @param queryPageId query page id or ALL if filter is not applied
   */
  private onQueryPageFilterChange = (queryPageId: number | "ALL") => {
    this.setState({
      filterQueryPageId: queryPageId
    });
  }

  /**
   * Event handler for expertise group ids filter change
   * 
   * @param expertiseGroupIds expertise group ids or ALL if filter is not applied
   */
  private onExpertiseGroupsChanged = (expertiseGroupIds: number[] | "ALL") => {
    this.setState({
      expertiseGroupIds: expertiseGroupIds
    });
  }

  /**
   * Event handler for user group ids filter change
   * 
   * @param panelUserGroupIds user group ids or ALL if filter is not applied
   */
  private onPanelUserGroupsChanged = (panelUserGroupIds: number[] | "ALL") => {
    this.setState({
      panelUserGroupIds: panelUserGroupIds
    });
  }

  

  private onCommentCategoriesChanged = (commentCategoryIds: number[] | "ALL") => {
    this.setState({
      commentCategoryIds: commentCategoryIds
    });
  }

  /**
   * Event handler for export as PDF click
   */
  private onExportReportContentsPdfClick = async () => {
    await this.requestReport(ReportType.TEXT, ReportFormat.PDF);
  }

  /**
   * Event handler for export as CSV click
   */
  private onExportReportSpreadsheetCsvClick = async () => {
    await this.requestReport(ReportType.SPREADSHEET, ReportFormat.CSV);
  }

  /**
   * Event handler for export as Google Sheet click
   */
  private onExportReportSpreadsheetGoogleSheetClick = async () => {
    await this.requestReport(ReportType.SPREADSHEET, ReportFormat.GOOGLESHEET);
  } 

  /**
   * Event handler for export as Google Document click
   */
  private onExportReportContentsGoogleDocumentClick  = async () => {
    await this.requestReport(ReportType.TEXT, ReportFormat.GOOGLEDOCUMENT);
  } 

  /**
   * Event handler for export images as PNGs click
   */
  private onExportReportImagesPngClick  = async () => {
    await this.requestReport(ReportType.IMAGES, ReportFormat.PNG);
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

export default connect(mapStateToProps, mapDispatchToProps)(Reports);
