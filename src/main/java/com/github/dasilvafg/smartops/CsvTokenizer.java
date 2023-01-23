package com.github.dasilvafg.smartops;

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

	@Override
	public boolean hasMoreElements() {
		return idx < list.size();
	}

	@Override
	public String nextElement() {
		if (idx < list.size()) {
			return list.get(idx++);
		} else {
			throw new NoSuchElementException("index: " + idx);
		}
	}

}
