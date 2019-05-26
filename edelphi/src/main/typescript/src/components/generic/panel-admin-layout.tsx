import * as React from "react";
import { Redirect } from "react-router-dom";
import HeaderBackground from "../../gfx/header_background.png";
import { Panel } from "edelphi-client";
import "../../styles/generic.scss";
import { Container, Grid, Dimmer, Loader } from "semantic-ui-react";
import strings from "../../localization/strings";

/**
 * Component props
 */
interface Props {
  redirectTo?: string,
  panel?: Panel,
  loading?: boolean,
  onBackLinkClick?: () => void
}

/**
 * Component state
 */
interface State {

}

/**
 * Generic layout for panel admin
 */
class PanelAdminLayout extends React.Component<Props, State> {

  /**
   * Component did mount life-cycle event
   */
  public componentDidMount = () => {
    window.scrollTo(0, 0);
  }

  /**
   * Component render method
   */
  public render() {
    if (this.props.redirectTo) {
      return <Redirect to={this.props.redirectTo} />
    }

    if (!this.props.panel || this.props.loading) {
      return (
        <Dimmer>
          <Loader>
            { strings.generic.loading }
          </Loader>
        </Dimmer>
      );
    }

    return (
      <div>
        { this.renderHeader() }
        { this.props.children }
      </div>  
    );
  }

  /**
   * Renders header
   */
  private renderHeader = () =>  {
    if (!this.props.panel) {
      return null;
    }

    return (
      <div className="header" style={{ backgroundImage: `url(${HeaderBackground})` }}>
        <Container>
          <Grid>
            <Grid.Row>
              <Grid.Column>
                <h1 className="header-title">
                  <a className="root-link" href="/">eDelphi.org</a>
                  <a className="panel-link" href={ "/" + this.props.panel.urlName }>{ this.props.panel.name }</a>
                </h1>
              </Grid.Column>
              <Grid.Column>
      
              </Grid.Column>
            </Grid.Row>

            <Grid.Row>
              <Grid.Column>
                { this.renderBackLink() }
              </Grid.Column>
            </Grid.Row>
          </Grid>
        </Container>
      </div>
    );
  }

  /**
   * Renders back link
   */
  private renderBackLink = () => {
    if (!this.props.onBackLinkClick) {
      return;
    }

    return (
      <a onClick={ this.props.onBackLinkClick } className="header-back-link"> { strings.generic.back } </a>
    );
  }
}

export default PanelAdminLayout;