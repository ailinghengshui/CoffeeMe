package com.hzjytech.coffeeme.entities;

/**
 * Created by Hades on 2016/5/16.
 */
public class GoodWithIngredients {

    /**
     * id : 4484
     * be_token : false
     * current_price : 0.10000000149011612
     * name : 卡布奇诺
     * original_price : 10
     * refund :  
     * item_id : 1
     * ingredients : [{"material_id":3, "name":"糖", "material_type":"Material", "value":3.0, "water":78.0, "sequence":2, "display_name":"糖", "display_value":"3.0克"}, {"material_id":4, "name":"咖啡豆", "material_type":"Material", "value":7, "water":50.0, "sequence":3, "display_name":"咖啡浓度", "display_value":"100%"}, {"material_id":3, "name":"糖", "material_type":"Material", "value":10.4, "water":57.0, "sequence":2, "display_name":"糖", "display_value":"10.4克"}, {"material_id":1, "material_type":"Material", "name":"水", "value":185}, {"material_id":2, "material_type":"Material", "name":"杯子", "value":1}]
     */

    private int id;
    private boolean be_token;
    private double current_price;
    private String name;
    private float original_price;
    private String refund;
    private int item_id;
    private String ingredients;

    public void setId(int id) {
        this.id = id;
    }

    public void setBe_token(boolean be_token) {
        this.be_token = be_token;
    }

    public void setCurrent_price(double current_price) {
        this.current_price = current_price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOriginal_price(float original_price) {
        this.original_price = original_price;
    }

    public void setRefund(String refund) {
        this.refund = refund;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public boolean isBe_token() {
        return be_token;
    }

    public double getCurrent_price() {
        return current_price;
    }

    public String getName() {
        return name;
    }

    public float getOriginal_price() {
        return original_price;
    }

    public String getRefund() {
        return refund;
    }

    public int getItem_id() {
        return item_id;
    }

    public String getIngredients() {
        return ingredients;
    }
}
