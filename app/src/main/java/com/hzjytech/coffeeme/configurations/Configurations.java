package com.hzjytech.coffeeme.configurations;


import com.hzjytech.coffeeme.BuildConfig;
import com.tencent.mm.sdk.modelbase.BaseResp;
public class Configurations {


    //    public static final String URL_PREFIX = "http://app.jijiakafei.com/";
 //public static final String URL_PREFIX ="http://192.168.0.188:3000/";
 //public static final String URL_PREFIX = "http://coffeeme.hzjytech.com/";
public static  String URL_PREFIX;
 public static  String NEW_URL_PREFIX;
 public static String WEB_URL_PREFIX;
  static {
   if(BuildConfig.DEBUG){
    URL_PREFIX ="https://test.coffee-me.com/";
    NEW_URL_PREFIX = "https://newtest.coffee-me.com/api/v1/";
    WEB_URL_PREFIX="https://newtest.coffee-me.com/dist/index.html#/h5/";
   }else{
    URL_PREFIX = "https://coffee-me.com/";
    NEW_URL_PREFIX = "https://new.coffee-me.com/api/v1/";
    WEB_URL_PREFIX="https://new.coffee-me.com/dist/index.html#/h5/";
   }
}
    //H5
    public static final String URL_NEW_COMMODITIES =WEB_URL_PREFIX+"pointMall" ;
    public static final String URL_INVITINGFRIENDS = WEB_URL_PREFIX+"invitingFriends";
    public static final String  URL_SHARE="https://coffee-me.com/";

    public static final String URL_DOMAIN = URL_PREFIX + "api/v1/";
    public static final String NEW_URL_DOMAIN = NEW_URL_PREFIX;
    public static final String PHONE = "phone";
    public static final String PASSWORD = "password";
    public static final String RESET_PASSWORD = "reset_password";
    public static final String SMS_CODE = "sms_code";
    public static final String FROM = "from";
    public static final String AUTH_TOKEN = "auth_token";
    public static final String REFERRAL_CODE = "referral_code";
    public static final String TOKEN = "token";
    public static final String IP = "ip";
    public static final String PROVIDER = "payment_provider";
    public static final String SUM = "sum";
    public static final String ORDERID = "order_id";
    public static final String VMID = "vm_id";
    public static final String URL_SESSIONS = NEW_URL_DOMAIN  + "sessions";
    public static final String URL_SEND_SMS_CODE = NEW_URL_DOMAIN  + "users/send_sms_code";
    public static final String URL_CHECK_SMS_CODE = NEW_URL_DOMAIN  + "users/check_sms_code";
    public static final String URL_SET_PASSWORD = NEW_URL_DOMAIN  + "users/set_password";
    public static final String URL_RESET_PASSWORD =NEW_URL_DOMAIN + "users/reset_password";
    public static final String URL_CHECK_TOKEN = NEW_URL_DOMAIN  + "sessions/check_token";
    public static final String URL_LOGIN_WECHAT = URL_DOMAIN + "sessions/wx_login";
    public static final String URL_BANNERS = NEW_URL_DOMAIN + "banners";
    public static final String URL_VENDING_MACHINES = NEW_URL_DOMAIN + "vending_machines";
    public static final String URL_CHECK_PASSWORD = NEW_URL_DOMAIN  + "users/check_password";

    public static final String URL_CHANGE_AVATOR = NEW_URL_DOMAIN + "users/change_avator";
    public static final String URL_CULTURE = URL_DOMAIN + "cultures";
    public static final String URL_NEW_CULTURE = NEW_URL_DOMAIN + "cultures";
    public static final String URL_APP_ITEMS = NEW_URL_DOMAIN + "app_items";
    public static final String URL_CHANGE_PASSWORD = NEW_URL_DOMAIN + "users/change_password";
    public static final String URL_APP_DOSAGES = URL_DOMAIN + "app_dosages";
    public static final String URL_GOODS = NEW_URL_DOMAIN + "goods";

    public static final String URL_COUPONS = NEW_URL_DOMAIN + "coupon/listMyCoupons";
    public static final String URL_ORDERS = NEW_URL_DOMAIN + "orders";
    public static final String URL_ORDER_PAY = NEW_URL_DOMAIN+ "order/pay";
    public static final String URL_UPDATE_NICKNAME = NEW_URL_DOMAIN+ "users/update_nickname";
    public static final String URL_THIRD_PARTY_LOGIN = NEW_URL_DOMAIN  + "sessions/third_party_login";


    public static final String URL_REDEEMED = URL_DOMAIN + "coupons/redeemed";
    public static final String URL_BINDING_PHONE = NEW_URL_DOMAIN + "users/binding_phone";
    public static final String URL_BINDING_THIRD_PARTY = NEW_URL_DOMAIN + "users/binding_third_party";
    public static final String URL_UNRELATED_THIRD_PARTY = NEW_URL_DOMAIN + "users/unrelated_third_party";
    public static final String URL_COUPON = NEW_URL_DOMAIN + "balance_records/coupon_config";
    public static final String URL_PREPAY = NEW_URL_DOMAIN + "balance_records";
    public static final String URL_ALIPAYBALANCENOTIFY = NEW_URL_DOMAIN + "pay_callback/alipay_balance_notify";
    public static final String URL_ALIPAYNOTIFY = NEW_URL_DOMAIN + "pay_callback/alipay_notify";
    public static final String URL_QRFETCH = NEW_URL_DOMAIN + "orders/qr_fetch";
    public static final String URL_AD = NEW_URL_DOMAIN + "banners/advertisement";
    public static final String URL_REFUND = NEW_URL_DOMAIN + "orders/refund";
    public static final String URL_COPY = NEW_URL_DOMAIN+ "order/copy";
    public static final String URL_ORDER_RATE = NEW_URL_DOMAIN + "orders/order_rate";
    public static final String URL_POINT_RECORDS = NEW_URL_DOMAIN + "points/point_records";
    public static final String URL_COMMODITIES = URL_PREFIX + "commodities";

    public static final String URL_ABOUT_POINT = URL_PREFIX + "about_point";
    public static final String URL_AGREEMENT = URL_PREFIX + "agreement";
    public static final String URL_APP_VERSION = NEW_URL_DOMAIN + "app_versions";
    public static final String URL_APK = "http://download.coffee-me.com/coffee-me.apk";
    public static final String URL_CAPTCHAS = NEW_URL_DOMAIN + "users/captchas";
    public static final String URL_FEEDBACKS = NEW_URL_DOMAIN + "feedbacks";
    //about result parameter
    public static final String STATUSCODE = "statusCode";
    public static final String STATUSMSG = "statusMsg";
    public static final String USER = "user";
    public static final String RESULTS = "results";
    public static final String WX_APP_ID = "wx889105edb2e40902";
    public static final String WX_APP_SECRET = "c85d980e38d9c0348c01289ae339f599";
    public static final String QINIU_BUCKET = "avators";
    //    public static final String QINIU_BUCKET = "jijiaapp";
    public static final String OPEN_ID = "open_id";
    public static final String NICKNAME = "nickname";
    public static final String AVATOR = "avator";
    public static final String URL_QiNiu = "http://7xr5wl.com1.z0.glb.clouddn.com/";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String SCOPE = "scope";
    public static final String OLD_PASSWORD = "old_password";
    public static final String TYPE = "type";
    public static final String APP_ITEM = "app_item";
    public static final String APP_ITEM_ID = "app_item_id";
    public static final String GOODS = "goods";
    public static final String PRICE = "price";
    public static final String NUMBER = "number";
    public static final String AVAILABLE = "available";
    public static final String PAYMENT_PROVIDER = "payment_provider";
    public static final String BALANCE = "balance";
    public static final String WEIX_OPEN_ID = "weix_open_id";
    public static final String COUPON_ID = "coupon_id";
    public static final String PAGE = "page";
    public static final String LOGIN_METHOD = "login_method";
    public static final String EMAIL = "email";


    public static final String DESCRIPTION = "description";
    public static final String VM_ID = "vm_id";
    public static final String KEY_UPDATE = "key_update";
    public static final String KEY_IGNORE_VERSION = "key_ignore_version";
    public static final String JPUSHTYPE = "jpushtype";
    public static final String REG_ID = "reg_id";
    public static final String APP_ID = "app_id";
    public static final String CAPTCHA = "captcha";
    public static final String SIGN = "sign";
    public static final String key="jijia2015";
    public static final String TIMESTAMP = "timestamp";
    public static final String DEVICE_ID = "device_id";

    public static final String URL_APP_MATERIALS=URL_DOMAIN+"app_materials";
 public static final String ITEM = "item";
    public static final String PAKAGE = "package";
 public static final String STATUS = "status";
 public static final String IDENTIFIER = "identifier";
 public static final String POINT = "point";
 public static final String REDEEMPTION_ID = "redeemption_id";

    public static final String PACKAGE_ID = "package_id";
    public static final String SUGAR = "sugar";
    public static BaseResp baseResp = null;
}
