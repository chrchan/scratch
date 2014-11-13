import java.util.Arrays;
import java.util.LinkedList;

public class PermFinder {


    public PermFinder() {
    }

    public int[] findPerm(int[] numbers, int order) throws Exception {
	// make copy of input array as a LinkedList
	LinkedList<Integer> numberList = new LinkedList<Integer>();
	for(int i=0; i<numbers.length; i++) {
	    numberList.add(numbers[i]);
	}
	return findPermHelper(new int[numbers.length], numberList, order, 0);
    }

    public int[] findPermHelper(int[] buildList, LinkedList<Integer> numbers, int order, int currentPosition) throws Exception {
	// base case
	if (currentPosition >= buildList.length) {
	    return buildList;
	}
	int numSubPerms = this.factorial(numbers.size() - 1);
	int numPerms = numSubPerms * (numbers.size());
	if (order > numPerms) throw new Exception(String.format("order=%d, numPerms=%d", order, numPerms));
	int position = order / numSubPerms;
	int newOrder = order % numSubPerms;
	buildList[currentPosition] = numbers.get(position);
	numbers.remove(position);
	return findPermHelper(buildList, numbers, newOrder, currentPosition + 1);
    }

    public static void main(String[] argv) {
	PermFinder pf = new PermFinder();
	try {
	    int[] numbers = new int[]{1, 2, 3, 4};
	    System.out.println(Arrays.toString(pf.findPerm(numbers, 6)));
	    for(int i=0; i<pf.factorial(numbers.length); i++) {
		System.out.println(Arrays.toString(pf.findPerm(numbers, i)));
	    }
	    numbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	    System.out.println(Arrays.toString(pf.findPerm(numbers, pf.factorial(numbers.length) - 1)));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public int factorial(int n) {
	if (n == 0) return 1;
	return n * factorial(n-1);
    }

}