package com.example.mycompany.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Users extends Observable {
    private List<User> users;

    public Users() {
        users = new ArrayList<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
        setChanged();
        notifyObservers();
    }

    public void removeUser(User user) {
        users.remove(user);
        setChanged();
        notifyObservers();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        setChanged();
        notifyObservers();
    }
} 