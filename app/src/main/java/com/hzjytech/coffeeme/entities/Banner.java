package com.hzjytech.coffeeme.entities;

/**
 * Created by Hades on 2016/2/25.
 */
public class Banner {

    /**
     * id : 1
     * article_url : http://mp.weixin.qq.com/s?__biz=MzA4Mzk2MzE5NQ==&mid=401716265&idx=1&sn=e330d3a1f38f813945c589e10a387b17#rd
     * banner_type : 1
     * image_url : http://banners.qiniu.jijiakafei.com/C0VtFSjU-lALODShNPc0BkM0C7g_750_400.png
     */

    private int id;
    private String article_url;
    private int banner_type;
    private String image_url;

    public Banner() {
    }

    public Banner(int id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setArticle_url(String article_url) {
        this.article_url = article_url;
    }

    public void setBanner_type(int banner_type) {
        this.banner_type = banner_type;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getId() {
        return id;
    }

    public String getArticle_url() {
        return article_url;
    }

    public int getBanner_type() {
        return banner_type;
    }

    public String getImage_url() {
        return image_url;
    }

    @Override
    public String toString() {
        return "Banner{" +
                "id=" + id +
                ", article_url='" + article_url + '\'' +
                ", banner_type=" + banner_type +
                ", image_url='" + image_url + '\'' +
                '}';
    }
}
