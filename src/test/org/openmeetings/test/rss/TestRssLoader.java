package org.openmeetings.test.rss;

import org.apache.log4j.Logger;
import org.openmeetings.app.rss.LoadAtomRssFeed;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestRssLoader extends AbstractOpenmeetingsSpringTest {
	
	private static final Logger log = Logger.getLogger(TestRssLoader.class);
	@Autowired
	private LoadAtomRssFeed loadAtomRssFeed;
	
	public void testLoadRssFeed(){
		String url = "http://groups.google.com/group/openmeetings-user/feed/atom_v1_0_msgs.xml";
		
		loadAtomRssFeed.parseRssFeed(url);
	}

}
