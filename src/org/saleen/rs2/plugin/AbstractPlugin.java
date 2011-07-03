package org.saleen.rs2.plugin;

/**
 * A basic plugin class which allows users to ignore the onLoad/onUnload
 * methods, though it is highly advised against.
 * 
 * @author Nikki
 * 
 */
public class AbstractPlugin extends Plugin {

	@Override
	public void onLoad() throws Exception {

	}

	@Override
	public void onUnload() throws Exception {

	}
}
