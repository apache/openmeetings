/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.webdav.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Locale;

/**
 * Parsers and Serializers for HTTP dates (RFC 7231, Section 7.1.1.1), using
 * {@link DateTimeFormatter} (from Java 8).
 */
public class HttpDateTimeFormatter {

    private static DateTimeFormatter IMFFIXDATE = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH)
            .withZone(ZoneOffset.UTC);

    // see
    // https://greenbytes.de/tech/webdav/rfc7231.html#rfc.section.7.1.1.1.p.6
    private static DateTimeFormatter RFC850DATE = new DateTimeFormatterBuilder().appendPattern("EEEE, dd-MMM-")
            .appendValueReduced(ChronoField.YEAR_OF_ERA, 2, 2, LocalDate.now().minusYears(50)).appendPattern(" HH:mm:ss 'GMT'")
            .toFormatter().withLocale(Locale.ENGLISH).withZone(ZoneOffset.UTC);

    private static DateTimeFormatter ASCTIMEDATE = new DateTimeFormatterBuilder().appendPattern("EEE MMM ").padNext(2, ' ')
            .appendValue(ChronoField.DAY_OF_MONTH).appendPattern(" HH:mm:ss yyyy").toFormatter().withLocale(Locale.ENGLISH)
            .withZone(ZoneOffset.UTC);

    /**
     * Parse HTTP "IMF-fixdate" format (see RFC 7231, Section 7.1.1.1)
     * 
     * @param fieldValue
     *            string value
     * @return ms since epoch throws DateTimeParseException on invalid input
     */
    public static long parseImfFixedDate(String fieldValue) {
        ZonedDateTime d = ZonedDateTime.parse(fieldValue, IMFFIXDATE);
        return d.toInstant().toEpochMilli();
    }

    /**
     * Parse HTTP "rfc850-date" format (see RFC 7231, Section 7.1.1.1)
     * 
     * @param fieldValue
     *            string value
     * @return ms since epoch throws DateTimeParseException on invalid input
     */
    public static long parseRfc850Date(String fieldValue) {
        ZonedDateTime d = ZonedDateTime.parse(fieldValue, RFC850DATE);
        return d.toInstant().toEpochMilli();
    }

    /**
     * Parse HTTP "asctime-date" format (see RFC 7231, Section 7.1.1.1)
     * 
     * @param fieldValue
     *            string value
     * @return ms since epoch throws DateTimeParseException on invalid input
     */
    public static long parseAscTimeDate(String fieldValue) {
        ZonedDateTime d = ZonedDateTime.parse(fieldValue, ASCTIMEDATE);
        return d.toInstant().toEpochMilli();
    }

    /**
     * Parse HTTP format, trying the three allowable formats defined in RFC
     * 7231, Section 7.1.1.1
     * 
     * @param fieldValue
     *            string value
     * @return ms since epoch throws DateTimeParseException on invalid input
     */
    public static long parse(String fieldValue) {
        try {
            return parseImfFixedDate(fieldValue);
        } catch (DateTimeParseException ex) {
            try {
                return parseRfc850Date(fieldValue);
            } catch (DateTimeParseException ex2) {
                try {
                    return parseAscTimeDate(fieldValue);
                } catch (DateTimeParseException ex3) {
                    // if we get here, throw original exception for IMFFIXDATE
                    throw ex;
                }
            }
        }
    }

    /**
     * Format as HTTP default date (IMF-fixdate) (see RFC 7231, Section 7.1.1.1)
     * 
     * @param millisSinceEpoch
     *            ms since epoch
     * @return string representation
     */
    public static String format(long millisSinceEpoch) {
        return IMFFIXDATE.format(Instant.ofEpochMilli(millisSinceEpoch));
    }

    /**
     * Format as HTTP "IMF-fixdate" (see RFC 7231, Section 7.1.1.1)
     * 
     * @param millisSinceEpoch
     *            ms since epoch
     * @return string representation
     */
    public static String formatImfFixed(long millisSinceEpoch) {
        return IMFFIXDATE.format(Instant.ofEpochMilli(millisSinceEpoch));
    }

    /**
     * Format as HTTP "rfc850-date" (see RFC 7231, Section 7.1.1.1)
     * 
     * @param millisSinceEpoch
     *            ms since epoch
     * @return string representation
     */
    public static String formatRfc850(long millisSinceEpoch) {
        return RFC850DATE.format(Instant.ofEpochMilli(millisSinceEpoch));
    }

    /**
     * Format as HTTP "asctime-date" (see RFC 7231, Section 7.1.1.1)
     * 
     * @param millisSinceEpoch
     *            ms since epoch
     * @return string representation
     */
    public static String formatAscTime(long millisSinceEpoch) {
        return ASCTIMEDATE.format(Instant.ofEpochMilli(millisSinceEpoch));
    }
}
