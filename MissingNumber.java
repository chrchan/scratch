

public class MissingNumber {

    public MissingNumber() {}

    public int find(int[] ar) {
	int actualSum = 0;
	for(int num : ar) {
	    actualSum += num;
	}
	int targetSum = determineTargetSum(ar.length+1);
	return targetSum - actualSum;
    }

    private int determineTargetSum(int n) {
	int total = 0;
	if (n%2 == 1) {
	    // if n is odd, decrease by 1 to make it even but initialize total to n
	    total = n;
	    n = n-1;
	}
	total += ((1 + n)*(n/2));
	return total;

    }

    public static void main(String[] argv) {
	MissingNumber mn = new MissingNumber();
	System.out.println(mn.find(new int[]{1, 2, 3, 4, 5, 7, 8, 9}));
	System.out.println(mn.find(new int[]{1, 2, 3, 4, 5, 7, 8, 9, 6}));
    }
}