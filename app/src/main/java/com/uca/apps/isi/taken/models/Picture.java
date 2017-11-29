package com.uca.apps.isi.taken.models;

import java.io.Serializable;

/**
 * Created by Mario Arce on 03/11/2017.
 */

public class Picture implements Serializable {
    private String title;
    private String url;
    private int complaintId;
    private boolean enable;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
