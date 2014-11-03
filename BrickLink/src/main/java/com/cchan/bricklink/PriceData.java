package com.cchan.bricklink;

public class PriceData implements Comparable<PriceData> {
	private String id;
	private String avgPrice;
	private long totalQty;
	private String name;
	private int series;
	private int perCase;

	public PriceData(String id, String avgPrice, long totalQty,
			String name, int series, int perCase) {
		this.id = id;
		this.avgPrice = avgPrice;
		this.totalQty = totalQty;
		this.name = name;
		this.series = series;
		this.perCase = perCase;
	}

	public int compareTo(PriceData other) {
		// highest price first
		float thisPrice = Float.parseFloat(this.avgPrice);
		float otherPrice = Float.parseFloat(other.avgPrice);
		return Float.compare(otherPrice, thisPrice);
	}

	public String toString() {
		return String.format("%s %d %s %s %d %d", avgPrice, totalQty, id,
				name, series, perCase);
	}
}

