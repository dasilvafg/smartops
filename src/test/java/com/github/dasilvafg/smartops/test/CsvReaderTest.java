package com.github.dasilvafg.smartops.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import com.github.dasilvafg.smartops.CsvReader;
import com.github.dasilvafg.smartops.CsvTokenizer;

public class CsvReaderTest {

	@Test
	public void testReadCsv() throws IOException {
		StringBuilder str = new StringBuilder();
		str.append("\"WORLD_CUP_EDITION\";\"CHAMPION\"\n");
		str.append("1958;\"Brazil\"\n");
		str.append("1962;\"Brazil\"\n");
		str.append("1966;\"England\"\n");
		str.append("1970;\"Brazil\"");

		CsvTokenizer token;
		CsvReader reader = new CsvReader(new StringReader(str.toString()), ";");

		assertNotNull(token = reader.readCsv());
		assertEquals("WORLD_CUP_EDITION", token.nextElement());
		assertEquals("CHAMPION", token.nextElement());
		assertFalse(token.hasMoreElements());

		assertNotNull(token = reader.readCsv());
		assertEquals("1958", token.nextElement());
		assertEquals("Brazil", token.nextElement());
		assertFalse(token.hasMoreElements());

		assertNotNull(token = reader.readCsv());
		assertEquals("1962", token.nextElement());
		assertEquals("Brazil", token.nextElement());
		assertFalse(token.hasMoreElements());

		assertNotNull(token = reader.readCsv());
		assertEquals("1966", token.nextElement());
		assertEquals("England", token.nextElement());
		assertFalse(token.hasMoreElements());

		assertNotNull(token = reader.readCsv());
		assertEquals("1970", token.nextElement());
		assertEquals("Brazil", token.nextElement());
		assertFalse(token.hasMoreElements());

		assertNull(token = reader.readCsv());
	}

}
