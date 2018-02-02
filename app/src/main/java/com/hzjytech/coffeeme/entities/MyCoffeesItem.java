package com.hzjytech.coffeeme.entities;

/**
 * Created by Hades on 2016/5/23.
 */
public class MyCoffeesItem {
    private boolean isHeader;
    private AppItems appItems;

    public MyCoffeesItem(boolean isHeader, AppItems appItems) {
        this.isHeader = isHeader;
        this.appItems = appItems;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public AppItems getAppItems() {
        return appItems;
    }

    public void setAppItems(AppItems appItems) {
        this.appItems = appItems;
    }
}
