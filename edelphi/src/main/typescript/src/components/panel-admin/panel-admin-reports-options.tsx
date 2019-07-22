import * as React from "react";
import * as actions from "../../actions";
import * as _ from "lodash";
import strings from "../../localization/strings";
import { StoreState } from "../../types";
import { connect } from "react-redux";
import Api, { QueryPage } from "edelphi-client";
import { QueryPagesService } from "edelphi-client/dist/api/api";
import { Segment, Dimmer, Loader, DropdownItemProps, Select, DropdownProps } from "semantic-ui-react";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: string,
  logggedUserId?: string,
  locale: string,
  panelId: number,
  queryId: number,
  queryPageId: number | "ALL",
  onQueryPageChange: (queryPageId: number | "ALL") => void,
  onExportReportContentsPdfClick: () => void
}

/**
 * Interface representing component state
 */
interface State {
  loading: boolean,
  queryPages: QueryPage[]
}

/**
 * React component for reports options component
 */
class PanelAdminReportsOptions extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true,
      queryPages: []
    };
  }

  /**
   * Component will mount life-cycle event
   */
  public async componentDidMount() {
    this.setState({
      loading: true
    });

    const queryPagesService = this.getQueryPagesService();
    const queryPages = await queryPagesService.listQueryPages(this.props.panelId, this.props.queryId, true);
    
    this.setState({
      loading: false,
      queryPages: queryPages
    });
  }

  /**
   * Component did update life-cycle event
   * 
   * @param prevProps previous props
   * @param prevState previous state
   */
  public async componentDidUpdate(prevProps: Props, prevState: State) {
    if (this.props.queryId !== prevProps.queryId) {
      this.setState({
        loading: true
      });

      const queryPagesService = this.getQueryPagesService();
      const queryPages = await queryPagesService.listQueryPages(this.props.panelId, this.props.queryId, true);

      this.setState({
        queryPages: queryPages,
        loading: false
      });
    }
  }

  /** 
   * Component render method
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
    
    return (
      <div>
        { this.renderFilters() }
        { this.renderSettings() }
        { this.renderExports() }
      </div>
    );
  }

  /**
   * Renders filters block
   */
  private renderFilters = () => {
    const queryPageFilterOptions: DropdownItemProps[] = this.state.queryPages.map((queryPage): DropdownItemProps => {
      return {
        key: queryPage.id,
        text: queryPage.title,
        value: queryPage.id
      }
    });

    queryPageFilterOptions.unshift({
      key: "ALL",
      value: "ALL",
      text: strings.panelAdmin.reports.exportFilterByPageAll
    });

    return (
      <div className="block">
        <h2> { strings.panelAdmin.reports.exportFilter } </h2>
        <h3> { strings.panelAdmin.reports.exportFilterByPage } </h3>
        <div>
          <Select value={ this.props.queryPageId } options={ queryPageFilterOptions } onChange={ this.onPageSelected } />
        </div>
      </div>
    );
  }

  /**
   * Renders settings block
   */
  private renderSettings = () => {

  }

  /**
   * Renders exports block
   */
  private renderExports = () => {
    return (
      <div className="block">
        <h2> { strings.panelAdmin.reports.exportReport } </h2>
        <h3> { strings.panelAdmin.reports.exportReportContents } </h3>
        <div>
          <a href="#" onClick={ this.onExportReportContentsPdfClick }> { strings.panelAdmin.reports.exportReportPDF } </a>
        </div>  
        <div>
          <a href="#" className="disabled"> { strings.panelAdmin.reports.exportReportGoogleDocument } </a>
        </div>
        <h3> { strings.panelAdmin.reports.exportCharts } </h3>
        <div>
          <a href="#" className="disabled"> { strings.panelAdmin.reports.exportChartsPNG } </a>
        </div>
        <h3> { strings.panelAdmin.reports.exportData } </h3>
        <div>
          <a href="#" className="disabled"> { strings.panelAdmin.reports.exportDataCSV } </a>
        </div>
        <div>
          <a href="#" className="disabled"> { strings.panelAdmin.reports.exportDataGoogleSpreadsheet } </a>
        </div>
      </div>
    );
  }

  /**
   * Event handler for export as PDF click
   */
  private onExportReportContentsPdfClick = (event: React.MouseEvent<HTMLAnchorElement, MouseEvent>) => {
    event.preventDefault();
    this.props.onExportReportContentsPdfClick();
  }

  /**
   * Event for page filter selected change
   */
  private onPageSelected = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
    this.props.onQueryPageChange(parseInt(data.value as string) || "ALL");
  }

  /**
   * Returns query pages API
   * 
   * @returns query pages API
   */
  private getQueryPagesService(): QueryPagesService {
    return Api.getQueryPagesService(this.props.accessToken);
  }
  
}

/**
 * Redux mapper for mapping store state to component props
 * 
 * @param state store state
 */
function mapStateToProps(state: StoreState) {
  return {
    accessToken: state.accessToken ? state.accessToken.token : null,
    logggedUserId: state.accessToken ? state.accessToken.userId : null,
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

export default connect(mapStateToProps, mapDispatchToProps)(PanelAdminReportsOptions);
