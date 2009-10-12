package org.openmeetings.app.conference.configutils;

import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.*;
import org.apache.xerces.parsers.DOMParser;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

public class BandwidthConfigFactory {
 
	protected static Logger log = Logger.getLogger(BandwidthConfigFactory.class);

	protected static String bandwidthonfigFileName = "WEB-INF/configbandwidth.xml";
	
	private HashMap<String,CustomBandwidth> xmlcrmred5BandwidthConfig = new HashMap<String,CustomBandwidth>();

	public BandwidthConfigFactory() { }

	public void getBandwidthConfigFile() {
		try {

			String completePath = System.getProperties().get("/xmlcrmred5").toString()+bandwidthonfigFileName;
			//log.debug("completePath: " + completePath);
			
			DOMParser bandwidthParser = new DOMParser();

			bandwidthParser.parse(completePath);

			Document doc = bandwidthParser.getDocument();

			recurseDOMTree(doc);

		} catch (Exception err) {
			System.out.println("Err " + err);
			log.error("Err: " + err);
		}
	}

	public void recurseDOMTree(Node node) throws Exception {

		switch (node.getNodeType()) {

			case Node.ELEMENT_NODE:

				String name = node.getNodeName();

//				NamedNodeMap attributes = node.getAttributes();

				if (name.equalsIgnoreCase("badconnection")) {

					CustomBandwidth badconnection = new CustomBandwidth();
//					badconnection.setGroupName(attributes.getNamedItem("name").getNodeValue());
//					badconnection.setDescription(attributes.getNamedItem("description").getNodeValue());
//					badconnection.setMaxpingTime(Integer.parseInt(attributes.getNamedItem("maxpingtime").getNodeValue()));
//					badconnection.setMinpingTime(Integer.parseInt(attributes.getNamedItem("minpingtime").getNodeValue()));
//					badconnection.setUpstreamBandwidth(Long.parseLong(attributes.getNamedItem("upstreambandwidth").getNodeValue()));
//					badconnection.setDownstreamBandwidth(Long.parseLong(attributes.getNamedItem("downstreambandwidth").getNodeValue()));
//					badconnection.setMaxBurst(Long.parseLong(attributes.getNamedItem("maxburst").getNodeValue()));
//					badconnection.setBurst(Long.parseLong(attributes.getNamedItem("burst").getNodeValue()));
//					badconnection.setOverallBandwidth(Long.parseLong(attributes.getNamedItem("overallbandwidth").getNodeValue()));									
//					log.debug("----------------> "+badconnection.getGroupName());
//					log.debug("----------------> "+badconnection.getDownstreamBandwidth());
					xmlcrmred5BandwidthConfig.put("badconnection", badconnection);

				} else if (name.equalsIgnoreCase("midconnection")) {

					CustomBandwidth midconnection = new CustomBandwidth();
//					midconnection.setGroupName(attributes.getNamedItem("name").getNodeValue());
//					midconnection.setDescription(attributes.getNamedItem("description").getNodeValue());
//					midconnection.setMaxpingTime(Integer.parseInt(attributes.getNamedItem("maxpingtime").getNodeValue()));
//					midconnection.setMinpingTime(Integer.parseInt(attributes.getNamedItem("minpingtime").getNodeValue()));
//					midconnection.setUpstreamBandwidth(Long.parseLong(attributes.getNamedItem("upstreambandwidth").getNodeValue()));
//					midconnection.setDownstreamBandwidth(Long.parseLong(attributes.getNamedItem("downstreambandwidth").getNodeValue()));
//					midconnection.setMaxBurst(Long.parseLong(attributes.getNamedItem("maxburst").getNodeValue()));
//					midconnection.setBurst(Long.parseLong(attributes.getNamedItem("burst").getNodeValue()));
//					midconnection.setOverallBandwidth(Long.parseLong(attributes.getNamedItem("overallbandwidth").getNodeValue()));					
//					log.debug(midconnection);
//					log.debug("----------------> "+midconnection.getGroupName());
					xmlcrmred5BandwidthConfig.put("midconnection", midconnection);

				} else if (name.equalsIgnoreCase("goodconnection")) {

					CustomBandwidth goodconnection = new CustomBandwidth();
//					goodconnection.setGroupName(attributes.getNamedItem("name").getNodeValue());
//					goodconnection.setDescription(attributes.getNamedItem("description").getNodeValue());
//					goodconnection.setMaxpingTime(Integer.parseInt(attributes.getNamedItem("maxpingtime").getNodeValue()));
//					goodconnection.setMinpingTime(Integer.parseInt(attributes.getNamedItem("minpingtime").getNodeValue()));
//					goodconnection.setUpstreamBandwidth(Long.parseLong(attributes.getNamedItem("upstreambandwidth").getNodeValue()));
//					goodconnection.setDownstreamBandwidth(Long.parseLong(attributes.getNamedItem("downstreambandwidth").getNodeValue()));
//					goodconnection.setMaxBurst(Long.parseLong(attributes.getNamedItem("maxburst").getNodeValue()));
//					goodconnection.setBurst(Long.parseLong(attributes.getNamedItem("burst").getNodeValue()));
//					goodconnection.setOverallBandwidth(Long.parseLong(attributes.getNamedItem("overallbandwidth").getNodeValue()));
//					log.debug(goodconnection);
//					log.debug("----------------> "+goodconnection.getGroupName());
					xmlcrmred5BandwidthConfig.put("goodconnection", goodconnection);

				}

				break;

		}

		NodeList children = node.getChildNodes();

		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				recurseDOMTree(children.item(i));
			}
		}

	}

	public CustomBandwidth getBandwidthForClient(int pingTime) throws Exception{
		Iterator<String> iter = xmlcrmred5BandwidthConfig.keySet().iterator();
		while(iter.hasNext()){
			CustomBandwidth bandWidthConfig = xmlcrmred5BandwidthConfig.get(iter.next());
			log.debug("----> pingTime"+pingTime);
			log.debug("----> getMinpingTime"+bandWidthConfig.getMinpingTime());
			log.debug("----> getMaxpingTime"+bandWidthConfig.getMaxpingTime());
			if (pingTime>=bandWidthConfig.getMinpingTime() && pingTime<=bandWidthConfig.getMaxpingTime()){
				log.debug("Found it: "+bandWidthConfig.getGroupName());
				return bandWidthConfig;
			}
		}
		return xmlcrmred5BandwidthConfig.get("badconnection");
	}

	/**
	 * @return the xmlcrmred5BandwidthConfig
	 */
	public HashMap<String, CustomBandwidth> getDokeosBandwidthConfig() {
		return xmlcrmred5BandwidthConfig;
	}

	/**
	 * @param xmlcrmred5BandwidthConfig the xmlcrmred5BandwidthConfig to set
	 */
	public void setDokeosBandwidthConfig(
			HashMap<String, CustomBandwidth> xmlcrmred5BandwidthConfig) {
		this.xmlcrmred5BandwidthConfig = xmlcrmred5BandwidthConfig;
	}


}
