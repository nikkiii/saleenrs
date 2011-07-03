package org.saleen.rs2.util;

import java.util.LinkedList;

import org.saleen.rs2.Constants;

/**
 * Text utility class.
 * 
 * @author Graham Edgecombe
 * 
 */
public class TextUtils {

	/**
	 * Unpacks text.
	 * 
	 * @param packedData
	 *            The packet text.
	 * @param size
	 *            The length.
	 * @return The string.
	 */
	public static String textUnpack(byte packedData[], int size) {
		byte[] decodeBuf = new byte[4096];
		int idx = 0, highNibble = -1;
		for (int i = 0; i < size * 2; i++) {
			int val = packedData[i / 2] >> (4 - 4 * (i % 2)) & 0xf;
			if (highNibble == -1) {
				if (val < 13) {
					decodeBuf[idx++] = (byte) Constants.XLATE_TABLE[val];
				} else {
					highNibble = val;
				}
			} else {
				decodeBuf[idx++] = (byte) Constants.XLATE_TABLE[((highNibble << 4) + val) - 195];
				highNibble = -1;
			}
		}
		return new String(decodeBuf, 0, idx);
	}

	public static String xlateText(byte[] data, int size) {
		char[] arr = new char[size];
		for (int l = 0; l < size; l++) {
			int b = data[l] & 0xff;
			arr[l] = Constants.XLATE_TABLE[b];
		}
		return new String(arr);
	}

	/**
	 * Optimises text.
	 * 
	 * @param text
	 *            The text to optimise.
	 * @return The text.
	 */
	public static String optimizeText(String text) {
		char buf[] = text.toCharArray();
		boolean endMarker = true;
		for (int i = 0; i < buf.length; i++) {
			char c = buf[i];
			if (endMarker && c >= 'a' && c <= 'z') {
				buf[i] -= 0x20;
				endMarker = false;
			}
			if (c == '.' || c == '!' || c == '?') {
				endMarker = true;
			}
		}
		return new String(buf, 0, buf.length);
	}

	/**
	 * Packs text.
	 * 
	 * @param packedData
	 *            The destination of the packed text.
	 * @param text
	 *            The unpacked text.
	 */
	public static void textPack(byte packedData[], String text) {
		if (text.length() > 80) {
			text = text.substring(0, 80);
		}
		text = text.toLowerCase();
		int carryOverNibble = -1;
		int ofs = 0;
		for (int idx = 0; idx < text.length(); idx++) {
			char c = text.charAt(idx);
			int tableIdx = 0;
			for (int i = 0; i < Constants.XLATE_TABLE.length; i++) {
				if (c == (byte) Constants.XLATE_TABLE[i]) {
					tableIdx = i;
					break;
				}
			}
			if (tableIdx > 12) {
				tableIdx += 195;
			}
			if (carryOverNibble == -1) {
				if (tableIdx < 13) {
					carryOverNibble = tableIdx;
				} else {
					packedData[ofs++] = (byte) (tableIdx);
				}
			} else if (tableIdx < 13) {
				packedData[ofs++] = (byte) ((carryOverNibble << 4) + tableIdx);
				carryOverNibble = -1;
			} else {
				packedData[ofs++] = (byte) ((carryOverNibble << 4) + (tableIdx >> 4));
				carryOverNibble = tableIdx & 0xf;
			}
		}
		if (carryOverNibble != -1) {
			packedData[ofs++] = (byte) (carryOverNibble << 4);
		}
	}

	/**
	 * Filters invalid characters out of a string.
	 * 
	 * @param s
	 *            The string.
	 * @return The filtered string.
	 */
	public static String filterText(String s) {
		StringBuilder bldr = new StringBuilder();
		for (char c : s.toLowerCase().toCharArray()) {
			boolean valid = false;
			for (char validChar : Constants.XLATE_TABLE) {
				if (validChar == c) {
					valid = true;
				}
			}
			if (valid) {
				bldr.append((char) c);
			}
		}
		return bldr.toString();
	}

	/**
	 * Format an enum object or other object from all uppercase to first
	 * uppercase.
	 * 
	 * @param object
	 *            The object to format
	 * @return The formatted name
	 */
	public static String formatEnum(Object object) {
		String s = object.toString().toLowerCase();
		return Character.toUpperCase(s.charAt(0))
				+ (s.substring(1).replaceAll("_", " "));
	}

	/**
	 * Parse a string as a real amount
	 * 
	 * @param amt
	 *            The amount
	 * @return The real amount
	 */
	public static int parseAmt(String amt) {
		if (amt.endsWith("m") || amt.endsWith("M")) {
			String trimmed = amt.substring(0, amt.length() - 1);
			if (trimmed.contains(".")) {
				return (int) (Double.parseDouble(trimmed) * 1000000);
			} else {
				return Integer.parseInt(trimmed) * 1000000;
			}
		} else if (amt.endsWith("k") || amt.endsWith("K")) {
			String trimmed = amt.substring(0, amt.length() - 1);
			if (trimmed.contains(".")) {
				return (int) (Double.parseDouble(trimmed) * 100000);
			} else {
				return Integer.parseInt(trimmed) * 100000;
			}
		}
		return 0;
	}

	public static Integer[] parseIntArray(String string, String delim) {
		string = string.trim();
		delim = delim.trim();
		if (!string.contains(delim)) {
			return new Integer[] { Integer.parseInt(string) };
		}
		String[] split = string.split(delim);
		LinkedList<Integer> list = new LinkedList<Integer>();
		for (String s : split) {
			if (s == null || s.equals("") || s.equals(delim)) {
				continue;
			}
			list.add(Integer.parseInt(s));
		}
		return list.toArray(new Integer[list.size()]);
	}

	public static int ipToInt(String address) {
		String[] sections = address.split("\\.");
		byte[] values = new byte[4];
		for (int i = 0; i < 4; i++) {
			values[i] = (byte) Integer.parseInt(sections[i]);
		}
		return ((values[0] & 0xFF) << 24) | ((values[1] & 0xFF) << 16)
				| ((values[2] & 0xFF) << 8) | (values[3] & 0xFF);
	}

}
