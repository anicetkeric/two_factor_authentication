package com.bootlabs.common;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@UtilityClass
public class DateUtils {

    /**
     * The default time zone identifier.
     */
    public final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    /**
     * The time zone to use by default.
     */
    public final ZoneId zoneId = DEFAULT_ZONE_ID;

    public Date convertFromLocalDateToDate() {
        return Date.from(LocalDateTime.now().atZone(zoneId).toInstant());
    }

    public LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now(zoneId);
    }
}
