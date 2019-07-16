import * as React from "react";
import * as actions from "../actions";
import * as _ from "lodash";
import { StoreState, AccessToken } from "../types";
import { connect } from "react-redux";
import PanelAdminLayout from "../components/generic/panel-admin-layout";
import Api, { Panel, Query } from "edelphi-client";
import "../styles/comment-view.scss";
import { PanelsService, ReportsService, QueriesService } from "edelphi-client/dist/api/api";
import * as queryString from "query-string";

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
  panel?: Panel,
  queries: Query[],
  loading: boolean,
  redirectTo?: string
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
      queries: []
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
    return (
      <PanelAdminLayout loading={ this.state.loading } panel={ this.state.panel } onBackLinkClick={ this.onBackLinkClick } redirectTo={ this.state.redirectTo }>
        { this.renderQueries() }
      </PanelAdminLayout>
    );
  }

  private renderQueries = () => {
    return (
      <ul>
        {
          this.state.queries.map((query) => {
            return (
              <div>
                <a onClick={ () => { this.onQueryClick(query.id!) } }>
                  { query.name }
                </a>
              </div>
            );
          })
        }
      </ul>
    );
  }

  private onQueryClick = async (id: number) => {
    if (!this.state.panel || !this.state.panel.id) {
      throw new Error("Could not load panel");
    }

    const reportsService = this.getReportsService();

    await reportsService.createReportRequest({
      format: "PDF",
      panelId: this.state.panel.id,
      queryId: id
    });
  }

  /**
   * Event handler for back link click
   */
  private onBackLinkClick = () => {
    if (!this.state.panel || !this.state.panel.id) {
      return;
    }

    window.location.href = `/panel/admin/dashboard.page?panelId=${this.state.panel.id}`;
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