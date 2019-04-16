/**
 * eDelphi REST API
 * REST API for eDelphi
 *
 * OpenAPI spec version: 2.0.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { QueryQuestionLive2dAnswerData } from './queryQuestionLive2dAnswerData';


export interface QueryQuestionAnswer { 
    /**
     * Id of the query answer. Id is a composed from queryReplyId and queryReplyId by joining them with minus sign (e.g. 123-456)
     */
    readonly id?: string;
    queryPageId: number;
    queryReplyId: number;
    data: QueryQuestionLive2dAnswerData;
}
export interface QueryQuestionAnswerOpt { 
    /**
     * Id of the query answer. Id is a composed from queryReplyId and queryReplyId by joining them with minus sign (e.g. 123-456)
     */
    readonly id?: string;
    queryPageId?: number;
    queryReplyId?: number;
    data?: QueryQuestionLive2dAnswerData;
}