import java.util.Arrays;
import java.util.LinkedList;

public class PermFinder {


    public PermFinder() {
    }

    public LinkedList<Integer> findPerm(LinkedList<Integer> numbers, int order) throws Exception {
	return findPermHelper(new LinkedList<Integer>(), numbers, order);
    }

    public LinkedList<Integer> findPermHelper(LinkedList<Integer> buildList, LinkedList<Integer> numbers, int order) throws Exception {
	// base case
	if (numbers.size() == 0) {
	    return buildList;
	}
	int numSubPerms = this.factorial(numbers.size()-1);
	int numPerms = numSubPerms * numbers.size();
	if (order > numPerms) throw new Exception(String.format("order=%d, numPerms=%d", order, numPerms));
	int position = order / numSubPerms;
	int newOrder = order % numSubPerms;
	buildList.add(numbers.get(position));
	numbers.remove(position);
	return findPermHelper(buildList, numbers, newOrder);
    }

    public static void main(String[] argv) {
	PermFinder pf = new PermFinder();
	try {
	    LinkedList<Integer> numbers = new LinkedList<Integer>(Arrays.asList(1, 2, 3, 4));
	    System.out.println(pf.findPerm(numbers, 6));
	    numbers = new LinkedList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
	    System.out.println(pf.findPerm(numbers, pf.factorial(numbers.size())-1));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public int factorial(int n) {
	if (n == 0) return 1;
	return n * factorial(n-1);
    }

}