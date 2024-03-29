import * as React from "react";
import * as _ from "lodash";
import PanelAdminLayout from "../../components/generic/panel-admin-layout";
import { Panel, PanelInvitation, PanelInvitationState, Query, User } from "../../generated/client/models";
import "../../styles/invitations.scss";
import { Grid, Container, List, Button, Icon, SemanticShorthandCollection, BreadcrumbSectionProps, Input, InputOnChangeData, Label, TextArea, TextAreaProps, Select, DropdownItemProps, Checkbox, CheckboxProps, DropdownProps, Message, Pagination } from "semantic-ui-react";
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
 * Type for invitation list
 */
type InvitationList = {
  items: PanelInvitation[],
  page: number;
  pageCount: number;
  totalCount: number;
};

/**
 * Type for state invitation list map
 */
type InvitationMap = { [key in PanelInvitationState ]: InvitationList };

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
  invitationMap: InvitationMap;
  skipInvitation: boolean;
  invitationTarget: number;
  password: string;
  messageHeader?: string;
  messageText?: string;
  containsAcceptLink: boolean;
  windowWidth: number;
}

/**
 * Amount of invitations per page
 */
const INVITATION_PAGE_SIZE = 10;

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
      invitationMap: this.emptyInvitationMap(),
      inviteEmail: "",
      inviteEmails: [],
      skipInvitation: false,
      invitationTarget: 0,
      password: (Math.random() + 0.1).toString(36).slice(2).substring(0, 8),
      containsAcceptLink: true,
      windowWidth: window.outerWidth
    };
  }

  /**
   * Component will mount life-cycle event
   */
  public componentDidMount = async () => {
    const { accessToken, panelId } = this.props;
    const { token, userId } = accessToken;

    this.setState({
      loading: true
    });

    const loads: Promise<any>[] = [];
    
    loads.push(Api.getPanelsApi(token).findPanel({ 
      panelId: panelId
    }));

    loads.push(Api.getQueriesApi(token).listQueries({ 
      panelId: panelId 
    }));

    loads.push(Api.getUsersApi(token).findUser({ 
      userId: userId 
    }));

    loads.push(this.loadInvitations({
      panelId: panelId,
      token: token
    }));

    const [ panel, queries, loggedUser, invitationMap ] = await Promise.all(loads);

    this.setState({
      loading: false,
      panel: panel,
      queries: queries,
      invitationMap: invitationMap,
      loggedUser: loggedUser,
      windowWidth: window.outerWidth
    });

    this.timer = setInterval(() => {
      this.updateInvitations();
    }, 1000 * 30);

    window.addEventListener("resize", this.onWindowResize);
  }

  /**
   * Component will unmount life-cycle event
   */
  public componentWillUnmount = () => {
    if (this.timer) {
      clearInterval(this.timer);
    }
    
    window.removeEventListener("resize", this.onWindowResize);
  }

  /** 
   * Component render method
   */
  public render() {
    const { panel, loggedUser, loading, error, redirectTo } = this.state;

    if (!panel || !loggedUser) {
      return null;
    }

    if (error) {
      return <ErrorDialog error={ error } onClose={ () => this.setState({ error: undefined }) } /> 
    }

    const breadcrumbs: SemanticShorthandCollection<BreadcrumbSectionProps> = [
      { key: "home", content: strings.generic.eDelphi, href: "/" },
      { key: "panel", content: panel.name, href: `/${panel.urlName}` },
      { key: "panel-admin", content: strings.generic.panelAdminBreadcrumb, href: `/panel/admin/dashboard.page?panelId=${panel.id}` },
      { key: "invitations", content: strings.panelAdmin.inviteUsers.breadcrumb, active: true }      
    ];

    return (
      <PanelAdminLayout loggedUser={ loggedUser } breadcrumbs={ breadcrumbs } loading={ loading } panel={ panel } redirectTo={ redirectTo }>
        <Container className="invite-users-screen-container">
          { this.renderMessageBlock() }
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
   * Renders message block
   */
  private renderMessageBlock = () => {
    const {
      skipInvitation,
      password,
      messageHeader,
      messageText
    } = this.state;

    if (messageHeader && messageText) {
      return (
        <div className="message-container">
          <Message info>
            <Message.Header>{ messageHeader }</Message.Header>
            <p>{ messageText }</p>
          </Message>
        </div>
      );
    }
    
    if (!skipInvitation) {
      return null;
    }

    return (
      <div className="message-container">
        <Message info>
          <Message.Header>{ strings.panelAdmin.inviteUsers.passwordHeader }</Message.Header>
          <p>{ strings.panelAdmin.inviteUsers.passwordText }<b>{ password }</b></p>
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
          <Input
            type="email"
            style={{ width: "100%", marginTop: 10, marginBottom: 10 }}
            value={ this.state.inviteEmail }
            onChange={ this.onEmailInputChange }
            placeholder={ strings.panelAdmin.inviteUsers.inviteBlock.emailPlaceholder }
          />
          <Button
            color="blue"
            size="tiny"
            disabled={ !EmailValidator.validate(this.state.inviteEmail) }
            onClick={ this.onAddInviteUserClick }
          >
            { strings.panelAdmin.inviteUsers.inviteBlock.addUser }
          </Button>
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
        <Input
          accept="text/csv"
          type="file"
          style={{ width: "100%" }}
          onChange={ this.onCsvInputChange }
        />
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
      PanelInvitationState.PENDING, 
      PanelInvitationState.SENDFAIL,
      PanelInvitationState.ADDED,
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
   * Renders users list
   */
  private renderUsersList = (invitationState: PanelInvitationState) => {
    const { invitationMap } = this.state;
    
    const invitations = invitationMap[invitationState];
    const listStrings = strings.panelAdmin.inviteUsers.usersListBlock.lists[invitationState];

    if (invitations.totalCount === 0) {
      return null;
    }
    
    return (
      <div key={ invitationState }>
        <h3> 
          <span>{ listStrings.title }</span>
          <span
            className="resend-all-link"
            color="blue"
            onClick={ () => this.resendInvitations(invitations.items) }
          >
            { strings.panelAdmin.inviteUsers.usersListBlock.resendInvitationToAllInPage }
          </span> 
        </h3>
        <List divided relaxed>
          { invitations.items.map(this.renderUsersListInvitation) }
        </List>
        <Grid>
          <Grid.Column textAlign="center">
            { this.renderUsersListPagination(invitationState) }
          </Grid.Column>
        </Grid>
      </div>
    );
  }

  /**
   * Renders users list pagination
   * 
   * @param invitationState invitation state
   * @returns users list pagination
   */
  private renderUsersListPagination = (invitationState: PanelInvitationState) => {
    const { invitationMap } = this.state;
    const invitations = invitationMap[invitationState];
    const siblingRange = this.getPaginationSiblingRange(invitations.page);

    return (
      <Pagination
        siblingRange={ siblingRange }
        activePage={ invitations.page + 1 }
        totalPages={ invitations.pageCount }
        boundaryRange={ 0 }
        size="mini"
        onPageChange={ async (event, data ) => {
          this.setState({
            loading: true
          })

          const updatedMap = { ...invitationMap };
          updatedMap[invitationState] = await this.loadStateInvitations((data.activePage as number) - 1, invitationState);

          this.setState({
            invitationMap: updatedMap,
            loading: false
          })
        }}
      />
    );
  }

  /**
   * Renders panel invitation list invitation
   * 
   * @param invitation invitation
   */
  private renderUsersListInvitation = (invitation: PanelInvitation) => {
    const listStrings = strings.panelAdmin.inviteUsers.usersListBlock.lists[invitation.state];
    const time = moment(invitation.lastModified).locale(strings.getLanguage()).format("LLL");
    const invitationTargetLabel = this.getInvitationTargetLabel(invitation);
    
    return (
      <List.Item key={ invitation.id }>
        <List.Icon name='user' size='large' verticalAlign='middle' color={ this.getInvitationIconColor(invitation.state) } />
        <List.Content>
          <List.Header>
            <span>{ invitation.email }{ invitationTargetLabel }</span>
            <span
              className="resend-link"
              color="blue"
              onClick={ () => this.resendInvitations([invitation]) }
            >
              { strings.panelAdmin.inviteUsers.usersListBlock.resendInvitationToUser }
            </span> 
          </List.Header>
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
        <h3>{ strings.panelAdmin.inviteUsers.inviteBlock.invitationFieldLabel }</h3>        
        <TextArea
          className="invite-template"
          value={ this.state.mailTemplate }
          onChange={ this.onMailTemplateChange }
        />
        { this.renderMailTemplateValidation() }        
      </div>
    );
  }

  /**
   * Renders mail template validation message if needed
   */
  private renderMailTemplateValidation = () => {
    if (this.state.containsAcceptLink) {
      return null;
    }

    return (
      <Label basic color='red' pointing>
        { strings.panelAdmin.inviteUsers.inviteBlock.acceptReplaceMissing }
      </Label>
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
        <Select
          style={{ width: "100%" }}
          options={ options }
          value={ this.state.invitationTarget }
          onChange={ this.onInvitationTargetChange }
        />
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
        <Button
        disabled={ !this.state.containsAcceptLink || this.state.inviteEmails.length === 0 }
        color="blue"
        onClick={ this.onSendInvitationsClick }
       >
         { strings.panelAdmin.inviteUsers.inviteBlock.sendInvitationsButtonLabel }
       </Button>
      </div>
    );
  }

  /**
   * Returns sibling range for pagination component based on the window size
   * 
   * @param page current page
   * @returns sibling range for pagination
   */
  private getPaginationSiblingRange = (page: number) => {
    const { windowWidth } = this.state;
    const min = 1;
    const max = 5;
    const correction = (page < 90 ? 0 : 2);
    return Math.max(min, max, Math.min(max - Math.ceil((1440 - windowWidth) / 180) - correction));
  }

  /**
   * Returns invitation target label
   * 
   * @param invitation invitation
   * @returns invitation target label
   */
  private getInvitationTargetLabel = (invitation: PanelInvitation): string => {
    if (invitation.queryId) {
      const queryName = this.state.queries.find(query => query.id === invitation.queryId)?.name || "";
      if (queryName) {
        return ` (${queryName})`
      }
    }

    return "";
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
      case PanelInvitationState.ADDED:    
        return "green";
      case PanelInvitationState.DECLINED:
      case PanelInvitationState.SENDFAIL:
        return "red";
    }

    return 'grey';
  }

  /**
   * Adds invitation emails to send queue
   * 
   * @param invitations invitations
   */
   private resendInvitations = (invitations: PanelInvitation[]) => {
    const inviteEmails = invitations.map(invitation => invitation.email);

    this.setState({
      inviteEmails: _.uniq([ ...this.state.inviteEmails, ...inviteEmails ])
    });
  }

  /**
   * Updates invitation list
   */
  private updateInvitations = async () => {
    const { accessToken, panelId } = this.props;

    const updatedMap = await this.loadInvitations({
      panelId: panelId,
      token: accessToken.token
    })
    
    this.setState({
      invitationMap: updatedMap
    });
  }

  /**
   * Loads invitation map
   * 
   * @param opts options
   * @returns invitation map
   */
  private loadInvitations = async (opts: { panelId: number, token: string }): Promise<InvitationMap> => {
    const { invitationMap } = this.state;

    const invitations = this.emptyInvitationMap();

    await Promise.all(Object.values(PanelInvitationState).map(async state => {            
      invitations[state] = await this.loadStateInvitations(invitationMap[state].page, state);
    }))

    return invitations;
  }

  /**
   * Loads invitation list for a state
   * 
   * @param page page
   * @param state state
   * @returns invitation list for a state
   */
  private loadStateInvitations = async (page: number, state: PanelInvitationState): Promise<InvitationList> => {
    const { accessToken, panelId } = this.props;
    
    const response = await Api.getPanelInvitationsApi(accessToken.token).listPanelInvitationsRaw({ 
      panelId: panelId,
      state: state,
      firstResult: page * INVITATION_PAGE_SIZE,
      maxResults: INVITATION_PAGE_SIZE
    });

    const totalCount = parseInt(response.raw.headers.get("X-Total-Count") || "0") || 0;

    return {
      page: page,
      totalCount: totalCount,
      pageCount: Math.ceil(totalCount / INVITATION_PAGE_SIZE),
      items: await response.value()
    }
  }

  /**
   * Returns empty invitation map
   * 
   * @returns empty invitation map
   */
  private emptyInvitationMap = (): InvitationMap => {
    return {
      ACCEPTED: this.emptyInvitationList(),
      ADDED: this.emptyInvitationList(),
      BEING_SENT: this.emptyInvitationList(),
      DECLINED: this.emptyInvitationList(),
      IN_QUEUE: this.emptyInvitationList(),
      PENDING: this.emptyInvitationList(),
      SEND_FAIL: this.emptyInvitationList()
    };
  }

  /**
   * Returns empty invitation list
   * 
   * @returns empty invitation list
   */
  private emptyInvitationList = (): InvitationList => {
    return {
      items: [],
      page: 0,
      pageCount: 0,
      totalCount: 0
    };
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
    const { inviteEmails, skipInvitation, mailTemplate, password, invitationTarget } = this.state;

    const response = await Api.getPanelInvitationsApi(accessToken.token).createPanelInvitationRequestRaw({
      panelId: this.props.panelId,
      panelInvitationRequest: {
        emails: inviteEmails,
        skipInvitation: skipInvitation,
        invitationMessage: mailTemplate,
        password: password,
        targetQueryId: invitationTarget ? invitationTarget : undefined 
      }
    }); 

    const newUserCount = parseInt(response.raw.headers.get("X-New-User-Count") || "0") || 0;
    const invitedUserCount = inviteEmails.length;
    const message = this.getInvitationsSentMessage(skipInvitation, password, newUserCount, invitedUserCount);

    this.setState({
      messageHeader: message.messageHeader,
      messageText: message.messageText
    });
  }

  /**
   * Returns invitation success message according to given parameters.
   * 
   * @param skipInvitation whether to add users directly or use invitations
   * @param password a default password
   * @param newUserCount count of new users
   * @param invitedUserCount count of all added or invited users
   * @returns object containing message header and text
   */
  private getInvitationsSentMessage = (skipInvitation: boolean, password: string, newUserCount: number, invitedUserCount: number) => {
    return {
      messageHeader: skipInvitation ? strings.panelAdmin.inviteUsers.usersAddedHeader : strings.panelAdmin.inviteUsers.invitationsSentHeader,
      messageText: strings.formatString<string | number>(this.getInvitationsSentTextTemplate(skipInvitation, newUserCount, invitedUserCount), newUserCount, password, invitedUserCount) as string
    };
  }

  /**
   * Returns invitation success message text template according to given parameters.
   * 
   * @param skipInvitation whether to add users directly or use invitations
   * @param newUserCount count of new users
   * @param invitedUserCount count of all added or invited users
   * @returns template for users added text
   */
  private getInvitationsSentTextTemplate = (skipInvitation: boolean, newUserCount: number, invitedUserCount: number) => {
    if (!skipInvitation) {
      return strings.panelAdmin.inviteUsers.invitationsSentText;
    }

    const existingUserCount = invitedUserCount - newUserCount;
    if (existingUserCount == 0 && newUserCount > 0) {
      return strings.panelAdmin.inviteUsers.usersAddedOnlyNew;
    }

    if (existingUserCount > 0 && newUserCount == 0) {
      return strings.panelAdmin.inviteUsers.usersAddedOnlyExisting;
    }
    
    return strings.panelAdmin.inviteUsers.usersAddedNewAndExisting;
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
    const mailTemplate = data.value as string;
    const containsAcceptLink = mailTemplate.indexOf(strings.panelAdmin.inviteUsers.inviteBlock.acceptReplace) > -1;
    
    this.setState({
      mailTemplate: mailTemplate,
      containsAcceptLink: containsAcceptLink
    });
  }

  /**
   * Event handler for invite user add click
   */
  private onAddInviteUserClick = () => {
    this.setState({
      inviteEmails: _.uniq([ ...this.state.inviteEmails, this.state.inviteEmail ]),
      inviteEmail: "",
      messageHeader: undefined,
      messageText: undefined
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
   * @param _event event
   * @param data data
   */
  private onSkipInvitationChange = (_event: React.FormEvent<HTMLInputElement>, data: CheckboxProps) => {
    this.setState({
      skipInvitation: !!data.checked,
      messageHeader: undefined,
      messageText: undefined
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
            } else if (row.length > 2) {
              inviteEmails.push(row[2]);
            }
          });
        }
      }

      this.setState({
        inviteEmails: _.uniq([ ...this.state.inviteEmails, ...inviteEmails.filter(EmailValidator.validate) ]),
        loading: false,
        messageHeader: undefined,
        messageText: undefined
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
      skipInvitation: false,
      inviteEmails: []
    })
  }

  /**
   * Event handler for window resize
   */
  private onWindowResize = () => {
    this.setState({
      windowWidth: window.outerWidth
    });
  }

}
