package com.hzjytech.coffeeme.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Hades on 2016/5/6.
 */
public class AppDosages implements Serializable {

    /**
     * id : 191
     * weight : 0
     * selfWater:咖啡自身的水
     * water : 78 除自身之外的水
     * sequence : 2
     * adjust_price:0.02价格，用于传递给recipeActivity重新计算价格
     * material_name : 糖
     */

    private int id;
    private float weight;
    private float selfWater;
    private float water;
    private int sequence;
    private float adjust_price;
    private String material_name;

    public void setId(int id) {
        this.id = id;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setWater(float water) {
        this.water = water;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public void setMaterial_name(String material_name) {
        this.material_name = material_name;
    }

    public float getAdjust_price() {
        return adjust_price;
    }

    public void setAdjust_price(float adjust_price) {
        this.adjust_price = adjust_price;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "AppDosages{" +
                "id=" + id +
                ", weight=" + weight +
                ", selfWater=" + selfWater +
                ", water=" + water +
                ", sequence=" + sequence +
                ", material_name='" + material_name + '\'' +
                '}';
    }

    public float getWeight() {
        return weight;
    }

    public float getWater() {
        return water;
    }

    public int getSequence() {
        return sequence;
    }

    public String getMaterial_name() {
        return material_name;
    }


    public AppDosages() {
    }

    public AppDosages(int id, float weight,float selfWater, float water,int sequence, String material_name) {
        this.id = id;
        this.weight = weight;
        this.selfWater=selfWater;
        this.water = water;
        this.sequence = sequence;
        this.material_name = material_name;
    }

    public float getSelfWater() {
        return selfWater;
    }

    public void setSelfWater(float selfWater) {
        this.selfWater = selfWater;
    }
}
