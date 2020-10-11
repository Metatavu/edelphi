import * as React from "react";
import * as _ from "lodash";
import PanelAdminLayout from "../../components/generic/panel-admin-layout";
import { Panel, PanelInvitation, PanelInvitationState, Query, User } from "../../generated/client/models";
import "../../styles/invitations.scss";
import { Grid, Container, List, Button, Icon, SemanticShorthandCollection, BreadcrumbSectionProps, Input, InputOnChangeData, Label, TextArea, TextAreaProps, Select, DropdownItemProps, Checkbox, CheckboxProps, DropdownProps, Message } from "semantic-ui-react";
import strings from "../../localization/strings";
import ErrorDialog from "../../components/error-dialog";
import Api from "../../api";
import moment from "moment";
import { SemanticCOLORS } from "semantic-ui-react/dist/commonjs/generic";
import * as EmailValidator from 'email-validator';
import Papa from "papaparse";
import { AccessToken } from "../../types";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: AccessToken;
  panelId: number;
}

/**
 * Interface representing component state
 */
interface State {
  error?: Error;
  loggedUser?: User;
  loading: boolean;
  panel?: Panel;
  redirectTo?: string;
  inviteEmail: string;
  inviteEmails: string[];
  mailTemplate: string;
  queries: Query[];
  panelInvitations: PanelInvitation[];
  skipInvitation: boolean;
  invitationTarget: number;
  password: string;
  message?: string;
}

/**
 * React component for invite users
 */
export default class InviteUsers extends React.Component<Props, State> {

  private timer?: any;

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false,
      queries: [],
      mailTemplate: strings.panelAdmin.inviteUsers.inviteBlock.mailTemplate,
      panelInvitations: [],
      inviteEmail: "",
      inviteEmails: [],
      skipInvitation: false,
      invitationTarget: 0,
      password: (Math.random() + 0.1).toString(36).slice(2).substring(0, 8)
    };
  }

  /**
   * Component will mount life-cycle event
   */
  public async componentDidMount() {
    const { accessToken } = this.props;

    this.setState({
      loading: true
    });
    
    const panel = await Api.getPanelsApi(accessToken.token).findPanel({ panelId: this.props.panelId });
    const queries = await Api.getQueriesApi(accessToken.token).listQueries({ panelId: this.props.panelId });
    const panelInvitations = await Api.getPanelInvitationsApi(accessToken.token).listPanelInvitations({ panelId: this.props.panelId });
    const loggedUser = await Api.getUsersApi(accessToken.token).findUser({ userId: accessToken.userId });

    this.setState({
      loading: false,
      panel: panel,
      queries: queries,
      panelInvitations: panelInvitations,
      loggedUser: loggedUser
    });

    this.timer = setInterval(() => {
      this.updateInvitations();
    }, 1000 * 30);
  }

  /**
   * Component will unmount life-cycle event
   */
  public componentWillUnmount() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  /** 
   * Component render method
   */
  public render() {
    if (!this.state.panel || !this.state.loggedUser) {
      return null;
    }

    if (this.state.error) {
      return <ErrorDialog error={ this.state.error } onClose={ () => this.setState({ error: undefined }) } /> 
    }

    const breadcrumbs: SemanticShorthandCollection<BreadcrumbSectionProps> = [
      { key: "home", content: strings.generic.eDelphi, href: "/" },
      { key: "panel", content: this.state.panel.name, href: `/${this.state.panel.urlName}` },
      { key: "invitations", content: strings.panelAdmin.inviteUsers.breadcrumb, active: true }      
    ];

    return (
      <PanelAdminLayout loggedUser={ this.state.loggedUser } breadcrumbs={ breadcrumbs } loading={ this.state.loading } panel={ this.state.panel } redirectTo={ this.state.redirectTo }>
        <Container className="invite-users-screen-container">
          { this.renderPasswordBlock() }
          <Grid>
            <Grid.Row>
              <Grid.Column width={ 16 }>
                <h1>{ strings.panelAdmin.inviteUsers.title }</h1>
              </Grid.Column>
            </Grid.Row>
            <Grid.Row>
              <Grid.Column width={ 7 }>
                { this.renderInviteBlock() }
              </Grid.Column>
              <Grid.Column width={ 9 }>
                { this.renderUsersListBlock() }
              </Grid.Column>
            </Grid.Row>
          </Grid>
        </Container>
      </PanelAdminLayout>
    );
  }

  /**
   * Renders generated password block
   */
  private renderPasswordBlock = () => {
    if (!this.state.skipInvitation) {
      return null;
    }

    return (
      <div className="message-container">
        <Message info>
          <Message.Header>{ strings.panelAdmin.inviteUsers.passwordHeader }</Message.Header>
          <p>{ strings.panelAdmin.inviteUsers.passwordText }<b>{ this.state.password }</b></p>
        </Message>
      </div>
    );
  }

  /**
   * Renders queries list
   */
  private renderInviteBlock = () => {
    if (!this.state.panel || !this.state.panel.id) {
      return null;
    }

    const panelId: number = this.state.panel.id;

    return (
      <div className="block">
        { this.renderInviteUserInput() }
        { this.renderCsvInput() }
        { this.renderInviteUsers() }
        { this.renderMailTemplate() }
        { this.renderInvitationTarget() }
        { this.renderSkipInvitation() }
        { this.renderSendInvitationsButton() }
      </div>
    );
  }

  /**
   * Renders user invite field
   */
  private renderInviteUserInput = () => {
    return (
      <>
        <h2>{ strings.panelAdmin.inviteUsers.inviteBlock.title }</h2>
        <div>
          <Input type="email" style={{ width: "100%", marginTop: 10, marginBottom: 10 }} value={ this.state.inviteEmail } onChange={ this.onEmailInputChange } placeholder={ strings.panelAdmin.inviteUsers.inviteBlock.emailPlaceholder }/>
          <Button color="blue" size="tiny" disabled={ !EmailValidator.validate(this.state.inviteEmail) } onClick={ this.onAddInviteUserClick }>{ strings.panelAdmin.inviteUsers.inviteBlock.addUser }</Button>
        </div>
      </>
    );
  }

  /**
   * Renders CSV input field
   */
  private renderCsvInput = () => {
    return (
      <>
        <h3>{ strings.panelAdmin.inviteUsers.inviteBlock.csvFieldLabel }</h3>
        <Input accept="text/csv" type="file" style={{ width: "100%" }} onChange={ this.onCsvInputChange } />
        <div className="example-csv-container">
          <a href={ `/_files/userimportexample_${strings.getLanguage()}.csv` }>{ strings.panelAdmin.inviteUsers.inviteBlock.csvExampleLinkLabel }</a>
        </div>
      </>
    );
  }

  /**
   * Renders queries list
   */
  private renderUsersListBlock = () => {
    const states: PanelInvitationState[] = [
      PanelInvitationState.INQUEUE, 
      PanelInvitationState.BEINGSENT, 
      PanelInvitationState.SENDFAIL, 
      PanelInvitationState.PENDING, 
      PanelInvitationState.ACCEPTED, 
      PanelInvitationState.DECLINED
    ];

      
    return (
      <div className="block">
        <h2>{ strings.panelAdmin.inviteUsers.usersListBlock.title }</h2>
        { states.map(this.renderUsersList) }
      </div>
    );
  }
  /**
   * Renders queries list
   */
  private renderUsersList = (invitationState: PanelInvitationState) => {
    const invitations = this.state.panelInvitations.filter(panelInvitation => panelInvitation.state == invitationState);
    const listStrings = strings.panelAdmin.inviteUsers.usersListBlock.lists[invitationState];

    if (invitations.length === 0) {
      return null;
    }
    
    return (
      <div key={ invitationState }>
        <h3> { listStrings.title } </h3>
        <List divided relaxed>
          { invitations.map(this.renderUsersListInvitation) }
        </List>
      </div>
    );
  }

  /**
   * Renders panel invitation list invitation
   * 
   * @param invitation invitation
   */
  private renderUsersListInvitation = (invitation: PanelInvitation) => {
    const listStrings = strings.panelAdmin.inviteUsers.usersListBlock.lists[invitation.state];
    const time = moment(invitation.created).locale(strings.getLanguage()).format("LLL");

    return (
      <List.Item key={ invitation.id }>
        <List.Icon name='user' size='large' verticalAlign='middle' color={ this.getInvitationIconColor(invitation.state) } />
        <List.Content>
          <List.Header>{ invitation.email }</List.Header>
          <List.Description>{ strings.formatString(listStrings.timeLabel, time) }</List.Description>
        </List.Content>
      </List.Item>
    );
  }

  /**
   * Renders invite users list
   */
  private renderInviteUsers = () => {
    return (
      <>
        <h3>{ strings.panelAdmin.inviteUsers.inviteBlock.usersToBeInvitedLabel }</h3>
        <div className="invite-users-list">
          { this.state.inviteEmails.map(this.renderInviteUser) }
        </div>
      </>
    );
  }

  /**
   * Renders an invite user label
   * 
   * @param inviteEmail invitation email address
   */
  private renderInviteUser = (inviteEmail: string) => {
    return (
      <Label color="blue" style={{ margin: 2 }}> 
        { inviteEmail }
        <Icon name="delete" onClick={ () => this.onInviteUserRemoveClick(inviteEmail) }/>
      </Label>
    );    
  }

  /**
   * Renders mail template
   */
  private renderMailTemplate = () => {
    return (
      <div>
        <h3> { strings.panelAdmin.inviteUsers.inviteBlock.invitationFieldLabel } </h3>
        <TextArea className="invite-template" value={ this.state.mailTemplate } onChange={ this.onMailTemplateChange }/>
      </div>
    );
  }

  /**
   * Renders mail template
   */
  private renderInvitationTarget = () => {
    const options: DropdownItemProps[] = [
      {
        key: "panel",
        value: 0,
        text: strings.panelAdmin.inviteUsers.inviteBlock.panelTarget
      }
    ];

    this.state.queries.forEach(query => {
      options.push({
        key: query.id,
        value: query.id,
        text: query.name
      });
    });
    
    return (
      <div>
        <h3> { strings.panelAdmin.inviteUsers.inviteBlock.invitationTarget } </h3>
        <Select style={{ width: "100%" }} options={ options } value={ this.state.invitationTarget } onChange={ this.onInvitationTargetChange }/>
      </div>
    );
  }

  /**
   * Renders skip invitation input
   */
  private renderSkipInvitation = () => {
    return (
      <div style={{ marginTop: 10, marginBottom: 10 }}>
        <Checkbox onChange={ this.onSkipInvitationChange } label={ strings.panelAdmin.inviteUsers.inviteBlock.addUsersWithoutInvitationLabel }/>
      </div>
    );
  }

  /**
   * Renders send invitations button
   */
  private renderSendInvitationsButton = () => {
    return (
      <div>
        <Button  disabled={ this.state.inviteEmails.length === 0 } color="blue" onClick={ this.onSendInvitationsClick }>{ strings.panelAdmin.inviteUsers.inviteBlock.sendInvitationsButtonLabel }</Button>
      </div>
    );
  }

  /**
   * Returns icon color for given status
   * 
   * @param invitationState invitation state 
   * @returns icon color for given status
   */
  private getInvitationIconColor = (invitationState: PanelInvitationState): SemanticCOLORS => {
    switch (invitationState) {
      case PanelInvitationState.ACCEPTED:
        return "green";
      case PanelInvitationState.DECLINED:
      case PanelInvitationState.SENDFAIL:
        return "red";
    }

    return 'grey';
  }

  /**
   * Updates invitation list
   */
  private updateInvitations = async () => {
    const { accessToken } = this.props;
    
    const panelInvitations = await Api.getPanelInvitationsApi(accessToken.token).listPanelInvitations({ panelId: this.props.panelId });

    this.setState({
      panelInvitations: panelInvitations
    });
  }

  /**
   * Read a file as string
   * 
   * @param file file
   * @returns string
   */
  private readFileAsBinaryString = (file: Blob): Promise<string> => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (event) => {
        const target: any = event.target;
        if (target && target.result) {
          resolve(target.result as any);
        } else {
          reject();
        }

      }
      reader.readAsBinaryString(file);
    });
  }

  /**
   * Invite users
   */
  private inviteUsers = async () => {
    const { accessToken } = this.props;

    await Api.getPanelInvitationsApi(accessToken.token).createPanelInvitationRequest({
      panelId: this.props.panelId,
      panelInvitationRequest: {
        emails: this.state.inviteEmails,
        skipInvitation: this.state.skipInvitation,
        invitationMessage: this.state.mailTemplate,
        password: this.state.password,
        targetQueryId: this.state.invitationTarget ? this.state.invitationTarget : undefined 
      }
    }); 
  }

  /**
   * Event handler for email input change
   * 
   * @param event event
   * @param data data
   */
  private onEmailInputChange = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
    this.setState({
      inviteEmail: data.value
    });
  }

  /**
   * Event handler for mail template change
   * 
   * @param event event
   * @param data data
   */
  private onMailTemplateChange = (event: React.FormEvent<HTMLTextAreaElement>, data: TextAreaProps) => {
    this.setState({
      mailTemplate: data.value as string
    });
  }

  /**
   * Event handler for invite user add click
   */
  private onAddInviteUserClick = () => {
    this.setState({
      inviteEmails: _.uniq([ ...this.state.inviteEmails, this.state.inviteEmail ]),
      inviteEmail: ""
    });
  }

  /**
   * Event handler for invite user remove click
   * 
   * @param inviteEmail invite email
   */
  private onInviteUserRemoveClick = (inviteEmail: string) => {
    this.setState({
      inviteEmails: _.without(this.state.inviteEmails, inviteEmail)
    });
  }

  /**
   * Event handler for skip invitaion change input
   * 
   * @param event event
   * @param data data
   */
  private onSkipInvitationChange = (event: React.FormEvent<HTMLInputElement>, data: CheckboxProps) => {
    this.setState({
      skipInvitation: !!data.checked
    });
  }

  /**
   * Event handler for invitation target change
   * 
   * @param event event
   * @param data data
   */
  private onInvitationTargetChange = (event: React.SyntheticEvent<HTMLElement>, data: DropdownProps) => {
    this.setState({
      invitationTarget: data.value as number
    });
  }

  /**
   * Event handler for CSV input change
   * 
   * @param event event
   */
  private onCsvInputChange = async(event: React.ChangeEvent<HTMLInputElement>) => {
    event.stopPropagation();
    const files = event.target.files;
    const file = files && files.length ? files[0] : null;
    
    if (file) {
      this.setState({
        loading: true
      });
      
      const inviteEmails: string[] = [];
      const data = await this.readFileAsBinaryString(file);
      if (data) {
        const parsed = Papa.parse(data);
        if (parsed) {
          const rows = parsed.data as string[][];
          rows.forEach(rowData => {
            const row = rowData.filter(cell => !!cell);
            if (row.length === 1) {
              inviteEmails.push(row[0]);
            } else if (row.length > 1) {
              inviteEmails.push(row[2]);
            }
          });
        }
      }

      this.setState({
        inviteEmails: _.uniq([ ...this.state.inviteEmails, ...inviteEmails.filter(EmailValidator.validate) ]),
        loading: false
      });
    }
  }

  /**
   * Event handler for send invitations click
   */
  private onSendInvitationsClick = async () => {
    this.setState({
      loading: true
    })

    await this.inviteUsers();

    this.setState({
      loading: false,
      inviteEmails: []
    })
  }

}