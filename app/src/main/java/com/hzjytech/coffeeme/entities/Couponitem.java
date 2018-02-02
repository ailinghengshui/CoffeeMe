package com.hzjytech.coffeeme.entities;

import java.io.Serializable;

/**
 * Created by dblr4287 on 2016/5/12.
 */
public class Couponitem implements Serializable {

    /**
     * sum : 0.01
     * bonus : 1.0
     * description : 满0.01赠1.0元
     */

    private int sum;
    private int bonus;
    private String description;

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
