package com.uca.apps.isi.taken.models;

import java.io.Serializable;

/**
 * Created by moisolutions on 30/10/17.
 */

public class User implements Serializable{
    private String email;
    private String password;
    private String username;
    private String realm;
    private int ttl;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }
}
