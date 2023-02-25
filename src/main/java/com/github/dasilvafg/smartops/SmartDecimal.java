package com.github.dasilvafg.smartops;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Utility class to perform sequential arithmetical calculations.
 * 
 * <p>
 * This class uses a {@link BigDecimal} for its numeric representation, and
 * delegates to that class most of its operations. Methods are designed to
 * accept three types of numeric values: {@link Number}, {@link String}, and
 * {@link SmartDecimal} itself.
 * 
 * <p>
 * When passing a {@link String} argument, the expression must be acceptable to
 * {@link BigDecimal#BigDecimal(String)}. This validation can be performed by
 * {@link Commons#isDecimal(String)}.
 * 
 * <p>
 * Scaling is not applied automatically during arithmetic operations, except in
 * those involving division, which delegates to
 * {@link BigDecimal#divide(BigDecimal, int, RoundingMode)}. Separate methods
 * are provided to set precision, rounding mode, or both.
 * 
 * <p>
 * It is the caller's responsability to handle mathematical errors and special
 * cases like {@link Double#POSITIVE_INFINITY},
 * {@link Double#NEGATIVE_INFINITY}, and {@link Double#NaN}. Null arguments will
 * throw an exception.
 * 
 * <p>
 * This class is immutable and thread-safe: all write methods operate on a new
 * copy.
 * 
 * @author Fábio Silva
 * @since 1.0.0
 */
public final class SmartDecimal implements Serializable, Cloneable, Comparable<SmartDecimal> {

	private static final long serialVersionUID = 657227566273465759L;

	/**
	 * A {@link BigDecimal} representation of zero.
	 */
	public static final BigDecimal ZERO = new BigDecimal(0);

	/**
	 * A {@link BigDecimal} representation of one.
	 */
	public static final BigDecimal ONE = new BigDecimal(1);

	/**
	 * A {@link BigDecimal} representation of 10.
	 */
	public static final BigDecimal TEN = new BigDecimal(10);

	/**
	 * A {@link BigDecimal} representation of 100.
	 */
	public static final BigDecimal HUNDRED = new BigDecimal(100);

	/**
	 * A {@link BigDecimal} representation of 1000.
	 */
	public static final BigDecimal THOUSAND = new BigDecimal(1000);

	/**
	 * The internal numeric representation.
	 */
	private BigDecimal mNumber;

	/**
	 * The scale of the number.
	 */
	private int mScale;

	/**
	 * The rounding mode.
	 */
	private RoundingMode mRounding;

	/**
	 * Constructs a new {@link SmartDecimal} from a value that can be a
	 * {@link SmartDecimal}, a {@link Number}, or a numeric {@link String}.
	 * 
	 * <p>
	 * If the value is a {@link SmartDecimal}, its configuration for scale and
	 * rounding will be copied to the new instance. Otherwise the scale will be
	 * retrieved from the resulting {@link BigDecimal}, and the rouding will be
	 * {@link RoundingMode#HALF_EVEN}.
	 * 
	 * <p>
	 * Unless the argument is a {@link BigDecimal}, this method will use the
	 * {@link BigDecimal#BigDecimal(String)} constructor.
	 * 
	 * @param value
	 *            The numeric value.
	 * @return The {@link SmartDecimal} wrapper.
	 * @throws NumberFormatException
	 *             If the value cannot be converted to a {@link BigDecimal}.
	 */
	public static SmartDecimal of(Object value) {
		return new SmartDecimal(value);
	}

	private SmartDecimal(Object value) {
		if (value instanceof SmartDecimal) {
			mNumber = new BigDecimal(value.toString());
			mScale = ((SmartDecimal) value).mScale;
			mRounding = ((SmartDecimal) value).mRounding;
		} else if (value instanceof BigDecimal) {
			mNumber = (BigDecimal) value;
		} else if (value instanceof Number) {
			mNumber = new BigDecimal(value.toString());
		} else if (value instanceof String) {
			mNumber = new BigDecimal((String) value);
		} else {
			throw new NumberFormatException("invalid number: " + value);
		}
		if (mRounding == null) {
			mScale = mNumber.scale();
			mRounding = RoundingMode.HALF_EVEN;
		}
	}

	/**
	 * Clones this instance.
	 */
	@Override
	public SmartDecimal clone() {
		return new SmartDecimal(this);
	}

	/**
	 * Sets new configuration for scale and rounding, applying it immediatly.
	 * 
	 * @param newScale
	 *            The new scale.
	 * @param newRounding
	 *            The new rounding mode.
	 * @return The new instance.
	 */
	public SmartDecimal config(int newScale, RoundingMode newRounding) {
		if (newScale < 0) {
			throw new IllegalArgumentException("invalid scale: " + newScale);
		}
		if (newRounding == null) {
			throw new IllegalArgumentException("newRounding cannot be null");
		}
		SmartDecimal copy = clone();
		copy.mScale = newScale;
		copy.mRounding = newRounding;
		return copy.scale();
	}

	/**
	 * Sets new configuration for scale, applying it immediatly.
	 * 
	 * @param newScale
	 *            The new scale.
	 * @return The new instance.
	 */
	public SmartDecimal precison(int newScale) {
		if (newScale < 0) {
			throw new IllegalArgumentException("invalid scale: " + newScale);
		}
		SmartDecimal copy = clone();
		copy.mScale = newScale;
		return copy.scale();
	}

	/**
	 * Sets new configuration for rounding, applying it immediatly.
	 * 
	 * @param newRounding
	 *            The new rounding mode.
	 * @return The new instance.
	 */
	public SmartDecimal rounding(RoundingMode newRounding) {
		if (newRounding == null) {
			throw new IllegalArgumentException("newRounding cannot be null");
		}
		SmartDecimal copy = clone();
		copy.mRounding = newRounding;
		return copy.scale();
	}

	/**
	 * Applies a previously set configuration for scale and rounding.
	 * 
	 * <p>
	 * This method delegates to {@link BigDecimal#setScale(int, RoundingMode)}.
	 * 
	 * @return The new instance.
	 */
	public SmartDecimal scale() {
		SmartDecimal copy = clone();
		copy.mNumber = copy.mNumber.setScale(mScale, mRounding);
		return copy;
	}

	/**
	 * Adds the addend to this number.
	 * 
	 * <p>
	 * This method delegates to {@link BigDecimal#add(BigDecimal)}.
	 * 
	 * @param addend
	 *            The number to add.
	 * @return The new instance.
	 * @throws NumberFormatException
	 *             If the addend cannot be converted to a {@link BigDecimal}.
	 */
	public SmartDecimal add(Object addend) {
		SmartDecimal copy = clone();
		copy.mNumber = copy.mNumber.add(of(addend).mNumber);
		return copy;
	}

	/**
	 * Subtracts the subtrahend from this number.
	 * 
	 * <p>
	 * This method delegates to {@link BigDecimal#subtract(BigDecimal)}.
	 * 
	 * @param subtrahend
	 *            The number to subtract.
	 * @return The new instance.
	 * @throws NumberFormatException
	 *             If the subtrahend cannot be converted to a
	 *             {@link BigDecimal}.
	 */
	public SmartDecimal subtract(Object subtrahend) {
		SmartDecimal copy = clone();
		copy.mNumber = copy.mNumber.subtract(of(subtrahend).mNumber);
		return copy;
	}

	/**
	 * Multiplies this number by the multiplier.
	 * 
	 * <p>
	 * This method delegates to {@link BigDecimal#multiply(BigDecimal)}.
	 * 
	 * @param multiplier
	 *            The number to multiply.
	 * @return The new instance.
	 * @throws NumberFormatException
	 *             If the multiplier cannot be converted to a
	 *             {@link BigDecimal}.
	 */
	public SmartDecimal multiply(Object multiplier) {
		SmartDecimal copy = clone();
		copy.mNumber = copy.mNumber.multiply(of(multiplier).mNumber);
		return copy;
	}

	/**
	 * Divides this number by the divisor, applying the previously set
	 * configuration for scale and rounding.
	 * 
	 * <p>
	 * This method delegates to
	 * {@link BigDecimal#divide(BigDecimal, int, RoundingMode)}.
	 * 
	 * @param divisor
	 *            The number to divide.
	 * @return The new instance.
	 * @throws NumberFormatException
	 *             If the divisor cannot be converted to a {@link BigDecimal}.
	 */
	public SmartDecimal divide(Object divisor) {
		SmartDecimal copy = clone();
		copy.mNumber = copy.mNumber.divide(of(divisor).mNumber, mScale, mRounding);
		return copy;
	}

	/**
	 * Gets the absolute value of this number.
	 * 
	 * <p>
	 * This method delegates to {@link BigDecimal#abs()}.
	 * 
	 * @return The new instance.
	 */
	public SmartDecimal abs() {
		SmartDecimal copy = clone();
		copy.mNumber = copy.mNumber.abs();
		return copy;
	}

	/**
	 * Raises this number to an arbitrary exponent.
	 * 
	 * <p>
	 * The calculation is done using the following algorithm:
	 * 
	 * <ol>
	 * <li>If the exponent is a positive {@link Byte}, {@link Short}, or
	 * {@link Integer}, this method delegates to {@link BigDecimal#pow(int)};
	 * 
	 * <li>Otherwise this method calculates the power using logarithms, allowing
	 * the exponent to be a floting point value. In such case, this method uses
	 * {@link Math#log(double)} and {@link Math#exp(double)} to solve the
	 * equation:
	 * <blockquote><code>x<sup>y</sup> = e<sup>y.log(x)</sup></code></blockquote>
	 * </ol>
	 * 
	 * @param exponent
	 *            The number to which this number will be raised.
	 * @return The new instance.
	 * @throws NumberFormatException
	 *             If the exponent cannot be converted to a {@link BigDecimal}.
	 */
	public SmartDecimal power(Object exponent) {
		if ((exponent instanceof Byte || exponent instanceof Short || exponent instanceof Integer)
				&& ((Number) exponent).intValue() >= 0) {
			// Use BigDecimal.pow()
			return of(mNumber.pow(((Number) exponent).intValue()));
		} else {
			// Use logarithms
			SmartDecimal product = of(exponent).multiply(Math.log(mNumber.doubleValue()));
			return of(Math.exp(product.mNumber.doubleValue())).config(mScale, mRounding);
		}
	}

	/**
	 * Extracts an arbitrary root from this number using logarithms, allowing
	 * the index to be a floating point value.
	 * 
	 * <p>
	 * This method uses {@link Math#log(double)} and {@link Math#exp(double)} to
	 * solve the equation:
	 * <blockquote><code>root(x,y) = e<sup>log(x)/y</sup></code></blockquote>
	 * where x is this number and y is the index.
	 * </p>
	 * 
	 * @param index
	 *            The index of the root.
	 * @return The new instance.
	 * @throws NumberFormatException
	 *             If the index cannot be converted to a {@link BigDecimal}.
	 */
	public SmartDecimal root(Object index) {
		double division = Math.log(mNumber.doubleValue()) / of(index).mNumber.doubleValue();
		return of(Math.exp(division)).config(mScale, mRounding);
	}

	/**
	 * Multiplies this number by a percentage rate, applying the previously set
	 * configuration for scale and rounding.
	 * 
	 * @param rate
	 *            The percentage rate.
	 * @return The new instance.
	 * @throws NumberFormatException
	 *             If the rate cannot be converted to a {@link BigDecimal}.
	 */
	public SmartDecimal timesPercent(Object rate) {
		return multiply(rate).divide(HUNDRED);
	}

	/**
	 * Adds a percentage rate to this number, applying the previously set
	 * configuration for scale and rounding.
	 * 
	 * @param rate
	 *            The percentage rate.
	 * @return The new instance.
	 * @throws NumberFormatException
	 *             If the rate cannot be converted to a {@link BigDecimal}.
	 */
	public SmartDecimal plusPercent(Object rate) {
		return add(timesPercent(rate));
	}

	/**
	 * Subtracts a percentage rate from this number, applying the previously set
	 * configuration for scale and rounding.
	 * 
	 * @param rate
	 *            The percentage rate.
	 * @return The new instance.
	 * @throws NumberFormatException
	 *             If the rate cannot be converted to a {@link BigDecimal}.
	 */
	public SmartDecimal minusPercent(Object rate) {
		return subtract(timesPercent(rate));
	}

	/**
	 * Divides this number by the denominator and multiplies by 100.
	 * 
	 * @param denominator
	 *            The denominator.
	 * @return The percentage.
	 * @throws NumberFormatException
	 *             If the denominator cannot be converted to a
	 *             {@link BigDecimal}.
	 */
	public SmartDecimal percentageOf(Object denominator) {
		return divide(denominator).multiply(HUNDRED);
	}

	/**
	 * Retrieves the enclosed {@link BigDecimal}.
	 * 
	 * @return The enclosed {@link BigDecimal}.
	 */
	public BigDecimal toBigDecimal() {
		return mNumber;
	}

	/**
	 * Delegates to {@link BigDecimal#toBigInteger()}.
	 * 
	 * @return The {@link BigInteger} representation.
	 */
	public BigInteger toBigInteger() {
		return mNumber.toBigInteger();
	}

	/**
	 * Delegates to {@link BigDecimal#doubleValue()}.
	 * 
	 * @return The {@code double} representation.
	 */
	public double toDouble() {
		return mNumber.doubleValue();
	}

	/**
	 * Delegates to {@link BigDecimal#floatValue()}.
	 * 
	 * @return The {@code float} representation.
	 */
	public float toFloat() {
		return mNumber.floatValue();
	}

	/**
	 * Delegates to {@link BigDecimal#longValue()}.
	 * 
	 * @return The {@code long} representation.
	 */
	public long toLong() {
		return mNumber.longValue();
	}

	/**
	 * Delegates to {@link BigDecimal#intValue()}.
	 * 
	 * @return The {@code int} representation.
	 */
	public int toInt() {
		return mNumber.intValue();
	}

	/**
	 * Delegates to {@link BigDecimal#shortValue()}.
	 * 
	 * @return The {@code short} representation.
	 */
	public short toShort() {
		return mNumber.shortValue();
	}

	/**
	 * Delegates to {@link BigDecimal#byteValue()}.
	 * 
	 * @return The {@code byte} representation.
	 */
	public byte toByte() {
		return mNumber.byteValue();
	}

	/**
	 * Delegates to {@link BigDecimal#toString()}.
	 * 
	 * @return The {@link String} representation.
	 */
	@Override
	public String toString() {
		return mNumber.toString();
	}

	/**
	 * Determines the equality of this object with the given reference.
	 * 
	 * <p>
	 * If the object is an instance of this class, delegates to
	 * {@link BigDecimal#equals(Object)}. Otherwise returns {@code false}.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof SmartDecimal) {
			return mNumber.equals(((SmartDecimal) obj).mNumber);
		}
		return false;
	}

	/**
	 * Delegates to {@link BigDecimal#hashCode()}.
	 */
	@Override
	public int hashCode() {
		return mNumber.hashCode();
	}

	/**
	 * Compares this object to another {@link SmartDecimal}.
	 * 
	 * <p>
	 * If the object is non-null, delegates to
	 * {@link BigDecimal#compareTo(BigDecimal)}. Otherwise returns -1.
	 */
	@Override
	public int compareTo(SmartDecimal obj) {
		if (obj != null) {
			return mNumber.compareTo(obj.mNumber);
		} else {
			return -1;
		}
	}

}
