import * as React from "react";
import * as moment from "moment";
import * as actions from "../actions";
import strings from "../localization/strings";
import { StoreState } from "../types";
import { connect } from "react-redux";
import Api, { QueryQuestionComment } from "edelphi-client";
import { Loader } from "semantic-ui-react";
import { QueryQuestionCommentsService } from "edelphi-client/dist/api/api";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken?: string,
  locale: string,
  comment: QueryQuestionComment,
  panelId: number,
  queryId: number,
  pageId: number
}

/**
 * Interface representing component state
 */
interface State {
  childComments?: QueryQuestionComment[],
}

/**
 * React component for comment editor
 */
class QueryCommentClass extends React.Component<Props, State> {

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
   * Component did update life-cycle event
   */
  public async componentWillMount() {
    this.loadChildComments();
  }

  /**
   * Component did update life-cycle event
   */
  public async componentDidUpdate() {
    this.loadChildComments();
  }

  /** 
   * Render edit pest view
   */
  public render() {
    return (
      <div key={ this.props.comment.id } className="queryComment">
        <a id={`comment.${this.props.comment.id}`}></a>
        <div className="queryCommentShowHideButton hideIcon"></div> 
        <div className="queryCommentHeader">
          <div className="queryCommentDate">{ strings.formatString(strings.panel.query.comments.commentDate, this.formatDateTime(this.props.comment.created)) } </div>
        </div>
        <div className="queryCommentContainerWrapper">
          <div className="queryCommentText">{ this.props.comment.contents }</div>
          <div className="queryCommentMeta">
            <div className="queryCommentNewComment"><a href="#" className="queryCommentNewCommentLink">{ strings.panel.query.comments.reply }</a></div>
            <div className="queryCommentShowComment"><a href="#" className="queryCommentShowCommentLink">{ strings.panel.query.comments.show }</a></div>
            <div className="queryCommentHideComment"><a href="#" className="queryCommentHideCommentLink">{ strings.panel.query.comments.hide }</a></div>
            <div className="queryCommentEditComment"><a href="#" className="queryCommentEditCommentLink">{ strings.panel.query.comments.edit }</a></div>
            <div className="queryCommentDeleteComment"><a href="#" className="queryCommentDeleteCommentLink">{ strings.panel.query.comments.edit }</a></div>
          </div>
          {
            this.renderChildComments()
          }          
        </div>
      </div>
    );
  }

  /**
   * Renders child comments
   */
  private renderChildComments() {
    if (!this.isRootComment()) {
      return null;
    }

    if (!this.state.childComments) {
      return <Loader/>
    }

    return (
      <div className="queryCommentChildren">
        { 
          this.state.childComments.map((comment) => {
            return <QueryComment comment={ comment } pageId={ this.props.pageId} panelId={ this.props.panelId} queryId={ this.props.queryId }/>
          })
        }        
      </div> 
    );
  }

  private async loadChildComments() {
    console.log("this.isRootComment()", this.isRootComment());

    if (!this.state.childComments && this.props.accessToken && this.props.comment.id && this.isRootComment()) {
      this.setState({
        childComments: await (this.getQueryQuestionCommentsService(this.props.accessToken)).listQueryQuestionComments(this.props.panelId, this.props.comment.id, this.props.queryId, this.props.pageId, undefined)
      });
    }
  }

  /**
   * Returns whether this is a root comment or not
   * 
   * @returns whether this is a root comment or not
   */
  private isRootComment() {
    return !this.props.comment.parentId;
  }

  /**
   * Formats date time
   * 
   * @param dateTime date time
   * @return formatted date time
   */
  private formatDateTime(dateTime?: Date | string) {
    return moment(dateTime).locale(this.props.locale).format("LLL"); 
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

const QueryComment = connect(mapStateToProps, mapDispatchToProps)(QueryCommentClass);
export default QueryComment;
