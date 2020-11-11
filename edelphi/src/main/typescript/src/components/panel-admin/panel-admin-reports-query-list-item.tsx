import * as React from "react";
import moment from "moment";
import * as _ from "lodash";
import strings from "../../localization/strings";
import { Query } from "../../generated/client/models";
import { List, Icon } from "semantic-ui-react";

/**
 * Interface representing component properties
 */
interface Props {
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
      <List.Item onClick={ this.props.onClick }>
        { this.props.selected ? <Icon name='triangle right' /> : null }
        <List.Content>
          <List.Header as='a'>{ this.props.query.name }</List.Header>
          <List.Description as='a'>{ strings.formatString(strings.panelAdmin.reports.queriesListDates, this.formatDate(this.props.query.created), this.formatDate(this.props.query.lastModified)) }</List.Description>
        </List.Content>
      </List.Item>
    );
  }

  /**
   * Formats date
   * 
   * @param date date
   * @return formatted date
   */
  private formatDate(date?: Date | string) {
    return moment(date).locale(strings.getLanguage()).format("L"); 
  }

}

export default PanelAdminReportsQueryListItem
