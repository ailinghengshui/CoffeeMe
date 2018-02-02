package com.hzjytech.coffeeme.entities;

import java.util.List;

/**
 * Created by Hades on 2016/7/28.
 */
public class PointRecord {
    /**
     * identifier : 2016080113140823811421
     * source_type : 1
     * value : 72
     * created_at : 2016-08-01T13:14:08.000+08:00
     * item_info : {"orders":{"id":6827,"created_at":"2016-08-01T13:14:08.000+08:00","description":"卡布奇诺×1","fetch_code":"78331340","identifier":"2016080113140823655878","original_sum":7.2,"payment_provider":3,"status":2,"sum":6.48,"goods":[{"id":23380,"be_token":false,"current_price":7.2,"name":"卡布奇诺","original_price":12,"item_id":1}]}}
     */

    private String identifier;
    private int source_type;
    private int value;
    private String created_at;
    /**
     * orders : {"id":6827,"created_at":"2016-08-01T13:14:08.000+08:00","description":"卡布奇诺×1","fetch_code":"78331340","identifier":"2016080113140823655878","original_sum":7.2,"payment_provider":3,"status":2,"sum":6.48,"goods":[{"id":23380,"be_token":false,"current_price":7.2,"name":"卡布奇诺","original_price":12,"item_id":1}]}
     */

    private ItemInfoBean item_info;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getSource_type() {
        return source_type;
    }

    public void setSource_type(int source_type) {
        this.source_type = source_type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public ItemInfoBean getItem_info() {
        return item_info;
    }

    public void setItem_info(ItemInfoBean item_info) {
        this.item_info = item_info;
    }

    public static class ItemInfoBean {
        /**
         * id : 6827
         * created_at : 2016-08-01T13:14:08.000+08:00
         * description : 卡布奇诺×1
         * fetch_code : 78331340
         * identifier : 2016080113140823655878
         * original_sum : 7.2
         * payment_provider : 3
         * status : 2
         * sum : 6.48
         * goods : [{"id":23380,"be_token":false,"current_price":7.2,"name":"卡布奇诺","original_price":12,"item_id":1}]
         */

        private OrdersBean orders;
        private ComOrdersBean com_orders;

        public OrdersBean getOrders() {
            return orders;
        }

        public ComOrdersBean getCom_orders() {
            return com_orders;
        }

        public void setCom_orders(ComOrdersBean com_orders) {
            this.com_orders = com_orders;
        }

        public void setOrders(OrdersBean orders) {
            this.orders = orders;
        }

        public static class OrdersBean {
            private int id;
            private String created_at;
            private String description;
            private String fetch_code;
            private String identifier;
            private double original_sum;
            private int payment_provider;
            private int status;
            private double sum;
            /**
             * id : 23380
             * be_token : false
             * current_price : 7.2
             * name : 卡布奇诺
             * original_price : 12
             * item_id : 1
             */

            private List<GoodsBean> goods;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getFetch_code() {
                return fetch_code;
            }

            public void setFetch_code(String fetch_code) {
                this.fetch_code = fetch_code;
            }

            public String getIdentifier() {
                return identifier;
            }

            public void setIdentifier(String identifier) {
                this.identifier = identifier;
            }

            public double getOriginal_sum() {
                return original_sum;
            }

            public void setOriginal_sum(double original_sum) {
                this.original_sum = original_sum;
            }

            public int getPayment_provider() {
                return payment_provider;
            }

            public void setPayment_provider(int payment_provider) {
                this.payment_provider = payment_provider;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public double getSum() {
                return sum;
            }

            public void setSum(double sum) {
                this.sum = sum;
            }

            public List<GoodsBean> getGoods() {
                return goods;
            }

            public void setGoods(List<GoodsBean> goods) {
                this.goods = goods;
            }

            public static class GoodsBean {
                private int id;
                private boolean be_token;
                private double current_price;
                private String name;
                private int original_price;
                private int item_id;

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

                public double getCurrent_price() {
                    return current_price;
                }

                public void setCurrent_price(double current_price) {
                    this.current_price = current_price;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getOriginal_price() {
                    return original_price;
                }

                public void setOriginal_price(int original_price) {
                    this.original_price = original_price;
                }

                public int getItem_id() {
                    return item_id;
                }

                public void setItem_id(int item_id) {
                    this.item_id = item_id;
                }
            }
        }

        public static class ComOrdersBean{

            /**
             * id : 2
             * identifier : 20160729124945164825861
             * description : xxx
             * created_at : 2016-07-29T13:57:32.000+08:00
             * com_goods : [{"id":1,"name":"xxx","com_type":1,"value":"xxx","point":11}]
             */

            private int id;
            private String identifier;
            private String description;
            private String created_at;
            /**
             * id : 1
             * name : xxx
             * com_type : 1
             * value : xxx
             * point : 11
             */

            private List<ComGoodsBean> com_goods;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getIdentifier() {
                return identifier;
            }

            public void setIdentifier(String identifier) {
                this.identifier = identifier;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public List<ComGoodsBean> getCom_goods() {
                return com_goods;
            }

            public void setCom_goods(List<ComGoodsBean> com_goods) {
                this.com_goods = com_goods;
            }

            public static class ComGoodsBean {
                private int id;
                private String name;
                private int com_type;
                private String value;
                private int point;

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

                public int getCom_type() {
                    return com_type;
                }

                public void setCom_type(int com_type) {
                    this.com_type = com_type;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public int getPoint() {
                    return point;
                }

                public void setPoint(int point) {
                    this.point = point;
                }
            }
        }
    }


//    /**
//     * identifier : 20160722112715164793180
//     * source_type :
//     * 1:下单抵扣支出
//     * 2：取货获得积分
//     * 3：优惠券兑换支出
//     * value : 100
//     * created_at : 2016-07-22T11:27:15.000+08:00
//     * item_info : {"id":4350,"created_at":"2016-07-22T11:27:15.000+08:00","description":"卡布奇诺x1","fetch_code":"91357621","identifier":"20160722112715164793180","original_sum":1.9,"payment_provider":1,"status":1,"sum":0.9,"goods":[{"id":13144,"be_token":false,"current_price":1.4,"name":"卡布奇诺","original_price":10.9,"item_id":1}]}
//     */
//
//    private String identifier;
//    private int source_type;
//    private int value;
//    private String created_at;
//
//
//    /**
//     * id : 4350
//     * created_at : 2016-07-22T11:27:15.000+08:00
//     * description : 卡布奇诺x1
//     * fetch_code : 91357621
//     * identifier : 20160722112715164793180
//     * original_sum : 1.9
//     * payment_provider : 1
//     * status : 1
//     * sum : 0.9
//     * goods : [{"id":13144,"be_token":false,"current_price":1.4,"name":"卡布奇诺","original_price":10.9,"item_id":1}]
//     */
//
//
//
//
//    public String getIdentifier() {
//        return identifier;
//    }
//
//    public void setIdentifier(String identifier) {
//        this.identifier = identifier;
//    }
//
//    public int getSource_type() {
//        return source_type;
//    }
//
//    public void setSource_type(int source_type) {
//        this.source_type = source_type;
//    }
//
//    public int getValue() {
//        return value;
//    }
//
//    public void setValue(int value) {
//        this.value = value;
//    }
//
//    public String getCreated_at() {
//        return created_at;
//    }
//
//    public void setCreated_at(String created_at) {
//        this.created_at = created_at;
//    }
//
//





}
