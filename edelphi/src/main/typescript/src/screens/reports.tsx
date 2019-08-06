import * as React from "react";
import * as actions from "../actions";
import * as _ from "lodash";
import { StoreState, AccessToken } from "../types";
import { connect } from "react-redux";
import PanelAdminLayout from "../components/generic/panel-admin-layout";
import Api, { Panel, Query, QueryPage, ReportFormat, ReportType } from "edelphi-client";
import "../styles/reports.scss";
import { PanelsService, QueriesService, ReportsService } from "edelphi-client/dist/api/api";
import * as queryString from "query-string";
import { Grid, Container, List, Modal, Button, Icon, SemanticShorthandCollection, BreadcrumbSectionProps } from "semantic-ui-react";
import strings from "../localization/strings";
import PanelAdminReportsQueryListItem from "../components/panel-admin/panel-admin-reports-query-list-item";
import PanelAdminReportsOptions from "../components/panel-admin/panel-admin-reports-options";
import ErrorDialog from "../components/error-dialog";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: AccessToken,
  location: any
}

/**
 * Interface representing component state
 */
interface State {
  error?: Error,
  panel?: Panel,
  queries: Query[],
  loading: boolean,
  redirectTo?: string,
  selectedQueryId?: number
  queryPages: QueryPage[],
  reportToEmailDialogVisible: boolean,
  filterQueryPageId: number | "ALL",
  expertiseGroupIds: number[] | "ALL"
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
      expertiseGroupIds: "ALL"
    };
  }

  /**
   * Component will mount life-cycle event
   */
  public async componentDidMount() {
    const queryParams = queryString.parse(this.props.location.search);
    
    const panelId = parseInt(queryParams.panelId as string);

    this.setState({
      loading: true
    });
    
    const panel = await this.getPanelsService().findPanel(panelId);
    const queries = await this.getQueriesService().listQueries(panelId);

    this.setState({
      loading: false,
      panel: panel,
      queries: queries
    });
  }

  /** 
   * Component render method
   */
  public render() {
    if (!this.state.panel) {
      return null;
    }

    if (this.state.error) {
      return <ErrorDialog error={ this.state.error } onClose={ () => this.setState({ error: undefined }) } /> 
    }

    const breadcrumbs: SemanticShorthandCollection<BreadcrumbSectionProps> = [
      { key: "home", content: strings.generic.eDelphi, link: true, href: "/" },
      { key: "panel", content: this.state.panel.name, link: true, href: `/${this.state.panel.urlName}` },
      { key: "reports", content: this.state.panel.name, active: true }      
    ];

    return (
      <PanelAdminLayout breadcrumbs={ breadcrumbs } loading={ this.state.loading } panel={ this.state.panel } redirectTo={ this.state.redirectTo }>
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
        <Container>
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

    return (
      <PanelAdminReportsOptions panelId={ this.state.panel.id } 
        queryId={ this.state.selectedQueryId } 
        queryPageId={ this.state.filterQueryPageId }
        expertiseGroupIds={ this.state.expertiseGroupIds } 
        onQueryPageChange={ this.onQueryPageFilterChange }
        onExpertiseGroupsChanged={ this.onExpertiseGroupsChanged }
        onExportReportContentsPdfClick={ this.onExportReportContentsPdfClick } 
        onExportReportSpreadsheetCsvClick={ this.onExportReportSpreadsheetCsvClick }
        onExportReportSpreadsheetGoogleSheetsClick={ this.onExportReportSpreadsheetGoogleSheetClick }
        onExportReportContentsGoogleDocumentClick={ this.onExportReportContentsGoogleDocumentClick }/>
    );
  }

  /**
   * Does a request for a report
   * 
   * @param type report type
   * @param format report format
   */
  private requestReport = async (type: ReportType, format: ReportFormat) => {
    try {
      if (!this.state.panel || !this.state.panel.id) {
        throw new Error("Could not load panel");
      }

      if (!this.state.selectedQueryId) {
        throw new Error("Query not selected");
      }

      const reportsService = this.getReportsService();

      await reportsService.createReportRequest({
        format: format,
        type: type,
        panelId: this.state.panel.id,
        queryId: this.state.selectedQueryId,
        options: {
          expertiseGroupIds: this.state.expertiseGroupIds == "ALL" ? undefined : this.state.expertiseGroupIds,
          queryPageIds: this.state.filterQueryPageId == "ALL" ? undefined : [ this.state.filterQueryPageId ]
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
   * Returns queries API
   * 
   * @returns queries API
   */
  private getQueriesService(): QueriesService {
    return Api.getQueriesService(this.props.accessToken.token);
  }

  /**
   * Returns panels API
   * 
   * @returns panels API
   */
  private getPanelsService(): PanelsService {
    return Api.getPanelsService(this.props.accessToken.token);
  }

  /**
   * Returns reports API
   * 
   * @returns reports API
   */
  private getReportsService(): ReportsService {
    return Api.getReportsService(this.props.accessToken.token);
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
   * Event handler for export as PDF click
   */
  private onExportReportContentsPdfClick = async () => {
    await this.requestReport("TEXT", "PDF");
  }

  /**
   * Event handler for export as CSV click
   */
  private onExportReportSpreadsheetCsvClick = async () => {
    await this.requestReport("SPREADSHEET", "CSV");
  }

  /**
   * Event handler for export as Google Sheet click
   */
  private onExportReportSpreadsheetGoogleSheetClick = async () => {
    await this.requestReport("SPREADSHEET", "GOOGLE_SHEET");
  } 

  /**
   * Event handler for export as Google Document click
   */
  private onExportReportContentsGoogleDocumentClick  = async () => {
    await this.requestReport("TEXT", "GOOGLE_DOCUMENT");
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