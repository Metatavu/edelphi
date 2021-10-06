import { QueryQuestionComment } from "../generated/client";

/**
 * Utility class for comments
 */
export class CommentUtils {

  /**
   * Compares two comments. Prefers logged user's comment if comment is a root comment
   * 
   * @param a comment a
   * @param b comment b
   * @param loggedUserId logged user ID
   * @return compare result
   */
  public static compareComments = (a: QueryQuestionComment, b: QueryQuestionComment, loggedUserId?: string): number => {
    if (a.parentId || b.parentId || !loggedUserId || a.creatorId == b.creatorId) {
      return 0;
    }
    
    if (a.creatorId == loggedUserId) {
      return -1;
    }

    if (b.creatorId == loggedUserId) {
      return 1;
    }

    return 0;
  }
}