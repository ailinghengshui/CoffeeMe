package com.hzjytech.coffeeme.entities;

/**
 * Created by hehongcan on 2016/12/19.
 */
public class RestoreAppDosages {
    /**
     * id : 191
     * weight : 0
     * water : 78
     * selfWater:0
     * sequence : 2
     * material_name : 糖
     */

    private int id;
    private float weight;
    private float water;
    private float selfWater;
    private int sequence;
    private String material_name;

    public RestoreAppDosages(int id, float weight, float selfWater,float water, int sequence, String material_name) {
        this.id = id;
        this.weight = weight;
        this.selfWater=selfWater;
        this.water = water;
        this.sequence = sequence;
        this.material_name = material_name;
    }

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

    public int getId() {
        return id;
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

    public float getSelfWater() {
        return selfWater;
    }

    public void setSelfWater(float selfWater) {
        this.selfWater = selfWater;
    }
}
