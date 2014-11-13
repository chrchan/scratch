import java.util.Arrays;
import java.util.LinkedList;

public class Permutations {

    public Permutations() {
    }

    public void printAllPermutations(int[] numbers) {
	// create LinkedList version of numbers
	LinkedList<Integer> numberList = new LinkedList<Integer>();
	for(int i=0; i<numbers.length; i++) {
	    numberList.add(numbers[i]);
	}
	
	printAllPermutationsHelper(numberList, new int[numbers.length], 0);
    }

    private void printAllPermutationsHelper(LinkedList<Integer> numbers, int[] buildList, int currentIndex) {
	if (currentIndex >= buildList.length) {
	    // we are done, print out buildList
	    System.out.println(Arrays.toString(buildList));
	}

	for(int i=0; i<numbers.size(); i++) {
	    buildList[currentIndex] = numbers.get(i);
	    Integer val = numbers.remove(i);
	    printAllPermutationsHelper(numbers, buildList, currentIndex + 1);
	    numbers.add(i, val);
	}
    }

    public static void main(String[] argv) {
	Permutations p = new Permutations();

	p.printAllPermutations(new int[]{1, 2, 3});
	p.printAllPermutations(new int[]{1, 2, 3, 4});
    }

}