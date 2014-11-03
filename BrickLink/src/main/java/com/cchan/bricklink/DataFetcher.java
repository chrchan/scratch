package com.cchan.bricklink;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.json.simple.JSONValue;

import com.bricklink.api.example.BLAuthSigner;

public class DataFetcher implements Callable<PriceData> {

	private static int BUFFER_SIZE = 2048;

	private static final String BL_CONSUMER_KEY = "1C51B3357FF24A1FA6A96E81ED6AB55E";

	private static final String BL_CONSUMER_SECRET = "627987A1A4D145F9B20D5C9877E585A5";

	private static final String BL_TOKEN_VALUE = "AE0E20C357994249A7ED6EB574C480E6";

	private static final String BL_TOKEN_SECRET = "77FAFBFED500459BA43A5E2F2CF3BD47";

	// private static final String PRICE_GUIDE_URL =
	// "http://www.bricklink.com/catalogPG.asp?S=%s";
	private static final String GET_ITEM_URL = "https://api.bricklink.com/api/store/v1/items/SET/%s";
	private static final String PRICE_GUIDE_URL = "https://api.bricklink.com/api/store/v1/items/SET/%s/price?new_or_used=N&guide_type=sold";
	private static final String PRICE_GUIDE_OAUTH_URL = "https://api.bricklink.com/api/store/v1/items/SET/%s/price";

	private static final int MAX_ATTEMPTS = 5;

	private String id;

	private CountDownLatch countdownLatch;

	public DataFetcher(String id, CountDownLatch countdownLatch) {
		this.id = id;
		this.countdownLatch = countdownLatch;
	}

	public PriceData call() {
		String itemName = fetchItemName();
		Map<String, Object> pricing = fetchPricing();
		String avgPrice = (String) pricing.get("qty_avg_price");
		long totalQty = (long) pricing.get("total_quantity");
		String[] idComponents = id.replace("col",  "").split("-");
		int series = Integer.parseInt(idComponents[0]);
		int figNumber = Integer.parseInt(idComponents[1]);
		int perCase = MinifigPriceScraper.PER_CASE[series-1][figNumber-1];
		PriceData priceData = new PriceData(this.id, avgPrice, totalQty, itemName.replaceAll(" - Complete Set",  ""), 12, perCase);
		countdownLatch.countDown();
		return priceData;
	}

	private String fetchItemName() {
		int attempts = 1;
		String itemName = null;
		while(itemName == null && attempts < MAX_ATTEMPTS) {
			itemName = fetchItemNameHelper();
			attempts++;
		}
		return itemName;
	}

	private Map<String, Object> fetchPricing() {
		int attempts = 1;
		Map<String, Object> pricing = null;
		while(pricing == null && attempts < MAX_ATTEMPTS) {
			pricing = fetchPricingHelper();
			attempts++;
		}
		return pricing;
	}

	private Map<String, Object> fetchPricingHelper() {
		URL url = null;
		InputStream is = null;
		StringBuilder builder = new StringBuilder();
		URLConnection urlConnection = null;
		String priceData = null;
		int bytesRead = 0;

		try {
			url = new URL(String.format(PRICE_GUIDE_URL, id));
			urlConnection = url.openConnection();
			BLAuthSigner authSigner = new BLAuthSigner(BL_CONSUMER_KEY, BL_CONSUMER_SECRET);
			authSigner.setToken(BL_TOKEN_VALUE, BL_TOKEN_SECRET);
			authSigner.setURL(String.format(PRICE_GUIDE_OAUTH_URL, id));
			authSigner.setVerb("GET");
			authSigner.addParameter("new_or_used", "N");
			authSigner.addParameter("guide_type", "sold");
			String authValue = authSigner.getAuthorizationHeaderValue();
			urlConnection.setRequestProperty("Authorization", authValue);
			is = urlConnection.getInputStream();
			byte[] buf = new byte[BUFFER_SIZE];
			do {
				bytesRead = is.read(buf, 0, BUFFER_SIZE);
				if (bytesRead != -1) {
					builder.append(new String(buf, 0, bytesRead));
				}
			} while (bytesRead != -1);
			priceData = builder.toString();
		} catch (Exception e) {
			// e.printStackTrace();
			return null; // figure out what to do here - return a null
							// PriceData?
		} finally {
			try {
				is.close();
			} catch (IOException ioe) {
				// ioe.printStackTrace();
			}
			countdownLatch.countDown();
		}
		Map<String, Object> res = (Map<String, Object>) JSONValue.parse(priceData);
		Map<String, Object> data = (Map<String, Object>) res.get("data");
		return data;
	}

	private String fetchItemNameHelper() {
		URL url = null;
		InputStream is = null;
		StringBuilder builder = new StringBuilder();
		URLConnection urlConnection = null;
		String priceData = null;
		int bytesRead = 0;

		try {
			url = new URL(String.format(GET_ITEM_URL, id));
			urlConnection = url.openConnection();
			BLAuthSigner authSigner = new BLAuthSigner(BL_CONSUMER_KEY, BL_CONSUMER_SECRET);
			authSigner.setToken(BL_TOKEN_VALUE, BL_TOKEN_SECRET);
			authSigner.setURL(url.toString());
			authSigner.setVerb("GET");;
			String authValue = authSigner.getAuthorizationHeaderValue();
			urlConnection.setRequestProperty("Authorization", authValue);
			is = urlConnection.getInputStream();
			byte[] buf = new byte[BUFFER_SIZE];
			do {
				bytesRead = is.read(buf, 0, BUFFER_SIZE);
				if (bytesRead != -1) {
					builder.append(new String(buf, 0, bytesRead));
				}
			} while (bytesRead != -1);
			String itemData = builder.toString();
			@SuppressWarnings("unchecked")
			Map<String, Object> itemMap = (Map<String, Object>) JSONValue.parse(itemData);
			@SuppressWarnings("unchecked")
			Map<String, Object> itemMapData = (Map<String, Object>) itemMap.get("data");
			return (String) itemMapData.get("name");
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		} finally {
			try {
				is.close();
			} catch (IOException ioe) {
				// ioe.printStackTrace();
			}
		}
	}
}
