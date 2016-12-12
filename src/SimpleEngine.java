
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

/**
 * A very simple search engine. Uses an inverted index over a folder of TXT
 * files.
 */

public class SimpleEngine {
	static HashMap<List<String>, NaiveInvertedIndex> fileIndexMap = new HashMap<List<String>, NaiveInvertedIndex>();
	
	public static NaiveInvertedIndex buildIndex(String corpusName) throws IOException {
		/*System.out.println("Enter the Corpus path for indexing: ");
		Scanner s = new Scanner(System.in);*/
		//String corpusName = s.nextLine();
            
                //System.out.println("corpus name: "+corpusName);
		final Path currentWorkingPath = Paths.get(corpusName).toAbsolutePath();
		final NaiveInvertedIndex index = new NaiveInvertedIndex();
		final List<String> fileNames = new ArrayList<String>();

		Files.walkFileTree(currentWorkingPath, new SimpleFileVisitor<Path>() {
			int mDocumentID = 0;

			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				if (currentWorkingPath.equals(dir)) {
					return FileVisitResult.CONTINUE;
				}
				return FileVisitResult.SKIP_SUBTREE;
			}

			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws FileNotFoundException {
				fileNames.add(file.getFileName().toString());
				if(file.getFileName().toString().matches("^.*.txt$")){
				indexFile(file.toFile(), index, mDocumentID);
				mDocumentID++;
				}
				return FileVisitResult.CONTINUE;
			}
			
			public FileVisitResult visitFileFailed(Path file, IOException e) {

				return FileVisitResult.CONTINUE;
			}
		});
//                if(corpusName=="test"){
//		printResults(index,fileNames);}
		//fileIndexMap.put(fileNames, index);
		//s.close();
		return index;
	}

	@SuppressWarnings("unchecked")
	public static void indexFile(File file, NaiveInvertedIndex index, int docID) throws FileNotFoundException {
		SimpleTokenStream simpleTokenObj = new SimpleTokenStream(file);
		int pos = 0;
		while (simpleTokenObj.hasNextToken()) {
			String temp = simpleTokenObj.nextToken();
			ArrayList<String> arr = new ArrayList<String>();
			arr.addAll(NormalizeToken.normalizeToken(temp));
			for(String temp1: arr){
			if(!(temp1 == null) && !temp1.matches("[0-9]*") && !temp1.matches("^[a-zA-Z]{3}$") && !temp1.matches("^[a-zA-Z]{2}$") && !temp1.matches("^[a-zA-Z]{1}$"))
			{
			index.addTerm(temp1, docID, pos);
			}
			pos++;
			}
		}
	}

	private static void printResults(NaiveInvertedIndex index,List<String> fileNames) {
		String token[] = index.getDictionary();
		for (String temp : token) {
			HashMap<Integer, List<Integer>> finalList = (index.getPostings(temp));
			System.out.println(temp + " : " + finalList);
//                        Iterator itr=finalList.entrySet().iterator();
//                        while(itr.hasNext()){
//						Map.Entry entry=(Map.Entry)itr.next();
//						System.out.println(fileNames.get(Integer.parseInt(entry.getKey().toString()))+" ");
//						//System.out.println(entry.getValue());
//                                        }
		}
	}

	@SuppressWarnings("rawtypes")
	public static void searchWord(NaiveInvertedIndex index, List<String> fileNames, String searchWord) {
		try {
			if (("null").equals(index.getPostings(searchWord).toString())) {
				System.out.println("Word does not present ");
			} else {
				HashMap<Integer, List<Integer>> mIndex = index.getPostings(searchWord);
				Iterator itr = mIndex.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry entry = (Map.Entry) itr.next();
					System.out.println(fileNames.get(Integer.parseInt(entry.getKey().toString())) + " ");
				}
			}
		} catch (NullPointerException e) {
			System.out.println("Word does not present ");
		}

	}

}
