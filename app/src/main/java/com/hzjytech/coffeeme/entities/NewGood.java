package com.hzjytech.coffeeme.entities;

import java.io.Serializable;

/**
 * Created by hehongcan on 2017/10/24.
 */

public class NewGood implements Serializable{

    /**
     * id : 13166
     * created_at : 1473751597000
     * be_token : false
     * sugar : 3
     * item : {"id":6,"buy_enable":true,"name":"卡布奇诺","nameEn":"Cappuchino","description":"卡布奇诺",
     * "volume":260,"price":"10.0","current_price":"1.0",
     * "image_key":"r06Yztjo-Selection_054.png","image_url":"img_url"}
     */

    private int id;
    private String created_at;
    private boolean be_token;
    private int sugar;
    private DisplayItems.AppItem item;

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public String getCreated_at() { return created_at;}

    public void setCreated_at(String created_at) { this.created_at = created_at;}

    public boolean isBe_token() { return be_token;}

    public void setBe_token(boolean be_token) { this.be_token = be_token;}

    public int getSugar() { return sugar;}

    public void setSugar(int sugar) { this.sugar = sugar;}

    public DisplayItems.AppItem getItem() { return item;}

    public void setItem(DisplayItems.AppItem item) { this.item = item;}

}
