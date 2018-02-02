package com.hzjytech.coffeeme.entities;

import java.io.Serializable;

/**
 * Created by Hades on 2016/9/9.
 */
public class AppVersion implements Serializable{


    /**
     * update_version_id : 4
     * force_update : true
     * download_url : http://download.coffee-me.com/coffee-me.apk
     * description : xxx
     */

    private int update_version_id;
    private boolean force_update;
    private String download_url;
    private String description;

    public int getUpdate_version_id() {
        return update_version_id;
    }


    public boolean isForce_update() {
        return force_update;
    }


    public String getDownload_url() {
        return download_url;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
