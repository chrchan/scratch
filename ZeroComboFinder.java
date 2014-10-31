import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ZeroComboFinder {
    
    public static void findCombosSort(int[] ar) {
        // sort the array
        Arrays.sort(ar);

        // go from left to right and find pairs of numbers to the right that combine for form the sum
        for(int i=0; i<ar.length; i++) {
            int lowInd = i+1;
            int highInd = ar.length-1;
            while(lowInd < highInd) {
                int sum = ar[i] + ar[lowInd] + ar[highInd];
                if (sum == 0) {
                    System.out.println(String.format("%d %d %d", ar[i], ar[lowInd], ar[highInd]));
                    

                } else if (sum < 0) {
                    lowInd++;
		} else {
                    highInd--;
		}
            }
        }
    }

    public static void findCombosMemoize(int[] ar) {
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

    public static void findCombosBruteForce(int[] ar) {
        int sum = 0;
        for(int i=0; i<ar.length; i++) {
            sum = ar[i];
            for(int j=i+1; j<ar.length; j++) {
                sum += ar[j];
                for(int k=j+1; k<ar.length; k++) {
                    sum += ar[k];
                    if (sum == 0) {
                        System.out.println(String.format("%d %d %d", ar[i], ar[j], ar[k]));
                    }
                    sum -= ar[k];
                }
                sum -= ar[j];
            }
        }
    }
    
    public static void main(String[] argv) {
        int[] ar = {2, 3, 1, -2, -1, 0, 2, -3, 0};
        long start=0, now=0;

        start = System.currentTimeMillis();
        findCombosBruteForce(ar);
        now = System.currentTimeMillis();
        System.out.println(String.format("Brute force: %d", now - start));

        start = System.currentTimeMillis();
        findCombosMemoize(ar);
        now = System.currentTimeMillis();
        System.out.println(String.format("Memoize: %d", now - start));

        start = System.currentTimeMillis();
        findCombosSort(ar);
        now = System.currentTimeMillis();
        System.out.println(String.format("Sort: %d", now - start));

    }
}
