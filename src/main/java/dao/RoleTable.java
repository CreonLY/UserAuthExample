package dao;

import entities.Role;
import entities.exceptions.DataDuplicateException;
import entities.exceptions.DataNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoleTable {

    // using singleton pattern to mimic actual situation:
    // only one db connection is kept alive in memory
    private volatile static RoleTable singleton;
    private final Map<String, Role> content = new ConcurrentHashMap<>();

    /**
     * sealed constructor to avoid creation of multiple instances
     */
    private RoleTable() {}

    /**
     * lazy singleton object initialization
     *
     * @return the singleton object
     */
    public static RoleTable getInstance() {
        if (null == singleton) {
            synchronized (RoleTable.class) {
                if (null == singleton)
                    singleton = new RoleTable();
            }
        }
        return singleton;
    }

    /**
     * create a new role in cache
     *
     * @param name name of the new role
     * @return <i>true</i> only if the creation is successful and same value
     * is not in the system already, returns <i>false</i> otherwise
     */
    public boolean addRole(String name) throws DataDuplicateException {
        Role res = content.putIfAbsent(name, new Role(name));
        if (null != res)
            throw new DataDuplicateException(String.format("Role \"%s\" already exists!", name));
        return true;
    }

    /**
     * delete a row from cache
     *
     * @param name name of the role to be deleted
     * @return <i>true</i> only if the value exists and is successfully deleted, returns <i>false</i> otherwise
     */
    public boolean deleteRole(String name) {
        return null != content.remove(name);
    }

    public Role getRole(String name) throws DataNotFoundException {
        Role res = content.get(name);
        if (null == res)
            throw new DataNotFoundException(String.format("Role \"%s\" not found!", name));
        return res;
    }

    /**
     * check if a given role is in cache
     *
     * @param role name of the role
     * @return <i>true</i> if exists, <i>false</i> if not
     */
    public boolean hasRole(String role) {
        return content.containsKey(role);
    }
}
