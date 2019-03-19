import * as React from "react";
import * as moment from "moment";
import * as actions from "../actions";
import QueryCommentContainer from "./query-comment-container";
import strings from "../localization/strings";
import { StoreState } from "../types";
import { connect } from "react-redux";
import Api, { QueryQuestionComment } from "edelphi-client";
import { QueryQuestionCommentsService } from "edelphi-client/dist/api/api";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken: string,
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
    this.state = { };
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
          { 
            this.renderLinks()
          }
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

    return <QueryCommentContainer className="queryCommentChildren" parentId={ this.props.comment.id! } pageId={ this.props.pageId } panelId={ this.props.panelId } queryId={ this.props.queryId }/>
  }

  /**
   * Renders comment links
   */
  private renderLinks() {
    return (
      <div className="queryCommentMeta">
        <div className="queryCommentNewComment"><a href="#" className="queryCommentNewCommentLink">{ strings.panel.query.comments.reply }</a></div>
        {
          this.props.comment.hidden 
            ? <div className="queryCommentShowComment"><a href="#" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onShowClick(event) } className="queryCommentShowCommentLink">{ strings.panel.query.comments.show }</a></div>
            : <div className="queryCommentHideComment"><a href="#" onClick={ (event: React.MouseEvent<HTMLElement>) => this.onHideClick(event) } className="queryCommentHideCommentLink">{ strings.panel.query.comments.hide }</a></div>
        }
        <div className="queryCommentEditComment"><a href="#" className="queryCommentEditCommentLink">{ strings.panel.query.comments.edit }</a></div>
        <div className="queryCommentDeleteComment"><a href="#" className="queryCommentDeleteCommentLink">{ strings.panel.query.comments.edit }</a></div>
      </div>  
    );
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

  /**
   * Click handler for show link
   * 
   * @param event event
   */
  private onShowClick(event: React.MouseEvent<HTMLElement>) {
    event.preventDefault();

    if (!this.props.accessToken || !this.props.comment.id) {
      return;
    }

    const queryQuestionCommentsService = this.getQueryQuestionCommentsService(this.props.accessToken);
    queryQuestionCommentsService.updateQueryQuestionComment({ ... this.props.comment, hidden: false }, this.props.panelId, this.props.comment.id);
  }

  /**
   * Click handler for hide link
   * 
   * @param event event
   */
  private onHideClick(event: React.MouseEvent<HTMLElement>) {
    event.preventDefault();

    if (!this.props.accessToken || !this.props.comment.id) {
      return;
    }

    const queryQuestionCommentsService = this.getQueryQuestionCommentsService(this.props.accessToken);
    queryQuestionCommentsService.updateQueryQuestionComment({ ... this.props.comment, hidden: true }, this.props.panelId, this.props.comment.id);
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
