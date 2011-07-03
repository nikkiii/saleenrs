package org.saleen.fileserver;

import java.nio.ByteBuffer;

/**
 * Represents a response to either a JAGGRAB or HTTP request.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Response {

	/**
	 * The data in the file.
	 */
	private ByteBuffer fileData;

	/**
	 * The MIME type.
	 */
	private String mimeType;

	private ResponseCode responseCode;

	/**
	 * Creates the response.
	 * 
	 * @param bytes
	 *            The data.
	 * @param mimeType
	 *            The MIME type.
	 */
	public Response(byte[] bytes, String mimeType) {
		ByteBuffer buf = ByteBuffer.allocate(bytes.length);
		buf.put(bytes);
		buf.flip();
		fileData = buf;
		this.mimeType = mimeType;
		this.responseCode = ResponseCode.OK;
	}

	/**
	 * Creates the response.
	 * 
	 * @param data
	 *            The data.
	 * @param mimeType
	 *            The MIME type.
	 */
	public Response(ByteBuffer data, String mimeType, ResponseCode respcode) {
		this(data, mimeType);
		this.responseCode = respcode;
	}

	/**
	 * Creates the response.
	 * 
	 * @param string
	 *            The string to send
	 * @param mimeType
	 *            The MIME type.
	 */
	public Response(String string, String mimeType) {
		byte[] bytes = string.getBytes();
		ByteBuffer buf = ByteBuffer.allocate(bytes.length);
		buf.put(bytes);
		buf.flip();
		fileData = buf;
		this.mimeType = mimeType;
		this.responseCode = ResponseCode.OK;
	}

	/**
	 * Creates the response.
	 * 
	 * @param fileData
	 *            The file data.
	 * @param mimeType
	 *            The MIME type.
	 */
	public Response(ByteBuffer fileData, String mimeType) {
		this.fileData = fileData;
		this.mimeType = mimeType;
		this.responseCode = ResponseCode.OK;
	}

	/**
	 * Gets the file data.
	 * 
	 * @return The file dtaa.
	 */
	public ByteBuffer getFileData() {
		return fileData;
	}

	/**
	 * Gets the MIME type.
	 * 
	 * @return The MIME type.
	 */
	public String getMimeType() {
		return mimeType;
	}

	public ResponseCode getRespCode() {
		return responseCode;
	}

	public enum ResponseCode {
		OK, NOT_FOUND;
	}

}
