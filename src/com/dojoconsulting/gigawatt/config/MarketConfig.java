package com.dojoconsulting.gigawatt.config;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 23-Oct-2007
 * Time: 03:06:19
 */
public class MarketConfig {
	private String product;
	private String classname;
	private String filename;
	private boolean keepHistory = false;


	public boolean requiresKeepHistory() {
		return keepHistory;
	}

	public void setKeepHistory(final boolean keepHistory) {
		this.keepHistory = keepHistory;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(final String product) {
		this.product = product;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(final String classname) {
		this.classname = classname;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}
}
