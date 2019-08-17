import { User } from '../model/user';
export declare class UsersService {
    private token;
    private basePath;
    constructor(basePath: string, token: string);
    /**
     * Finds an user by id
     * @summary Find user
     * @param userId user id
    */
    findUser(userId: string): Promise<User>;
}
