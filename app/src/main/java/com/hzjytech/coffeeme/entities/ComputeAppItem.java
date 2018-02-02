package com.hzjytech.coffeeme.entities;

import java.util.List;

/**
 * Created by hehongcan on 2017/2/21.
 */
public class ComputeAppItem {

    /**
     * 用于获取某个自定义咖啡后重新计算价格
     * id : 22
     * name : 123456
     * volume : 260
     * price : 0.01
     * parent_id : 2
     * description : 123456
     * current_price : null
     * created_at : 2016-04-15T04:15:25.000-07:00
     * updated_at : 2016-04-15T04:15:25.000-07:00
     * 系统标准
     * sys_app_item : {"id":2,"name":"摩卡","volume":260,"price":"14.0","current_price":"8.4","app_dosages":[{"id":3,"weight":7,"water":80,"sequence":2,"material_name":"糖","sys_price":"0.0"},{"id":4,"weight":100,"water":40,"sequence":4,"material_name":"咖啡豆","sys_price":"0.5"},{"id":5,"weight":10,"water":48,"sequence":1,"material_name":"奶粉","sys_price":"0.15"},{"id":16,"weight":5,"water":36,"sequence":3,"material_name":"巧克力粉","sys_price":"0.15"}]}
     * app_dosages : [{"id":59,"weight":2.08219,"water":66,"sequence":2,"material_name":"糖"},{"id":60,"weight":100,"water":50,"sequence":3,"material_name":"咖啡豆"},{"id":61,"weight":8,"water":30,"sequence":1,"material_name":"奶粉"},{"id":62,"weight":7,"water":30,"sequence":4,"material_name":"巧克力粉"}]
     */

    private int id;
    private String name;
    private int volume;
    private String price;
    private int parent_id;
    private String description;
    private int current_price;
    private String created_at;
    private String updated_at;
    private SysAppItemBean sys_app_item;
    private List<AppDosagesBean> app_dosages;

    public ComputeAppItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(int current_price) {
        this.current_price = current_price;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public SysAppItemBean getSys_app_item() {
        return sys_app_item;
    }

    public void setSys_app_item(SysAppItemBean sys_app_item) {
        this.sys_app_item = sys_app_item;
    }

    public List<AppDosagesBean> getApp_dosages() {
        return app_dosages;
    }

    public void setApp_dosages(List<AppDosagesBean> app_dosages) {
        this.app_dosages = app_dosages;
    }

    public static class SysAppItemBean {
        /**
         * id : 2
         * name : 摩卡
         * volume : 260
         * price : 14.0
         * current_price : 8.4
         * app_dosages : [{"id":3,"weight":7,"water":80,"sequence":2,"material_name":"糖","sys_price":"0.0"},{"id":4,"weight":100,"water":40,"sequence":4,"material_name":"咖啡豆","sys_price":"0.5"},{"id":5,"weight":10,"water":48,"sequence":1,"material_name":"奶粉","sys_price":"0.15"},{"id":16,"weight":5,"water":36,"sequence":3,"material_name":"巧克力粉","sys_price":"0.15"}]
         */

        private int id;
        private String name;
        private int volume;
        private String price;
        private String current_price;
        private List<AppDosagesBean> app_dosages;

        public SysAppItemBean() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getCurrent_price() {
            return current_price;
        }

        public void setCurrent_price(String current_price) {
            this.current_price = current_price;
        }

        public List<AppDosagesBean> getApp_dosages() {
            return app_dosages;
        }

        public void setApp_dosages(List<AppDosagesBean> app_dosages) {
            this.app_dosages = app_dosages;
        }

        @Override
        public String toString() {
            return "SysAppItemBean{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", volume=" + volume +
                    ", price='" + price + '\'' +
                    ", current_price='" + current_price + '\'' +
                    ", app_dosages=" + app_dosages +
                    '}';
        }
    }

    public static class AppDosagesBean {
        /**
         * id : 59
         * weight : 2.08219
         * water : 66
         * sequence : 2
         * material_name : 糖
         */

        private int id;
        private float weight;
        private float water;
        private int sequence;
        private float adjust_price;
        private float sys_price;
        private String material_name;

        public AppDosagesBean() {
        }

        public AppDosagesBean(int id, float weight, float water, int sequence, float adjust_price, float sys_price, String material_name) {
            this.id = id;
            this.weight = weight;
            this.water = water;
            this.sequence = sequence;
            this.adjust_price = adjust_price;
            this.sys_price = sys_price;
            this.material_name = material_name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public float getWater() {
            return water;
        }

        public void setWater(float water) {
            this.water = water;
        }
        public float getAdjust_price() {
            return adjust_price;
        }

        public void setAdjust_price(float adjust_price) {
            this.adjust_price = adjust_price;
        }

        public float getSys_price() {
            return sys_price;
        }

        public void setSys_price(float sys_price) {
            this.sys_price = sys_price;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }

        public String getMaterial_name() {
            return material_name;
        }

        public void setMaterial_name(String material_name) {
            this.material_name = material_name;
        }

        @Override
        public String toString() {
            return "AppDosagesBean{" +
                    "id=" + id +
                    ", weight=" + weight +
                    ", water=" + water +
                    ", sequence=" + sequence +
                    ", adjust_price=" + adjust_price +
                    ", sys_price=" + sys_price +
                    ", material_name='" + material_name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ComputeAppItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", volume=" + volume +
                ", price='" + price + '\'' +
                ", parent_id=" + parent_id +
                ", description='" + description + '\'' +
                ", current_price=" + current_price +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", sys_app_item=" + sys_app_item +
                ", app_dosages=" + app_dosages +
                '}';
    }
}


