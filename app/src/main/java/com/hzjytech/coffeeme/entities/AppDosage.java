package com.hzjytech.coffeeme.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hades on 2016/3/24.
 */
public class AppDosage implements Parcelable {

    /**
     * id : 1
     * name : 糖
     * unit_price : 2.0
     */

    private AppMaterialEntity app_material;
    /**
     * app_material : {"id":1,"name":"糖","unit_price":"2.0"}
     * max_weight : 12
     * min_weight : 1
     * water : 67
     * weight:2
     * sequence : 1
     */

    private int id;
    private float max_weight;
    private float min_weight;
    private float weight;
    private float water;
    private int sequence;

    public AppDosage(AppMaterialEntity app_material, int id, float max_weight, float min_weight, float weight, float water, int sequence) {
        this.app_material = app_material;
        this.id = id;
        this.max_weight = max_weight;
        this.min_weight = min_weight;
        this.weight = weight;
        this.water = water;
        this.sequence = sequence;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setApp_material(AppMaterialEntity app_material) {
        this.app_material = app_material;
    }

    public void setMax_weight(float max_weight) {
        this.max_weight = max_weight;
    }

    public void setMin_weight(float min_weight) {
        this.min_weight = min_weight;
    }

    public void setWater(float water) {
        this.water = water;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public AppMaterialEntity getApp_material() {
        return app_material;
    }

    public float getMax_weight() {
        return max_weight;
    }

    public float getMin_weight() {
        return min_weight;
    }

    public float getWater() {
        return water;
    }

    public int getSequence() {
        return sequence;
    }


    public static class AppMaterialEntity  implements Parcelable{

        private int id;
        private String name;
        private float unit_price;

        @Override
        public String toString() {
            return "AppMaterialEntity{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", unit_price=" + unit_price +
                    '}';
        }

        public AppMaterialEntity(int id, String name, float unit_price) {
            this.id = id;
            this.name = name;
            this.unit_price = unit_price;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setUnit_price(float unit_price) {
            this.unit_price = unit_price;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public float getUnit_price() {
            return unit_price;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.name);
            dest.writeFloat(this.unit_price);
        }

        public AppMaterialEntity() {
        }

        protected AppMaterialEntity(Parcel in) {
            this.id = in.readInt();
            this.name = in.readString();
            this.unit_price = in.readFloat();
        }

        public static final Creator<AppMaterialEntity> CREATOR = new Creator<AppMaterialEntity>() {
            @Override
            public AppMaterialEntity createFromParcel(Parcel source) {
                return new AppMaterialEntity(source);
            }

            @Override
            public AppMaterialEntity[] newArray(int size) {
                return new AppMaterialEntity[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.app_material, flags);
        dest.writeInt(this.id);
        dest.writeFloat(this.max_weight);
        dest.writeFloat(this.min_weight);
        dest.writeFloat(this.weight);
        dest.writeFloat(this.water);
        dest.writeInt(this.sequence);
    }

    public AppDosage() {
    }

    protected AppDosage(Parcel in) {
        this.app_material = in.readParcelable(AppMaterialEntity.class.getClassLoader());
        this.id = in.readInt();
        this.max_weight = in.readFloat();
        this.min_weight = in.readFloat();
        this.weight = in.readFloat();
        this.water = in.readFloat();
        this.sequence = in.readInt();
    }

    public static final Creator<AppDosage> CREATOR = new Creator<AppDosage>() {
        @Override
        public AppDosage createFromParcel(Parcel source) {
            return new AppDosage(source);
        }

        @Override
        public AppDosage[] newArray(int size) {
            return new AppDosage[size];
        }
    };

    @Override
    public String toString() {
        return "AppDosage{" +
                "app_material=" + app_material +
                ", id=" + id +
                ", max_weight=" + max_weight +
                ", min_weight=" + min_weight +
                ", weight=" + weight +
                ", water=" + water +
                ", sequence=" + sequence +
                '}';
    }
}
