import * as React from "react";
import * as actions from "../actions";
import strings from "../localization/strings";
import QueryRootComment from "./query-root-comment";
import { StoreState } from "../types";
import { connect } from "react-redux";
import Api, { QueryQuestionComment } from "edelphi-client";
import { QueryQuestionCommentsService } from "edelphi-client/dist/api/api";
import { Loader } from "semantic-ui-react";

/**
 * Interface representing component properties
 */
interface Props {
  queryId: number,
  panelId: number,
  pageId: number,
  accessToken?: string,
  locale: string
}

/**
 * Interface representing component state
 */
interface State {
  rootComments?: QueryQuestionComment[]
}

/**
 * React component for comment editor
 */
class QueryCommentList extends React.Component<Props, State> {

  /**
   * Constructor
   * 
   * @param props component properties
   */
  constructor(props: Props) {
    super(props);
    this.state = { 

    };
  }

  /**
   * Component did update life-cycly event
   */
  public async componentDidUpdate() {
    if (!this.state.rootComments && this.props.accessToken) {
      this.setState({
        rootComments: await (this.getQueryQuestionCommentsService(this.props.accessToken)).listQueryQuestionComments(this.props.panelId, 0, this.props.queryId, this.props.pageId, undefined)
      });
    }
  }

  /** 
   * Render edit pest view
   */
  public render() {
    return (
      <div className="queryCommentList">
        <h2 className="querySubTitle queryCommentListSubTitle">{ strings.panel.query.comments.title }</h2>
        { this.renderRootComments() } 
      </div>
    );
  }

  /**
   * Renders root comments
   */
  private renderRootComments() {
    if (!this.state.rootComments || !this.props.accessToken) {
      return <Loader/>;
    }

    return <div className="queryCommentsContainer">
      {
        this.state.rootComments.map((rootComment) => {
          return <QueryRootComment rootComment={ rootComment }/>
        })
      } 
    </div>
  }

  /**
   * Returns query question comments API
   * 
   * @returns query question comments API
   */
  private getQueryQuestionCommentsService(accessToken: string): QueryQuestionCommentsService {
    return Api.getQueryQuestionCommentsService(accessToken);
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

export default connect(mapStateToProps, mapDispatchToProps)(QueryCommentList);
