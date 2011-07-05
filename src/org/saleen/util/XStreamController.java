package org.saleen.util;

import com.thoughtworks.xstream.XStream;

/**
 * A simple XML Loading class
 * 
 * @author Nikki
 * 
 */
public class XStreamController {

	/**
	 * The XStream instance
	 */
	private static XStream xstream;

	/**
	 * Get the XStream instance or create one and alias the classes
	 * 
	 * @return
	 */
	public static XStream getXStream() {
		if (xstream == null) {
			xstream = new XStream();
			xstream.alias("area", org.saleen.rs2.content.area.BasicArea.class);
			xstream.alias("compositearea",
					org.saleen.rs2.content.area.CompositeArea.class);
		}
		return xstream;
	}
}
