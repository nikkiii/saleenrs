package org.saleen.util;

import java.io.IOException;
import java.net.InetAddress;

import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.UPNPResponseException;

/**
 * A simple UPNP Portforwarding implementation
 * 
 * @author Nikki
 * 
 */
public class PortForwarding {

	/**
	 * The local computer address
	 */
	private static InetAddress local;

	/**
	 * Open a port using UPNP
	 * 
	 * @param port
	 *            The port
	 * @return True, if opened
	 * @throws IOException
	 *             If a problem getting the local dadress or binding the port
	 *             occurs
	 * @throws UPNPResponseException
	 *             ??
	 */
	public static boolean openPort(int port) throws IOException,
			UPNPResponseException {
		return openPort("Port " + port, port);
	}

	/**
	 * Open a port using UPNP
	 * 
	 * @param name
	 *            The mapping name
	 * @param port
	 *            The port
	 * @throws IOException
	 *             If a problem getting the local address or binding to the port
	 *             occurs
	 * @throws UPNPResponseException
	 *             ??
	 */
	public static boolean openPort(String name, int port) throws IOException,
			UPNPResponseException {
		if (local == null) {
			local = InetAddress.getLocalHost();
		}
		InternetGatewayDevice dev = PortForwarding.findDevice(500);
		if (dev == null) {
			throw new RuntimeException("Device not found!!!");
		}
		return dev.addPortMapping(name, null, port, port,
				local.getHostAddress(), 0, "TCP");
	}

	/**
	 * Find a UPNP Device
	 * 
	 * @return The device
	 * @throws IOException
	 *             If an error occurs
	 */
	public static InternetGatewayDevice findDevice(int timeout)
			throws IOException {
		InternetGatewayDevice[] devices = InternetGatewayDevice
				.getDevices(timeout);
		if (devices.length > 0 && devices[0] != null) {
			return devices[0];
		}
		return null;
	}
}
