package com.supaham.powerjuice.util;

import org.apache.commons.lang.Validate;

/**
 * Time related utility class.
 */
public class TimeUtils {

    /**
     * Gets the needed duration format based on the given seconds. <p />
     * 60 seconds: "ss" <br />
     * 90 seconds: "mm:ss" <br />
     * 4,000 seconds: "HH:mm:ss" <br />
     * 100,000: "dd:HH:mm:ss"
     *
     * @param seconds seconds to base duration format off
     * @return the duration format
     */
    public static String getNeededDurationFormat(int seconds) {
        Validate.isTrue(seconds > 0, "seconds must be larger than 0.");
        String format = "";
        if (seconds > 86400) format = "dd"; // more than 24 hours
        if (seconds > 3600) format += (!format.isEmpty() ? ":" : "") + "HH"; // more than 60 minutes
        if (seconds > 60) format += (!format.isEmpty() ? ":" : "") + "mm"; // more than 60 seconds
        format += (!format.isEmpty() ? ":" : "") + "ss";
        return format;
    }
}
