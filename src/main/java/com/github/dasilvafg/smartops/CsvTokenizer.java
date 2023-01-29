package com.github.dasilvafg.smartops;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class reads fields from a CSV line using the provided field separators.
 * 
 * @author Fábio Silva
 * @since 1.0.0
 */
public class CsvTokenizer implements Enumeration<String> {

	private final List<String> list;
	private int idx;

	/**
	 * Constructs the tokenizer from the CSV record with the given delimiters.
	 * 
	 * @param str
	 *            The CSV record.
	 * @param delims
	 *            The characters accepted as fied separators.
	 */
	public CsvTokenizer(String str, String delims) {
		delims = delims.replaceAll("\\$", "\\\\\\$");
		delims = delims.replaceAll("\\[", "\\\\\\[");
		delims = delims.replaceAll("\\]", "\\\\\\]");
		delims = "[" + delims + "]";
		String regExp = "(?:^|" + delims + ")\\s*(?:(?:(?=\")\"([^\"].*?)\")|(?:(?!\")(.*?)))(?="
				+ delims + "|$)";
		Matcher tokenizer = Pattern.compile(regExp).matcher(str);
		list = new ArrayList<>();
		while (tokenizer.find()) {
			String s = tokenizer.group(1);
			String t = tokenizer.group(2);
			list.add(s != null ? s : t);
		}
	}

	/**
	 * Determines whether this enumeration has another element to provide.
	 */
	@Override
	public boolean hasMoreElements() {
		return idx < list.size();
	}

	/**
	 * Retrieves the next element of this enumeration.
	 */
	@Override
	public String nextElement() {
		if (idx < list.size()) {
			return list.get(idx++);
		} else {
			throw new NoSuchElementException("index: " + idx);
		}
	}

	/**
	 * Retrieves the next element of this enumeration as an {@link Integer}.
	 * 
	 * @return The next element of this enumeration converted to
	 *         {@link Integer}.
	 * @throws NoSuchElementException
	 *             If this enumeration has no more elements.
	 * @throws NumberFormatException
	 *             If the element cannot be converted to {@link Integer}.
	 */
	public Integer nextInteger() {
		String val = nextElement();
		return val.isEmpty() ? null : Integer.valueOf(val);
	}

	/**
	 * Retrieves the next element of this enumeration as a {@link Long}.
	 * 
	 * @return The next element of this enumeration converted to {@link Long}.
	 * @throws NoSuchElementException
	 *             If this enumeration has no more elements.
	 * @throws NumberFormatException
	 *             If the element cannot be converted to {@link Long}.
	 */
	public Long nextLong() {
		String val = nextElement();
		return val.isEmpty() ? null : Long.valueOf(val);
	}

	/**
	 * Retrieves the next element of this enumeration as a {@link Float}.
	 * 
	 * @return The next element of this enumeration converted to {@link Float}.
	 * @throws NoSuchElementException
	 *             If this enumeration has no more elements.
	 * @throws NumberFormatException
	 *             If the element cannot be converted to {@link Float}.
	 */
	public Float nextFloat() {
		String val = nextElement();
		return val.isEmpty() ? null : Float.valueOf(val);
	}

	/**
	 * Retrieves the next element of this enumeration as a {@link Double}.
	 * 
	 * @return The next element of this enumeration converted to {@link Double}.
	 * @throws NoSuchElementException
	 *             If this enumeration has no more elements.
	 * @throws NumberFormatException
	 *             If the element cannot be converted to {@link Double}.
	 */
	public Double nextDouble() {
		String val = nextElement();
		return val.isEmpty() ? null : Double.valueOf(val);
	}

	/**
	 * Retrieves the next element of this enumeration as a {@link BigInteger}.
	 * 
	 * @return The next element of this enumeration converted to
	 *         {@link BigInteger}.
	 * @throws NoSuchElementException
	 *             If this enumeration has no more elements.
	 * @throws NumberFormatException
	 *             If the element cannot be converted to {@link BigInteger}.
	 */
	public BigInteger nextBigInteger() {
		String val = nextElement();
		return val.isEmpty() ? null : new BigInteger(val);
	}

	/**
	 * Retrieves the next element of this enumeration as a {@link BigDecimal}.
	 * 
	 * @return The next element of this enumeration converted to
	 *         {@link BigDecimal}.
	 * @throws NoSuchElementException
	 *             If this enumeration has no more elements.
	 * @throws NumberFormatException
	 *             If the element cannot be converted to {@link BigDecimal}.
	 */
	public BigDecimal nextBigDecimal() {
		String val = nextElement();
		return val.isEmpty() ? null : new BigDecimal(val);
	}

	/**
	 * Retrieves the next element of this enumeration as a {@link SmartDecimal}.
	 * 
	 * @return The next element of this enumeration converted to
	 *         {@link SmartDecimal}.
	 * @throws NoSuchElementException
	 *             If this enumeration has no more elements.
	 * @throws NumberFormatException
	 *             If the element cannot be converted to {@link SmartDecimal}.
	 */
	public SmartDecimal nextSmartDecimal() {
		String val = nextElement();
		return val.isEmpty() ? null : SmartDecimal.of(val);
	}

}
