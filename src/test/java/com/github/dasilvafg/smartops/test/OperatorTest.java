package com.github.dasilvafg.smartops.test;

import static com.github.dasilvafg.smartops.Operators.capitalize;
import static com.github.dasilvafg.smartops.Operators.coalesce;
import static com.github.dasilvafg.smartops.Operators.isAlpha;
import static com.github.dasilvafg.smartops.Operators.isBase64;
import static com.github.dasilvafg.smartops.Operators.isBlank;
import static com.github.dasilvafg.smartops.Operators.isDecimal;
import static com.github.dasilvafg.smartops.Operators.isEmail;
import static com.github.dasilvafg.smartops.Operators.isEmpty;
import static com.github.dasilvafg.smartops.Operators.isHexa;
import static com.github.dasilvafg.smartops.Operators.isNumeric;
import static com.github.dasilvafg.smartops.Operators.isWord;
import static com.github.dasilvafg.smartops.Operators.normalize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;

import com.github.dasilvafg.smartops.SmartDecimal;

public class OperatorTest {

	@Test
	public void testIsEmpty() throws Exception {
		assertTrue(isEmpty(null));

		assertTrue(isEmpty(new StringBuilder()));
		assertFalse(isEmpty(new StringBuilder("Test 123")));

		assertTrue(isEmpty('\u0000'));
		assertFalse(isEmpty('X'));

		assertTrue(isEmpty(Byte.valueOf("0")));
		assertFalse(isEmpty(Byte.valueOf("1")));
		assertFalse(isEmpty(Byte.valueOf("-1")));
		assertTrue(isEmpty(Short.valueOf("0")));
		assertFalse(isEmpty(Short.valueOf("1")));
		assertFalse(isEmpty(Short.valueOf("-1")));
		assertTrue(isEmpty(Integer.valueOf("0")));
		assertFalse(isEmpty(Integer.valueOf("1")));
		assertFalse(isEmpty(Integer.valueOf("-1")));
		assertTrue(isEmpty(Long.valueOf("0")));
		assertFalse(isEmpty(Long.valueOf("1")));
		assertFalse(isEmpty(Long.valueOf("-1")));
		assertTrue(isEmpty(Float.valueOf("0")));
		assertFalse(isEmpty(Float.valueOf("1")));
		assertFalse(isEmpty(Float.valueOf("-1")));
		assertTrue(isEmpty(Double.valueOf("0")));
		assertFalse(isEmpty(Double.valueOf("1")));
		assertFalse(isEmpty(Double.valueOf("-1")));
		assertTrue(isEmpty(BigInteger.valueOf(0)));
		assertFalse(isEmpty(BigInteger.valueOf(1)));
		assertFalse(isEmpty(BigInteger.valueOf(-1)));
		assertTrue(isEmpty(BigDecimal.valueOf(0)));
		assertFalse(isEmpty(BigDecimal.valueOf(1)));
		assertFalse(isEmpty(BigDecimal.valueOf(-1)));
		assertTrue(isEmpty(SmartDecimal.of(0)));
		assertFalse(isEmpty(SmartDecimal.of(1)));
		assertFalse(isEmpty(SmartDecimal.of(-1)));

		assertTrue(isEmpty(new ArrayList<>()));
		assertTrue(isEmpty(new HashMap<>()));
		assertTrue(isEmpty(new HashMap<>().keySet().iterator()));
		assertFalse(isEmpty(Arrays.asList(1, 2, 3)));
		HashMap<Integer, Integer> map = new HashMap<>();
		map.put(1, 2);
		assertFalse(isEmpty(map));
		assertFalse(isEmpty(map.keySet().iterator()));
		assertTrue(isEmpty(new StringTokenizer("")));
		assertFalse(isEmpty(new StringTokenizer("1 2 3")));

		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		doc.appendChild(doc.createElement("doc"));
		assertTrue(isEmpty(doc.getDocumentElement()));
		doc.getDocumentElement().appendChild(doc.createElement("test"));
		assertFalse(isEmpty(doc.getDocumentElement()));

		assertTrue(isEmpty(new int[] {}));
		assertFalse(isEmpty(new int[] {
				1, 2, 3
		}));
		assertFalse(isEmpty(getClass()));
	}

	@Test
	public void testIsBlank() {
		assertTrue(isBlank(null));
		assertTrue(isBlank(""));
		assertTrue(isBlank("   \r\n  \n \t\t "));
		assertTrue(isBlank(new StringBuilder("   \r\n  \n \t\t ")));
		assertFalse(isBlank("   \r\n  TEST \t\t "));
		assertFalse(isBlank(new StringBuilder("   \r\n TEST \n \t\t ")));
	}

	@Test
	public void testIsNumeric() {
		assertFalse(isNumeric(null));
		assertFalse(isNumeric(""));
		assertFalse(isNumeric("a111b"));
		assertTrue(isNumeric("234"));
		assertTrue(isNumeric("234.5"));
		assertTrue(isNumeric("234.5678"));
		assertTrue(isNumeric("234.567e89"));
		assertTrue(isNumeric("-23"));
		assertTrue(isNumeric("-23.45"));
		assertTrue(isNumeric("-23.45e18"));
	}

	@Test
	public void testIsDecimal() {
		assertFalse(isDecimal(null));
		assertFalse(isDecimal(""));
		assertFalse(isDecimal("111b"));
		assertTrue(isDecimal("234"));
		assertFalse(isDecimal("234.5"));
		assertFalse(isDecimal("234.5678"));
		assertFalse(isDecimal("234.567e89"));
		assertFalse(isDecimal("-23."));
		assertFalse(isDecimal("-23:45"));
	}

	@Test
	public void testIsHexa() {
		assertFalse(isHexa(null));
		assertFalse(isHexa(""));
		assertTrue(isHexa("111b"));
		assertTrue(isHexa("234"));
		assertTrue(isHexa("A23B45C67"));
		assertTrue(isHexa("a23b45c67"));
		assertFalse(isHexa("234.5"));
		assertFalse(isHexa("234F5G22"));
		assertFalse(isHexa("-23"));
	}

	@Test
	public void testIsAlpha() {
		assertFalse(isAlpha(null));
		assertFalse(isAlpha(""));
		assertTrue(isAlpha("test"));
		assertTrue(isAlpha("ÁaàäÿAeoñXç"));
		assertTrue(isAlpha("testöeüèxx"));
		assertFalse(isAlpha("AXX234"));
		assertFalse(isAlpha("234F5G22"));
		assertFalse(isAlpha("XXX-A"));
	}

	@Test
	public void testIsBase64() {
		assertFalse(isBase64(null));
		assertFalse(isBase64(""));
		assertTrue(isBase64("test"));
		assertTrue(isBase64("testabc/a="));
		assertTrue(isBase64("123testAax+a/"));
		assertFalse(isBase64("AXX234áa"));
		assertFalse(isBase64("234F%5G22"));
		assertFalse(isBase64("XXX-A"));
	}

	@Test
	public void testIsWord() {
		assertFalse(isWord(null));
		assertFalse(isWord(""));
		assertTrue(isWord("test"));
		assertFalse(isWord("testabca_"));
		assertTrue(isWord("123testAax_a"));
		assertTrue(isWord("AXX234áa"));
		assertFalse(isWord("234F%5G22"));
		assertTrue(isWord("XXX-A"));
		assertTrue(isWord("1990's"));
		assertTrue(isWord("'90s"));
		assertFalse(isWord("1990's'test"));
	}

	@Test
	public void testIsValidEmail() {
		assertFalse(isEmail(null));
		assertFalse(isEmail(""));
		assertFalse(isEmail("test"));
		assertTrue(isEmail("a@b"));
		assertFalse(isEmail("a@b."));
		assertTrue(isEmail("aa-bb@cc-dd.ee"));
		assertTrue(isEmail("aa_bb@cc_dd.ee"));
		assertFalse(isEmail("a+b@test"));
	}

	@Test
	public void testNormalize() {
		assertEquals(normalize("áéíóúàèìòùâêîôûäëïöüãẽĩõũñç"), "aeiouaeiouaeiouaeiouaeiounc");
		assertEquals(normalize("ÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÄËÏÖÜÃẼĨÕŨÑÇ"), "AEIOUAEIOUAEIOUAEIOUAEIOUNC");
	}

	@Test
	public void testCoalesce() {
		assertEquals(coalesce(" test \t  \n test  "), "test test");
	}

	@Test
	public void testCapitalize() {
		assertEquals(capitalize("THIS IS A NEW TEST\nAND THIS IS A NEW LINE"),
				"This Is A New Test\nAnd This Is A New Line");
	}

}
