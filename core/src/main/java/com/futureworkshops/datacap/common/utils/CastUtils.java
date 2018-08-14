package com.futureworkshops.datacap.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert from lists of objects to lists of interfaces.
 */
public class CastUtils {

    public static <T> List<T> castList(List originalList) {
        List<T> ret = new ArrayList<>();
        if (originalList != null) {
            for (Object obj : originalList) {
                ret.add((T) obj);
            }
        }
        return ret;
    }
}
