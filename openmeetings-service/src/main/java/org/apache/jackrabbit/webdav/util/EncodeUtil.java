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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * <code>EncodeUtil</code> provides helper methods for URL encoding and decoding
 * (copied from jcr-commons jackrabbit.util.Text).
 *
 * @see <a href="https://issues.apache.org/jira/browse/JCR-2897">JCR-2897</a>.
 */
public final class EncodeUtil {

    /**
     * logger instance
     */
    private static final Logger log = LoggerFactory.getLogger(EncodeUtil.class);

    /**
     * hextable used for {@link #escape(String, char, boolean)}
     */
    public static final char[] hexTable = "0123456789abcdef".toCharArray();

    /**
     * The list of characters that are not encoded by the <code>escape()</code>
     * and <code>unescape()</code> METHODS. They contains the characters as
     * defined 'unreserved' in section 2.3 of the RFC 2396 'URI generic syntax':
     * <p>
     * <pre>
     * unreserved  = alphanum | mark
     * mark        = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
     * </pre>
     */
    private static BitSet URISave;

    /**
     * Same as {@link #URISave} but also contains the '/'
     */
    private static BitSet URISaveEx;

    static {
        URISave = new BitSet(256);
        int i;
        for (i = 'a'; i <= 'z'; i++) {
            URISave.set(i);
        }
        for (i = 'A'; i <= 'Z'; i++) {
            URISave.set(i);
        }
        for (i = '0'; i <= '9'; i++) {
            URISave.set(i);
        }
        URISave.set('-');
        URISave.set('_');
        URISave.set('.');
        URISave.set('!');
        URISave.set('~');
        URISave.set('*');
        URISave.set('\'');
        URISave.set('(');
        URISave.set(')');

        URISaveEx = (BitSet) URISave.clone();
        URISaveEx.set('/');
    }

    /**
     * Does a URL encoding of the <code>string</code>. The characters that
     * don't need encoding are those defined 'unreserved' in section 2.3 of
     * the 'URI generic syntax' RFC 2396.
     *
     * @param string the string to encode
     * @return the escaped string
     * @throws NullPointerException if <code>string</code> is <code>null</code>.
     */
    public static String escape(String string) {
        return escape(string, '%', false);
    }

    /**
     * Does a URL encoding of the <code>path</code>. The characters that
     * don't need encoding are those defined 'unreserved' in section 2.3 of
     * the 'URI generic syntax' RFC 2396. In contrast to the
     * {@link #escape(String)} method, not the entire path string is escaped,
     * but every individual part (i.e. the slashes are not escaped).
     *
     * @param path the path to encode
     * @return the escaped path
     * @throws NullPointerException if <code>path</code> is <code>null</code>.
     */
    public static String escapePath(String path) {
        return escape(path, '%', true);
    }

    /**
     * Does an URL encoding of the <code>string</code> using the
     * <code>escape</code> character. The characters that don't need encoding
     * are those defined 'unreserved' in section 2.3 of the 'URI generic syntax'
     * RFC 2396, but without the escape character. If <code>isPath</code> is
     * <code>true</code>, additionally the slash '/' is ignored, too.
     *
     * @param string the string to encode.
     * @param escape the escape character.
     * @param isPath if <code>true</code>, the string is treated as path
     * @return the escaped string
     * @throws NullPointerException if <code>string</code> is <code>null</code>.
     */
    private static String escape(String string, char escape, boolean isPath) {
        BitSet validChars = isPath ? URISaveEx : URISave;
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        StringBuffer out = new StringBuffer(bytes.length);
        for (byte aByte : bytes) {
            int c = aByte & 0xff;
            if (validChars.get(c) && c != escape) {
                out.append((char) c);
            } else {
                out.append(escape);
                out.append(hexTable[(c >> 4) & 0x0f]);
                out.append(hexTable[(c) & 0x0f]);
            }
        }
        return out.toString();
    }

    /**
     * Does a URL decoding of the <code>string</code>. Please note that in
     * opposite to the {@link java.net.URLDecoder} it does not transform the +
     * into spaces.
     *
     * @param string the string to decode
     * @return the decoded string
     * @throws NullPointerException           if <code>string</code> is <code>null</code>.
     * @throws ArrayIndexOutOfBoundsException if not enough character follow an
     *                                        escape character
     * @throws IllegalArgumentException       if the 2 characters following the escape
     *                                        character do not represent a hex-number.
     */
    public static String unescape(String string) {
        return unescape(string, '%');
    }
    
    /**
     * Does a URL decoding of the <code>string</code> using the
     * <code>escape</code> character. Please note that in opposite to the
     * {@link java.net.URLDecoder} it does not transform the + into spaces.
     *
     * @param string the string to decode
     * @param escape the escape character
     * @return the decoded string
     * @throws NullPointerException           if <code>string</code> is <code>null</code>.
     * @throws IllegalArgumentException       if the 2 characters following the escape
     *                                        character do not represent a hex-number
     *                                        or if not enough characters follow an
     *                                        escape character
     */
    private static String unescape(String string, char escape)  {
        byte[] utf8 = string.getBytes(StandardCharsets.UTF_8);

        // Check whether escape occurs at invalid position
        if ((utf8.length >= 1 && utf8[utf8.length - 1] == escape) ||
            (utf8.length >= 2 && utf8[utf8.length - 2] == escape)) {
            throw new IllegalArgumentException("Premature end of escape sequence at end of input");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(utf8.length);
        for (int k = 0; k < utf8.length; k++) {
            byte b = utf8[k];
            if (b == escape) {
                out.write((decodeDigit(utf8[++k]) << 4) + decodeDigit(utf8[++k]));
            }
            else {
                out.write(b);
            }
        }

        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
    
    private static byte decodeDigit(byte b) {
        if (b >= 0x30 && b <= 0x39) {
            return (byte) (b - 0x30);
        }
        else if (b >= 0x41 && b <= 0x46) {
            return (byte) (b - 0x37);
        }
        else if (b >= 0x61 && b <= 0x66) {
            return (byte) (b - 0x57);
        }
        else {
            throw new IllegalArgumentException("Escape sequence is not hexadecimal: " + (char)b);
        }
    }

    /**
     * Private constructor: avoid instantiation.
     */
    private EncodeUtil() {
    }
}