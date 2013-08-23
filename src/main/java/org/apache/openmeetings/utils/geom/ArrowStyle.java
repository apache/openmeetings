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
package org.apache.openmeetings.utils.geom;

public class ArrowStyle {

	public double headWidth=-1; //Relative width of arrow head

	/**
	 * 
	 * Not used in drawArrowHead because the length is 
	 * determined by the points passed in
	 * 
	 */
	public double headLength=10; //Pixel Length of arrow head
			
	
	public double shaftThickness=2;
	public float shaftPosition=0;
	
	/**
	 *  Not used in drawArrow, only drawArrowHead
	 * 	This let's you curve the line at the base of the arrow
	 */
	public double shaftControlPosition=.5;
	/**
	 * Not used in drawArrow, only drawArrowHead
	 * This let's you curve the line at the base of the arrow
	 */
	public double shaftControlSize=.5;
	
	
	public double edgeControlPosition=.5;
	public double edgeControlSize=.5;
	
	
	public ArrowStyle(double edgeControlPosition, double edgeControlSize,
			double headLength, double headWidth, double shaftControlPosition,
			double shaftControlSize, float shaftPosition, double shaftThickness) {
		super();
		this.edgeControlPosition = edgeControlPosition;
		this.edgeControlSize = edgeControlSize;
		this.headLength = headLength;
		this.headWidth = headWidth;
		this.shaftControlPosition = shaftControlPosition;
		this.shaftControlSize = shaftControlSize;
		this.shaftPosition = shaftPosition;
		this.shaftThickness = shaftThickness;
	}
	
	
}
