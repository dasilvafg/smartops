package com.github.dasilvafg.smartops;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * This class reads CSV lines using the {@link CsvTokenizer}.
 * 
 * @author Fábio Silva
 * @since 1.0.0
 */
public class CsvReader extends BufferedReader {

	private final String delims;

	/**
	 * Constructs a new {@link CsvReader} from another reader with a set of
	 * delimiters.
	 * 
	 * @param reader
	 *            The input reader.
	 * @param delims
	 *            The characters to be accepted as field delimiters.
	 */
	public CsvReader(Reader reader, String delims) {
		super(reader);
		if (delims == null || delims.isEmpty()) {
			throw new IllegalArgumentException("delims cannot be empty");
		}
		this.delims = delims;
	}

	/**
	 * Reads a CSV line.
	 * 
	 * @return A {@link CsvTokenizer} that parses the fields, or {@code null} if
	 *         {@code EOF} is reached.
	 * @throws IOException
	 *             If reading fails.
	 */
	public CsvTokenizer readCsv() throws IOException {
		String line = super.readLine();
		return (line != null) ? new CsvTokenizer(line, delims) : null;
	}

}
