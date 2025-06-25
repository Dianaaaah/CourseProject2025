package com.example.mycompany.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Roles extends Observable {
    private List<Role> roles;

    public Roles() {
        roles = new ArrayList<>();
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        roles.add(role);
        setChanged();
        notifyObservers();
    }

    public void removeRole(Role role) {
        roles.remove(role);
        setChanged();
        notifyObservers();
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
        setChanged();
        notifyObservers();
    }
} 