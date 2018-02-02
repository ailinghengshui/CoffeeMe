package com.hzjytech.coffeeme.entities;

/**
 * Created by Hades on 2016/4/23.
 */
public class LocationNearby {

    private String name;
    private float distance;

    public LocationNearby(String name, float distance) {
        this.name = name;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return String.valueOf(distance);
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
