package com.github.dasilvafg.smartops.test;

import static org.junit.Assert.assertEquals;

import java.math.RoundingMode;

import org.junit.Test;

import com.github.dasilvafg.smartops.SmartDecimal;

public class SmartDecimalTest {

	@Test
	public void testRounding() {
		assertEquals(SmartDecimal.of(12.3456).config(2, RoundingMode.UP).toString(), "12.35");
		assertEquals(SmartDecimal.of(-12.3456).config(2, RoundingMode.UP).toString(), "-12.35");
		assertEquals(SmartDecimal.of(12.3456).config(2, RoundingMode.DOWN).toString(), "12.34");
		assertEquals(SmartDecimal.of(-12.3456).config(2, RoundingMode.DOWN).toString(), "-12.34");
		assertEquals(SmartDecimal.of(12.3456).config(2, RoundingMode.CEILING).toString(), "12.35");
		assertEquals(SmartDecimal.of(-12.3456).config(2, RoundingMode.CEILING).toString(),
				"-12.34");
		assertEquals(SmartDecimal.of(12.3456).config(2, RoundingMode.FLOOR).toString(), "12.34");
		assertEquals(SmartDecimal.of(-12.3456).config(2, RoundingMode.FLOOR).toString(), "-12.35");
		assertEquals(SmartDecimal.of(256).root(4).precison(0).toString(), "4");
		assertEquals(SmartDecimal.of(4).power(4).precison(0).toString(), "256");
	}

}
