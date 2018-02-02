package com.hzjytech.coffeeme.entities;

import java.io.Serializable;

/**
 * Created by dblr4287 on 2016/5/12.
 */
public class prePayInfo implements Serializable {
    private int id;

    private String identifier;

    private int br_type;

    private String sum;

    private int user_id;

    private String order_id;

    private int status;

    private String created_at;

    private String updated_at;

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setIdentifier(String identifier){
        this.identifier = identifier;
    }
    public String getIdentifier(){
        return this.identifier;
    }
    public void setBr_type(int br_type){
        this.br_type = br_type;
    }
    public int getBr_type(){
        return this.br_type;
    }
    public void setSum(String sum){
        this.sum = sum;
    }
    public String getSum(){
        return this.sum;
    }
    public void setUser_id(int user_id){
        this.user_id = user_id;
    }
    public int getUser_id(){
        return this.user_id;
    }
    public void setOrder_id(String order_id){
        this.order_id = order_id;
    }
    public String getOrder_id(){
        return this.order_id;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public void setCreated_at(String created_at){
        this.created_at = created_at;
    }
    public String getCreated_at(){
        return this.created_at;
    }
    public void setUpdated_at(String updated_at){
        this.updated_at = updated_at;
    }
    public String getUpdated_at(){
        return this.updated_at;
    }

}
