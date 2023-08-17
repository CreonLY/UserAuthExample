package dao;

import entities.Token;
import entities.exceptions.DataNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenCache {

    // using singleton pattern to mimic actual situation:
    // only one db connection is kept alive in memory
    private volatile static TokenCache singleton;
    private final Map<String, String> content = new ConcurrentHashMap<>();

    /**
     * sealed constructor to avoid creation of multiple instances
     */
    private TokenCache() {}

    /**
     * lazy singleton object initialization
     *
     * @return the singleton object
     */
    public static TokenCache getInstance() {
        if (null == singleton) {
            synchronized (TokenCache.class) {
                if (null == singleton)
                    singleton = new TokenCache();
            }
        }
        return singleton;
    }

    public String getToken(String uuid) {
        String res = content.get(uuid);
        if (null == res)
            throw new DataNotFoundException(String.format("No token for user id: \"%s\" found.", uuid));
        return res;
    }

    public boolean upsertToken(String uuid, String token) {
        return null == content.put(uuid, token);
    }

    public boolean deleteToken(String uuid) {
        return null != content.remove(uuid);
    }
}
