package org.saleen.rs2.util;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.saleen.cache.Archive;
import org.saleen.cache.Cache;

/**
 * Censor class from the Jagex client, mostly renamed... For use in Hyperion and
 * Saleen
 * 
 * @author Jagex LTD
 */
public class Censor {

	private int[] fragments;
	private char[][] badEncChar;
	private byte[][][] badEncByte;
	private char[][] domainEnc;
	private char[][] tldList;
	private int[] tldArray;

	private final String[] WORD_EXCEPTIONS = { "cook", "cook's", "cooks",
			"seeks", "sheet", "woop", "woops", "faq", "noob", "noobs" };

	public void load(Cache cache) throws IOException {
		Archive archive = new Archive(cache.getFile(0, 7));

		readFragmentsEnc(archive.getFileAsByteBuffer("fragmentsenc.txt"));
		readBadEnc(archive.getFileAsByteBuffer("badenc.txt"));
		readDomainEnc(archive.getFileAsByteBuffer("domainenc.txt"));
		readTldList(archive.getFileAsByteBuffer("tldlist.txt"));
	}

	private void readTldList(ByteBuffer buffer) {
		int length = buffer.getInt();
		tldList = new char[length][];
		tldArray = new int[length];
		for (int j = 0; j < length; j++) {
			tldArray[j] = buffer.get();
			char ac[] = new char[buffer.get() & 0xff];
			for (int k = 0; k < ac.length; k++)
				ac[k] = (char) (buffer.get() & 0xff);
			System.out.println("Domain enc : " + new String(ac));

			tldList[j] = ac;
		}
	}

	private void readBadEnc(ByteBuffer buffer) {
		int length = buffer.getInt();
		badEncChar = new char[length][];
		badEncByte = new byte[length][][];
		for (int j = 0; j < badEncChar.length; j++) {
			char ac1[] = new char[buffer.get() & 0xff];
			for (int k = 0; k < ac1.length; k++) {
				ac1[k] = (char) (buffer.get() & 0xff);
			}

			badEncChar[j] = ac1;

			byte abyte1[][] = new byte[buffer.get() & 0xff][2];
			for (int l = 0; l < abyte1.length; l++) {
				abyte1[l][0] = (byte) (buffer.get() & 0xff);
				abyte1[l][1] = (byte) (buffer.get() & 0xff);
			}

			if (abyte1.length > 0)
				badEncByte[j] = abyte1;
		}
	}

	private void readDomainEnc(ByteBuffer buffer) {
		int length = buffer.getInt();
		domainEnc = new char[length][];
		for (int j = 0; j < domainEnc.length; j++) {
			char ac1[] = new char[buffer.get() & 0xff];
			for (int k = 0; k < ac1.length; k++)
				ac1[k] = (char) (buffer.get() & 0xff);

			domainEnc[j] = ac1;
		}
	}

	private void readFragmentsEnc(ByteBuffer buffer) {
		fragments = new int[buffer.getInt()];
		for (int i = 0; i < fragments.length; i++)
			fragments[i] = buffer.getShort();
	}

	private void cleanText(char ac[]) {
		int i = 0;
		for (int j = 0; j < ac.length; j++) {
			if (isValidChar(ac[j]))
				ac[i] = ac[j];
			else
				ac[i] = ' ';

			if (i == 0 || ac[i] != ' ' || ac[i - 1] != ' ')
				i++;
		}
		for (int k = i; k < ac.length; k++)
			ac[k] = ' ';
	}

	private boolean isValidChar(char c) {
		return c >= ' ' && c <= '\177' || c == ' ' || c == '\n' || c == '\t'
				|| c == '\243' || c == '\u20AC';
	}

	public String filter(String s) {
		char chars[] = s.toCharArray();
		cleanText(chars);
		String s1 = new String(chars).trim();

		chars = s1.toLowerCase().toCharArray();

		String s2 = s1.toLowerCase();
		filterTld(chars);
		filterBadEnc(chars);
		filterDomain(chars);
		filterFragments(chars);
		for (int j = 0; j < WORD_EXCEPTIONS.length; j++) {
			for (int k = -1; (k = s2.indexOf(WORD_EXCEPTIONS[j], k + 1)) != -1;) {
				char ac1[] = WORD_EXCEPTIONS[j].toCharArray();
				System.arraycopy(ac1, 0, chars, k, ac1.length);
			}
		}
		copyCapitals(s1.toCharArray(), chars);
		filterExtraCharacters(chars);
		return new String(chars).trim();
	}

	private void copyCapitals(char ac[], char ac1[]) {
		for (int j = 0; j < ac.length; j++) {
			if (ac1[j] != '*' && isUpperCaseLetter(ac[j])) {
				ac1[j] = ac[j];
			}
		}
	}

	private void filterExtraCharacters(char ac[]) {
		boolean flag = true;
		for (int j = 0; j < ac.length; j++) {
			char c = ac[j];
			if (isLetter(c)) {
				if (flag) {
					if (isLowerCaseLetter(c))
						flag = false;
				} else if (isUpperCaseLetter(c))
					ac[j] = (char) ((c + 97) - 65);
			} else {
				flag = true;
			}
		}
	}

	private void filterBadEnc(char ac[]) {
		for (int i = 0; i < 2; i++) {
			for (int j = badEncChar.length - 1; j >= 0; j--)
				method509(badEncByte[j], ac, badEncChar[j]);
		}
	}

	private void filterDomain(char ac[]) {
		char ac1[] = ac.clone();
		char at[] = { '(', 'a', ')' };
		method509(null, ac1, at);
		char ac3[] = ac.clone();
		char ac4[] = { 'd', 'o', 't' };
		method509(null, ac3, ac4);
		for (int i = domainEnc.length - 1; i >= 0; i--)
			method502(ac, domainEnc[i], ac3, ac1);
	}

	private void method502(char ac[], char ac1[], char ac2[], char ac3[]) {
		if (ac1.length > ac.length)
			return;
		int j;
		for (int k = 0; k <= ac.length - ac1.length; k += j) {
			int l = k;
			int i1 = 0;
			j = 1;
			while (l < ac.length) {
				int j1;
				char c = ac[l];
				char c1 = '\0';
				if (l + 1 < ac.length)
					c1 = ac[l + 1];
				if (i1 < ac1.length && (j1 = method511(c, ac1[i1], c1)) > 0) {
					l += j1;
					i1++;
					continue;
				}
				if (i1 == 0)
					break;
				if ((j1 = method511(c, ac1[i1 - 1], c1)) > 0) {
					l += j1;
					if (i1 == 1)
						j++;
					continue;
				}
				if (i1 >= ac1.length || !isSpecialChar(c))
					break;
				l++;
			}
			if (i1 >= ac1.length) {
				boolean flag1 = false;
				int k1 = method503(ac, ac3, k);
				int l1 = method504(ac2, l - 1, ac);
				if (k1 > 2 || l1 > 2)
					flag1 = true;
				if (flag1) {
					for (int i2 = k; i2 < l; i2++)
						ac[i2] = '*';

				}
			}
		}

	}

	private int method503(char ac[], char ac1[], int j) {
		if (j == 0)
			return 2;
		for (int k = j - 1; k >= 0; k--) {
			if (!isSpecialChar(ac[k]))
				break;
			if (ac[k] == '@')
				return 3;
		}

		int l = 0;
		for (int i1 = j - 1; i1 >= 0; i1--) {
			if (!isSpecialChar(ac1[i1]))
				break;
			if (ac1[i1] == '*')
				l++;
		}

		if (l >= 3)
			return 4;
		return !isSpecialChar(ac[j - 1]) ? 0 : 1;
	}

	private int method504(char ac[], int i, char ac1[]) {
		if (i + 1 == ac1.length)
			return 2;
		for (int j = i + 1; j < ac1.length; j++) {
			if (!isSpecialChar(ac1[j]))
				break;
			if (ac1[j] == '.' || ac1[j] == ',')
				return 3;
		}
		int k = 0;
		for (int l = i + 1; l < ac1.length; l++) {
			if (!isSpecialChar(ac[l]))
				break;
			if (ac[l] == '*')
				k++;
		}

		if (k >= 3)
			return 4;
		return !isSpecialChar(ac1[i + 1]) ? 0 : 1;
	}

	private void filterTld(char ac[]) {
		char ac1[] = ac.clone();
		char ac2[] = { 'd', 'o', 't' };
		method509(null, ac1, ac2);
		char ac3[] = ac.clone();
		char ac4[] = { 's', 'l', 'a', 's', 'h' };
		method509(null, ac3, ac4);
		for (int i = 0; i < tldList.length; i++)
			method506(ac3, tldList[i], tldArray[i], ac1, ac);
	}

	private void method506(char ac[], char ac1[], int i, char ac2[], char ac3[]) {
		if (ac1.length > ac3.length)
			return;
		int j;
		for (int k = 0; k <= ac3.length - ac1.length; k += j) {
			int l = k;
			int i1 = 0;
			j = 1;
			while (l < ac3.length) {
				int j1;
				char c = ac3[l];
				char c1 = '\0';
				if (l + 1 < ac3.length)
					c1 = ac3[l + 1];
				if (i1 < ac1.length && (j1 = method511(c, ac1[i1], c1)) > 0) {
					l += j1;
					i1++;
					continue;
				}
				if (i1 == 0)
					break;
				if ((j1 = method511(c, ac1[i1 - 1], c1)) > 0) {
					l += j1;
					if (i1 == 1)
						j++;
					continue;
				}
				if (i1 >= ac1.length || !isSpecialChar(c))
					break;
				l++;
			}
			if (i1 >= ac1.length) {
				boolean flag1 = false;
				int k1 = method507(ac3, k, ac2);
				int l1 = method508(ac3, ac, l - 1);
				if (i == 1 && k1 > 0 && l1 > 0)
					flag1 = true;
				if (i == 2 && (k1 > 2 && l1 > 0 || k1 > 0 && l1 > 2))
					flag1 = true;
				if (i == 3 && k1 > 0 && l1 > 2)
					flag1 = true;
				if (flag1) {
					int i2 = k;
					int j2 = l - 1;
					if (k1 > 2) {
						if (k1 == 4) {
							boolean flag2 = false;
							for (int l2 = i2 - 1; l2 >= 0; l2--)
								if (flag2) {
									if (ac2[l2] != '*')
										break;
									i2 = l2;
								} else if (ac2[l2] == '*') {
									i2 = l2;
									flag2 = true;
								}

						}
						boolean flag3 = false;
						for (int i3 = i2 - 1; i3 >= 0; i3--)
							if (flag3) {
								if (isSpecialChar(ac3[i3]))
									break;
								i2 = i3;
							} else if (!isSpecialChar(ac3[i3])) {
								flag3 = true;
								i2 = i3;
							}
					}
					if (l1 > 2) {
						if (l1 == 4) {
							boolean flag4 = false;
							for (int j3 = j2 + 1; j3 < ac3.length; j3++)
								if (flag4) {
									if (ac[j3] != '*')
										break;
									j2 = j3;
								} else if (ac[j3] == '*') {
									j2 = j3;
									flag4 = true;
								}

						}
						boolean flag5 = false;
						for (int k3 = j2 + 1; k3 < ac3.length; k3++)
							if (flag5) {
								if (isSpecialChar(ac3[k3]))
									break;
								j2 = k3;
							} else if (!isSpecialChar(ac3[k3])) {
								flag5 = true;
								j2 = k3;
							}

					}
					for (int k2 = i2; k2 <= j2; k2++)
						ac3[k2] = '*';
				}
			}
		}
	}

	private int method507(char ac[], int j, char ac1[]) {
		if (j == 0)
			return 2;
		for (int k = j - 1; k >= 0; k--) {
			if (!isSpecialChar(ac[k]))
				break;
			if (ac[k] == ',' || ac[k] == '.')
				return 3;
		}
		int l = 0;
		for (int i1 = j - 1; i1 >= 0; i1--) {
			if (!isSpecialChar(ac1[i1]))
				break;
			if (ac1[i1] == '*')
				l++;
		}
		if (l >= 3)
			return 4;
		return !isSpecialChar(ac[j - 1]) ? 0 : 1;
	}

	private int method508(char ac[], char ac1[], int i) {
		if (i + 1 == ac.length)
			return 2;
		for (int j = i + 1; j < ac.length; j++) {
			if (!isSpecialChar(ac[j]))
				break;
			if (ac[j] == '\\' || ac[j] == '/')
				return 3;
		}

		int k = 0;
		for (int l = i + 1; l < ac.length; l++) {
			if (!isSpecialChar(ac1[l]))
				break;
			if (ac1[l] == '*')
				k++;
		}

		if (k >= 5)
			return 4;
		return !isSpecialChar(ac[i + 1]) ? 0 : 1;
	}

	private void method509(byte abyte0[][], char ac[], char ac1[]) {
		if (ac1.length > ac.length)
			return;
		int j;
		for (int k = 0; k <= ac.length - ac1.length; k += j) {
			int l = k;
			int i1 = 0;
			int j1 = 0;
			j = 1;
			boolean flag1 = false;
			boolean flag2 = false;
			boolean flag3 = false;
			while (l < ac.length && (!flag2 || !flag3)) {
				int k1;
				char c = ac[l];
				char c2 = '\0';
				if (l + 1 < ac.length)
					c2 = ac[l + 1];
				if (i1 < ac1.length && (k1 = method512(c2, c, ac1[i1])) > 0) {
					if (k1 == 1 && isDigit(c))
						flag2 = true;
					if (k1 == 2 && (isDigit(c) || isDigit(c2)))
						flag2 = true;
					l += k1;
					i1++;
					continue;
				}
				if (i1 == 0)
					break;
				if ((k1 = method512(c2, c, ac1[i1 - 1])) > 0) {
					l += k1;
					if (i1 == 1)
						j++;
					continue;
				}
				if (i1 >= ac1.length || !method518(c))
					break;
				if (isSpecialChar(c) && c != '\'')
					flag1 = true;
				if (isDigit(c))
					flag3 = true;
				l++;
				if ((++j1 * 100) / (l - k) > 90)
					break;
			}
			if (i1 >= ac1.length && (!flag2 || !flag3)) {
				boolean flag4 = true;
				if (!flag1) {
					char c1 = ' ';
					if (k - 1 >= 0)
						c1 = ac[k - 1];
					char c3 = ' ';
					if (l < ac.length)
						c3 = ac[l];
					byte byte0 = method513(c1);
					byte byte1 = method513(c3);
					if (abyte0 != null && method510(byte0, abyte0, byte1))
						flag4 = false;
				} else {
					boolean flag5 = false;
					boolean flag6 = false;
					if (k - 1 < 0 || isSpecialChar(ac[k - 1])
							&& ac[k - 1] != '\'')
						flag5 = true;
					if (l >= ac.length || isSpecialChar(ac[l]) && ac[l] != '\'')
						flag6 = true;
					if (!flag5 || !flag6) {
						boolean flag7 = false;
						int k2 = k - 2;
						if (flag5)
							k2 = k;
						for (; !flag7 && k2 < l; k2++)
							if (k2 >= 0
									&& (!isSpecialChar(ac[k2]) || ac[k2] == '\'')) {
								char ac2[] = new char[3];
								int j3;
								for (j3 = 0; j3 < 3; j3++) {
									if (k2 + j3 >= ac.length
											|| isSpecialChar(ac[k2 + j3])
											&& ac[k2 + j3] != '\'')
										break;
									ac2[j3] = ac[k2 + j3];
								}

								boolean flag8 = true;
								if (j3 == 0)
									flag8 = false;
								if (j3 < 3
										&& k2 - 1 >= 0
										&& (!isSpecialChar(ac[k2 - 1]) || ac[k2 - 1] == '\''))
									flag8 = false;
								if (flag8 && !method523(ac2))
									flag7 = true;
							}

						if (!flag7)
							flag4 = false;
					}
				}
				if (flag4) {
					int l1 = 0;
					int i2 = 0;
					int j2 = -1;
					for (int l2 = k; l2 < l; l2++)
						if (isDigit(ac[l2]))
							l1++;
						else if (isLetter(ac[l2])) {
							i2++;
							j2 = l2;
						}
					if (j2 > -1)
						l1 -= l - 1 - j2;
					if (l1 <= i2) {
						for (int i3 = k; i3 < l; i3++)
							ac[i3] = '*';

					} else {
						j = 1;
					}
				}
			}
		}
	}

	private boolean method510(byte byte0, byte abyte0[][], byte byte2) {
		int i = 0;
		if (abyte0[i][0] == byte0 && abyte0[i][1] == byte2)
			return true;
		int j = abyte0.length - 1;
		if (abyte0[j][0] == byte0 && abyte0[j][1] == byte2)
			return true;
		do {
			int k = (i + j) / 2;
			if (abyte0[k][0] == byte0 && abyte0[k][1] == byte2)
				return true;
			if (byte0 < abyte0[k][0] || byte0 == abyte0[k][0]
					&& byte2 < abyte0[k][1])
				j = k;
			else
				i = k;
		} while (i != j && i + 1 != j);
		return false;
	}

	private int method511(char c, char c1, char c2) {
		if (c1 == c)
			return 1;
		if (c1 == 'o' && c == '0')
			return 1;
		if (c1 == 'o' && c == '(' && c2 == ')')
			return 2;
		if (c1 == 'c' && (c == '(' || c == '<' || c == '['))
			return 1;
		if (c1 == 'e' && c == '\u20AC')
			return 1;
		if (c1 == 's' && c == '$')
			return 1;
		return c1 != 'l' || c != 'i' ? 0 : 1;
	}

	private int method512(char c, char c1, char c2) {
		if (c2 == c1)
			return 1;
		if (c2 >= 'a' && c2 <= 'm') {
			if (c2 == 'a') {
				if (c1 == '4' || c1 == '@' || c1 == '^')
					return 1;
				return c1 != '/' || c != '\\' ? 0 : 2;
			}
			if (c2 == 'b') {
				if (c1 == '6' || c1 == '8')
					return 1;
				return (c1 != '1' || c != '3') && (c1 != 'i' || c != '3') ? 0
						: 2;
			}
			if (c2 == 'c')
				return c1 != '(' && c1 != '<' && c1 != '{' && c1 != '[' ? 0 : 1;
			if (c2 == 'd')
				return (c1 != '[' || c != ')') && (c1 != 'i' || c != ')') ? 0
						: 2;
			if (c2 == 'e')
				return c1 != '3' && c1 != '\u20AC' ? 0 : 1;
			if (c2 == 'f') {
				if (c1 == 'p' && c == 'h')
					return 2;
				return c1 != '\243' ? 0 : 1;
			}
			if (c2 == 'g')
				return c1 != '9' && c1 != '6' && c1 != 'q' ? 0 : 1;
			if (c2 == 'h')
				return c1 != '#' ? 0 : 1;
			if (c2 == 'i')
				return c1 != 'y' && c1 != 'l' && c1 != 'j' && c1 != '1'
						&& c1 != '!' && c1 != ':' && c1 != ';' && c1 != '|' ? 0
						: 1;
			if (c2 == 'j')
				return 0;
			if (c2 == 'k')
				return 0;
			if (c2 == 'l')
				return c1 != '1' && c1 != '|' && c1 != 'i' ? 0 : 1;
			if (c2 == 'm')
				return 0;
		}
		if (c2 >= 'n' && c2 <= 'z') {
			if (c2 == 'n')
				return 0;
			if (c2 == 'o') {
				if (c1 == '0' || c1 == '*')
					return 1;
				return (c1 != '(' || c != ')') && (c1 != '[' || c != ']')
						&& (c1 != '{' || c != '}') && (c1 != '<' || c != '>') ? 0
						: 2;
			}
			if (c2 == 'p')
				return 0;
			if (c2 == 'q')
				return 0;
			if (c2 == 'r')
				return 0;
			if (c2 == 's')
				return c1 != '5' && c1 != 'z' && c1 != '$' && c1 != '2' ? 0 : 1;
			if (c2 == 't')
				return c1 != '7' && c1 != '+' ? 0 : 1;
			if (c2 == 'u') {
				if (c1 == 'v')
					return 1;
				return (c1 != '\\' || c != '/') && (c1 != '\\' || c != '|')
						&& (c1 != '|' || c != '/') ? 0 : 2;
			}
			if (c2 == 'v')
				return (c1 != '\\' || c != '/') && (c1 != '\\' || c != '|')
						&& (c1 != '|' || c != '/') ? 0 : 2;
			if (c2 == 'w')
				return c1 != 'v' || c != 'v' ? 0 : 2;
			if (c2 == 'x')
				return (c1 != ')' || c != '(') && (c1 != '}' || c != '{')
						&& (c1 != ']' || c != '[') && (c1 != '>' || c != '<') ? 0
						: 2;
			if (c2 == 'y')
				return 0;
			if (c2 == 'z')
				return 0;
		}
		if (c2 >= '0' && c2 <= '9') {
			if (c2 == '0') {
				if (c1 == 'o' || c1 == 'O')
					return 1;
				return (c1 != '(' || c != ')') && (c1 != '{' || c != '}')
						&& (c1 != '[' || c != ']') ? 0 : 2;
			}
			if (c2 == '1')
				return c1 != 'l' ? 0 : 1;
			else
				return 0;
		}
		if (c2 == ',')
			return c1 != '.' ? 0 : 1;
		if (c2 == '.')
			return c1 != ',' ? 0 : 1;
		if (c2 == '!')
			return c1 != 'i' ? 0 : 1;
		else
			return 0;
	}

	private byte method513(char c) {
		if (c >= 'a' && c <= 'z')
			return (byte) ((c - 97) + 1);
		if (c == '\'')
			return 28;
		if (c >= '0' && c <= '9')
			return (byte) ((c - 48) + 29);
		else
			return 27;
	}

	private void filterFragments(char ac[]) {
		int j;
		int k = 0;
		int l = 0;
		int i1 = 0;
		while ((j = findFirstDigit(ac, k)) != -1) {
			boolean flag = false;
			for (int j1 = k; j1 >= 0 && j1 < j && !flag; j1++)
				if (!isSpecialChar(ac[j1]) && !method518(ac[j1]))
					flag = true;

			if (flag)
				l = 0;
			if (l == 0)
				i1 = j;
			k = findFirstNonDigit(ac, j);
			int k1 = 0;
			for (int l1 = j; l1 < k; l1++)
				k1 = (k1 * 10 + ac[l1]) - 48;

			if (k1 > 255 || k - j > 8)
				l = 0;
			else
				l++;
			if (l == 4) {
				for (int i2 = i1; i2 < k; i2++)
					ac[i2] = '*';

				l = 0;
			}
		}
	}

	private int findFirstDigit(char ac[], int i) {
		for (int k = i; k < ac.length && k >= 0; k++)
			if (ac[k] >= '0' && ac[k] <= '9')
				return k;
		return -1;
	}

	private int findFirstNonDigit(char ac[], int j) {
		for (int k = j; k < ac.length && k >= 0; k++)
			if (ac[k] < '0' || ac[k] > '9')
				return k;
		return ac.length;
	}

	private boolean isSpecialChar(char c) {
		return !isLetter(c) && !isDigit(c);
	}

	private boolean method518(char c) {
		return c < 'a' || c > 'z' || c == 'v' || c == 'x' || c == 'j'
				|| c == 'q' || c == 'z';
	}

	private boolean isLetter(char c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isLowerCaseLetter(char c) {
		return c >= 'a' && c <= 'z';
	}

	private boolean isUpperCaseLetter(char c) {
		return c >= 'A' && c <= 'Z';
	}

	private boolean method523(char ac[]) {
		boolean flag = true;
		for (int i = 0; i < ac.length; i++)
			if (!isDigit(ac[i]) && ac[i] != 0)
				flag = false;
		if (flag)
			return true;
		int j = method524(ac);
		int k = 0;
		int l = fragments.length - 1;
		if (j == fragments[k] || j == fragments[l])
			return true;
		do {
			int i1 = (k + l) / 2;
			if (j == fragments[i1])
				return true;
			if (j < fragments[i1])
				l = i1;
			else
				k = i1;
		} while (k != l && k + 1 != l);
		return false;
	}

	private int method524(char ac[]) {
		if (ac.length > 6)
			return 0;
		int k = 0;
		for (int l = 0; l < ac.length; l++) {
			char c = ac[ac.length - l - 1];
			if (c >= 'a' && c <= 'z')
				k = k * 38 + ((c - 97) + 1);
			else if (c == '\'')
				k = k * 38 + 27;
			else if (c >= '0' && c <= '9')
				k = k * 38 + ((c - 48) + 28);
			else if (c != 0)
				return 0;
		}
		return k;
	}
}
