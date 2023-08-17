package services.impl;

import dao.RoleTable;
import dao.SecretTable;
import dao.TokenCache;
import dao.UserTable;
import entities.BaseResponse;
import entities.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class AuthenticationServiceImplTest {

    UserTable userTable = UserTable.getInstance();
    TokenCache tokenCache = TokenCache.getInstance();
    SecretTable secretTable = SecretTable.getInstance();
    RoleTable roleTable = RoleTable.getInstance();

    private static final ThreadLocal<AuthenticationServiceImpl> authenticationService
            = ThreadLocal.withInitial(() -> null);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCreateUser() throws InterruptedException {
        Thread userThread1 = new Thread(() -> {
            if (null == authenticationService.get()) {
                authenticationService.set(new AuthenticationServiceImpl());
                AuthenticationServiceImpl service =
                        AuthenticationServiceImplTest.authenticationService.get();
                BaseResponse res = service.createUser("John", "123456");
                System.out.println(Thread.currentThread().getName() + ":" + res.getMessage());
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread userThread2 = new Thread(() -> {
            if (null == authenticationService.get()) {
                authenticationService.set(new AuthenticationServiceImpl());
                AuthenticationServiceImpl service =
                        AuthenticationServiceImplTest.authenticationService.get();
                BaseResponse res = service.createUser("John", "123456");
                System.out.println(Thread.currentThread().getName() + ":" + res.getMessage());
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread userThread3 = new Thread(() -> {
            if (null == authenticationService.get()) {
                authenticationService.set(new AuthenticationServiceImpl());
                AuthenticationServiceImpl service =
                        AuthenticationServiceImplTest.authenticationService.get();
                BaseResponse res = service.createUser("Micheal", "123456");
                System.out.println(Thread.currentThread().getName() + ":" + res.getMessage());
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        userThread1.start();
        userThread2.start();
        userThread3.start();
        Thread.sleep(300L);

        UserTable userTable = UserTable.getInstance();
        SecretTable secretTable = SecretTable.getInstance();
        Assert.assertTrue(userTable.getAll().stream().map(User::getName).anyMatch("John"::equals));
        Assert.assertTrue(userTable.getAll().stream().map(User::getName).anyMatch("Micheal"::equals));
    }

    @Test
    public void testDeleteUser() throws Throwable {
        User user = new User("deleteUser");
        userTable.createUser(user);
        tokenCache.upsertToken(user.getUuid(), "");
        secretTable.addSecret(user.getUuid(), "");

        AuthenticationServiceImpl service = new AuthenticationServiceImpl();
        BaseResponse res1 = service.deleteUser("deleteUser");
        System.out.println(res1.getMessage());
        Assert.assertTrue(Boolean.parseBoolean((String) res1.getData()));
        BaseResponse res2 = service.deleteUser("deleteUser");
        System.out.println(res2.getMessage());
        Assert.assertFalse(Boolean.parseBoolean((String) res2.getData()));
    }

    @Test
    public void testCreateRole() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl();
        BaseResponse res1 = service.createRole("createRole");
        Assert.assertTrue(Boolean.parseBoolean((String) res1.getData()));
        BaseResponse res2 = service.createRole("createRole");
        Assert.assertFalse(Boolean.parseBoolean((String) res2.getData()));
    }

    @Test
    public void testDeleteRole() {
        roleTable.addRole("deleteRole");

        AuthenticationServiceImpl service = new AuthenticationServiceImpl();
        BaseResponse res1 = service.deleteRole("deleteRole");
        System.out.println(res1.getMessage());
        Assert.assertTrue(Boolean.parseBoolean((String) res1.getData()));
        BaseResponse res2 = service.deleteRole("deleteRole");
        System.out.println(res2.getMessage());
        Assert.assertFalse(Boolean.parseBoolean((String) res2.getData()));
    }

    @Test
    public void testCreateUserRole() {
        User user = new User("testCreateUserRole");
        roleTable.addRole("newUserRole");
        userTable.createUser(user);

        AuthenticationServiceImpl service = new AuthenticationServiceImpl();
        BaseResponse res1 = service.createUserRole("testCreateUserRole", "newUserRole");
        Assert.assertTrue(Boolean.parseBoolean((String) res1.getData()));
        BaseResponse res2 = service.createUserRole("abc", "newUserRole");
        BaseResponse res3 = service.createUserRole("testCreateUserRole", "abc");
        Assert.assertFalse(Boolean.parseBoolean((String) res2.getData()));
        Assert.assertFalse(Boolean.parseBoolean((String) res3.getData()));

    }

    @Test
    public void testAuthUser() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl();
        service.createUser("testAuthUser", "testAuthUser");
        User user = userTable.getUser("testAuthUser");

        BaseResponse res1 = service.authUser("testAuthUser", "testAuthUser");
        Assert.assertTrue(Boolean.parseBoolean((String) res1.getData()));
        String token = tokenCache.getToken(user.getUuid());
        Assert.assertFalse(null == token || token.isEmpty());
    }

    @Test
    public void testInvalidateToken() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl();
        service.createUser("testInvalidateToken", "testInvalidateToken");
        User user = userTable.getUser("testInvalidateToken");

        service.authUser("testInvalidateToken", "testInvalidateToken");
        String token = tokenCache.getToken(user.getUuid());
        Assert.assertFalse(null == token || token.isEmpty());
        service.invalidateToken("testInvalidateToken");
        String token2 = tokenCache.getToken(user.getUuid());
        Assert.assertFalse(null == token2 || token2.isEmpty());
    }

    @Test
    public void testAuthUserRole() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl();
        service.createUser("authUserRole", "authUserRole");
        User user = userTable.getUser("authUserRole");
        service.createRole("authUserRole");
        service.authUser("authUserRole", "authUserRole");
        String token = tokenCache.getToken(user.getUuid());
        service.createUserRole("authUserRole", "authUserRole");
        BaseResponse res = service.authUserRole(token, "authUserRole");
        Assert.assertTrue(Boolean.parseBoolean((String) res.getData()));
    }

    @Test
    public void testGetUserRoles() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl();
        service.createUser("testGetUserRoles", "testGetUserRoles");
        User user = userTable.getUser("testGetUserRoles");
        service.authUser("testGetUserRoles", "testGetUserRoles");
        String token = tokenCache.getToken(user.getUuid());

        service.createRole("testGetUserRoles1");
        service.createRole("testGetUserRoles2");
        service.createUserRole("testGetUserRoles", "testGetUserRoles1");
        service.createUserRole("testGetUserRoles", "testGetUserRoles2");
        BaseResponse res = service.getUserRoles(token);
        Assert.assertEquals("testGetUserRoles1,testGetUserRoles2", (String) res.getData());
    }
}