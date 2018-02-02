package com.hzjytech.coffeeme.entities;

import java.io.Serializable;

/**
 * Created by Hades on 2016/5/16.
 */
public class Ingredient implements Serializable {


    /**
     * material_id : 3
     * name : 糖
     * material_type : Material
     * value : 3.0
     * water : 78.0
     * sequence : 2
     * display_name : 糖
     * display_value : 3.0克
     */

    private int material_id;
    private String name;
    private String material_type;
    private double value;
    private double water;
    private int sequence;
    private String display_name;
    private String display_value;

    public void setMaterial_id(int material_id) {
        this.material_id = material_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaterial_type(String material_type) {
        this.material_type = material_type;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setWater(double water) {
        this.water = water;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public void setDisplay_value(String display_value) {
        this.display_value = display_value;
    }

    public int getMaterial_id() {
        return material_id;
    }

    public String getName() {
        return name;
    }

    public String getMaterial_type() {
        return material_type;
    }

    public double getValue() {
        return value;
    }

    public double getWater() {
        return water;
    }

    public int getSequence() {
        return sequence;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getDisplay_value() {
        return display_value;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "material_id=" + material_id +
                ", name='" + name + '\'' +
                ", material_type='" + material_type + '\'' +
                ", value=" + value +
                ", water=" + water +
                ", sequence=" + sequence +
                ", display_name='" + display_name + '\'' +
                ", display_value='" + display_value + '\'' +
                '}';
    }
}
