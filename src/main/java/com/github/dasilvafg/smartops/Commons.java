package com.github.dasilvafg.smartops;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.NodeList;

/**
 * Utility class with several methods for validation and conversion. All methods
 * are thread-safe and null-safe.
 * 
 * @author Fábio Silva
 * @since 1.0.0
 */
public final class Commons {

	private static final String REXP_EMAIL = "^(\\p{Alnum}+((\\.|_+|-+)\\p{Alnum}+)*){1,64}@(\\p{Alnum}+((\\.|_+|-+)\\p{Alnum}+)*){1,255}$";
	private static final String REXP_WORD = "(?s)[\\p{L}\\d]+([_-][\\p{L}\\d]+)*('[\\p{L}\\d]*)?|'[\\p{L}\\d]+$";
	private static final String REXP_DATE_SIMP = "^\\d{4}(\\-\\d{2}){2}$";
	private static final String REXP_DATE_TIME = "^\\d{4}(\\-\\d{2}){2}T\\d{2}(:\\d{2}){1,2}(\\.\\d{3}Z?)$";
	private static final String REXP_DATE_OFFS = "^\\d{4}(\\-\\d{2}){2}T\\d{2}(:\\d{2}){2}\\.\\d+[+-]\\d{2}:?\\d{2}$";
	private static final String REXP_DATE_STDD = "^(Sun|Mon|Tue|Wed|Thu|Fri|Sat) (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \\d{1,2} \\d{2}(:\\d{2}){2} [A-Z]+ \\d{4}$";

	private Commons() {
	}

	/**
	 * Determines whether a value is empty.
	 * 
	 * <p>
	 * A value is considered empty according to the following rules:
	 * 
	 * <ol>
	 * <li>If the object is {@code null} returns {@code true};
	 * 
	 * <li>If the object is an {@code array}, returns the equality of
	 * {@link Array#getLength(Object)} to {@code zero};
	 * 
	 * <li>If the object is a {@link CharSequence}, returns the equality of its
	 * {@link CharSequence#length()} to {@code zero};
	 * 
	 * <li>If the object is a {@link Character}, returns the equality of its
	 * {@link Character#charValue()} to {@code zero};
	 * 
	 * <li>If the object is a {@link Number}, returns the equality of its
	 * {@link Number#doubleValue()} to {@code 0D};
	 * 
	 * <li>If the object is a {@link SmartDecimal}, returns the equality of its
	 * {@link SmartDecimal#toBigDecimal()} to {@link BigDecimal#ZERO};
	 * 
	 * <li>If the object is a {@link NodeList}, returns the equality of its
	 * {@link NodeList#getLength()} to {@code zero};
	 * 
	 * <li>If the object is a {@link Collection}, returns the result of its
	 * {@link Collection#isEmpty()} method;
	 * 
	 * <li>If the object is a {@link Map}, returns the result of its
	 * {@link Map#isEmpty()} method;
	 * 
	 * <li>If the object is an {@link Iterator}, returns the negation of its
	 * {@link Iterator#hasNext()} method;
	 * 
	 * <li>If the object is an {@link Enumeration}, returns the negation of its
	 * {@link Enumeration#hasMoreElements()} method;
	 * 
	 * <li>Otherwise returns {@code false}.
	 * </ol>
	 * 
	 * @param obj
	 *            The object reference to be tested.
	 * @return {@code true} if the object is empty.
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		}
		if (obj instanceof CharSequence) {
			return ((CharSequence) obj).length() == 0;
		}
		if (obj instanceof Character) {
			return ((Character) obj).charValue() == 0;
		}
		if (obj instanceof Number) {
			return ((Number) obj).doubleValue() == 0D;
		}
		if (obj instanceof NodeList) {
			return ((NodeList) obj).getLength() == 0;
		}
		if (obj instanceof SmartDecimal) {
			return ((SmartDecimal) obj).toBigDecimal().equals(BigDecimal.ZERO);
		}
		if (obj instanceof Collection) {
			return ((Collection<?>) obj).isEmpty();
		}
		if (obj instanceof Map) {
			return ((Map<?, ?>) obj).isEmpty();
		}
		if (obj instanceof Iterator) {
			return !((Iterator<?>) obj).hasNext();
		}
		if (obj instanceof Enumeration) {
			return !((Enumeration<?>) obj).hasMoreElements();
		}
		return false;
	}

	/**
	 * Determines whether a {@link CharSequence} is blank.
	 * 
	 * <p>
	 * A {@link CharSequence} will be considered blank if it's null or only
	 * formed of these characters:
	 * 
	 * <ul>
	 * <li>NUL (0x00)
	 * <li>Horizontal tab (0x09)
	 * <li>Line feed (0x0a)
	 * <li>Vertical tab (0x0b)
	 * <li>Form feed (0x0c)
	 * <li>Carriage return (0x0d)
	 * <li>Space (0x20)
	 * </ul>
	 * 
	 * @param seq
	 *            The char sequence.
	 * @return {@code true} if the sequence is blank.
	 */
	public static boolean isBlank(CharSequence seq) {
		if (seq == null) {
			return true;
		}
		for (int i = 0; i < seq.length(); i++) {
			char ch = seq.charAt(i);
			if (ch != 0x00 && ch != '\t' && ch != '\n' && ch != 0x0B && ch != '\f' && ch != '\r'
					&& ch != ' ') {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determines whether a string is acceptable to the constructor
	 * {@link BigDecimal#BigDecimal(String)}.
	 * 
	 * @param str
	 *            The string.
	 * @return {@code true} if the string is not null and numeric.
	 */
	public static boolean isDecimal(String str) {
		return str != null && str.matches("(?s)^[+-]?\\d+(\\.\\d+)?([Ee][+-]?\\d+)?$");
	}

	/**
	 * Determines whether a string is acceptable to the constructor
	 * {@link BigInteger#BigInteger(String)}.
	 * 
	 * @param str
	 *            The string.
	 * @return {@code true} if the string is not null and a decimal number.
	 */
	public static boolean isInteger(String str) {
		return str != null && str.matches("(?s)^[+-]?\\d+$");
	}

	/**
	 * Determines whether a string is hexadecimal.
	 * 
	 * @param str
	 *            The string.
	 * @return {@code true} if the string is not null and made of hexadecimal
	 *         digits.
	 */
	public static boolean isHexa(String str) {
		return str != null && str.matches("(?s)^[0-9A-Fa-f]+$");
	}

	/**
	 * Determines whether a string is alphabetic.
	 * 
	 * @param str
	 *            The string.
	 * @return {@code true} if the string is not null and made of alphabetic
	 *         letters in upper or lower case.
	 */
	public static boolean isAlpha(String str) {
		return str != null && str.matches("(?s)^\\p{L}+$");
	}

	/**
	 * Determines whether a string is made of Base64 characters.
	 * 
	 * @param str
	 *            The string.
	 * @return {@code true} if the string is not null and made of base64
	 *         characters.
	 */
	public static boolean isBase64(String str) {
		return str != null && str.matches("(?s)^[0-9A-Za-z+/=]+$");
	}

	/**
	 * Determines whether a string is a word. Recognizes letters and digits,
	 * compound words separated by '-' or '_', and partial words prefixed or
	 * suffixed by single quotes.
	 * 
	 * @param str
	 *            The string.
	 * @return {@code true} if the string is a valid word.
	 */
	public static boolean isWord(String str) {
		return str != null && str.matches(REXP_WORD);
	}

	/**
	 * Determines whether a string is a valid email address according to the
	 * rules from
	 * <a href="https://www.rfc-editor.org/rfc/rfc3696#section-3">RFC-3696</a>.
	 * 
	 * @param addr
	 *            The address to validade.
	 * @return {@code true} if the address is valid.
	 */
	public static boolean isEmail(String addr) {
		return addr != null && addr.matches(REXP_EMAIL);
	}

	/**
	 * Determines whether a string is a valid URL according to the rules from
	 * <a href="https://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>.
	 * 
	 * <p>
	 * This method delegates to {@link URL#URL(URL, String, URLStreamHandler)}.
	 * 
	 * @param url
	 *            The URL to validate.
	 * @return {@code true} if the URL is valid.
	 */
	public static boolean isUrl(String url) {
		try {
			new URL((URL) null, url, null);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Converts a text string to plain ASCII by applying a {@link Normalizer
	 * canonical decomposition} and removing the diacritical marks and control
	 * characters.
	 * 
	 * <p>
	 * This method will also replace back quotes with the apostrophe and the
	 * dash with the hyphen.
	 * 
	 * @param str
	 *            The string.
	 * @return The normalized string.
	 */
	public static String normalize(String str) {
		if (str == null) {
			str = "";
		}
		str = Normalizer.normalize(str, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
				.replaceAll("\\p{Cntrl}+", "-")
				.replaceAll("[´`]", "'")
				.replaceAll("–", "-");
		return str;
	}

	/**
	 * Coalesces a string by trimming its borders and replacing all internal
	 * blank characters with one single space.
	 * 
	 * @param str
	 *            The string.
	 * @return The coalesced string.
	 */
	public static String coalesce(String str) {
		return (str != null) ? str.trim().replaceAll("\\s+", " ") : "";
	}

	/**
	 * Truncates a string to a given maximum length.
	 * 
	 * @param str
	 *            The string.
	 * @param maxlen
	 *            The maximum length.
	 * @return The truncated string.
	 */
	public static String truncate(String str, int maxlen) {
		if (str == null) {
			str = "";
		} else if (str.length() > maxlen) {
			str = str.substring(0, maxlen);
		}
		return str;
	}

	/**
	 * Converts a string to lower case, then capitalizes all its initials.
	 * 
	 * @param str
	 *            The string.
	 * @return The capitalized string.
	 */
	public static String capitalize(String str) {
		str = (str != null) ? str.toLowerCase() : "";
		StringBuilder buffer = new StringBuilder();
		boolean upper = true;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '\t' || ch == '\n' || ch == 0x0B || ch == '\f' || ch == '\r' || ch == ' ') {
				upper = true;
				buffer.append(ch);
			} else if (upper) {
				buffer.append(Character.toUpperCase(ch));
				upper = false;
			} else {
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}

	/**
	 * Converts an object to a {@link Date}.
	 * 
	 * <p>
	 * The following types and expressions are recognized:
	 * <ol>
	 * <li>A {@link Date} instance;
	 * <li>A {@link LocalDate} instance;
	 * <li>A {@link LocalDateTime} instance;
	 * <li>A {@link ZonedDateTime} instance;
	 * <li>A {@link OffsetDateTime} instance;
	 * <li>A {@link Calendar} instance;
	 * <li>A string compatible with {@link Date#toString()};
	 * <li>A string compatible with {@link LocalDate#toString()};
	 * <li>A string compatible with {@link LocalDateTime#toString()};
	 * <li>A string compatible with {@link ZonedDateTime#toString()};
	 * <li>A string compatible with {@link OffsetDateTime#toString()}.
	 * </ol>
	 * 
	 * @param obj
	 *            The date value or expression.
	 * @return The {@link Date} object, or null if the value is null.
	 * @throws IllegalArgumentException
	 *             If the value is not recognized as a valid date.
	 */
	public static Date toDate(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Date) {
			return (Date) obj;
		}
		if (obj instanceof LocalDate) {
			return Date.from(((LocalDate) obj).atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		if (obj instanceof LocalDateTime) {
			return Date.from(((LocalDateTime) obj).atZone(ZoneId.systemDefault()).toInstant());
		}
		if (obj instanceof ZonedDateTime) {
			return Date.from(((ZonedDateTime) obj).toInstant());
		}
		if (obj instanceof OffsetDateTime) {
			return Date.from(((OffsetDateTime) obj).toInstant());
		}
		if (obj instanceof Calendar) {
			return ((Calendar) obj).getTime();
		}
		if (obj instanceof String) {
			String str = ((String) obj).replace(' ', 'T');
			if (str.matches(REXP_DATE_SIMP)) {
				return Date.from(
						LocalDate.parse(str).atStartOfDay(ZoneId.systemDefault()).toInstant());
			}
			if (str.matches(REXP_DATE_TIME)) {
				if (str.endsWith("Z")) {
					return Date.from(ZonedDateTime.parse(str).toInstant());
				} else {
					return Date.from(
							LocalDateTime.parse(str).atZone(ZoneId.systemDefault()).toInstant());
				}
			}
			if (str.matches(REXP_DATE_OFFS)) {
				int n = str.length();
				char ch = str.charAt(n - 3);
				if (ch != ':') {
					str = str.substring(0, n - 2) + ':' + str.substring(n - 2);
				}
				return Date.from(OffsetDateTime.parse(str).toInstant());
			}
			if (str.matches(REXP_DATE_STDD)) {
				ParsePosition pos = new ParsePosition(0);
				Date date = new SimpleDateFormat(REXP_DATE_STDD).parse(str, pos);
				if (pos.getIndex() > 0) {
					return date;
				} else {
					throw new IllegalArgumentException("Invalid date: " + str);
				}
			}
		}
		throw new IllegalArgumentException("Unknown date type: " + obj);
	}

	/**
	 * Converts an object to a {@link LocalDate}.
	 * 
	 * <p>
	 * The following types and expressions are recognized:
	 * <ol>
	 * <li>A {@link Date} instance;
	 * <li>A {@link LocalDate} instance;
	 * <li>A {@link LocalDateTime} instance;
	 * <li>A {@link ZonedDateTime} instance;
	 * <li>A {@link OffsetDateTime} instance;
	 * <li>A {@link Calendar} instance;
	 * <li>A string compatible with {@link Date#toString()};
	 * <li>A string compatible with {@link LocalDate#toString()};
	 * <li>A string compatible with {@link LocalDateTime#toString()};
	 * <li>A string compatible with {@link ZonedDateTime#toString()};
	 * <li>A string compatible with {@link OffsetDateTime#toString()}.
	 * </ol>
	 * 
	 * @param obj
	 *            The date value or expression.
	 * @return The {@link LocalDate} object, or null if the value is null.
	 * @throws IllegalArgumentException
	 *             If the value is not recognized as a valid date.
	 */
	public static LocalDate toLocalDate(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof LocalDate) {
			return (LocalDate) obj;
		}
		if (obj instanceof LocalDateTime) {
			return ((LocalDateTime) obj).toLocalDate();
		}
		if (obj instanceof ZonedDateTime) {
			return ((ZonedDateTime) obj).toLocalDate();
		}
		if (obj instanceof OffsetDateTime) {
			return ((OffsetDateTime) obj).toLocalDate();
		}
		if (obj instanceof Date) {
			return ((Date) obj).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}
		if (obj instanceof Calendar) {
			return ((Calendar) obj).getTime()
					.toInstant()
					.atZone(ZoneId.systemDefault())
					.toLocalDate();
		}
		if (obj instanceof String) {
			String str = ((String) obj).replace(' ', 'T');
			if (str.matches(REXP_DATE_SIMP)) {
				return LocalDate.parse(str);
			}
			if (str.matches(REXP_DATE_TIME)) {
				return str.endsWith("Z") ? ZonedDateTime.parse(str).toLocalDate()
						: LocalDateTime.parse(str).toLocalDate();
			}
			if (str.matches(REXP_DATE_OFFS)) {
				int n = str.length();
				char ch = str.charAt(n - 3);
				if (ch != ':') {
					str = str.substring(0, n - 2) + ':' + str.substring(n - 2);
				}
				return OffsetDateTime.parse(str).toLocalDate();
			}
			if (str.matches(REXP_DATE_STDD)) {
				ParsePosition pos = new ParsePosition(0);
				Date date = new SimpleDateFormat(REXP_DATE_STDD).parse(str, pos);
				if (pos.getIndex() > 0) {
					return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				} else {
					throw new IllegalArgumentException("Invalid date: " + str);
				}
			}
		}
		throw new IllegalArgumentException("Unknown date type: " + obj);
	}

	/**
	 * Converts an object to a {@link LocalDateTime}.
	 * 
	 * <p>
	 * The following types and expressions are recognized:
	 * <ol>
	 * <li>A {@link Date} instance;
	 * <li>A {@link LocalDate} instance;
	 * <li>A {@link LocalDateTime} instance;
	 * <li>A {@link ZonedDateTime} instance;
	 * <li>A {@link OffsetDateTime} instance;
	 * <li>A {@link Calendar} instance;
	 * <li>A string compatible with {@link Date#toString()};
	 * <li>A string compatible with {@link LocalDate#toString()};
	 * <li>A string compatible with {@link LocalDateTime#toString()};
	 * <li>A string compatible with {@link ZonedDateTime#toString()};
	 * <li>A string compatible with {@link OffsetDateTime#toString()}.
	 * </ol>
	 * 
	 * @param obj
	 *            The date value or expression.
	 * @return The {@link LocalDateTime} object, or null if the value is null.
	 * @throws IllegalArgumentException
	 *             If the value is not recognized as a valid date.
	 */
	public static LocalDateTime toLocalDateTime(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof LocalDate) {
			return ((LocalDate) obj).atStartOfDay();
		}
		if (obj instanceof LocalDateTime) {
			return (LocalDateTime) obj;
		}
		if (obj instanceof ZonedDateTime) {
			return ((ZonedDateTime) obj).toLocalDateTime();
		}
		if (obj instanceof OffsetDateTime) {
			return ((OffsetDateTime) obj).toLocalDateTime();
		}
		if (obj instanceof Date) {
			return ((Date) obj).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		}
		if (obj instanceof Calendar) {
			return ((Calendar) obj).getTime()
					.toInstant()
					.atZone(ZoneId.systemDefault())
					.toLocalDateTime();
		}
		if (obj instanceof String) {
			String str = ((String) obj).replace(' ', 'T');
			if (str.matches(REXP_DATE_SIMP)) {
				return LocalDate.parse(str).atStartOfDay();
			}
			if (str.matches(REXP_DATE_TIME)) {
				return str.endsWith("Z") ? ZonedDateTime.parse(str).toLocalDateTime()
						: LocalDateTime.parse(str);
			}
			if (str.matches(REXP_DATE_OFFS)) {
				int n = str.length();
				char ch = str.charAt(n - 3);
				if (ch != ':') {
					str = str.substring(0, n - 2) + ':' + str.substring(n - 2);
				}
				return OffsetDateTime.parse(str).toLocalDateTime();
			}
			if (str.matches(REXP_DATE_STDD)) {
				ParsePosition pos = new ParsePosition(0);
				Date date = new SimpleDateFormat(REXP_DATE_STDD).parse(str, pos);
				if (pos.getIndex() > 0) {
					return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				} else {
					throw new IllegalArgumentException("Invalid date: " + str);
				}
			}
		}
		throw new IllegalArgumentException("Unknown date type: " + obj);
	}

	/**
	 * Detects the MIME type from a data source.
	 * 
	 * <p>
	 * If the source is a {@link Path}, {@link File}, {@link URL} or {@link URI}
	 * instance, this method delegates to {@link Files#probeContentType(Path)}.
	 * 
	 * <p>
	 * If the source is a {@code byte[]} with more than 4 bytes, this method
	 * will try to determine the MIME type from the first 4 bytes. If the type
	 * is not recognized, the default {@code application/octet-stream} will be
	 * returned. If the source is a {@code char[]} or a {@link String} with more
	 * than 5 chars, this method will try to determine the MIME type from the
	 * first 5 chars. If the type is not recognized, the default
	 * {@code text/plain} will be returned.
	 * 
	 * <p>
	 * If the source is an {@link InputStream}, this method will try to read up
	 * to 10 bytes and apply the same algorithm as to a {@code byte[]} source.
	 * If the source is a {@link Reader}, this method will try to read up to 10
	 * chars and apply the same algorithm as to a {@code char[]} source.
	 * <b>Either of these attempts may spoil the source if it does not support
	 * mark/reset operations.</b>
	 * 
	 * <p>
	 * If none of the above tests finds a match, this method returns
	 * {@code null}.
	 * 
	 * @param src
	 *            The data source.
	 * @return The MIME type or {@code null} if not recognized.
	 */
	public static String detectMimeType(Object src) {
		try {
			if (src instanceof Path) {
				return Files.probeContentType((Path) src);
			}
			if (src instanceof File) {
				return Files.probeContentType(((File) src).toPath());
			}
			if (src instanceof URL) {
				return Files.probeContentType(Paths.get(((URL) src).toURI()));
			}
			if (src instanceof URI) {
				return Files.probeContentType(Paths.get((URI) src));
			}
		} catch (IOException | URISyntaxException e) {
			return null;
		}

		int n = 0;
		byte[] bin = null;
		char[] chr = null;

		try {
			if (src instanceof byte[]) {
				bin = (byte[]) src;
				n = bin.length;
			} else if (src instanceof char[]) {
				chr = (char[]) src;
				n = chr.length;
			} else if (src instanceof InputStream) {
				InputStream in = (InputStream) src;
				bin = new byte[10];
				in.mark(bin.length);
				n = in.read(bin);
				in.reset();
			} else if (src instanceof Reader) {
				Reader in = (Reader) src;
				chr = new char[10];
				in.mark(chr.length);
				n = in.read(chr);
				in.reset();
			}
		} catch (IOException e) {
			return null;
		}

		if (bin != null && n > 4) {
			int n0 = bin[0] & 0xFF;
			int n1 = bin[1] & 0xFF;
			int n2 = bin[2] & 0xFF;
			int n3 = bin[3] & 0xFF;
			if (n0 == 0xFF && n1 == 0xD8 && n2 == 0xFF && n3 == 0xE0) {
				return "image/jpeg";
			}
			if (n0 == 0x89 && n1 == 0x50 && n2 == 0x4E && n3 == 0x47) {
				return "image/png";
			}
			if (n0 == 0x47 && n1 == 0x49 && n2 == 0x46 && n3 == 0x38) {
				return "image/gif";
			}
			if (n0 == 0x52 && n1 == 0x49 && n2 == 0x46 && n3 == 0x46) {
				return "image/webp";
			}
			if (n0 == 0x25 && n1 == 0x50 && n2 == 0x44 && n3 == 0x46) {
				return "application/pdf";
			}
			if (n0 == 0x50 && n1 == 0x4B && n2 == 0x03 && n3 == 0x04) {
				return "application/zip";
			}
			return "application/octet-stream";
		}

		if (chr != null && n > 5) {
			if (chr[0] == '{') {
				return "application/json";
			}
			if (chr[0] == '<' && chr[1] == '?' && Character.toLowerCase(chr[2]) == 'x'
					&& Character.toLowerCase(chr[3]) == 'm'
					&& Character.toLowerCase(chr[4]) == 'l') {
				return "application/xml";
			}
			return "text/plain";
		}

		if (src instanceof String && ((String) src).length() > 5) {
			String str = (String) src;
			if (str.charAt(0) == '{') {
				return "application/json";
			}
			if (str.charAt(0) == '<' && str.charAt(1) == '?'
					&& Character.toLowerCase(str.charAt(2)) == 'x'
					&& Character.toLowerCase(str.charAt(3)) == 'm'
					&& Character.toLowerCase(str.charAt(4)) == 'l') {
				return "application/xml";
			}
			return "text/plain";
		}

		return null;
	}

}
