package com.hzjytech.coffeeme.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.Path;


public class NewCulture implements Parcelable{

    /**
     * banners : {"banners_id":1,"banners_name":"极伽活动","banners_content":[{"id":1,
     * "title":"极伽时光\u201c碰撞\u201d魔都背后的故事","content":"极伽时光在魔都的故事，要是不睡觉的话，怕也要说上三 天三夜。",
     * "image_url":"http://banners.qiniu.jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE
     * .jpg","article_url":"http://banners.qiniu.jijiakafei
     * .com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","create_at":"2017-11-14 15:00:00"},
     * {"id":1,"title":"极伽时光\u201c碰撞\u201d魔都背后的故事","content":"极伽时光在魔都的故事，要是不睡觉的话，怕也要说上三 天三夜。",
     * "image_url":"http://banners.qiniu.jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE
     * .jpg","article_url":"http://banners.qiniu.jijiakafei
     * .com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","create_at":"2017-11-14 15:00:00"}]}
     * plates : [{"plate_id":1,"plate_name":"极伽志趣","plate_content":[{"id":1,"title":"活动",
     * "content":"jutide具体的huodongneirong","image_url":"http://banners.qiniu.jijiakafei
     * .com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","article_url":"http://banners.qiniu
     * .jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","create_at":"2017-11-14
     * 15:00:00"},{"id":1,"title":"活动","content":"jutide具体的huodongneirong",
     * "image_url":"http://banners.qiniu.jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE
     * .jpg","article_url":"http://banners.qiniu.jijiakafei
     * .com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","create_at":"2017-11-14 15:00:00"}]},
     * {"plate_id":2,"plate_name":"极伽品牌","plate_content":[{"id":2,"title":"活动",
     * "content":"jutide具huodongneirong","image_url":"http://banners.qiniu.jijiakafei
     * .com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","article_url":"http://banners.qiniu
     * .jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg"},{"id":2,"title":"活动",
     * "content":"jutide具huodongneirong","image_url":"http://banners.qiniu.jijiakafei
     * .com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","article_url":"http://banners.qiniu
     * .jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg"}]}]
     */

    private BannersBean banners;
    private List<PlatesBean> plates;

    public BannersBean getBanners() { return banners;}

    public void setBanners(BannersBean banners) { this.banners = banners;}

    public List<PlatesBean> getPlates() { return plates;}

    public void setPlates(List<PlatesBean> plates) { this.plates = plates;}

    public static class BannersBean implements Parcelable{
        /**
         * banners_id : 1
         * banners_name : 极伽活动
         * banners_content : [{"id":1,"title":"极伽时光\u201c碰撞\u201d魔都背后的故事",
         * "content":"极伽时光在魔都的故事，要是不睡觉的话，怕也要说上三 天三夜。","image_url":"http://banners.qiniu
         * .jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg",
         * "article_url":"http://banners.qiniu.jijiakafei
         * .com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","create_at":"2017-11-14 15:00:00"},
         * {"id":1,"title":"极伽时光\u201c碰撞\u201d魔都背后的故事","content":"极伽时光在魔都的故事，要是不睡觉的话，怕也要说上三
         * 天三夜。","image_url":"http://banners.qiniu.jijiakafei
         * .com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","article_url":"http://banners.qiniu
         * .jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","create_at":"2017-11-14
         * 15:00:00"}]
         */

        private int banners_id;
        private String banners_name;
        private List<BannersContentBean> banners_content;

        public int getBanners_id() { return banners_id;}

        public void setBanners_id(int banners_id) { this.banners_id = banners_id;}

        public String getBanners_name() { return banners_name;}

        public void setBanners_name(String banners_name) { this.banners_name = banners_name;}

        public List<BannersContentBean> getBanners_content() { return banners_content;}

        public void setBanners_content(List<BannersContentBean> banners_content) { this
                .banners_content = banners_content;}

        public static class BannersContentBean implements Parcelable{
            /**
             * id : 1
             * title : 极伽时光“碰撞”魔都背后的故事
             * content : 极伽时光在魔都的故事，要是不睡觉的话，怕也要说上三 天三夜。
             * image_url : http://banners.qiniu.jijiakafei
             * .com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg
             * article_url : http://banners.qiniu.jijiakafei
             * .com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg
             * create_at : 2017-11-14 15:00:00
             */

            private int id;
            private String title;
            private String content;
            private String image_url;
            private String article_url;
            private String create_at;

            public int getId() { return id;}

            public void setId(int id) { this.id = id;}

            public String getTitle() { return title;}

            public void setTitle(String title) { this.title = title;}

            public String getContent() { return content;}

            public void setContent(String content) { this.content = content;}

            public String getImage_url() { return image_url;}

            public void setImage_url(String image_url) { this.image_url = image_url;}

            public String getArticle_url() { return article_url;}

            public void setArticle_url(String article_url) { this.article_url = article_url;}

            public String getCreate_at() { return create_at;}

            public void setCreate_at(String create_at) { this.create_at = create_at;}

            @Override
            public int describeContents() { return 0; }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.id);
                dest.writeString(this.title);
                dest.writeString(this.content);
                dest.writeString(this.image_url);
                dest.writeString(this.article_url);
                dest.writeString(this.create_at);
            }

            public BannersContentBean() {}

            protected BannersContentBean(Parcel in) {
                this.id = in.readInt();
                this.title = in.readString();
                this.content = in.readString();
                this.image_url = in.readString();
                this.article_url = in.readString();
                this.create_at = in.readString();
            }

            public static final Creator<BannersContentBean> CREATOR = new Creator<BannersContentBean>() {
                @Override
                public BannersContentBean createFromParcel(Parcel source) {
                    return new BannersContentBean(source);
                }

                @Override
                public BannersContentBean[] newArray(int size) {return new BannersContentBean[size];}
            };
        }

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.banners_id);
            dest.writeString(this.banners_name);
            dest.writeList(this.banners_content);
        }

        public BannersBean() {}

        protected BannersBean(Parcel in) {
            this.banners_id = in.readInt();
            this.banners_name = in.readString();
            this.banners_content = new ArrayList<BannersContentBean>();
            in.readList(this.banners_content, BannersContentBean.class.getClassLoader());
        }

        public static final Creator<BannersBean> CREATOR = new Creator<BannersBean>() {
            @Override
            public BannersBean createFromParcel(Parcel source) {return new BannersBean(source);}

            @Override
            public BannersBean[] newArray(int size) {return new BannersBean[size];}
        };
    }

    public static class PlatesBean implements Parcelable{
        /**
         * plate_id : 1
         * plate_name : 极伽志趣
         * plate_content : [{"id":1,"title":"活动","content":"jutide具体的huodongneirong",
         * "image_url":"http://banners.qiniu.jijiakafei
         * .com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","article_url":"http://banners.qiniu
         * .jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","create_at":"2017-11-14
         * 15:00:00"},{"id":1,"title":"活动","content":"jutide具体的huodongneirong",
         * "image_url":"http://banners.qiniu.jijiakafei
         * .com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","article_url":"http://banners.qiniu
         * .jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg","create_at":"2017-11-14
         * 15:00:00"}]
         */

        private int plate_id;
        private String plate_name;
        private List<PlateContentBean> plate_content;

        public int getPlate_id() { return plate_id;}

        public void setPlate_id(int plate_id) { this.plate_id = plate_id;}

        public String getPlate_name() { return plate_name;}

        public void setPlate_name(String plate_name) { this.plate_name = plate_name;}

        public List<PlateContentBean> getPlate_content() { return plate_content;}

        public void setPlate_content(List<PlateContentBean> plate_content) { this.plate_content =
                plate_content;}

        public static class PlateContentBean implements Parcelable{
            /**
             * id : 1
             * title : 活动
             * content : jutide具体的huodongneirong
             * image_url : http://banners.qiniu.jijiakafei
             * .com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg
             * article_url : http://banners.qiniu.jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg
             * create_at : 2017-11-14 15:00:00
             */

            private int id;
            private String title;
            private String content;
            private String image_url;
            private String article_url;
            private String create_at;

            public int getId() { return id;}

            public void setId(int id) { this.id = id;}

            public String getTitle() { return title;}

            public void setTitle(String title) { this.title = title;}

            public String getContent() { return content;}

            public void setContent(String content) { this.content = content;}

            public String getImage_url() { return image_url;}

            public void setImage_url(String image_url) { this.image_url = image_url;}

            public String getArticle_url() { return article_url;}

            public void setArticle_url(String article_url) { this.article_url = article_url;}

            public String getCreate_at() { return create_at;}

            public void setCreate_at(String create_at) { this.create_at = create_at;}

            @Override
            public int describeContents() { return 0; }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.id);
                dest.writeString(this.title);
                dest.writeString(this.content);
                dest.writeString(this.image_url);
                dest.writeString(this.article_url);
                dest.writeString(this.create_at);
            }

            public PlateContentBean() {}

            protected PlateContentBean(Parcel in) {
                this.id = in.readInt();
                this.title = in.readString();
                this.content = in.readString();
                this.image_url = in.readString();
                this.article_url = in.readString();
                this.create_at = in.readString();
            }

            public static final Creator<PlateContentBean> CREATOR = new Creator<PlateContentBean>() {
                @Override
                public PlateContentBean createFromParcel(Parcel source) {
                    return new PlateContentBean(source);
                }

                @Override
                public PlateContentBean[] newArray(int size) {return new PlateContentBean[size];}
            };
        }

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.plate_id);
            dest.writeString(this.plate_name);
            dest.writeList(this.plate_content);
        }

        public PlatesBean() {}

        protected PlatesBean(Parcel in) {
            this.plate_id = in.readInt();
            this.plate_name = in.readString();
            this.plate_content = new ArrayList<PlateContentBean>();
            in.readList(this.plate_content, PlateContentBean.class.getClassLoader());
        }

        public static final Creator<PlatesBean> CREATOR = new Creator<PlatesBean>() {
            @Override
            public PlatesBean createFromParcel(Parcel source) {return new PlatesBean(source);}

            @Override
            public PlatesBean[] newArray(int size) {return new PlatesBean[size];}
        };
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.banners, flags);
        dest.writeTypedList(this.plates);
    }

    public NewCulture() {}

    protected NewCulture(Parcel in) {
        this.banners = in.readParcelable(BannersBean.class.getClassLoader());
        this.plates = in.createTypedArrayList(PlatesBean.CREATOR);
    }

    public static final Creator<NewCulture> CREATOR = new Creator<NewCulture>() {
        @Override
        public NewCulture createFromParcel(Parcel source) {return new NewCulture(source);}

        @Override
        public NewCulture[] newArray(int size) {return new NewCulture[size];}
    };
}
