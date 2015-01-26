package com.supaham.powerjuice.util;

import java.util.List;

/**
 * Contains Collection related utility methods.
 */
public class CollectionUtil {

    /**
     * Gets a random element in a {@link List}.
     *
     * @param list list to use
     * @return the random element
     */
    public static <T> T getRandomElement(List<T> list) {
        return list.get(NumberUtil.getRandom().nextInt(list.size()));
    }
}
