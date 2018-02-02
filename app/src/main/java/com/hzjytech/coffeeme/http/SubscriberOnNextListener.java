package com.hzjytech.coffeeme.http;

public interface SubscriberOnNextListener<T> {
    void onNext(T t);
}
