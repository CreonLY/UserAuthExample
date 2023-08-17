package entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User implements Cloneable {

    private String name;
    private String uuid = UUID.randomUUID().toString();
    private Set<Role> roles = new HashSet<>();

    public User(String name, String uuid, Collection<Role> roles) {
        this.name = name;
        this.uuid = uuid;
        this.roles.addAll(roles);
    }

    public User(String name) {
        this.name = name;
    }

    public User(String name, Role role) {
        this.name = name;
        this.roles.add(role);
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public User clone() throws CloneNotSupportedException {
        User clone = (User) super.clone();
        clone.setName(new String(name));
        clone.setUuid(new String(uuid));
        clone.setRoles(new HashSet<>(roles));
        return clone;
    }
}
