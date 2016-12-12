
import java.util.*;

@SuppressWarnings("rawtypes")
public class NaiveInvertedIndex {
	// private HashMap<String, List<Integer>> mIndex;
	private HashMap<String, HashMap<Integer, List<Integer>>> mIndex;
	private HashMap<Integer, Integer> docSize;
	private Double avgDocSize;
	@SuppressWarnings("unused")
	private HashMap<Integer, Integer> totalTerms;

	public Double getAvgDocSize() {
		Iterator it = docSize.entrySet().iterator();
		int totalDocSize = 0;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			totalDocSize = totalDocSize + Integer.valueOf((pair.getValue()).toString());
			
		}
                
		avgDocSize = (double) (totalDocSize / docSize.size());
		return avgDocSize;
	}

	public HashMap<Integer, Integer> getDocSize() {
		return docSize;
	}

	public NaiveInvertedIndex() {
		mIndex = new HashMap<String, HashMap<Integer, List<Integer>>>();
		docSize = new HashMap<Integer, Integer>();
		totalTerms = new HashMap<Integer, Integer>();
	}

	public void addTerm(String term, Integer documentID, int pos) {
		// TO-DO: add the term to the index hashtable. If the table does not
		// have
		// an entry for the term, initialize a new ArrayList<Integer>, add the
		// docID to the list, and put it into the map. Otherwise add the docID
		// to the list that already exists in the map, but ONLY IF the list does
		// not already contain the docID.
		if (mIndex.get(term) == null) {
			HashMap<Integer, List<Integer>> docMap = new HashMap<Integer, List<Integer>>();
			ArrayList<Integer> termPos = new ArrayList<Integer>();
			termPos.add(pos);
			// docList.add(documentID);
			docMap.put(documentID, termPos);
			if (!docSize.containsKey(documentID)) {
				docSize.put(documentID, 1);

			} else {
				Integer i = docSize.get(documentID);
				i++;
				docSize.put(documentID, i);
			}

			mIndex.put(term, docMap);
		} else if (!mIndex.get(term).containsKey(documentID)) {
			ArrayList<Integer> termPos = new ArrayList<Integer>();
			termPos.add(pos);

			(mIndex.get(term)).put(documentID, termPos);
		} else if (!mIndex.get(term).get(documentID).equals(pos)) {

			(mIndex.get(term)).get(documentID).add(pos);
		}
	}

	public HashMap<Integer, List<Integer>> getPostings(String term) {
		// TO-DO: return the postings list for the given term from the index
		// map.
		return mIndex.get(term);
	}

	public int getTermCount() {
		// TO-DO: return the number of terms in the index.

		return mIndex.size();
	}

	public Set<Integer> getDocumentId(String term) {
		// TO-DO: return the postings list for the given term from the index
		// map.
		Set<Integer> docIdSet = new HashSet<Integer>();
		if(!mIndex.containsKey(term))
		{
		return docIdSet;
		} else {
			docIdSet.addAll(mIndex.get(term).keySet());
			return docIdSet;
		}
	}

	public String[] getDictionary() {
		// TO-DO: fill an array of Strings with all the keys from the hashtable.
		// Sort the array and return it.
		Map<String, HashMap<Integer, List<Integer>>> treeMap = new TreeMap<String, HashMap<Integer, List<Integer>>>(
				mIndex);
		Iterator iter = treeMap.entrySet().iterator();
		String temp[] = new String[mIndex.size()];
		int i = 0;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			temp[i] = entry.getKey().toString();

			i++;
		}
		return temp;
	}
	
	public List<String> getVocabList() {
		// TO-DO: fill list of Strings with all the keys from the hashtable.
		// Sort the array and return it.
		Map<String, HashMap<Integer, List<Integer>>> treeMap = new TreeMap<String, HashMap<Integer, List<Integer>>>(
				mIndex);
		Iterator iter = treeMap.entrySet().iterator();
		List<String> temp = new ArrayList<String>();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			temp.add(entry.getKey().toString());
		}
		return temp;
	}
	
}
