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
package org.apache.openmeetings.test.navi;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.openmeetings.data.basic.NaviBuilder;
import org.apache.openmeetings.persistence.beans.basic.Naviglobal;
import org.apache.openmeetings.persistence.beans.basic.Navimain;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestNavi extends AbstractOpenmeetingsSpringTest {
	
	@Autowired
	private NaviBuilder navimanagement;
	
	@Test
	public void testGetNavi(){
		
        List<Naviglobal> ll = navimanagement.getMainMenu(3L, 1L, 1L);

        assertTrue("GlobalNavi size should be greater than zero: " + ll.size(), ll.size() > 0);
        System.out.println("NaviGlobal size: " + ll.size());

        for (Naviglobal navigl : ll) {
        	assertNotNull("Naviglobal retrieved should not be null", navigl);
            System.out.println("Naviglobal label: " + navigl.getLabel().getValue());

        	assertNotNull("Naviglobal retrieved should have Navimain entries", navigl.getMainnavi());
            for (Navimain navim : navigl.getMainnavi()) {
            	assertNotNull("Navimain retrieved should not be null", navim);
                System.out.println("-->" + navim.getLabel().getValue());
            }
        }
		
	}

}
