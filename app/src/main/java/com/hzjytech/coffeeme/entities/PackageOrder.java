package com.hzjytech.coffeeme.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hehongcan on 2018/1/5.
 */

public class PackageOrder implements Parcelable {

    /**
     * identifier : 20180104101518667433995
     * payment_provider : null
     * created_at : 2018-01-04 10:15:18
     * description : 卡布奇诺x1
     * original_sum : 12
     * sum : 1
     * id : 59599
     * get_point : 10
     * vending_machine_id : null
     * status : 0
     */

    private String identifier;
    private int payment_provider;
    private String created_at;
    private String description;
    private double original_sum;
    private double sum;
    private int id;
    private double get_point;
    private int vending_machine_id;
    private int status;

    public PackageOrder(
            String identifier,
            int payment_provider,
            String created_at,
            String description,
            double original_sum,
            double sum,
            int id,
            double get_point,
            int vending_machine_id,
            int status) {
        this.identifier = identifier;
        this.payment_provider = payment_provider;
        this.created_at = created_at;
        this.description = description;
        this.original_sum = original_sum;
        this.sum = sum;
        this.id = id;
        this.get_point = get_point;
        this.vending_machine_id = vending_machine_id;
        this.status = status;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getPayment_provider() {
        return payment_provider;
    }

    public void setPayment_provider(int payment_provider) {
        this.payment_provider = payment_provider;
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

    public double getOriginal_sum() {
        return original_sum;
    }

    public void setOriginal_sum(double original_sum) {
        this.original_sum = original_sum;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getGet_point() {
        return get_point;
    }

    public void setGet_point(double get_point) {
        this.get_point = get_point;
    }

    public int getVending_machine_id() {
        return vending_machine_id;
    }

    public void setVending_machine_id(int vending_machine_id) {
        this.vending_machine_id = vending_machine_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.identifier);
        dest.writeInt(this.payment_provider);
        dest.writeString(this.created_at);
        dest.writeString(this.description);
        dest.writeDouble(this.original_sum);
        dest.writeDouble(this.sum);
        dest.writeInt(this.id);
        dest.writeDouble(this.get_point);
        dest.writeInt(this.vending_machine_id);
        dest.writeInt(this.status);
    }

    protected PackageOrder(Parcel in) {
        this.identifier = in.readString();
        this.payment_provider = in.readInt();
        this.created_at = in.readString();
        this.description = in.readString();
        this.original_sum = in.readDouble();
        this.sum = in.readDouble();
        this.id = in.readInt();
        this.get_point = in.readDouble();
        this.vending_machine_id = in.readInt();
        this.status = in.readInt();
    }

    public static final Creator<PackageOrder> CREATOR = new Creator<PackageOrder>() {
        @Override
        public PackageOrder createFromParcel(Parcel source) {return new PackageOrder(source);}

        @Override
        public PackageOrder[] newArray(int size) {return new PackageOrder[size];}
    };
}
