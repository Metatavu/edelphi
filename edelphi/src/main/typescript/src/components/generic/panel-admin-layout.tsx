import * as React from "react";
import { Redirect } from "react-router-dom";
import HeaderBackground from "../../gfx/header_background.png";
import { Panel, User } from "edelphi-client";
import "../../styles/generic.scss";
import { Container, Grid, Dimmer, Loader, Breadcrumb, SemanticShorthandCollection, BreadcrumbSectionProps } from "semantic-ui-react";
import strings from "../../localization/strings";

/**
 * Component props
 */
interface Props {
  redirectTo?: string,
  panel?: Panel,
  loading?: boolean,
  loggedUser: User,
  breadcrumbs: SemanticShorthandCollection<BreadcrumbSectionProps>
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
    return (
      <header style={{ backgroundImage: `url(${HeaderBackground})` }}>
        <Container>
          <Grid>
            <Grid.Row style={{ paddingBottom: 0 }}>
              <Grid.Column width={ 6 }>
                { this.renderTitle() }
              </Grid.Column>
              <Grid.Column width={ 5 } textAlign="center">
                { this.renderLocaleChange() } 
              </Grid.Column>
              <Grid.Column width={ 5 } textAlign="right">
                { this.remderProfileDetails() }
              </Grid.Column>
            </Grid.Row>
            <Grid.Row style={{ paddingTop: 0 }}>
              <Grid.Column width={ 16 }>
                { this.renderNavigation() }
              </Grid.Column>
            </Grid.Row>
            <Grid.Row style={{ paddingTop: "0px", paddingLeft: "10px" }}>
              <Grid.Column>
                  <Breadcrumb icon='right angle' sections={ this.props.breadcrumbs } />
                </Grid.Column>
              </Grid.Row>
          </Grid>
        </Container>
      </header>
    );
  }

  /**
   * Renders title
   */
  private renderTitle = () => {
    if (!this.props.panel) {
      return null;
    }

    return (
      <h1 className="header-title">
        <a className="root-link" href="/">eDelphi.org</a>
        <a className="panel-link" href={ "/" + this.props.panel.urlName }>{ this.props.panel.name }</a>
      </h1>
    );
  }

  /**
   * Renders back link
   */
  private renderNavigation = () => {
    if (!this.props.panel) {
      return null;
    }
    
    return (
      <nav className="header-nav">
        <a href={ `/${this.props.panel.urlName}` } className="header-nav-link"> { strings.panelAdmin.navigation.panel } </a>
        <a href={ `/panel/admin/dashboard.page?panelId=${this.props.panel.id}` } className="header-nav-link header-nav-link-selected"> { strings.panelAdmin.navigation.administration } </a>
        <a href={ `/panel/reportissue.page?panelId=${this.props.panel.id}` } className="header-nav-link"> { strings.panelAdmin.navigation.reportAnIssue } </a>
      </nav>
    );
  }

  /**
   * Renders locale change links
   */
  private renderLocaleChange = () => {
    const selectedLanguage = strings.getLanguage();

    return (
      <div>
        <a className={ selectedLanguage == "fi" ? "header-locale-link header-locale-link-selected" : "header-locale-link"} href="#" onClick={ this.onLocaleChangeFiClick }>Suomeksi</a>
        <a className={ selectedLanguage == "en" ? "header-locale-link header-locale-link-selected" : "header-locale-link"} href="#" onClick={ this.onLocaleChangeEnClick }>In English</a>
      </div>
    );
  }

  /**
   * Renders profile details
   */
  private remderProfileDetails = () => {
    return (
      <div style={{ marginTop: "10px" }}>
        <Grid>
          <Grid.Row>
            <Grid.Column width={ 12 }>
              <div style={{ color: "#fff" }}> { strings.formatString(strings.generic.welcomeUser, `${this.props.loggedUser.firstName} ${this.props.loggedUser.lastName}`) } </div>
              <div><a href="/profile.page"> { strings.generic.profileLink } </a></div>
              <div><a href="/logout.page"> { strings.generic.logoutLink } </a></div>
            </Grid.Column>
            <Grid.Column width={ 4 }>
              { this.renderProfileImage() }
            </Grid.Column>
          </Grid.Row>
        </Grid>
      </div>
    );
  }

  /**
   * Renders profile image
   */
  private renderProfileImage = () => {
    if (!this.props.loggedUser.profileImageUrl) {
      return null;
    }

    return (
      <img style={{ maxWidth: "65px", maxHeight: "58px" }} src={ this.props.loggedUser.profileImageUrl }/>
    );
  }

  /**
   * Changes legacy UI locale
   * 
   * @param locale locale
   */
  private changeLegacyLocale = async (locale: string) => {
    const date = new Date();
    date.setTime(date.getTime() + (3650*24*60*60*1000));
    const expires = "; expires=" + date.toUTCString();
    document.cookie = "eDelphiLocale=" + locale + expires + "; path=/";
    await (await fetch(`/locale/setlocale.json?locale=${locale}`)).json();
  }

  /**
   * Changes locale
   * 
   * @param locale locale
   */
  private changeLocale = async (locale: string) => {
    await this.changeLegacyLocale(locale);
    // TODO: locale to redux
    window.location.reload(true);
  }

  /**
   * Event handler for locale fi link click
   * 
   * @param event event
   */
  private onLocaleChangeFiClick = async (event: React.MouseEvent<HTMLAnchorElement, MouseEvent>) => {
    event.preventDefault();
    await this.changeLocale("fi");
  }

  /**
   * Event handler for locale en link click
   * 
   * @param event event
   */
  private onLocaleChangeEnClick = async (event: React.MouseEvent<HTMLAnchorElement, MouseEvent>) => {
    event.preventDefault();
    await this.changeLocale("en");
  }
}

export default PanelAdminLayout;