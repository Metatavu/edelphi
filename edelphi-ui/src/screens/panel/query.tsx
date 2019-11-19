import * as React from "react";
import * as actions from "../../actions";
import PanelLayout from "../../components/generic/panel-layout";
import { User, Panel, Query } from "../../generated/client";
import { SemanticShorthandCollection, BreadcrumbSectionProps } from "semantic-ui-react";
import strings from "../../localization/strings";
import ErrorDialog from "../../components/error-dialog";
import api from "../../api/api";
import { StoreState, AccessToken } from "../../types";
import { connect } from "react-redux";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: AccessToken,
  panelSlug: string,
  querySlug: string
}

/**
 * Interface representing component state
 */
interface State {
  loading: boolean
  error?: Error,
  loggedUser?: User,
  panel?: Panel,
  query?: Query,
  redirectTo?: string
}

/**
 * QueryPage component
 */
class QueryPage extends React.Component<Props, State> {

  /**
   * Constructor
   *
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  /**
   * Component did mount life-cycle handler
   */
  public componentDidMount = async () => {
    this.setState({
      loading: true
    });

    const panels = await api.getPanelsService(this.props.accessToken.token).listPanels({
      urlName: this.props.panelSlug
    });

    const panel = panels.length ? panels[0] : undefined;
    if (!panel || !panel.id) {
      // TODO: handle panel not found gracefully  
      throw new Error("Could not find panel");
    }

    const queries = await api.getQueriesService(this.props.accessToken.token).listQueries({
      panelId: panel.id,
      urlName: this.props.querySlug
    });

    const query = queries.length ? queries[0] : undefined;
    if (!query || !query.id) {
      // TODO: handle query not found gracefully  
      throw new Error("Could not find query");
    }

    const loggedUser = await api.getUsersService(this.props.accessToken.token).findUser({
      userId: this.props.accessToken.userId
    });

    this.setState({
      loading: false,
      panel: panel,
      query: query,
      loggedUser: loggedUser
    });
  }

  /**
   * Component render method
   */
  public render() {
    if (!this.state.panel || !this.state.query || !this.state.loggedUser) {
      return null;
    }

    if (this.state.error) {
      return <ErrorDialog error={ this.state.error } onClose={ () => this.setState({ error: undefined }) } /> 
    }

    const breadcrumbs: SemanticShorthandCollection<BreadcrumbSectionProps> = [
      { key: "home", content: strings.generic.eDelphi, href: "/" },
      { key: "panel", content: this.state.panel.name, href: `/${this.state.panel.urlName}` },
      { key: "query", content: this.state.query.name, active: true }      
    ];
    
    return (
      <PanelLayout loggedUser={ this.state.loggedUser } breadcrumbs={ breadcrumbs } loading={ this.state.loading } panel={ this.state.panel } redirectTo={ this.state.redirectTo }>
        { this.props.panelSlug } / { this.props.querySlug } 
      </PanelLayout>
    );
  }
}

/**
 * Redux mapper for mapping store state to component props
 * 
 * @param state store state
 */
function mapStateToProps(state: StoreState) {
  return {
    accessToken: state.accessToken as AccessToken
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

export default connect(mapStateToProps, mapDispatchToProps)(QueryPage);
