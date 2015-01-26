package com.supaham.powerjuice.util;

import com.google.common.base.CharMatcher;

/**
 * Contains {@link String}-related utility methods.
 */
public class StringUtil {

    /**
     * Checks if a {@link String} contains ASCII characters only.
     *
     * @param text the String to check
     * @return whether the {@code text} is ASCII only
     */
    public static boolean isASCII(String text) {
        return CharMatcher.ASCII.matchesAllOf(text);
    }
}
