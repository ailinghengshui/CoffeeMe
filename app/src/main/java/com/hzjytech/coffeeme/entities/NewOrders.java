package com.hzjytech.coffeeme.entities;

import java.util.List;

/**
 * Created by hehongcan on 2017/10/25.
 */

public class NewOrders {

    /**
     * total : 2
     * orders : [{"identifier":"20171025105858590039346","coupon_count":0,
     * "fetch_code":"12750210","description":"卡布奇诺×1","original_sum":7.2,"created_at":"2017-10-25
     * 11:05:25.9","goods":[{"item":{"image_url":"http://coffees.qiniu.jijiakafei
     * .com/sI6QJdDF-%E5%8D%A1%E5%B8%83%E5%A5%87%E8%AF%BA
     * .png?e=1508904325&token=2VQYwL7KbVJwSfBnILVDSpaGbE7F4qB_7zV6lltd:vRG5e3EyHVVf0FcgAeoDYo0gH
     * -4=","isIced":false,"description":"咖啡/牛奶/奶泡= 1：1：1\r\n醇正浓郁·口感丰盈","active":true,
     * "buy_enable":true,"nameEn":"Cappuccino","activity_url_web":"http://coffees.qiniu
     * .jijiakafei.com/gtmEbCBM-D8QEppmB-%E5%8D%A1%E5%B8%83%E5%A5%87%E8%AF%BA
     * .png?e=1508904325&token=2VQYwL7KbVJwSfBnILVDSpaGbE7F4qB_7zV6lltd:g_dn4eZMeopmGf
     * -sPqdRWvIMky4=","volume":260,"price":12,"image_key":"gtmEbCBM-D8QEppmB-卡布奇诺.png",
     * "name":"卡布奇诺","id":1,"current_price":7.2,"sugar":1,"app_image":"sI6QJdDF-卡布奇诺.png"},
     * "be_token":false,"created_at":"2017-10-25 10:58:58","id":136764,"sugar":null}],"sum":7.2,
     * "vending_machine_id":null,"coupon_info":"无优惠","coupon_id":null,"payment_provider":3,
     * "id":53192,"status":1},{"identifier":"20171025104239590779736","coupon_count":0,
     * "fetch_code":"37450300","description":"摩卡×1","original_sum":8,"created_at":"2017-10-25
     * 11:05:25.9","goods":[{"item":{"image_url":"http://coffees.qiniu.jijiakafei
     * .com/pURkZOzH-%E6%91%A9%E5%8D%A1.png?e=1508904325&token
     * =2VQYwL7KbVJwSfBnILVDSpaGbE7F4qB_7zV6lltd:xY53KIFcp2h60L2aiYwrqsMQD1c=","isIced":false,
     * "description":"【咖啡 + 牛奶 + 巧克力】\r\n浓厚香甜·美味多滋","active":true,"buy_enable":true,
     * "nameEn":"Mocha","activity_url_web":"http://coffees.qiniu.jijiakafei
     * .com/OUoVIK2C-%E6%91%A9%E5%8D%A1.png?e=1508904325&token
     * =2VQYwL7KbVJwSfBnILVDSpaGbE7F4qB_7zV6lltd:y4HwimGG8QbIFh493zhnj2wYxcQ=","volume":260,
     * "price":14,"image_key":"OUoVIK2C-摩卡.png","name":"摩卡","id":2,"current_price":8,
     * "sugar":null,"app_image":"pURkZOzH-摩卡.png"},"be_token":false,"created_at":"2017-10-25
     * 10:42:39","id":136758,"sugar":null}],"sum":8,"vending_machine_id":null,
     * "coupon_info":"无优惠","coupon_id":null,"payment_provider":2,"id":53188,"status":0}]
     * able_take_count : 0
     */

    private int total;
    private int able_take_count;
    private List<NewOrder> orders;

    public int getTotal() { return total;}

    public void setTotal(int total) { this.total = total;}

    public int getAble_take_count() { return able_take_count;}

    public void setAble_take_count(int able_take_count) { this.able_take_count = able_take_count;}

    public List<NewOrder> getOrders() { return orders;}

    public void setOrders(List<NewOrder> orders) { this.orders = orders;}
}
