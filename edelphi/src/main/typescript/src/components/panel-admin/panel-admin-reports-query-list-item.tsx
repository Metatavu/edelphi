import * as React from "react";
import * as moment from "moment";
import * as actions from "../../actions";
import * as _ from "lodash";
import strings from "../../localization/strings";
import { StoreState } from "../../types";
import { connect } from "react-redux";
import Api, { Query, QueryPage } from "edelphi-client";
import { QueryPagesService } from "edelphi-client/dist/api/api";
import { List } from "semantic-ui-react";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: string,
  logggedUserId?: string,
  locale: string,
  panelId: number,
  query: Query,
  selected: boolean,
  onClick: () => void
}

/**
 * Interface representing component state
 */
interface State {
  loading: boolean
}

/**
 * React component for panel admin reports view query list item
 */
class PanelAdminReportsQueryListItem extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true
    };
  }

  /**
   * Component will mount life-cycle event
   */
  public async componentDidMount() {
    this.setState({
      loading: true
    });

    this.setState({
      loading: false
    });
  }

  /** 
   * Render edit pest view
   */
  public render() {
    return (
      <List.Item className={ this.getClassNames() } onClick={ this.props.onClick }>
        <List.Content>
          <List.Header as='a'>{ this.props.query.name }</List.Header>
          <List.Description as='a'>{ strings.formatString(strings.panelAdmin.reports.queriesListDates, this.formatDate(this.props.query.created), this.formatDate(this.props.query.lastModified)) }</List.Description>
        </List.Content>
      </List.Item>
    );
  }

  private getClassNames = () => {
    const result = ["query-admin-reports-query-list-item"];
    if (this.props.selected) {
      result.push("selected");
    }

    return result.join(" ");
  }

  /**
   * Formats date
   * 
   * @param date date
   * @return formatted date
   */
  private formatDate(date?: Date | string) {
    return moment(date).locale(this.props.locale).format("L"); 
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

export default connect(mapStateToProps, mapDispatchToProps)(PanelAdminReportsQueryListItem);
