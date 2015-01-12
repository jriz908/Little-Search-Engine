package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		
		HashMap<String,Occurrence> a = new HashMap<String,Occurrence>();
		
		Scanner words = new Scanner(new File(docFile));
		
			while (words.hasNext())
			{
				String word = words.next();

				if(getKeyWord(word) != null)
				{	
					word = getKeyWord(word);
					if(!a.containsKey(word))
					{
						Occurrence occurs = new Occurrence(docFile,1);
						a.put(word, occurs);
					}
					else
					{
						a.get(word).frequency++;
					}
				}
			}

		return a;
	}
		
		
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		
		ArrayList<Occurrence> a = new ArrayList<Occurrence>();

		for(String key: kws.keySet())
		{	
			Occurrence occ = kws.get(key);

			if(!keywordsIndex.containsKey(key))
			{
				ArrayList<Occurrence> b = new ArrayList<Occurrence>();				
				b.add(occ);
				keywordsIndex.put(key, b);
			}
			else
			{
				a = keywordsIndex.get(key);
				a.add(occ);
				insertLastOccurrence(a);
				keywordsIndex.put(key, a);
			}	
		}
		
		
		// COMPLETE THIS METHOD
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		
		if(word.length() == 0)
			return null;
		
		word.trim();
		word = word.toLowerCase();
		
		while(word.length() > 1 && (word.charAt(word.length()-1) == '!' || word.charAt(word.length()-1) == '.' || word.charAt(word.length()-1) == ',' || word.charAt(word.length()-1) == '?' || word.charAt(word.length()-1) == ':' || word.charAt(word.length()-1) == ';')){
			
			word = word.substring(0, word.length() - 1);
			
		}
		
		if(word.length() == 0)
			return null;
		
		for(String noiseWord: noiseWords.keySet())
		{
			if(word.equalsIgnoreCase(noiseWord))
			{
				return null;
			}
		}
		
		for(int i=0; i < word.length(); i++){
			if(Character.isLetter(word.charAt(i)) == false)
				return null;
		}
		
		
		return word;
		
		
		
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		
		if(occs.size() == 1)
			return null;
		
		int n = occs.size();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		Occurrence newOcc = occs.get(n-1);
		occs.remove(n-1);
		n--;
		
		int high = n-1;
		int low = 0;
		int mid = 0;
		
		while(high >= low){
			mid = (high+low)/2;
			indices.add(mid);
			
			if(occs.get(mid).frequency == newOcc.frequency){
				occs.add(mid, newOcc);
				return indices;
			}
			
			else if(occs.get(mid).frequency > newOcc.frequency)
				low = mid + 1;
				
			else
				high = mid -1;			
		}
		
		if(mid == 0){
			if(newOcc.frequency < occs.get(0).frequency){
				occs.add(1, newOcc);
				return indices;
			}
		}
			
		occs.add(mid, newOcc);
		return indices;
		
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<Occurrence> L1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> L2 = new ArrayList<Occurrence>();
		
		kw1 = kw1.toLowerCase();
		kw2 = kw2.toLowerCase();
		
		if(keywordsIndex.get(kw1) != null)
			L1 = keywordsIndex.get(kw1);
		
		
		if(keywordsIndex.get(kw2) != null)
			L2 = keywordsIndex.get(kw2);
		
		for(int p = 0; p < L1.size(); p++)
		{
			if(result.size() <= 4)
			{
				int one = L1.get(p).frequency;
				String D1 = L1.get(p).document;

				for(int w = 0; w < L2.size(); w++)
				{
					String D2 = L2.get(w).document;
					int two = L2.get(w).frequency;

					if(two <= one)
					{
						if(!result.contains(D1) && result.size() <= 4)
						{
							result.add(D1);
						}
					}
					else 
					{
						if(!result.contains(D2) && result.size() <= 4)
						{
							result.add(D2);
						}
					}
					
				}
				
				
				
			}
			
			
		}
		
		if(result.size() == 0)
			return null;
		
		
		
		
		
		
		
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		return result;
	}
	
	public static void main(String[] args){
		LittleSearchEngine google = new LittleSearchEngine();
		Occurrence a = new Occurrence("asdas", 1);
		Occurrence b = new Occurrence("asdas", 2);
		Occurrence c = new Occurrence("asdas", 3);
		Occurrence d = new Occurrence("asdas", 4);
		Occurrence e = new Occurrence("asdas", 5);
		Occurrence f = new Occurrence("asdas", 6);
		Occurrence g = new Occurrence("asdas", 8);
		Occurrence h = new Occurrence("asdas", 9);
		Occurrence i = new Occurrence("asdas", 10);
		
		ArrayList<Occurrence> occs = new ArrayList<Occurrence>();
		occs.add(i);occs.add(h);occs.add(g);occs.add(f);occs.add(e);occs.add(d);occs.add(c);occs.add(b);occs.add(a);
		
		Occurrence newOcc = new Occurrence("asdad", 11);
		occs.add(newOcc);
		
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		indices = google.insertLastOccurrence(occs);
		System.out.println(occs);
		System.out.println(indices);
		
	}
	
	
}
