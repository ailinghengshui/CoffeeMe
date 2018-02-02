package com.hzjytech.coffeeme.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by hehongcan on 2016/12/14.
 */
public class Material implements Parcelable {

        /**
         * id : 1
         * name : 糖
         * unit_price : 0.0
         * material_id : 3
         * max_weight : 2.0
         * water_rate : 40.0
         */

        private int id;
        private String name;
        private String adjust_price;
        private int material_id;
        private double max_weight;
        private double water_rate;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAdjust_price() {
            return adjust_price;
        }

        public void setAdjust_price(String unit_price) {
            this.adjust_price = adjust_price;
        }

        public int getMaterial_id() {
            return material_id;
        }

        public void setMaterial_id(int material_id) {
            this.material_id = material_id;
        }

        public double getMax_weight() {
            return max_weight;
        }

        public void setMax_weight(double max_weight) {
            this.max_weight = max_weight;
        }

        public double getWater_rate() {
            return water_rate;
        }

        public void setWater_rate(double water_rate) {
            this.water_rate = water_rate;
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.adjust_price);
        dest.writeInt(this.material_id);
        dest.writeDouble(this.max_weight);
        dest.writeDouble(this.water_rate);
    }

    public Material() {
    }

    protected Material(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.adjust_price = in.readString();
        this.material_id = in.readInt();
        this.max_weight = in.readDouble();
        this.water_rate = in.readDouble();
    }

    public static final Creator<Material> CREATOR = new Creator<Material>() {
        @Override
        public Material createFromParcel(Parcel source) {
            return new Material(source);
        }

        @Override
        public Material[] newArray(int size) {
            return new Material[size];
        }
    };

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", adjust_price='" + adjust_price + '\'' +
                ", material_id=" + material_id +
                ", max_weight=" + max_weight +
                ", water_rate=" + water_rate +
                '}';
    }
}
