package services.impl;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import dao.RoleTable;
import dao.SecretTable;
import dao.TokenCache;
import dao.UserTable;
import entities.BaseResponse;
import entities.Role;
import entities.User;
import entities.exceptions.AuthorizationFailException;
import entities.exceptions.DataDuplicateException;
import entities.exceptions.DataNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.AuthenticationService;
import utils.PasswordUtil;
import utils.TokenUtil;

import java.util.Collection;
import java.util.stream.Collectors;

public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserTable userTable = UserTable.getInstance();
    private final RoleTable roleTable = RoleTable.getInstance();
    private final SecretTable secretTable = SecretTable.getInstance();
    private final TokenCache tokenCache = TokenCache.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Override
    public BaseResponse createUser(String userName, String password) {
        User user = new User(userName);
        String encPwd = PasswordUtil.encode(password);

        try {
            synchronized (secretTable) {
                if (userTable.createUser(user)) {
                    secretTable.addSecret(user.getUuid(), encPwd);
                }
            }
        } catch (DataDuplicateException e) {
            logger.info(e.getMessage());
            return new BaseResponse(e.getMessage(), "false");
        }

        return new BaseResponse("success", "true");
    }

    @Override
    public BaseResponse deleteUser(String userName) {
        User user;

        try {
            user = userTable.getUser(userName);
            boolean r1 = userTable.deleteUser(user);
            boolean r2 = tokenCache.deleteToken(user.getUuid());
            boolean r3 = secretTable.deleteSecret(user.getUuid());
            if (!(r1 && r3))
                return new BaseResponse("failed", "false");
        } catch (DataNotFoundException e) {
            logger.info(e.getMessage());
            return new BaseResponse(e.getMessage(), "false");
        }
        return new BaseResponse("success", "true");
    }

    @Override
    public BaseResponse createRole(String roleName) {
        try {
            roleTable.addRole(roleName);
        } catch (DataDuplicateException e) {
            logger.info(e.getMessage());
            return new BaseResponse(e.getMessage(), "false");
        }

        return new BaseResponse("success", "true");
    }

    @Override
    public BaseResponse deleteRole(String roleName) {
        Role role;
        try {
            role = roleTable.getRole(roleName);
        } catch (DataNotFoundException e) {
            logger.info(e.getMessage());
            return new BaseResponse(e.getMessage(), "false");
        }

        synchronized (userTable) {
            roleTable.deleteRole(roleName);
            Collection<User> allUsers = userTable.getAll();
            allUsers.stream()
                    .filter(user -> user.getRoles().contains(role))
                    .forEach(user -> user.getRoles().remove(role));
        }
        return new BaseResponse("success", "true");
    }

    @Override
    public BaseResponse createUserRole(String userName, String roleName) {
        try {
            Role role = roleTable.getRole(roleName);
            User user = userTable.getUser(userName).clone();
            user.getRoles().add(role);
            userTable.updateUser(user);
            return new BaseResponse("success", "true");
        } catch (DataNotFoundException | CloneNotSupportedException e) {
            logger.info(e.getMessage());
            return new BaseResponse(e.getMessage(), "false");
        }
    }

    @Override
    public BaseResponse authUser(String userName, String password) {
        try {
            User user = userTable.getUser(userName);
            String secret = secretTable.getSecret(user.getUuid());
            if (PasswordUtil.verification(password, secret)) {
                String token = TokenUtil.getToken(user.getUuid());
                tokenCache.upsertToken(user.getUuid(), token);
                return new BaseResponse("success", "true");
            }
        } catch (DataNotFoundException e) {
            logger.info(e.getMessage());
            return new BaseResponse(e.getMessage(), "false");
        } catch (AuthorizationFailException ignored) {}
        return new BaseResponse("failed", "false");
    }

    @Override
    public BaseResponse invalidateToken(String userName) {
        try {
            User user = userTable.getUser(userName);
            String token = tokenCache.getToken(user.getUuid());
            if (TokenUtil.isValidated(token)) {
                tokenCache.deleteToken(userName);
                return new BaseResponse("success", "true");
            }
        } catch (DataNotFoundException e) {
            logger.info(e.getMessage());
            return new BaseResponse(e.getMessage(), "false");
        } catch (JWTVerificationException e) {
            return new BaseResponse("You're not authorized.", "false");
        }
        return new BaseResponse("failed", "false");
    }

    @Override
    public BaseResponse authUserRole(String token, String roleName) {
        try {
            DecodedJWT validate = TokenUtil.validate(token);
            String uuid = validate.getClaim("uuid").asString();
            User user = userTable.getUserById(uuid);
            Role role = roleTable.getRole(roleName);
            if (user.getRoles().contains(role))
                return new BaseResponse("success", "true");
        } catch (JWTVerificationException ignored) {

        } catch (DataNotFoundException e) {
            logger.info(e.getMessage());
            return new BaseResponse(e.getMessage(), "false");
        }
        return new BaseResponse("failed", "false");
    }

    @Override
    public BaseResponse getUserRoles(String token) {
        try {
            DecodedJWT validate = TokenUtil.validate(token);
            String uuid = validate.getClaim("uuid").asString();
            User user = userTable.getUserById(uuid);
            return new BaseResponse(
                    "success",
                    user.getRoles().stream().map(Role::getName).sorted().collect(Collectors.joining(",")));
        } catch (DataNotFoundException e) {
            logger.info(e.getMessage());
            return new BaseResponse(e.getMessage(), "false");
        } catch (JWTVerificationException ignored) {}
        return new BaseResponse("failed", "false");
    }
}
