import * as React from "react";
import * as actions from "../../actions";
import * as _ from "lodash";
import strings from "../../localization/strings";
import { StoreState } from "../../types";
import { connect } from "react-redux";
import Api, { QueryPage, PanelExpertiseGroup, PanelInterestClass, PanelExpertiseClass, QueryQuestionCommentCategory } from "edelphi-client";
import { QueryPagesService, PanelExpertiseService, QueryQuestionCommentCategoriesService } from "edelphi-client/dist/api/api";
import { Segment, Dimmer, Loader, DropdownItemProps, Select, DropdownProps, Table, Icon, Checkbox } from "semantic-ui-react";

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
  commentCategoryIds: number[] | "ALL",
  onExpertiseGroupsChanged: (expertiseGroupIds: number[] | "ALL") => void,
  onCommentCategoriesChanged: (selectedCommentCategories: number[] | "ALL") => void,
  onQueryPageChange: (queryPageId: number | "ALL") => void,
  onExportReportContentsPdfClick: () => void,
  onExportReportSpreadsheetCsvClick: () => void,
  onExportReportSpreadsheetGoogleSheetsClick: () => void,
  onExportReportContentsGoogleDocumentClick: () => void
}

/**
 * Interface representing component state
 */
interface State {
  loading: boolean,
  queryPages: QueryPage[],
  panelExpertiseGroups: PanelExpertiseGroup[],
  panelInterestClasses: PanelInterestClass[],
  panelExpertiseClasses: PanelExpertiseClass[],
  commentCategories: QueryQuestionCommentCategory[],
  commentCategoryMap: { [key: string] : QueryQuestionCommentCategory[] }
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
      loading: false,
      queryPages: [],
      panelExpertiseGroups: [],
      panelInterestClasses: [],
      panelExpertiseClasses: [],
      commentCategories: [],
      commentCategoryMap: {}
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
    const commentCategories = await this.loadCommentCategories();

    this.setState({
      loading: false,
      queryPages: queryPages,
      panelExpertiseGroups: panelExpertiseGroups,
      panelInterestClasses: panelInterestClasses,
      panelExpertiseClasses: panelExpertiseClasses,
      commentCategories: commentCategories,
      commentCategoryMap: this.getCommentCategoryMap(commentCategories)
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
      const commentCategories = await this.loadCommentCategories();

      this.setState({
        queryPages: queryPages,
        loading: false,
        commentCategories: commentCategories,
        commentCategoryMap: this.getCommentCategoryMap(commentCategories)
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
        { this.renderCommentCategoriesFilter() }
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
   * Renders comment categories filter
   */
  private renderCommentCategoriesFilter = () => {
    if (this.state.commentCategories.length == 0) {
      return null;
    }

    return ( 
      <div>
        <h3> { strings.panelAdmin.reports.exportFilterByCommentCategory } </h3>
        {
          Object.keys(this.state.commentCategoryMap).map((categoryName) => {
            const categoryIds = this.state.commentCategoryMap[categoryName].map((commentCategory) => commentCategory.id!);
            const selectedCategoryIds = this.getCommentCategoryIds();
            const checked = !!categoryIds.find((categoryId) => selectedCategoryIds.indexOf(categoryId) !== -1);
      
            return (
              <div> <Checkbox checked={ checked } onChange={ () => {
                let commentCategoryIds = [];
      
                if (checked) {
                  commentCategoryIds = selectedCategoryIds.filter((selectedCategoryId) => {
                    return categoryIds.indexOf(selectedCategoryId) === -1;
                  });
                } else {
                  commentCategoryIds = _.union(selectedCategoryIds, categoryIds);
                }
                
                this.props.onCommentCategoriesChanged(commentCategoryIds);
              }}/> <label>{ categoryName }</label> </div>
            );
          })
        }
      </div>
    );
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
          <a href="#" onClick={ this.onExportReportContentsGoogleDocumentClick }> { strings.panelAdmin.reports.exportReportGoogleDocument } </a>
        </div>
        <h3> { strings.panelAdmin.reports.exportCharts } </h3>
        <div>
          <a href="#" className="disabled"> { strings.panelAdmin.reports.exportChartsPNG } </a>
        </div>
        <h3> { strings.panelAdmin.reports.exportData } </h3>
        <div>
          <a href="#" onClick={ this.onExportReportSpreadsheetCsvClick }> { strings.panelAdmin.reports.exportDataCSV } </a>
        </div>
        <div>
          <a href="#" onClick={ this.onExportReportSpreadsheetGoogleSheetsClick }> { strings.panelAdmin.reports.exportDataGoogleSpreadsheet } </a>
        </div>
      </div>
    );
  }

  /**
   * Loads comment categories
   */
  private loadCommentCategories = async () => {
    const queryQuestionCommentCategoriesService = this.getQueryQuestionCommentCategoriesService();
    return await queryQuestionCommentCategoriesService.listQueryQuestionCommentCategories(this.props.panelId, undefined, this.props.queryId);
  }

  /**
   * Returns comment category map
   * 
   * @return comment category map
   */
  private getCommentCategoryMap = (commentCategories: QueryQuestionCommentCategory[]) => {
    const result: { [key: string] : QueryQuestionCommentCategory[] } = {};

    for (let i = 0; i < commentCategories.length; i++) {
      const name = commentCategories[i].name;
      result[name] = result[name] || [];
      result[name].push(commentCategories[i]);
    }

    return result;
  }

  /**
   * Returns comment categoy ids as array
   * 
   * @return comment categoy ids as array
   */
  private getCommentCategoryIds = () => {
    if (this.props.commentCategoryIds == "ALL") {
      return this.state.commentCategories.map(category => category.id!);
    }

    return this.props.commentCategoryIds;
  }

  /**
   * Event handler for export as PDF click
   */
  private onExportReportContentsPdfClick = (event: React.MouseEvent<HTMLAnchorElement, MouseEvent>) => {
    event.preventDefault();
    this.props.onExportReportContentsPdfClick();
  }

  /**
   * Event handler for export as PDF click
   */
  private onExportReportContentsGoogleDocumentClick = (event: React.MouseEvent<HTMLAnchorElement, MouseEvent>) => {
    event.preventDefault();
    this.props.onExportReportContentsGoogleDocumentClick();
  }

  /**
   * Event handler for export as CSV click
   */
  private onExportReportSpreadsheetCsvClick = (event: React.MouseEvent<HTMLAnchorElement, MouseEvent>) => {
    event.preventDefault();
    this.props.onExportReportSpreadsheetCsvClick();
  }  

  /**
   * Event handler for export as Google Sheet click
   */
  private onExportReportSpreadsheetGoogleSheetsClick = (event: React.MouseEvent<HTMLAnchorElement, MouseEvent>) => {
    event.preventDefault();
    this.props.onExportReportSpreadsheetGoogleSheetsClick();
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
   * Returns query comment categories API
   * 
   * @returns query comment categories API
   */
  private getQueryQuestionCommentCategoriesService(): QueryQuestionCommentCategoriesService {
    return Api.getQueryQuestionCommentCategoriesService(this.props.accessToken);
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
