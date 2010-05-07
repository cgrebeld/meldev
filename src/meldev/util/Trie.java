package meldev.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Trie {
	public class Word {
		public Word(String word, Object data) {
			fData = data;
			fWord = word;
		}
		public String fWord;
		public Object fData;
	}
	public class Node {
		private boolean fIsTerm;
		private Object fData;
		
		public Node(boolean isterm) {
			fIsTerm = isterm;
			fChildren = new HashMap<Character,Node>();
		}
		public boolean isTerminated() {
			return fIsTerm;
		}
		public void setTerminated(boolean isterm) {
			fIsTerm = isterm;
		}
		public Object getData() {
			return fData;
		}
		public void setData(Object data) {
			fData = data;
		}
		public Node addChild(Character c, boolean isterm) {
			Node n = new Node(isterm);
			fChildren.put(c, n);
			return n;
		}
		//! Add the string starting from this node
		public void addWord(String s, Object data) {
			if (s.length() > 0) {
				Node n = this;
				int i = 0;
				for(; i < s.length() - 1; ++i) {
					Node np = n.fChildren.get(s.charAt(i));
					if (null != np) {
						// edge already exists, next
						n = np;
					} else {
						// add edge, continue at new node
						n = n.addChild(s.charAt(i), false);
					}
				}
				// at the last character, give it the end-of-word marker
				Node np = n.fChildren.get(s.charAt(i));
				if (null != np) {
					np.setTerminated(true);
				} else {
					np = n.addChild(s.charAt(i), true);
				}
				np.setData(data);
			} 
		}
		
		//! Get all the paths from here to terminators.
		public void getPaths(String wordSoFar, ArrayList<Word> result) {
			if (isTerminated()) {
				// i am terminated.  That means the wordSoFar is in fact a word
				// so add it to result.  All future words are prefixed by it.
				result.add(new Word(wordSoFar, fData));
			}
			Iterator<Entry<Character, Node>> it = fChildren.entrySet().iterator();
			while (it.hasNext()) {
				// Get the completed words from each child recursively
				Entry<Character, Node> n = it.next();
				n.getValue().getPaths(wordSoFar + n.getKey(), result);
			}
		}
		
		public HashMap<Character,Node> fChildren;
	}

	private Node fHead;
	
	public Trie() {
		fHead = new Node(false);
	}
	
	//! Add a word to the trie
	public void addWord(Word word) {
		fHead.addWord(word.fWord, word.fData);
	}
	
	public void addWord(String word, Object data) {
		fHead.addWord(word, data);
	}
	
	//! Find a word in the Trie and return the Node for the last character
	public Node find(String word) {
		if (word.length() > 0) {
			Node n = fHead;
			int i = 0;
			for(; i < word.length() && null != n; ++i) {
				Node np = n.fChildren.get(word.charAt(i));
				n = np;
			}
			if (n != null && i == word.length()) {
				return n;
			}
		}
		return null;
	}
	
	//! Get all the words in the trie which start with the given prefix
	public ArrayList<Word> getCompletions(String prefix) {
		ArrayList<Word> result = new ArrayList<Word>();
		Node n = find(prefix);
		if (null != n) {
			n.getPaths(prefix, result);
		}
		return result;
	}
}