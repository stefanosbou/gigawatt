package com.dojoconsulting.oanda.fxtrade.data;

import com.dojoconsulting.gigawatt.core.GigawattException;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 29-Nov-2007
 * Time: 00:58:11
 */
public class GigawattStreamInMemoryMarketData extends GenericFXMarketDataReader {
	private static Log logger = LogFactory.getLog(GigawattStreamInMemoryMarketData.class);
	private boolean moreData = true;

	public GigawattStreamInMemoryMarketData(final FXPair pair, final String path) {
		super(pair, path);
	}

	protected boolean hasMoreData() {
		return moreData;
	}

	protected void getNextRecord(final TickRecord tickRecord) {
		try {
			tickRecord.setTimeInMillis(in.getLong());
			tickRecord.setBid(in.getDouble());
			tickRecord.setAsk(in.getDouble());
			tickRecord.setEmpty(false);
		} catch (BufferUnderflowException e) {
			tickRecord.setEmpty(true);
			moreData = false;
		}
	}

	private MappedByteBuffer in;

	public void init() {
		long timestamp = 0;
		if (logger.isInfoEnabled()) {
			timestamp = System.currentTimeMillis();
			logger.info("GigawattStreamInMemoryMarketData: Started init()");
		}
		try {
			final long length = new File(filePath).length();
			in = new FileInputStream(filePath).getChannel().map(FileChannel.MapMode.READ_ONLY, 0, length).load();
		}
		catch (FileNotFoundException e) {
			throw new GigawattException("GenericFXMarketDataReader: Could not find the file (" + filePath + ") for " + pair, e);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (logger.isInfoEnabled()) {
			logger.info("GigawattStreamInMemoryMarketData: Finished init() in " + (System.currentTimeMillis() - timestamp));
		}
	}

}