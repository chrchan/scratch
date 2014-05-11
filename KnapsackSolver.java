import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class KnapsackSolver {

	public static final boolean DEBUG = true;
	
	public static final boolean ENABLE_MEMOIZE = true;
	
	// the format is <numItems>\n<capacity>\n<item1Weight> <item1Value>\n<item2Weight> <item2Value>\netc...
	public static String SAMPLE_DATA = "6\n" +
			"20\n" +
			"4 8\n" +
			"3 7\n" +
			"5 12\n" +
			"8 13\n" +
			"2 5\n" +
			"1 8\n";
	public static String SAMPLE_DATA2 = "6\n" +
			"4\n" +
			"1 4\n" +
			"2 5\n" +
			"3 6\n" +
			"4 7\n" +
			"5 8\n" +
			"6 9\n";
	
	Item[] items;
	
	int calls = 0;
	
	int bestItems = 0;
	
	int bestItemsVal = 0;
	
	HashMap<String, Integer> table = new HashMap<String, Integer>();
	
	public static class Item {
		int weight;
		int value;
		
		public Item(int weight, int value) {
			this.weight = weight;
			this.value = value;
		}
	}
	
	public KnapsackSolver(Item[] items) {
		this.items = items;
	}
	
	// Generate a human-readable representation of the bitmap showing whether we have
	// put the item in the knapsack or not. 1=in, 0=not in, ?=not decided yet.
	public String niceBitmap(int b, int index) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<this.items.length; i++) {
			if (i >= index) {
				sb.append("?");
			} else if ((b & (1 << i)) != 0) {
				sb.append("1");
			} else {
				sb.append("0");
			}
		}
		return sb.toString();
	}
	
	private void memoize(int capacity, int index, int value) {
		table.put(capacity + ":" + index, value);
	}
	
	private Integer recall(int capacity, int index) {
		return table.get(capacity + ":" + index);
	}
	
	/**
	 * Recursive call to figure out the optimal set of items to put in the knapsack.
	 * 
	 * @param capacity how much room is left in the knapsack
	 * @param itemBitmap bitmap of which items we've already decided to put in the knapsack
	 * @param index the index of the item we are currently considering whether or not to put in the knapsack
	 * @param value the current value of the items we've already decided to put in the knapsack
	 * @return the total value of the knapsack after we've decided whether or not to put the (index)th item in
	 */
	public int solve(int capacity, int itemBitmap, int index, int value) {

		// see if the value was memoized and if so, return that
		Integer tableVal = recall(capacity, index);
		if (ENABLE_MEMOIZE && tableVal != null) {
			return tableVal.intValue();
		}

		// keep track of the number of calls made
		if (DEBUG) {
			System.out.println(String.format("%d: capacity=%d, items=%s, index=%d, value=%d", ++this.calls, capacity, niceBitmap(itemBitmap, index), index, value));
		}
		
		if (capacity == 0) {
			// base case #1: no more room in the bag
			if (value > bestItemsVal) {
				bestItemsVal = value;
				bestItems = itemBitmap;
			}
			memoize(capacity, index, value);
			return value;
		} else if (index >= this.items.length - 1) {
			// base case #2: last item
			if (capacity >= this.items[index].weight) {
				// last item fits, add it
				value = value + this.items[index].value;
				itemBitmap = itemBitmap | (1 << index);
			}
			if (value > bestItemsVal) {
				bestItemsVal = value;
				bestItems = itemBitmap;
			}
			memoize(capacity, index, value);
			return value;
		} else if (capacity < this.items[index].weight) {
			// current item does not fit
			int best = solve(capacity, itemBitmap, index + 1, value);
			memoize(capacity, index, best);
			return best;
		} else {
			// current item does fit - we now need to decide if we are better off putting the item in or not
			int avec = solve(capacity - this.items[index].weight, itemBitmap | (1 << index), index + 1, value + this.items[index].value);
			int sans = solve(capacity, itemBitmap, index + 1, value);
			if (avec > sans) {
				memoize(capacity, index, avec);
				return avec;
			} else {
				memoize(capacity, index, sans);
				return sans;
			}
		}
	}
	
	public static void main(String[] argv) throws Exception {
		if (DEBUG) {
			System.setIn(new ByteArrayInputStream(KnapsackSolver.SAMPLE_DATA.getBytes()));
		}

		// read item data from stdin
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		// read the first line and get the number of items
		String line = in.readLine();
		int totalItems = Integer.parseInt(line);
		Item[] items = new Item[totalItems];
		
		// read the second line and get the knapsack capacity
		line = in.readLine();
		int capacity = Integer.parseInt(line);

		// read in the item weights and values
		for(int i=0; i<totalItems; i++) {
			line = in.readLine();
			String[] parts = line.split(" ");
			items[i] = new Item(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
		}
		in.close();

		KnapsackSolver solver = new KnapsackSolver(items);
		solver.solve(capacity, 0, 0, 0);
		System.out.println(String.format("items: %s, value=%d", solver.niceBitmap(solver.bestItems, solver.items.length), solver.bestItemsVal));
	}
}
