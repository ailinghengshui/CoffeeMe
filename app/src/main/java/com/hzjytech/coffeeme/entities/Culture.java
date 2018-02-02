package com.hzjytech.coffeeme.entities;

/**
 * Created by Hades on 2016/3/9.
 */
public class Culture {


    /**
     * id : 1
     * article_url : http://mp.weixin.qq.com/s?__biz=MzA4Mzk2MzE5NQ==&mid=402126403&idx=1&sn=fb7bfeb56d4d61aed520df9492221202#rd
     * image_url : http://cultures.qiniu.jijiakafei.com/C7OTGVP--lALODSdnis0C-M0BzA_460_760.png
     */

    private int id;
    private String article_url;
    private String image_url;

    public void setId(int id) {
        this.id = id;
    }

    public void setArticle_url(String article_url) {
        this.article_url = article_url;
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

    public String getImage_url() {
        return image_url;
    }
}
