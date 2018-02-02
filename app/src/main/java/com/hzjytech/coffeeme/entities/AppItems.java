package com.hzjytech.coffeeme.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Hades on 2016/5/6.
 */
public class AppItems implements Parcelable {


    /**
     * id : 65
     * name : 32
     * volume : 260
     * price : 0.0
     * description : 启动页广告后台设置及API开发

     * current_price : null
     * created_at : 2016-04-26T18:54:12.000+08:00
     * updated_at:2016-04-26T18:54:12.000+08:00
     * app_dosages : [{"id":191,"weight":0,"water":78,"sequence":2,"material_name":"糖"},{"id":192,"weight":60,"water":50,"sequence":3,"material_name":"咖啡豆"},{"id":193,"weight":5,"water":57,"sequence":2,"material_name":"糖"}]
     */

    private int id;
    private String name;
    private int volume;
    private float price;
    private int parent_id;
    private String description;
    private float current_price;
    private String created_at;
    private String updated_at;
    /**
     * id : 191
     * weight : 0
     * water : 78
     * sequence : 2
     * material_name : 糖
     */

    private List<AppDosages> app_dosages;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCurrent_price(float current_price) {
        this.current_price = current_price;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setApp_dosages(List<AppDosages> app_dosages) {
        this.app_dosages = app_dosages;
    }
    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVolume() {
        return volume;
    }

    public float getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public float getCurrent_price() {
        return current_price;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public List<AppDosages> getApp_dosages() {
        return app_dosages;
    }
    @Override
    public String toString() {
        return "AppItems{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", volume=" + volume +
                ", price=" + price +
                ", parent_id=" + parent_id +
                ", description='" + description + '\'' +
                ", current_price=" + current_price +
                ", created_at='" + created_at + '\'' +",updated_at="+updated_at+'\''+
                ", app_dosages=" + app_dosages +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.volume);
        dest.writeFloat(this.price);
        dest.writeInt(this.parent_id);
        dest.writeString(this.description);
        dest.writeFloat(this.current_price);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
    }

    public AppItems() {
    }

    protected AppItems(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.volume = in.readInt();
        this.price = in.readFloat();
        this.description = in.readString();
        this.current_price = in.readFloat();
        this.created_at = in.readString();
        this.updated_at=in.readString();
        this.parent_id=in.readInt();
    }

    public static final Parcelable.Creator<AppItems> CREATOR = new Parcelable.Creator<AppItems>() {
        @Override
        public AppItems createFromParcel(Parcel source) {
            return new AppItems(source);
        }

        @Override
        public AppItems[] newArray(int size) {
            return new AppItems[size];
        }
    };
}
