package com.github.dasilvafg.smartops;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Map;

/**
 * Wrapper class with utility functions for URL encoding, form encoding, and
 * HTTP connections.
 * 
 * @author Fábio Silva
 * @since 1.0.0
 */
public final class HttpUtil {

	private static final String CHARSET = "UTF-8";
	private static final String CRLF = "\r\n";
	private static final String HYPHEN = "--";

	private HttpUtil() {
	}

	/**
	 * Encodes a URL string as a UTF-8.
	 * 
	 * <p>
	 * This method delegates to {@link URLEncoder#encode(String, String)}.
	 * 
	 * @param obj
	 *            The source. Can be any object conversible {@link #toString()}.
	 * @return The UTF-8 URL encoded string.
	 */
	public static String encodeUrl(Object obj) {
		String str = obj != null ? obj.toString().trim() : "";
		try {
			return URLEncoder.encode(str, CHARSET);
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	/**
	 * Decodes a URL string from UTF-8.
	 * 
	 * <p>
	 * This method delegates to {@link URLDecoder#decode(String, String)}.
	 * 
	 * @param str
	 *            The UTF-8 URL string.
	 * @return The decoded string.
	 */
	public static String decodeUrl(String str) {
		if (str == null) {
			str = "";
		}
		try {
			return URLDecoder.decode(str, CHARSET).trim();
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	/**
	 * Encodes an object in Base64.
	 * 
	 * <p>
	 * This method delegates to {@link Encoder#encodeToString(byte[])}.
	 * 
	 * @param obj
	 *            The objec to encode. If the object is not of type
	 *            {@code bte[]}, it will be converted with
	 *            {@link Object#toString()} and {@link String#getBytes()}.
	 * @return The encoded Base64 string.
	 */
	public static String encodeBase64(Object obj) {
		if (obj instanceof byte[]) {
			return Base64.getEncoder().encodeToString((byte[]) obj);
		} else {
			return Base64.getEncoder().encodeToString(obj.toString().getBytes());
		}
	}

	/**
	 * Decodes a Base64 string.
	 * 
	 * <p>
	 * This method delegates to {@link Decoder#decode(String)}, with the
	 * convinience of returning a string instead of a byte array.
	 * 
	 * @param str
	 *            The Base64 string to decode.
	 * @return The decoded string.
	 */
	public static String decodeBase64(String str) {
		byte[] buffer = Base64.getDecoder().decode(str);
		return new String(buffer);
	}

	/**
	 * Encodes a map as a UTF-8 URL query string.
	 * 
	 * @param args
	 *            The map to encode.
	 * @return The UTF-8 encoded query string.
	 */
	public static String mapToFormUrl(Map<String, Object> args) {
		StringBuilder str = new StringBuilder();
		for (String field : args.keySet()) {
			if (str.length() > 0) {
				str.append("&");
			}
			str.append(encodeUrl(field)).append("=").append(encodeUrl(args.get(field)));
		}
		return str.toString();
	}

	/**
	 * Encodes an array as a UTF-8 URL query string.
	 * 
	 * @param args
	 *            The array to encode. Even positions are keys, odds are values.
	 * @return The UTF-8 encoded query string.
	 */
	public static String arrayToFormUrl(Object... args) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < args.length - 1; i += 2) {
			if (str.length() > 0) {
				str.append("&");
			}
			str.append(encodeUrl(args[i])).append("=").append(encodeUrl(args[i + 1]));
		}
		return str.toString();
	}

	/**
	 * Encodes a map as a UTF-8 form-data string.
	 * 
	 * @param args
	 *            The map to encode.
	 * @return An array with two positions: The {@code Content-Type} header and
	 *         the encoded body.
	 */
	public static String[] mapToFormData(Map<String, Object> args) {
		String boundary = "Http_" + System.currentTimeMillis();
		StringBuilder body = new StringBuilder();
		for (String field : args.keySet()) {
			String value = args.get(field) != null ? args.get(field).toString().trim() : "";
			String header1 = "Content-Disposition: application/form-data; name=\"" + field + "\"";
			String header2 = "Content-Type: text/plain; charset=" + CHARSET;
			body.append(HYPHEN)
					.append(boundary)
					.append(CRLF)
					.append(header1)
					.append(CRLF)
					.append(header2)
					.append(CRLF)
					.append(CRLF)
					.append(value)
					.append(CRLF);
		}
		body.append(HYPHEN).append(boundary).append(HYPHEN).append(CRLF);
		return new String[] {
				"application/form-data; boundary=" + boundary, body.toString()
		};
	}

	/**
	 * Create and HTTP connection to the given URL.
	 * 
	 * @param obj
	 *            The URL to connect to. Can be an instance of {@link URL} or a
	 *            string accaptable to {@link URL#URL(String)}.
	 * @return The HTTP connection.
	 * @throws IOException
	 *             If the connection fails.
	 */
	public static HttpURLConnection connect(Object obj) throws IOException {
		URL url = (obj instanceof URL) ? (URL) obj : new URL(obj.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(10000);
		conn.setRequestProperty("Connection", "close");
		conn.setRequestProperty("User-Agent", "Java");
		return conn;
	}

	/**
	 * Reads a text response from an HTTP connection.
	 * 
	 * <p>
	 * This method proceeds as follows:
	 * <ul>
	 * <li>If the response code is less than 300, the
	 * {@link HttpURLConnection#getInputStream() input stream} will be read;
	 * <li>Otherwise the {@link HttpURLConnection#getErrorStream() error stream}
	 * will be read.
	 * <li>In both cases, this method will read at most {@code maxlen} bytes and
	 * throw an {@link IOException} if that limit is exceeded.
	 * </ul>
	 * 
	 * @param conn
	 *            The connection to read from.
	 * @param maxlen
	 *            The maximum length to read.
	 * @return The string read, or empty string if the stream is null.
	 * @throws IOException
	 *             If the stream cannot be read, or the maximum length is
	 *             exceeded.
	 */
	public static String readTextResponse(HttpURLConnection conn, int maxlen) throws IOException {
		InputStream in = conn.getResponseCode() < 300 ? conn.getInputStream()
				: conn.getErrorStream();
		if (in != null) {
			byte[] buffer = new byte[maxlen];
			int n = in.read(buffer);
			if (n < maxlen || in.read() < 0) {
				return new String(buffer, 0, n);
			} else {
				throw new IOException("Buffer overflow");
			}
		} else {
			return "";
		}
	}

	/**
	 * Reads a binary response from an HTTP connection.
	 * 
	 * <p>
	 * This method is identical to
	 * {@link #readTextResponse(HttpURLConnection, int)} except that it returns
	 * a byte array instead of a string.
	 * 
	 * @param conn
	 *            The connection to read from.
	 * @param maxlen
	 *            The maximum length to read.
	 * @return The byte array, or empty array if the stream is null.
	 * @throws IOException
	 *             If the stream cannot be read, or the maximum length is
	 *             exceeded.
	 */
	public static byte[] readBynaryResponse(HttpURLConnection conn, int maxlen) throws IOException {
		InputStream in = conn.getResponseCode() < 300 ? conn.getInputStream()
				: conn.getErrorStream();
		if (in != null) {
			byte[] buffer = new byte[maxlen];
			int n = in.read(buffer);
			if (n < maxlen || in.read() < 0) {
				return Arrays.copyOf(buffer, n);
			} else {
				throw new IOException("Buffer overflow");
			}
		} else {
			return new byte[0];
		}
	}

}
