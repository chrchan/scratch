package com.cchan.bricklink;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MinifigPriceScraper {

	private static final int SERIES_SIZE = 12;

	private static final String DEFAULT_SERIES = "12";

	private static final int THREAD_POOL_SIZE = 12;

	private static final String ID_PATTERN = "col%s-%d";

	private String series;

	private ExecutorService executorService;

	private CountDownLatch countdownLatch;

	public static final int[][] PER_CASE = {
			{ 3, 3, 4, 3, 3, 3, 4, 5, 4, 3, 3, 5, 4, 4, 5, 4 },
			{ 4, 3, 5, 4, 4, 5, 3, 4, 4, 4, 3, 3, 3, 5, 3, 3 },
			{ 3, 3, 4, 4, 2, 3, 3, 5, 3, 3, 5, 5, 5, 4, 4, 4 },
			{ 3, 4, 3, 4, 3, 3, 4, 3, 3, 5, 5, 5, 4, 4, 3, 4 },
			{ 3, 3, 5, 3, 4, 4, 5, 4, 4, 4, 4, 3, 5, 3, 3, 3 },
			{ 4, 5, 3, 3, 5, 4, 3, 5, 4, 3, 3, 4, 4, 3, 4, 3 },
			{ 3, 3, 3, 3, 3, 4, 4, 5, 4, 5, 4, 4, 4, 5, 3, 3 },
			{ 5, 4, 3, 4, 4, 3, 3, 3, 3, 4, 4, 5, 4, 3, 5, 3 },
			{ 2, 6, 2, 6, 4, 6, 4, 2, 2, 4, 6, 4, 4, 4, 2, 2 },
			{ 2, 2, 6, 4, 4, 6, 2, 2, 4, 2, 4, 4, 6, 2, 4, 6 },
			{ 4, 2, 2, 4, 2, 4, 4, 6, 6, 6, 4, 2, 2, 2, 6, 4 },
			{ 4, 3, 3, 5, 4, 5, 3, 3, 3, 4, 4, 4, 5, 4, 3, 3 }
			};

	public List<PriceData> scrape() {
		return scrapeAsync();
	}

	private List<PriceData> scrapeSync() {
		countdownLatch = new CountDownLatch(SERIES_SIZE);
		List<PriceData> ret = new ArrayList<PriceData>(SERIES_SIZE);
		for (int i = 1; i <= SERIES_SIZE; i++) {
			String id = String.format(ID_PATTERN, this.series, i);
			DataFetcher fetcher = new DataFetcher(id, countdownLatch);
			ret.add(fetcher.call());
		}
		return ret;
	}

	private List<PriceData> scrapeAsync() {
		countdownLatch = new CountDownLatch(SERIES_SIZE);
		List<Future<PriceData>> results = new ArrayList<Future<PriceData>>(
				SERIES_SIZE);
		for (int i = 1; i <= SERIES_SIZE; i++) {
			String id = String.format(ID_PATTERN, this.series, i);
			DataFetcher fetcher = new DataFetcher(id, countdownLatch);
			Future<PriceData> result = this.executorService.submit(fetcher);
			results.add(result);
		}
		List<PriceData> ret = new ArrayList<PriceData>(SERIES_SIZE);
		try {
			countdownLatch.await();
			for (Future<PriceData> result : results) {
				ret.add(result.get());
			}
		} catch (Exception e) {
		}
		return ret;
	}

	public MinifigPriceScraper(String series) {
		this.series = series;
		this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	}

	public static void main(String[] argv) {
		String series = DEFAULT_SERIES;
		if (argv.length > 0) {
			series = argv[0];
		}
		MinifigPriceScraper scraper = new MinifigPriceScraper(series);
		List<PriceData> results = scraper.scrape();
		PriceData[] resultsAr = new PriceData[results.size()];
		resultsAr = (PriceData[]) results.toArray(resultsAr);
		Arrays.sort(resultsAr);
		for (PriceData priceData : resultsAr) {
			System.out.println(priceData);
		}
		System.exit(0);
	}

}