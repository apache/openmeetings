/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.medint.rtpsharer.util;

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

/**
 * ImageUtility.java
 * @author benbac
 */

public class ImageUtility {

    static {ImageIO.setUseCache(false);}

    public static BufferedImage read(InputStream in) throws IOException {
        BufferedImage image = null;  
        image = ImageIO.read(in);
        if (image == null)
            throw new IOException("Read fails");                  
        return image;
    }
 
    public static BufferedImage read(byte[] bytes) {
        try {
            return read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            e.getStackTrace();
            return null;
        }
    } 

    public static byte[] toByteArray(BufferedImage image) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", out); // write without compression                                
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
