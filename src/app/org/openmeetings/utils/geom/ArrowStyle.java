package org.openmeetings.utils.geom;

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
