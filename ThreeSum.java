import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ThreeSum {

    public static void findCombos(int[] ar) {
        // build hash
        HashMap<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
        for(int i=0; i<ar.length; i++) {
            List<Integer> entry = map.get(ar[i]);
            if (entry == null) {
                entry = new LinkedList<Integer>();
                map.put(ar[i], entry);
	    }
            entry.add(i);
	}

        // for each pair of numbers, see if the negative value of the sum exists
        for(int i=0; i<ar.length; i++) {
            for(int j=i+1; j<ar.length; j++) {
                int sum = ar[i] + ar[j];
                List<Integer> entries = map.get(-sum);
                if (entries != null) {
                    for(Integer entry : entries) {
                        if (entry > j) {
                            System.out.println(String.format("%d %d %d", ar[i], ar[j], ar[entry]));
                        }
                    }
                }
	    }
	}
    }

    public static void main(String[] args) {
        int[] ar = {2, 3, 1, -2, -1, 0, 2, -3, 0};
        findCombos(ar);
    }
}