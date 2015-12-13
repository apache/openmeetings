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
package org.apache.openmeetings.servlet.outputhandler;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class NetworkTestingController {

    enum TestType {
        UNKNOWN,
        PING,
        JITTER,
        DOWNLOAD_SPEED,
        UPLOAD_SPEED
    }

    private static final int PING_PACKET_SIZE = 64;
    private static final int JITTER_PACKET_SIZE = 1024;
    private static final int DOWNLOAD_PACKET_SIZE = 1024*1024;

    private final byte[] pingData;
    private final byte[] jitterData;
    private final byte[] downloadData;

    public NetworkTestingController() {
        pingData = new byte[PING_PACKET_SIZE];
        jitterData = new byte[JITTER_PACKET_SIZE];
        downloadData = new byte[DOWNLOAD_PACKET_SIZE];

        Arrays.fill(pingData, (byte) '0');
        Arrays.fill(jitterData, (byte) '0');
        Arrays.fill(downloadData, (byte) '0');
    }

    @RequestMapping(value = "/networktest.upload", method = RequestMethod.GET)
	public void serviceGet(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
        String typeStr = request.getParameter("testType");
        TestType testType = this.getTypeByString(typeStr);

        // choose data to send
        byte[] data = null;
        switch (testType) {
            case UNKNOWN:
                return;
            case PING:
                data = pingData;
                break;
            case JITTER:
                data = jitterData;
                break;
            case DOWNLOAD_SPEED:
                data = downloadData;
                break;
			case UPLOAD_SPEED:
				break;
			default:
				break;
        }

        // set needed headers
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Content-Length", String.valueOf(data.length));

        // send data
        ServletOutputStream outStream = response.getOutputStream();
        outStream.write(data);
        outStream.flush();
    }

    @RequestMapping(value = "/networktest.upload", method = RequestMethod.POST)
	public void servicePost(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
    }


    private TestType getTypeByString(String typeString) {
        if ("ping".equals(typeString)) {
            return TestType.PING;
        } else if ("jitter".equals(typeString)) {
            return TestType.JITTER;
        } else if ("download".equals(typeString)) {
            return TestType.DOWNLOAD_SPEED;
        } else if ("upload".equals(typeString)) {
            return TestType.UPLOAD_SPEED;
        }

        return TestType.UNKNOWN;
    }

}
