package com.hzjytech.coffeeme.entities;

import java.io.Serializable;

/**
 * Created by Hades on 2016/4/29.
 */
public class Good  implements Serializable{


    /**
     * id : 2565
     * be_token : false
     * current_price : 925
     * name : 卡布奇诺
     * original_price : 10
     * refund :  
     * item_id : 1
     * ingredients :fjakhfja
     *
     * created_at
     */

    private int id;
    private boolean be_token;
    private float current_price;
    private String name;
    private float original_price;
    private String refund;
    private int item_id;
    private String ingredients;

    private String created_at;
    private String order_id;
    private int user_id;
    private NewestPriceBean newest_price;
    public Good() {
    }

    public Good(String name) {
        this.name = name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isBe_token() {
        return be_token;
    }

    public void setBe_token(boolean be_token) {
        this.be_token = be_token;
    }

    public float getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(float current_price) {
        this.current_price = current_price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(float original_price) {
        this.original_price = original_price;
    }

    public String getRefund() {
        return refund;
    }

    public void setRefund(String refund) {
        this.refund = refund;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
    public NewestPriceBean getNewest_price() {
        return newest_price;
    }

    public void setNewest_price(NewestPriceBean newest_price) {
        this.newest_price = newest_price;
    }
    public static class NewestPriceBean implements Serializable {
        /**
         * new_current_price : 3
         * new_original_price : 5
         */

        private float new_current_price;
        private float new_original_price;

        public float getNew_current_price() {
            return new_current_price;
        }

        public void setNew_current_price(float new_current_price) {
            this.new_current_price = new_current_price;
        }

        public float getNew_original_price() {
            return new_original_price;
        }

        public void setNew_original_price(float new_original_price) {
            this.new_original_price = new_original_price;
        }

        @Override
        public String toString() {
            return "NewestPriceBean{" +
                    "new_current_price=" + new_current_price +
                    ", new_original_price=" + new_original_price +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Good{" +
                "id=" + id +
                ", be_token=" + be_token +
                ", current_price=" + current_price +
                ", name='" + name + '\'' +
                ", original_price=" + original_price +
                ", refund='" + refund + '\'' +
                ", item_id=" + item_id +
                ", ingredients='" + ingredients + '\'' +
                ", created_at='" + created_at + '\'' +
                ", order_id='" + order_id + '\'' +
                ", user_id=" + user_id +
                ", newest_price=" + newest_price +
                '}';
    }
}
