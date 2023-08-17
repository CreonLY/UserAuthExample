package dao;

import entities.User;
import entities.exceptions.DataDuplicateException;
import entities.exceptions.DataNotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserTable {
    private volatile static UserTable singleton;
    private final Map<String, User> content = new HashMap<>();

    /**
     * sealed constructor to avoid creation of multiple instances
     */
    private UserTable() {}

    /**
     * lazy singleton object initialization & getting
     *
     * @return the singleton object
     */
    public static UserTable getInstance() {
        if (null == singleton) {
            synchronized (UserTable.class) {
                if (null == singleton)
                    singleton = new UserTable();
            }
        }
        return singleton;
    }

    public boolean createUser(User user) throws DataDuplicateException {
        User res = content.putIfAbsent(user.getName(), user);
        if (null != res)
            throw new DataDuplicateException(String.format("User \"%s\" is already created!", user.getName()));
        return true;
    }

    public boolean updateUser(User user) throws DataNotFoundException {
        User res = content.replace(user.getName(), user);
        if (null == res)
            throw new DataNotFoundException(String.format("User \"%s\" is not found!", user.getName()));
        return true;
    }

    public boolean deleteUser(User user) {
        return null != content.remove(user.getName());
    }

    public User getUser(String name) throws DataNotFoundException {
        User res = content.get(name);
        if (null == res)
            throw new DataNotFoundException(String.format("User \"%s\" is not found!", name));
        return res;
    }

    public User getUserById(String uuid) throws DataNotFoundException {
        Optional<User> res =
                content.values()
                        .stream()
                        .filter(user -> uuid.equals(user.getUuid()))
                        .findFirst();
        if (res.isEmpty())
            throw new DataNotFoundException(String.format("No such user with id: \"%s\" exists.", uuid));
        return res.get();
    }

    public Collection<User> getAll() {
        return content.values();
    }

}
