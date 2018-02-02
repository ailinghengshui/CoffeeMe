package com.hzjytech.coffeeme.entities;

import java.io.Serializable;

/**
 * Created by Hades on 2016/2/16.
 */
public class User implements Serializable {

    private int id;
    private String login = "";
    private String nickname = "";
    private String phone = "";
    private String auth_token = "";
    private String status = "";
    private String wx_open_id =null;
    private String avator = "";
    private float balance;
    private String referral_code = "";
    private String weibo_open_id = null;
    /**
     * available_coupon_count : 0
     * avator_url : http://avators.qiniu.jijiakafei.com/default_avatar_male_50.gif?e=1462871670&token=2VQYwL7KbVJwSfBnILVDSpaGbE7F4qB_7zV6lltd:lNo4d9iU2JeaeY0NYyPS8gh9OAM=
     */

    private int available_coupon_count;
    private String avator_url;
    /**
     * point : 0
     * level : 0
     * user_level : {"level_name":"v1","image":"http://banners.qiniu.jijiakafei.com/lRBT-4WG-V1@3x.png","start":0,"end":100,"level":1}
     */

    // all points
    private int point;
    //current available points
    private int level;
    /**
     * level_name : v1
     * image : http://banners.qiniu.jijiakafei.com/lRBT-4WG-V1@3x.png
     * start : 0
     * end : 100
     * level : 1
     */

    private UserLevelBean user_level;
    private BenefitCouponBean benefit_coupon;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                ", phone='" + phone + '\'' +
                ", level=" + level +
                ", user_level=" + user_level +
                ", balance=" + balance +
                '}';
    }

    public String getWx_open_id() {
        return wx_open_id;
    }

    public void setWx_open_id(String wx_open_id) {
        this.wx_open_id = wx_open_id;
    }

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getReferral_code() {
        return referral_code;
    }

    public void setReferral_code(String referral_code) {
        this.referral_code = referral_code;
    }

    public String getWeibo_open_id() {
        return weibo_open_id;
    }

    public void setWeibo_open_id(String weibo_open_id) {
        this.weibo_open_id = weibo_open_id;
    }

    public void setAvailable_coupon_count(int available_coupon_count) {
        this.available_coupon_count = available_coupon_count;
    }

    public void setAvator_url(String avator_url) {
        this.avator_url = avator_url;
    }

    public int getAvailable_coupon_count() {
        return available_coupon_count;
    }

    public String getAvator_url() {
        return avator_url;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public UserLevelBean getUser_level() {
        return user_level;
    }

    public void setUser_level(UserLevelBean user_level) {
        this.user_level = user_level;
    }
    public BenefitCouponBean getBenefit_coupon() {
        return benefit_coupon;
    }

    public void setBenefit_coupon(BenefitCouponBean benefit_coupon) {
        this.benefit_coupon = benefit_coupon;
    }


    public static class UserLevelBean {
        private String level_name;
        private String image;
        private int start;
        private int end;
        private int level;

        public String getLevel_name() {
            return level_name;
        }

        public void setLevel_name(String level_name) {
            this.level_name = level_name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }
    }
    public static class BenefitCouponBean {
        /**
         * coupon_type : 1
         * title : 新用户优惠券
         * value : 50
         * "end_date": "2016-01-01T00:00:00.000+08:00"
         */

        private int coupon_type;
        private String title;
        private String value;
        private String end_date;

        public int getCoupon_type() {
            return coupon_type;
        }
        public void setCoupon_type(int coupon_type) {
            this.coupon_type = coupon_type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

    }
}
