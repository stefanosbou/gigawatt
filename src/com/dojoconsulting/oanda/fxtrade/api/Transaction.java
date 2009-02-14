package com.dojoconsulting.oanda.fxtrade.api;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 17-Oct-2007
 * Time: 02:50:52
 * The Transaction object contains all information related to transactions occuring on OANDA's servers.
 */
public final class Transaction implements Cloneable {

	public static final int FX_USER = 100;
	public static final int FX_DURATION = 101;
	public static final int FX_SL = 102;
	public static final int FX_TP = 103;
	public static final int FX_MARGIN = 104;
	public static final int FX_XFR_ORDER = 105;
	public static final int FX_ROLLOVER = 106;
	public static final int FX_INTEREST = 107;
	public static final int FX_NSF = 108;
	public static final int FX_DELFUNDS = 109;
	public static final int FX_CSBALACECORRECTION = 110;
	public static final int FX_CSINTERESTCORRECTION = 111;
	public static final int FX_CSINTERESTTODAYCORRECTION = 112;
	public static final int FX_CSPLCORRECTION = 113;
	public static final int FX_CSTRADECORRECTION = 114;
	public static final int FX_CSCLOSEWITHOUTPENALTY = 115;
	public static final int FX_INTDEFERRED = 116;
	public static final int FX_CSTRADECANCEL = 117;
	public static final int FX_ORDERCANCELBOUNDSVIOLATION = 118;

	private static String[] transactionTypes = new String[]{
			"User",							 //100
			"Duration",						 //101
			"Stop Loss",						//102
			"Take Profit",					  //103
			"Margin Call",					  //104
			"Xfr Order",						//105
			"Interest",						 //106
			"Interest ",						//107
			"NSF",							  //108
			"DelFunds",						 //109
			"Balance Correction",			   //110
			"Interest Correction",			  //111
			"Interest Today Correction",		//112
			"PL Correction",					//113
			"Trade Correction",				 //114
			"Close Without Penalty",			//115
			"Interest Deferred",				//116
			"Trade Cancel",					 //117
			"Order Cancel Bounds Violation",	//118

	};

	private double amount;
	private double balance;
	private int completionCode;
	private int diaspora;
	private double interest;
	private double margin;
	private FXPair pair;
	private double price;
	private double stopLoss;
	private double takeProfit;
	private long timestamp;
	private int transactionLink;
	private int transactionNumber;
	private long units;
	private String type;
	private double lowerBound;
	private double upperBound;
	private long expiry;
	private double profitLoss;

	Transaction(final double amount, final double balance, final int completionCode, final int diaspora, final double interest, final double margin, final FXPair pair, final double price, final double stopLoss, final double takeProfit, final long timestamp, final int transactionLink, final int transactionNumber, final long units, final String type, final long expiry, final double lowerBound, final double upperBound, final double profitLoss) {
		this.amount = amount;
		this.balance = balance;
		this.completionCode = completionCode;
		this.diaspora = diaspora;
		this.interest = interest;
		this.margin = margin;
		this.pair = pair;
		this.price = price;
		this.stopLoss = stopLoss;
		this.takeProfit = takeProfit;
		this.timestamp = timestamp;
		this.transactionLink = transactionLink;
		this.transactionNumber = transactionNumber;
		this.units = units;
		this.type = type;
		this.expiry = expiry;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.profitLoss = profitLoss;
	}

	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public boolean equals(final Object o) {
		return o instanceof Transaction && super.equals(o);
		//TODO proper: Implement proper equals()
	}

	public String toString() {
		return super.toString();
		//TODO proper: Implement toString()
	}

	public double getAmount() {
		return amount;
	}

	public double getBalance() {
		return balance;
	}

	public int getCompletionCode() {
		return completionCode;
	}

	public int getDiaspora() {
		return diaspora;
	}

	public double getInterest() {
		return interest;
	}

	public double getMargin() {
		return margin;
	}

	public FXPair getPair() {
		return pair;
	}

	public double getPrice() {
		return price;
	}

	public double getStopLoss() {
		return stopLoss;
	}

	public double getTakeProfit() {
		return takeProfit;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getTransactionLink() {
		return transactionLink;
	}

	public long getUnits() {
		return units;
	}

	public int getTransactionNumber() {
		return transactionNumber;
	}

	public String getType() {
		return type;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public long getExpiry() {
		return expiry;
	}

	public double getProfitLoss() {
		return profitLoss;
	}
}
