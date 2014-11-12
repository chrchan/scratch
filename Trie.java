import java.io.BufferedReader;
import java.io.FileReader;

public class Trie {

    private Trie[] nodes = new Trie[27]; // 0-25 are for the letters a-z. 26th index is for '\0' word terminator char

    public Trie() {
    }

    public void add(String word) {
	this.addHelper(word.toLowerCase());
    }

    private void addHelper(String word) {
	if (word == null) return;
	char c = word.charAt(0);
	int ind = c - 'a';
	if (ind < 0 || ind > 25) {
	    // skip the character
	    this.add(word.substring(1));
	} else {
	    Trie subTrie = this.nodes[ind];
	    if (subTrie == null) {
		subTrie = new Trie();
		this.nodes[ind] = subTrie;
	    }
	    if (word.length() > 1) {
		subTrie.add(word.substring(1));
	    } else {
		// last character of word, so add entry for terminator char
		subTrie.nodes[26] = new Trie();
	    }
	}
    }

    public boolean contains(String word) {
	return this.containsHelper(word.toLowerCase());
    }

    private boolean containsHelper(String word) {
	if (word == null) return false;
	char c = word.charAt(0);
	int ind = c - 'a';
	if (ind < 0 || ind > 25) {
	    // skip the character
	    return this.contains(word.substring(1));
	}
	Trie subTrie = this.nodes[ind];
	if (subTrie == null) return false;
	if (word.length() > 1) {
	    return subTrie.contains(word.substring(1));
	} else {
	    return subTrie.nodes[26] != null;
	}
    }

    public static void main(String argv[]) {
	Trie trie = new Trie();
	if (argv.length > 0) {
	    for(String filename : argv) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String word = reader.readLine();
			while (word != null) {
			    trie.add(word);
			    word = reader.readLine();
			}
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	    System.out.println(trie.contains("Jean-Christophe"));
	    System.out.println(trie.contains("JeanChristophe"));
	    System.out.println(trie.contains("WEB"));
	    System.out.println(trie.contains("wire"));
	    System.out.println(trie.contains("spaghetti"));
	    System.out.println(trie.contains("asdf"));
	}
    }

}