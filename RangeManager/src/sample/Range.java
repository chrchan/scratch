package sample;

/**
 * Represent an inclusive integer range from low to high.
 */
public class Range implements Comparable<Range> {
    int low;
    int high;

    public Range(int low, int high) {
        this.low = low;
        this.high = high;
    }

    public int getLow() {
        return low;
    }
    public void setLow(int low) {
        this.low = low;
    }

    public int getHigh() {
        return high;
    }
    public void setHigh(int high) {
        this.high = high;
    }

    @Override
    public boolean equals(Object other) {
    	if (!(other instanceof Range)) {
    		return false;
    	}
    	Range otherRange = (Range) other;
        return (this.low == otherRange.low && this.high == otherRange.high);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        return hash * 17 * this.low * 31 * this.high;
    }

    @Override
    public int compareTo(Range other) {
        return this.low - other.low;
    }

    @Override
    public String toString() {
    	return String.format("%d,%d",this.low, this.high);
    }
}
