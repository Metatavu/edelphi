import * as React from "react";
import * as actions from "../../actions";
import * as _ from "lodash";
import strings from "../../localization/strings";
import { StoreState } from "../../types";
import { connect } from "react-redux";
import Api, { QueryPage, PanelExpertiseGroup, PanelInterestClass, PanelExpertiseClass } from "edelphi-client";
import { QueryPagesService, PanelExpertiseService } from "edelphi-client/dist/api/api";
import { Segment, Dimmer, Loader, DropdownItemProps, Select, DropdownProps, Table, Icon } from "semantic-ui-react";

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
  expertiseGroupIds: number[] | "ALL",
  onExpertiseGroupsChanged: (expertiseGroupIds: number[] | "ALL") => void
  onQueryPageChange: (queryPageId: number | "ALL") => void,
  onExportReportContentsPdfClick: () => void
}

/**
 * Interface representing component state
 */
interface State {
  loading: boolean,
  queryPages: QueryPage[],
  panelExpertiseGroups: PanelExpertiseGroup[],
  panelInterestClasses: PanelInterestClass[],
  panelExpertiseClasses: PanelExpertiseClass[]
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
      queryPages: [],
      panelExpertiseGroups: [],
      panelInterestClasses: [],
      panelExpertiseClasses: []
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

    const panelExpertiseGroups = await this.getPanelExpertiseService().listExpertiseGroups(this.props.panelId);
    const panelInterestClasses = await this.getPanelExpertiseService().listInterestClasses(this.props.panelId);
    const panelExpertiseClasses = await this.getPanelExpertiseService().listExpertiseClasses(this.props.panelId);
    
    this.setState({
      loading: false,
      queryPages: queryPages,
      panelExpertiseGroups: panelExpertiseGroups,
      panelInterestClasses: panelInterestClasses,
      panelExpertiseClasses: panelExpertiseClasses
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
        <h3> { strings.panelAdmin.reports.exportFilterByExpertise } </h3>
        { this.renderExpertiseMatrixFilter() }
        <h3> { strings.panelAdmin.reports.exportFilterByPage } </h3>
        <div>
          <Select value={ this.props.queryPageId } options={ queryPageFilterOptions } onChange={ this.onPageSelected } />
        </div>
      </div>
    );
  }

  /**
   * Renders expertise matrix filter table
   */
  private renderExpertiseMatrixFilter = () => {
    return (
      <Table>
        { this.renderExpertiseMatrixHeader() }
        { this.renderExpertiseMatrixRows() }
      </Table>
    );
  }

  /**
   * Renders expertise matrix filter table header
   */
  private renderExpertiseMatrixHeader = () => {
    return ( 
      <Table.Header>
        <Table.Row>
          <Table.HeaderCell/>
          {
            this.state.panelExpertiseClasses.map((expertiseClass) => {
              return (<Table.HeaderCell>{ expertiseClass.name }</Table.HeaderCell>)
            })
          }
        </Table.Row>
      </Table.Header>
    )
  } 

  /**
   * Renders expertise matrix filter table rows
   */
  private renderExpertiseMatrixRows = () => {
    return this.state.panelInterestClasses.map((interestClass) => {
      return (
        <Table.Row>
          <Table.Cell> { interestClass.name } </Table.Cell>
          {
            this.state.panelExpertiseClasses.map((expertiseClass) => {
              return ( this.renderExpertiseMatrixCell(interestClass, expertiseClass) )
            })
          }
        </Table.Row>
      );
    });
  }

  /**
   * Renders expertise matrix filter table cell
   * 
   * @param interestClass interest class
   * @param expertiseClass expertise class
   */
  private renderExpertiseMatrixCell = (interestClass: PanelInterestClass, expertiseClass: PanelExpertiseClass) => {
    const expertiseGroupId = this.getExpertiseGroupId(interestClass.id, expertiseClass.id);
    const checked = this.props.expertiseGroupIds == "ALL" || this.props.expertiseGroupIds.indexOf(expertiseGroupId) != -1;

    return (
      <Table.Cell onClick={ () => this.onExpertiseGroupClick(expertiseGroupId) } textAlign='center'>
        <Icon name="check" color={ checked  ? "green" : "grey" } />
      </Table.Cell>
    );
  }

  /**
   * Returns an expertise group id for interest and expertise class ids
   * 
   * @param interestClassId interest class id
   * @param expertiseClassId expertise class id
   * @returns expertise group id
   */
  private getExpertiseGroupId = (interestClassId: number | undefined, expertiseClassId: number | undefined): number => {
    const group = this.state.panelExpertiseGroups.find((panelExpertiseGroup) => {
      return panelExpertiseGroup.expertiseClassId == expertiseClassId && panelExpertiseGroup.interestClassId == interestClassId;
    });

    if (!group || !group.id) {
      throw new Error("Could not find expertise group for interest group / expertise group");
    }

    return group.id;
  }

  /**
   * Event handler for expertise group click
   * 
   * @param expertiseGroupId clicked expertise group id
   */
  private onExpertiseGroupClick = (expertiseGroupId: number) => {
    if (this.props.expertiseGroupIds == "ALL") {
      this.props.onExpertiseGroupsChanged([expertiseGroupId]);
    } else {
      const result = this.props.expertiseGroupIds.indexOf(expertiseGroupId) != -1 ? _.without(this.props.expertiseGroupIds, expertiseGroupId) : [expertiseGroupId].concat(this.props.expertiseGroupIds);
      this.props.onExpertiseGroupsChanged(result.length ? result : "ALL");
    }
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

  /**
   * Returns panel expertise service
   * 
   * @returns panel expertise service
   */
  private getPanelExpertiseService(): PanelExpertiseService {
    return Api.getPanelExpertiseService(this.props.accessToken);
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
