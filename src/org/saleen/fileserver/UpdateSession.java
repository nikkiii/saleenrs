package org.saleen.fileserver;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.saleen.fileserver.Response.ResponseCode;

/**
 * Represents a single update channel.
 * 
 * @author Graham Edgecombe
 * 
 */
public class UpdateSession {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(UpdateSession.class
			.getName());

	/**
	 * An enum which describes the type of channel.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public enum Type {

		/**
		 * A plain HTTP channel (which the loader will fall back to if port 443
		 * cannot be used).
		 */
		HTTP,

		/**
		 * A JAGGRAB channel (which is the primary choice of the loader).
		 */
		JAGGRAB;
	}

	/**
	 * The <code>Channel</code> we are serving.
	 */
	private Channel channel;

	/**
	 * The type of channel we are.
	 */
	private Type type;

	/**
	 * The request we are serving.
	 */
	private Request request;

	/**
	 * Creates the update channel.
	 * 
	 * @param type
	 *            The type of channel.
	 * @param channel
	 *            The <code>Channel</code>.
	 */
	public UpdateSession(Type type, Channel channel) {
		this.type = type;
		this.channel = channel;
	}

	/**
	 * Reads a line of input data.
	 * 
	 * @param line
	 *            The line.
	 */
	public void readLine(String line) {
		if (request == null) {
			switch (type) {
			case JAGGRAB:
				readJaggrabPath(line);
				break;
			case HTTP:
				readHttpPath(line);
				break;
			}
		} else {
			if (type == Type.HTTP) {
				if (line.length() == 0) {
					serve();
				}
			}
		}
	}

	/**
	 * Servers the requested file.
	 */
	private void serve() {
		if (request == null) {
			channel.close();
			return;
		}
		logger.fine("Serving " + type + " request : " + request.getPath());
		Response resp = RequestHandler.handle(request);
		if (resp.getRespCode() == ResponseCode.NOT_FOUND) {
			error404(resp);
			return;
		}

		StringBuilder header = new StringBuilder();
		if (type == Type.HTTP) {
			header.append("HTTP/1.0 200 OK\r\n");
			header.append("Content-Length: ")
					.append(resp.getFileData().remaining()).append("\r\n");
			header.append("Connection: close\r\n");
			header.append("Server: Hyperion/1.0\r\n");
			header.append("Content-Type: " + resp.getMimeType() + "\r\n");
			header.append("\r\n");
		}
		byte[] headerBytes = header.toString().getBytes();

		ByteBuffer bb = resp.getFileData();
		ChannelBuffer ib = ChannelBuffers.buffer(bb.remaining()
				+ headerBytes.length);
		ib.writeBytes(headerBytes);
		ib.writeBytes(bb);
		channel.write(ib).addListener(ChannelFutureListener.CLOSE);
	}

	private void error404(Response resp) {
		StringBuilder header = new StringBuilder();
		if (type == Type.HTTP) {
			header.append("HTTP/1.1 200 OK\r\n");
			header.append("Content-Length: ")
					.append(resp.getFileData().remaining()).append("\r\n");
			header.append("Connection: close\r\n");
			header.append("Server: Hyperion/1.0\r\n");
			header.append("Content-Type: " + resp.getMimeType() + "\r\n");
			header.append("\r\n");
		}
		byte[] headerBytes = header.toString().getBytes();

		ByteBuffer bb = resp.getFileData();
		ChannelBuffer ib = ChannelBuffers.buffer(bb.remaining()
				+ headerBytes.length);
		ib.writeBytes(headerBytes);
		ib.writeBytes(bb);
		channel.write(ib).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * Reads the path from a HTTP request line.
	 * 
	 * @param line
	 *            The request line.
	 */
	private void readHttpPath(String line) {
		String[] parts = line.split(" ");
		if (parts.length != 3) {
			channel.close();
		} else {
			request = new Request(parts[1].trim());
		}
	}

	/**
	 * Reads the path from a JAGGRAB request line.
	 * 
	 * @param line
	 *            The request line.
	 */
	private void readJaggrabPath(String line) {
		final String START = "JAGGRAB ";
		if (line.startsWith(START)) {
			request = new Request(line.substring(START.length()).trim());
		} else {
			channel.close();
		}
		serve();
	}

}
