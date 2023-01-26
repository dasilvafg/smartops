package com.github.dasilvafg.smartops;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.Normalizer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.NodeList;

/**
 * Wrapper class with various operators. All methods are thread-safe and
 * null-safe.
 * 
 * @author Fábio Silva
 * @since 1.0.0
 */
public final class Operators {

	private static final Pattern pEmail = Pattern.compile(
			"^(\\p{Alnum}+((\\.|_+|-+)\\p{Alnum}+)*)@(\\p{Alnum}+((\\.|_+|-+)\\p{Alnum}+)*)$");

	private Operators() {
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
	 * <li>HORIZONTAL TAB (0x09)
	 * <li>LINE FEED (0x0a)
	 * <li>VERTICAL TAB (0x0b)
	 * <li>FORM FEED (0x0c)
	 * <li>CARRIAGE RETURN (0x0d)
	 * <li>SPACE (0x20)<br>
	 * <br>
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
	public static boolean isNumeric(String str) {
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
	public static boolean isDecimal(String str) {
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
		return str != null && str
				.matches("(?s)[\\p{L}\\d]+([_-][\\p{L}\\d]+)*('[\\p{L}\\d]*)?|'[\\p{L}\\d]+$");
	}

	/**
	 * Determines whether a string is a valid email address according to the
	 * rules from
	 * <a href="https://www.rfc-editor.org/rfc/rfc3696#section-3">RFC-3696</a>.
	 * 
	 * @param addr
	 *            The email
	 * @return {@code true} if the email is valid.
	 */
	public static boolean isEmail(String addr) {
		if (addr != null) {
			Matcher mat = pEmail.matcher(addr);
			return mat.find() && mat.group(1).length() <= 64 && mat.group(4).length() <= 255;
		} else {
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

}
