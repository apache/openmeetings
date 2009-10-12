package org.openmeetings.rss;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import org.openmeetings.app.rss.LoadAtomRssFeed;

public class TestRssLoader extends TestCase {
	
	private static final Logger log = Logger.getLogger(TestRssLoader.class);
	
	public TestRssLoader(String testname){
		super(testname);
	}
	
	public void testLoadRssFeed(){
		String url = "http://groups.google.com/group/openmeetings-user/feed/atom_v1_0_msgs.xml";
		
		LoadAtomRssFeed.getInstance().parseRssFeed(url);
	}

}
