package utils;

import junit.framework.TestCase;

public class PasswordUtilTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testEncode() {
        String res = PasswordUtil.encode("123456");
        assertNotNull(res);
    }

    public void testTestEncode() {
        String res = PasswordUtil.encode("123456", "EFEF");
        assertEquals(
                "PBKDF2&SHA256$10000$EFEF$520f11531d922a87df17a7db28343279fad6801418a7e2365fa0a2d4ef71e1fd",
                res);
    }

    public void testTestEncode1() {
        String res = PasswordUtil.encode("123456", 1000);
        assertNotNull(res);
    }

    public void testTestEncode2() {
        String res1 = PasswordUtil.encode("123456", "EFEF", 1000);
        assertEquals(
                "PBKDF2&SHA256$1000$EFEF$4bfad3ee6605f01731fc240fa96847f7abb8108ecf9292ff1e5425d66042ea69",
                res1);
        String res2 = PasswordUtil.encode("sd93ejfo28", "23HF", 100_000);
        assertEquals(
                "PBKDF2&SHA256$100000$23HF$9f1dd6b9e11d6344befea04179e15486245e2bcbb0c563e0be5a8cd91798e8cd",
                res2);
    }

    public void testVerification() {
        boolean res = PasswordUtil.verification(
                "123456",
                "PBKDF2&SHA256$1000$EFEF$4bfad3ee6605f01731fc240fa96847f7abb8108ecf9292ff1e5425d66042ea69");
        assertTrue(res);
    }

    public void testGetEncodedHash() {
        String res = PasswordUtil.getEncodedHash("123456", "EFEF", 1000);
        assertEquals(
                "4bfad3ee6605f01731fc240fa96847f7abb8108ecf9292ff1e5425d66042ea69",
                res);
    }

    public void testGetSalt() {
        String salt = PasswordUtil.getSalt();
        assertNotNull(salt);
    }
}