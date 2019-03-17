import * as React from "react";
import * as moment from "moment";
import * as actions from "../actions";
import strings from "../localization/strings";
import { StoreState } from "../types";
import { connect } from "react-redux";
import { QueryQuestionComment } from "edelphi-client";

/**
 * Interface representing component properties
 */
interface Props {
  accessToken?: string,
  locale: string,
  rootComment: QueryQuestionComment
}

/**
 * Interface representing component state
 */
interface State {
}

/**
 * React component for comment editor
 */
class QueryRootComment extends React.Component<Props, State> {

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
   * Render edit pest view
   */
  public render() {
    return (
      <div key={ this.props.rootComment.id } className="queryComment">
        <a id={`comment.${this.props.rootComment.id}`}></a>
        <div className="queryCommentShowHideButton hideIcon"></div> 
        <div className="queryCommentHeader">
          <div className="queryCommentDate">{ strings.formatString(strings.panel.query.comments.commentDate, this.formatDateTime(this.props.rootComment.created)) } </div>
        </div>
        <div className="queryCommentContainerWrapper">
          <div className="queryCommentText">{ this.props.rootComment.contents }</div>
            <div className="queryCommentMeta">
              <div className="queryCommentNewComment"><a href="#" className="queryCommentNewCommentLink">{ strings.panel.query.comments.reply }</a></div>
              <div className="queryCommentShowComment"><a href="#" className="queryCommentShowCommentLink">{ strings.panel.query.comments.show }</a></div>
              <div className="queryCommentHideComment"><a href="#" className="queryCommentHideCommentLink">{ strings.panel.query.comments.hide }</a></div>
              <div className="queryCommentEditComment"><a href="#" className="queryCommentEditCommentLink">{ strings.panel.query.comments.edit }</a></div>
              <div className="queryCommentDeleteComment"><a href="#" className="queryCommentDeleteCommentLink">{ strings.panel.query.comments.edit }</a></div>
            </div>
          <div className="queryCommentChildren"> REPLIES </div>
        </div>
      </div>
    );
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

export default connect(mapStateToProps, mapDispatchToProps)(QueryRootComment);
