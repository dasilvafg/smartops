package com.github.dasilvafg.smartops;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Helper class to perform sequential arithmetic calculations.
 * 
 * <p>
 * This class uses a {@link BigDecimal} for its numeric representation, and
 * delegates to that class most of its operations. Methods are designed to
 * accept three types of numeric values: {@link Number}, {@link String}, and
 * {@link SmartDecimal} itself. Scaling is never applied automatically during
 * arithmetic operations.
 * 
 * <p>
 * It is the caller's responsability to handle mathematical errors and special
 * cases like {@link Double#POSITIVE_INFINITY},
 * {@link Double#NEGATIVE_INFINITY}, and {@link Double#NaN}. Null arguments will
 * throw an exception.
 * 
 * <p>
 * This class is immutable and thread-safe: all methods operate on a new copy.
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
	 * retrieved from the resulting {@link BigDecimal}, and the rouding will by
	 * {@link RoundingMode#HALF_EVEN}.
	 * 
	 * <p>
	 * Unless the argument is a {@link BigDecimal}, this method will use the
	 * {@link BigDecimal#BigDecimal(String)} constructor.
	 * 
	 * @param value
	 *            The numeric value.
	 * @return The {@link SmartDecimal} wrapper.
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
			throw new IllegalArgumentException("illegal argument: " + value);
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
	 * Clones this instance and sets new configuration for scale and rounding,
	 * applying it immediatly.
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
	 * Clones this instance and sets new configuration for scale, applying it
	 * immediatly.
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
	 * Clones this instance and sets new configuration for rounding, applying it
	 * immediatly.
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
	 * Clones this instance and applies a previously set configuration for scale
	 * and rounding to the clone.
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
	 * Clones this instance and adds the addend to the clone.
	 * 
	 * <p>
	 * This method delegates to {@link BigDecimal#add(BigDecimal)}.
	 * 
	 * @param addend
	 *            The number to add.
	 * @return The new instance.
	 */
	public SmartDecimal add(Object addend) {
		SmartDecimal copy = clone();
		copy.mNumber = copy.mNumber.add(new SmartDecimal(addend).mNumber);
		return copy;
	}

	/**
	 * Clones this instance and subtracts the subtrahend from the clone.
	 * 
	 * <p>
	 * This method delegates to {@link BigDecimal#subtract(BigDecimal)}.
	 * 
	 * @param subtrahend
	 *            The number to subtract.
	 * @return The new instance.
	 */
	public SmartDecimal subtract(Object subtrahend) {
		SmartDecimal copy = clone();
		copy.mNumber = copy.mNumber.subtract(new SmartDecimal(subtrahend).mNumber);
		return copy;
	}

	/**
	 * Clones this instance and multiplies the clone by the multiplier.
	 * 
	 * <p>
	 * This method delegates to {@link BigDecimal#multiply(BigDecimal)}.
	 * 
	 * @param multiplier
	 *            The number to multiply.
	 * @return The new instance.
	 */
	public SmartDecimal multiply(Object multiplier) {
		SmartDecimal copy = clone();
		copy.mNumber = copy.mNumber.multiply(new SmartDecimal(multiplier).mNumber);
		return copy;
	}

	/**
	 * Clones this instance and divides the clone by the divisor, applying the
	 * previously set configuration for scale and rounding.
	 * 
	 * <p>
	 * This method delegates to
	 * {@link BigDecimal#divide(BigDecimal, int, RoundingMode)}.
	 * 
	 * @param divisor
	 *            The number to divide.
	 * @return The new instance.
	 */
	public SmartDecimal divide(Object divisor) {
		SmartDecimal copy = clone();
		copy.mNumber = copy.mNumber.divide(new SmartDecimal(divisor).mNumber, mScale, mRounding);
		return copy;
	}

	/**
	 * Clones this instance and sets the clone to its absolute value.
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
	 * Raises this instance to an arbitrary exponent.
	 * 
	 * <p>
	 * The calculation is done using the following algorithm:
	 * 
	 * <ol>
	 * <li>If the exponent is a {@link Byte}, {@link Short}, or {@link Integer},
	 * this method delegates to {@link BigDecimal#pow(int)};
	 * 
	 * <li>Otherwise this method calculates the power using logarithms, allowing
	 * the exponent to be a floting point value. In such case, this method uses
	 * {@link Math#log(double)} and {@link Math#exp(double)}.
	 * </ol>
	 * 
	 * @param exponent
	 *            The number to which this number will be raised.
	 * @return The new instance.
	 */
	public SmartDecimal power(Object exponent) {
		if (exponent instanceof Byte || exponent instanceof Short || exponent instanceof Integer) {
			// Use BigDecimal.pow()
			return new SmartDecimal(mNumber.pow(((Number) exponent).intValue()));
		} else {
			// Use logarithms
			SmartDecimal product = new SmartDecimal(exponent)
					.multiply(Math.log(mNumber.doubleValue()));
			return new SmartDecimal(Math.exp(product.mNumber.doubleValue()));
		}
	}

	/**
	 * Extracts an arbitrary root from this instance using logarithms, allowing
	 * the degree to be a floating point value. This method uses
	 * {@link Math#log(double)} and {@link Math#exp(double)}.
	 * 
	 * @param degree
	 *            The degree of the root.
	 * @return The new instance.
	 */
	public SmartDecimal root(Object degree) {
		double division = Math.log(mNumber.doubleValue())
				/ new SmartDecimal(degree).mNumber.doubleValue();
		return new SmartDecimal(Math.exp(division));
	}

	/**
	 * Clones this instance and multiplies the clone by a percentage rate,
	 * applying the previously set configuration for scale and rounding.
	 * 
	 * @param rate
	 *            The percentage rate.
	 * @return The new instance.
	 */
	public SmartDecimal timesPercent(Object rate) {
		return multiply(rate).divide(HUNDRED);
	}

	/**
	 * Clones this instance and adds a percentage rate to the clone, applying
	 * the previously set configuration for scale and rounding.
	 * 
	 * @param rate
	 *            The percentage rate.
	 * @return The new instance.
	 */
	public SmartDecimal plusPercent(Object rate) {
		return add(timesPercent(rate));
	}

	/**
	 * Clones this instance and subtracts a percentage rate from the clone,
	 * applying the previously set configuration for scale and rounding.
	 * 
	 * @param rate
	 *            The percentage rate.
	 * @return The new instance.
	 */
	public SmartDecimal minusPercent(Object rate) {
		return subtract(timesPercent(rate));
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
	public short toByte() {
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
		return mNumber.compareTo(obj.mNumber);
	}

}
