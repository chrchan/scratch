/**
 * Observation: We want to include any sub-sequence whose sum is positive.
 * Once we come across a sub-sequence whose sum is negative, we can discard
 * that subsequence entirely and start looking again for a new sub-sequence.
 */
public class LargestConsecutiveSum {

    public static int largestSum(int[] ar) {
        int runningSum = 0;
        int largestSum = Integer.MIN_VALUE;
        for(int i=0; i<ar.length; i++) {
            runningSum += ar[i];
            if (runningSum < 0) {
                runningSum = 0; // start over
	    } else {
                if (runningSum > largestSum) {
                    largestSum = runningSum;
		}
	    }
	}
        return largestSum;
    }

    public static void main(String[] argv) {
        System.out.println(largestSum(new int[]{1,1,1,1}));
        System.out.println(largestSum(new int[]{1,-1,1,1}));
        System.out.println(largestSum(new int[]{1,1,1,-1}));
        System.out.println(largestSum(new int[]{-1,1,1,-1}));
        System.out.println(largestSum(new int[]{1,1,-10,1}));
        System.out.println(largestSum(new int[]{1, 1, 1, 1, -1, 5, 1}));
        System.out.println(largestSum(new int[]{1, 1, 1, 1, -1, 3, 0}));
        System.out.println(largestSum(new int[]{1, 1, 1, 1, -3, 5}));
    }
}