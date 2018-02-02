package com.hzjytech.coffeeme.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hehongcan on 2017/10/19.
 */

public class DrinkItem implements Parcelable{

    /**
     * id : 6
     * buy_enable : true
     * name : 卡布奇诺
     * description : 卡布奇诺
     * volume : 260
     * price : 10.0
     * sugar : 1
     * current_price : 1.0
     * image_key : r06Yztjo-Selection_054.png
     * image_url : img_url
     */

    private int id;
    private boolean buy_enable;
    private String name;
    private String name_en;
    private String description;
    private int volume;
    private double price;
    private int sugar;
    private double current_price;
    private String image_key;
    private String image_url;

    public DrinkItem(
            int id,
            boolean buy_enable,
            String name,
            String name_en,
            String description,
            int volume,
            double price,
            int sugar,
            double current_price,
            String image_key,
            String image_url) {
        this.id = id;
        this.buy_enable = buy_enable;
        this.name = name;
        this.name_en=name_en;
        this.description = description;
        this.volume = volume;
        this.price = price;
        this.sugar = sugar;
        this.current_price = current_price;
        this.image_key = image_key;
        this.image_url = image_url;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public boolean isBuy_enable() { return buy_enable;}

    public void setBuy_enable(boolean buy_enable) { this.buy_enable = buy_enable;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getDescription() { return description;}

    public void setDescription(String description) { this.description = description;}

    public int getVolume() { return volume;}

    public void setVolume(int volume) { this.volume = volume;}

    public double getPrice() { return price;}

    public void setPrice(double price) { this.price = price;}

    public int getSugar() { return sugar;}

    public void setSugar(int sugar) { this.sugar = sugar;}

    public double getCurrent_price() { return current_price;}

    public void setCurrent_price(double current_price) { this.current_price = current_price;}

    public String getImage_key() { return image_key;}

    public void setImage_key(String image_key) { this.image_key = image_key;}

    public String getImage_url() { return image_url;}

    public void setImage_url(String image_url) { this.image_url = image_url;}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeByte(this.buy_enable ? (byte) 1 : (byte) 0);
        dest.writeString(this.name);
        dest.writeString(this.name_en);
        dest.writeString(this.description);
        dest.writeInt(this.volume);
        dest.writeDouble(this.price);
        dest.writeInt(this.sugar);
        dest.writeDouble(this.current_price);
        dest.writeString(this.image_key);
        dest.writeString(this.image_url);
    }

    protected DrinkItem(Parcel in) {
        this.id = in.readInt();
        this.buy_enable = in.readByte() != 0;
        this.name = in.readString();
        this.name_en = in.readString();
        this.description = in.readString();
        this.volume = in.readInt();
        this.price = in.readDouble();
        this.sugar = in.readInt();
        this.current_price = in.readDouble();
        this.image_key = in.readString();
        this.image_url = in.readString();
    }

    public static final Creator<DrinkItem> CREATOR = new Creator<DrinkItem>() {
        @Override
        public DrinkItem createFromParcel(Parcel source) {return new DrinkItem(source);}

        @Override
        public DrinkItem[] newArray(int size) {return new DrinkItem[size];}
    };
}
