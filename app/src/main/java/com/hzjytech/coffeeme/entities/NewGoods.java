package com.hzjytech.coffeeme.entities;

import java.util.List;

/**
 * Created by hehongcan on 2017/10/24.
 */

public class NewGoods {
    private int total;
    private List<NewGood> good;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<NewGood> getGood() {
        return good;
    }

    public void setGood(List<NewGood> good) {
        this.good = good;
    }
}
