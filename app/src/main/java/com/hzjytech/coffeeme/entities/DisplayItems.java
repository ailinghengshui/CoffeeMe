package com.hzjytech.coffeeme.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Hades on 2016/3/16.
 */
public class DisplayItems {


    private List<AppItem> hot_items;
    private List<AppItem> ice_items;
    private List<Packages> packages;

    public List<AppItem> getHot_items() { return hot_items;}

    public void setHot_items(List<AppItem> hot_items) { this.hot_items = hot_items;}

    public List<AppItem> getIce_items() { return ice_items;}

    public void setIce_items(List<AppItem> ice_items) { this.ice_items = ice_items;}

    public List<Packages> getPackages() {
        return packages;
    }

    public void setPackages(List<Packages> packages) {
        this.packages = packages;
    }

    public static class AppItem implements Serializable{
        /**
         * id : 6
         * buy_enable : true
         * name : 卡布奇诺
         * nameEn : Cappuchino
         * description : 卡布奇诺
         * volume : 260
         * price : 10.0
         * current_price : 1.0
         * image_key : r06Yztjo-Selection_054.png
         * image_url : img_url
         */

        private int id;
        private boolean buy_enable;
        private String name;
        private String nameEn;
        private String description;
        private float volume;
        private float price;
        private float current_price;
        private String image_key;
        private String image_url;
        private int sugar;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isBuy_enable() {
            return buy_enable;
        }

        public void setBuy_enable(boolean buy_enable) {
            this.buy_enable = buy_enable;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public float getVolume() {
            return volume;
        }

        public void setVolume(float volume) {
            this.volume = volume;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public float getCurrent_price() {
            return current_price;
        }

        public void setCurrent_price(float current_price) {
            this.current_price = current_price;
        }

        public String getImage_key() {
            return image_key;
        }

        public void setImage_key(String image_key) {
            this.image_key = image_key;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public int getSugar() {
            return sugar;
        }

        public void setSugar(int sugar) {
            this.sugar = sugar;
        }
    }
  public static class Packages implements Serializable{

      /**
       * image : package-1514858018116.jpg
       * original_price : 20
       * validDay : 3
       * active : true
       * description : 1
       2
       1
       2
       * itemInfo : [{"id":1,"name":"卡布奇诺","count":1},{"id":2,"name":"摩卡","count":1}]
       * title : 套餐1
       * createdAt : 1514537090000
       * deleted : false
       * img_url : http://coffees.qiniu.jijiakafei.com/package-1514858018116.jpg
       * price : 10
       * startTime : 2017-12-01 04:44:27
       * endTime : 2018-01-06 16:45:27
       * id : 6
       * updatedAt : 1514882807000
       */

      private String image;
      private float original_price;
      private int validDay;
      private boolean active;
      private String description;
      private String itemInfo;
      private String title;
      private long createdAt;
      private boolean deleted;
      private String img_url;
      private float price;
      private String startTime;
      private String endTime;
      private int id;
      private long updatedAt;

      public String getImage() { return image;}

      public void setImage(String image) { this.image = image;}

      public float getOriginal_price() { return original_price;}

      public void setOriginal_price(float original_price) { this.original_price = original_price;}

      public int getValidDay() { return validDay;}

      public void setValidDay(int validDay) { this.validDay = validDay;}

      public boolean isActive() { return active;}

      public void setActive(boolean active) { this.active = active;}

      public String getDescription() { return description;}

      public void setDescription(String description) { this.description = description;}

      public String getItemInfo() { return itemInfo;}

      public void setItemInfo(String itemInfo) { this.itemInfo = itemInfo;}

      public String getTitle() { return title;}

      public void setTitle(String title) { this.title = title;}

      public long getCreatedAt() { return createdAt;}

      public void setCreatedAt(long createdAt) { this.createdAt = createdAt;}

      public boolean isDeleted() { return deleted;}

      public void setDeleted(boolean deleted) { this.deleted = deleted;}

      public String getImg_url() { return img_url;}

      public void setImg_url(String img_url) { this.img_url = img_url;}

      public float getPrice() { return price;}

      public void setPrice(float price) { this.price = price;}

      public String getStartTime() { return startTime;}

      public void setStartTime(String startTime) { this.startTime = startTime;}

      public String getEndTime() { return endTime;}

      public void setEndTime(String endTime) { this.endTime = endTime;}

      public int getId() { return id;}

      public void setId(int id) { this.id = id;}

      public long getUpdatedAt() { return updatedAt;}

      public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt;}
  }

}
