package services;

import entities.BaseResponse;

public interface AuthenticationService {
    /**
     * create and store new user data to system
     * @param userName username
     * @param password literal password
     * @return boxed <i>true</i> object in response if succeeded, <i>false</i> otherwise
     */
    BaseResponse createUser(String userName, String password);

    /**
     * delete user from system
     * @param userName username
     * @return boxed <i>true</i> object in response if succeeded, <i>false</i> otherwise
     */
    BaseResponse deleteUser(String userName);

    /**
     * create new role in system
     * @param roleName role name
     * @return boxed <i>true</i> object in response if succeeded, <i>false</i> otherwise
     */
    BaseResponse createRole(String roleName);

    /**
     * delete role from system
     * @param roleName role name
     * @return boxed <i>true</i> object in response if succeeded, <i>false</i> otherwise
     */
    BaseResponse deleteRole(String roleName);

    /**
     * give new role to existing user
     * @param userName username
     * @param roleName rolename
     * @return boxed <i>true</i> object in response if succeeded, <i>false</i> otherwise
     */
    BaseResponse createUserRole(String userName, String roleName);

    /**
     * authorize
     * @param userName username
     * @param password literal password
     * @return current token if verified
     */
    BaseResponse authUser(String userName, String password);

    /**
     * invalidate user's token in system
     * @param userName username
     * @return boxed <i>true</i> object in response if succeeded, <i>false</i> otherwise
     */
    BaseResponse invalidateToken(String userName);

    /**
     * autorize if user have corresponding role
     * @param token user token
     * @param role role name
     * @return boxed <i>true</i> object in response if user exists and have given role
     * , <i>false</i> otherwise
     */
    BaseResponse authUserRole(String token, String role);

    /**
     * list all roles of the user
     * @param token user token
     * @return comma delimited list of roles of the user if exists
     */
    BaseResponse getUserRoles(String token);

}
