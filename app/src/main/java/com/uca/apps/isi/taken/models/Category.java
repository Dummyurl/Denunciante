package com.uca.apps.isi.taken.models;

import java.io.Serializable;

/**
 * Created by Mario Arce on 16/10/2017.
 */

public class Category implements Serializable {

    private String name;
    private String icon;
    private int id;
    private Boolean enabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}