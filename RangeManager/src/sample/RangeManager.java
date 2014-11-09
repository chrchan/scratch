package sample;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RangeManager {

	private volatile SortedSet<Range> ranges = new TreeSet<Range>();

	private volatile ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public synchronized void add(Range r) {
    	Set<Range> toDelete = new HashSet<Range>();
    	boolean span = false;
    	for(Range currentRange: this.ranges) {
    		if (r.getLow() <= currentRange.getHigh() + 1 && r.getLow() >= currentRange.getLow()) {
    			// if the current range will merge with the low-end of r, set r's low to currentRange's low and delete currentRange
    			r.setLow(currentRange.getLow());
    			toDelete.add(currentRange);
    			span = true;
    		} else if (r.getHigh() >= currentRange.getLow() - 1 && r.getHigh() <= currentRange.getHigh()) {
    			// if the current range will merge with the high-end of r, set r's high to currentRange's high and delete currentRange
    			r.setHigh(currentRange.getHigh());
    			toDelete.add(currentRange);
    			span = false;
    		} else if (span && currentRange.getHigh() < r.getHigh()) {
    			// if currentRange is spanned entirely by r
    			toDelete.add(currentRange);
    		}
    	}

    	// do not allow reading of this.ranges while we perform the adds and deletes
    	this.lock.writeLock().lock();
    	try {
    		this.ranges.removeAll(toDelete);
    		this.ranges.add(r);
    	} finally {
    		this.lock.writeLock().unlock();
    	}
    }

    public synchronized void remove(Range r) {
    	Set<Range> toDelete = new HashSet<Range>();
    	Range toAdd = null;
    	for(Range currentRange: this.ranges) {
    		if (currentRange.getLow() >= r.getLow() && currentRange.getHigh() <= r.getHigh()) {
    			// if r entirely contains currentRange, delete currentRange
    	    	toDelete.add(currentRange);
    		} else if (currentRange.getLow() <= r.getLow() && currentRange.getHigh() >= r.getHigh()) {
    			// if currentRange entirely contains r, we need to perform a split
    			toAdd = new Range(r.getHigh() + 1, currentRange.getHigh());
    			currentRange.setHigh(r.getLow() - 1);
    		} else if (currentRange.getLow() <= r.getLow() && currentRange.getHigh() >= r.getLow()) {
    			// if currentRange overlaps with r.low, set currentRange.high to r.low-1
    			currentRange.setHigh(r.getLow() - 1);
    		} else if (currentRange.getLow() <= r.getHigh() && currentRange.getHigh() >= r.getHigh()) {
    			// if currentRange overlaps with r.high, set currentRange.low to r.high+1
    			currentRange.setLow(r.getHigh() + 1);
    		}
    	}

    	// do not allow reading of this.ranges while we perform the adds and deletes
		if (!toDelete.isEmpty() || toAdd != null) {
			this.lock.writeLock().lock();
			try {
				this.ranges.removeAll(toDelete);
				if (toAdd != null) {
					this.ranges.add(toAdd);
				}
			} finally {
				this.lock.writeLock().unlock();
			}
		}
    }

    public List<Range> toList() {
    	this.lock.readLock().lock();
    	try {
    		return new ArrayList<Range>(ranges);
    	} finally {
    		this.lock.readLock().unlock();
    	}
    }

    public static void main(String[] argv) {
    	RangeManager rm = new RangeManager();
    	rm.add(new Range(8, 10));
    	System.out.println(rm.toList()); // initial add
    	rm.add(new Range(1, 4));
    	System.out.println(rm.toList()); // non-spanning add
    	rm.add(new Range(5, 7));
    	System.out.println(rm.toList()); // bridge above and below
    	rm.add(new Range(20, 30));
    	System.out.println(rm.toList());
    	rm.add(new Range(40, 50));
    	System.out.println(rm.toList());
    	rm.add(new Range(6, 45));
    	System.out.println(rm.toList()); // bridge across multiple ranges
    	rm.add(new Range(100, 200));
    	rm.add(new Range(51, 97)); // bridge below only
    	System.out.println(rm.toList());
    	rm.add(new Range(201, 300));
    	System.out.println(rm.toList());
    	rm.add(new Range(99, 99)); // bridge above only;
    	System.out.println(rm.toList());

    	rm = new RangeManager();
    	rm.add(new Range(3, 15));
    	System.out.println(rm.toList());
    	rm.remove(new Range(6, 7));
    	System.out.println(rm.toList());
    	rm.remove(new Range(8, 15));
    	System.out.println(rm.toList());
    }
}
