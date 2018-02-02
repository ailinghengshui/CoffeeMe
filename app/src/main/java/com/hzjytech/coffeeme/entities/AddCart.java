package com.hzjytech.coffeeme.entities;

/**
 * Created by hehongcan on 2017/10/24.
 */

public class AddCart {
    private String token;
    private int number;
    private DisplayItems.AppItem item;

    public AddCart(String token, int number, DisplayItems.AppItem item) {
        this.token = token;
        this.number = number;
        this.item = item;
    }
}
