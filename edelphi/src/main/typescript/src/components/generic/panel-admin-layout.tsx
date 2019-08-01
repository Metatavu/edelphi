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
      <header style={{ backgroundImage: `url(${HeaderBackground})` }}>
        <Container>
          <Grid>
            <Grid.Row>
              <Grid.Column width={ 6 }>
                <h1 className="header-title">
                  <a className="root-link" href="/">eDelphi.org</a>
                  <a className="panel-link" href={ "/" + this.props.panel.urlName }>{ this.props.panel.name }</a>
                </h1>
              </Grid.Column>
              <Grid.Column width={ 6 } textAlign="center">
                { this.renderLocaleChange() }
              </Grid.Column>
              <Grid.Column width={ 6 }>
              </Grid.Column>
            </Grid.Row>

            <Grid.Row>
              <Grid.Column>
                { this.renderBackLink() }
              </Grid.Column>
            </Grid.Row>
          </Grid>
        </Container>
      </header>
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