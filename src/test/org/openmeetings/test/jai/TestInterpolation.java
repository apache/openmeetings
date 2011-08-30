package org.openmeetings.test.jai;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

public class TestInterpolation extends TestCase {
	
	private static final Logger log = Logger.getLogger(TestInterpolation.class);
	
	@Test
	public void testInterpolate() {
		try {
			
//			GeomPoint p1 = new Point(-60,-100);
//			GeomPoint p2 = new Point(20, 50);
//
//			//double[] samples = {-100,-50};
//			
//			GeomPoint gPoint = GeomPoint.interpolate(p1, p2, new Float(0.5));
//			
//			log.debug("Interpolated: "+gPoint);
//			
//			
//			GeomPoint p3 = new GeomPoint();
//			p3.setLocation(-2,4);
//			//p3.setLocation(3,4);
//			p3.normalize(10);
//			//GeomPoint
//			
//			
//			p3.setLocation(2,-4);
//			//p3.setLocation(3,4);
//			p3.normalize(10);
//			
		} catch (Exception er) {
			log.error("ERROR ", er);
			System.out.println("Error exporting: " + er);
			er.printStackTrace();
		}
	}

	
}
