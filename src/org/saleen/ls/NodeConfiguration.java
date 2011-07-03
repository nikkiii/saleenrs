package org.saleen.ls;

public class NodeConfiguration {
	private int nodeid;
	private String host;
	private int port;
	private String password;
	private String description;
	private WorldType type;

	public NodeConfiguration(int nodeid, String host, int port,
			String password, String description, WorldType type) {
		this.nodeid = nodeid;
		this.host = host;
		this.port = port;
		this.password = password;
		this.description = description;
		this.type = type;
	}

	public int getNodeid() {
		return nodeid;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getPassword() {
		return password;
	}

	public String getDescription() {
		return description;
	}

	public WorldType getType() {
		return type;
	}

	public enum WorldType {
		NON_MEMBERS, MEMBERS
	}
}
