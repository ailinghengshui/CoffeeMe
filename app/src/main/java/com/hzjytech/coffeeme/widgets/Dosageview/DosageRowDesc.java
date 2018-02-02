package com.hzjytech.coffeeme.widgets.Dosageview;

/**
 * Created by Hades on 2016/4/18.
 */
public class DosageRowDesc {

    private String name;
    private String weight;
    private String unit;

    public DosageRowDesc(String name, Object weight, Object unit){
        this.name=name;
        this.weight=String.valueOf(weight);
        this.unit=String.valueOf(unit);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
