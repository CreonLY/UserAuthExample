package dao;

import entities.exceptions.DataNotFoundException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SecretTable {

    private volatile static SecretTable singleton;
    private final Map<String, String> content = new ConcurrentHashMap<>();

    /**
     * sealed constructor to avoid creation of multiple instances
     */
    private SecretTable() {}

    /**
     * lazy singleton object initialization & getting
     *
     * @return the singleton object
     */
    public static SecretTable getInstance() {
        if (null == singleton) {
            synchronized (SecretTable.class) {
                if (null == singleton)
                    singleton = new SecretTable();
            }
        }
        return singleton;
    }

    public boolean addSecret(String uuid, String pwd) {
        return null == content.put(uuid, pwd);
    }

    public String getSecret(String uuid){
        String res = content.get(uuid);
        if (null == res)
            throw new DataNotFoundException(String.format("User id: \"%s\" has no credential stored.", uuid));
        return res;
    }

    public boolean updateSecret(String uuid, String pwd) {
        return null != content.put(uuid, pwd);
    }

    public boolean deleteSecret(String uuid) {
        return null != content.remove(uuid);
    }

    public Set<Map.Entry<String, String>> getAll() {
        return content.entrySet();
    }
}
