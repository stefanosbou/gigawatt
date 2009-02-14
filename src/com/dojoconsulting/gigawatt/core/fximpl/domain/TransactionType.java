package com.dojoconsulting.gigawatt.core.fximpl.domain;

import com.dojoconsulting.oanda.fxtrade.api.Transaction;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 22-Dec-2007
 * Time: 04:28:50
 */

public enum TransactionType {
	STOP_LOSS(Transaction.FX_SL, "StopLoss"),
	TAKE_PROFIT(Transaction.FX_TP, "TakeProfit"),
	MARGIN_CALL(Transaction.FX_MARGIN, "MarginCall"),
	ORDER_FILL(Transaction.FX_XFR_ORDER),
	ORDER_BSV(Transaction.FX_ORDERCANCELBOUNDSVIOLATION),
	ORDER_NSF(Transaction.FX_NSF),
	ORDER_EXPIRY(Transaction.FX_DURATION),
	USER(Transaction.FX_USER, "CloseTrade"),
	USER_POSITION(Transaction.FX_USER, "ClosePosition");

	private int completionCode;
	private String closeType;

	TransactionType(final int completionCode) {
		this.completionCode = completionCode;
		this.closeType = null;
	}

	TransactionType(final int completionCode, final String closeType) {
		this.completionCode = completionCode;
		this.closeType = closeType;
	}

	public int getCompletionCode() {
		return completionCode;
	}

	public String getCloseType() {
		return closeType;
	}
}
