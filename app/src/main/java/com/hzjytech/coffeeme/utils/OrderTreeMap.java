package com.hzjytech.coffeeme.utils;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by hehongcan on 2017/11/16.
 */

public class OrderTreeMap extends TreeMap {
    @Override
    public Comparator comparator() {
        return new CollatorComparator();
    }
    public class CollatorComparator implements Comparator {
        Collator collator = Collator.getInstance();
        public int compare(Object element1, Object element2) {
            CollationKey key1 = collator.getCollationKey(element1.toString().toLowerCase());
            CollationKey key2 = collator.getCollationKey(element2.toString());
            return key1.compareTo(key2);
        }
    }
}
