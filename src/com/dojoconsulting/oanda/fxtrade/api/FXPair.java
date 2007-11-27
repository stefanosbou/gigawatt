package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.core.GigawattException;
import com.dojoconsulting.gigawatt.data.IProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * An FXPair object represents a pair of ISO currency symbols.
 */
public class FXPair implements Cloneable, IProduct {
	private String base;
	private String quote;
	private String pair;

	private static final List<String> majors = new ArrayList<String>();

	static {
		majors.add("AUD");
		majors.add("CAD");
		majors.add("CHF");
		majors.add("EUR");
		majors.add("GBP");
		majors.add("JPY");
		majors.add("USD");
		majors.add("NZD");
		majors.add("NOK");
		majors.add("SEK");
		majors.add("DKK");
		majors.add("XAG");
		majors.add("XAU");
	}

	public FXPair(final String base, final String quote) {
		this.base = base;
		this.quote = quote;
		this.pair = base + "/" + quote;
		//TODO:  Throw exception if it is an invalid FXPair
	}

	public FXPair(final String pair) {
		setPair(pair);
	}

	public String toString() {
		return pair;
		//TODO proper: Implement toString() - is this correct implementation?
	}

	public int compareTo(final FXPair fxpair) {
		return this.pair.compareTo(fxpair.pair);
	}

	public boolean equals(final Object x) {
		return x instanceof FXPair && x.toString().equals(this.toString());
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public String getBase() {
		return base;
	}

	public String getQuote() {
		return quote;
	}

	public String getPair() {
		return pair;
	}

	public FXPair getInverse() {
		return new FXPair(quote, base);
	}

	public void setBase(final String base) {
		setPair(base + "/" + quote);
	}

	public void setQuote(final String quote) {
		setPair(base + "/" + quote);
	}

	public void setPair(final String pair) {
		if (pair.length() != 7) {
			throw new FXPairException("Invalid currency: " + pair);
		}
		this.base = pair.substring(0, 3);
		this.quote = pair.substring(4, 7);
		this.pair = pair;
	}

	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new GigawattException("CloneNotSupported for FXPair", e);
		}
	}

	boolean isMajor() {
		return majors.contains(base) && majors.contains(quote);
	}
}
