package com.hzjytech.coffeeme.entities;

import java.io.Serializable;

/**
 * Created by Hades on 2016/3/16.
 */
public class AppItem  implements Serializable{


    /**
     * id:22
     * name : 卡布奇诺
     * description : 启动页广告后台设置及API开发
     * name_en:英文名
     * volume : 260
     * price : 10.0
     * current_price : 0.1
     * image_key : lCIY6SMF-Selection_053.png
     * "app_image": "img_url"
     * creat_at:创建时间
     */

    private int id;
    private String name;
    private String description;
    private int volume;
    private float price;
    private float current_price;
    private String image_key;
    private String name_en;
    private String app_image;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    private String created_at;
    public int getId() {
        return id;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setCurrent_price(float current_price) {
        this.current_price = current_price;
    }

    public void setImage_key(String image_key) {
        this.image_key = image_key;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getVolume() {
        return volume;
    }

    public float getPrice() {
        return price;
    }

    public float getCurrent_price() {
        return current_price;
    }

    public String getImage_key() {
        return image_key;
    }

    public String getApp_image() {
        return app_image;
    }

    public void setApp_image(String app_image) {
        this.app_image = app_image;
    }

    @Override
    public String toString() {
        return "AppItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", volume=" + volume +
                ", price=" + price +
                ", current_price=" + current_price +
                ", image_key='" + image_key + '\'' +
                ", name_en='" + name_en + '\'' +
                ", app_image='" + app_image + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
