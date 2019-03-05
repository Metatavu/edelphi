import * as React from "react";
import { TextArea } from "semantic-ui-react";

/**
 * Interface representing component properties
 */
interface Props {
  queryReplyId: number | null
}

/**
 * Interface representing component state
 */
interface State {
}

/**
 * React component for comment editor
 */
class QueryCommentEditor extends React.Component<Props, State> {

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
      <div className="queryCommentEditor">
        <h2 className="querySubTitle">Kommentoi</h2>
        <div className="formFieldContainer formMemoFieldContainer">
          <TextArea className="formField formMemoField queryComment"/>
        </div>
      </div>
    );
  }
}

export default QueryCommentEditor;