import { ErrorResponse } from '../model/errorResponse';
import { User } from '../model/user';
import * as URI from "urijs";
import { ApiUtils } from "./api";

export class UsersService {

  private token: string;
  private basePath: string;

  constructor(basePath: string, token: string) {
    this.token = token;
    this.basePath = basePath;
  }


  /**
   * Finds an user by id
   * @summary Find user
   * @param userId user id
  */
  public findUser(userId: string, ):Promise<User> {
    const uri = new URI(`${this.basePath}/users/${encodeURIComponent(String(userId))}`);
    const options = {
      method: "get",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${this.token}`
      }
    };

    return fetch(uri.toString(), options).then((response) => {
      return ApiUtils.handleResponse(response);
    });
  }

}