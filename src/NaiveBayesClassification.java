import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class NaiveBayesClassification {

	private static HashMap<String, Double> calculateConditionalProbabilities(NaiveInvertedIndex index,
			List<String> featureWordsList) {
		
		double vocabCountIndex = featureWordsList.size();
		double totalTermFrequencyIndex = totalTermsInClass(index);
		HashMap<String, Double> condProbIndex = new HashMap<String, Double>();
		for (String featureTerm : featureWordsList) {
			double termFrequencyIndex = termFrequencyInClass(featureTerm, index);
			Double condProb = (termFrequencyIndex + 1) / (totalTermFrequencyIndex + vocabCountIndex);
			condProbIndex.put(featureTerm, condProb);
		}
		return condProbIndex;
	}

	private static double calculatePriori(double nDocsIndex, double totalDocsInCorpus) {
		//System.out.println("# docs in class: " + nDocsIndex + " total Docs In Corpus: " + totalDocsInCorpus);
		double prioriIndex = nDocsIndex / totalDocsInCorpus;
		//System.out.println("Class Priroi: " + prioriIndex);

		return prioriIndex;
	}

	private static BigDecimal calculateDocProbability(NaiveInvertedIndex indexPredict, NaiveInvertedIndex index,
			HashMap<String, Double> condProbindex, List<String> featureWords, double totalDocCount) {
		double nDocsClass = index.getDocSize().size();
		double classProb = Math.log(calculatePriori(nDocsClass, totalDocCount));
		BigDecimal classProbB = new BigDecimal(classProb).setScale(9, RoundingMode.HALF_UP);
		BigDecimal docScore = new BigDecimal(0.0);
		BigDecimal docScoreTemp = new BigDecimal(0.0);
		List<String> docWordsList = indexPredict.getVocabList();
		docWordsList.retainAll(featureWords);
		for (String term : docWordsList) {
			docScore = new BigDecimal(Math.log(condProbindex.get(term))).setScale(9, RoundingMode.HALF_UP);
			docScoreTemp = docScoreTemp.add(docScore).setScale(9, RoundingMode.HALF_UP);
			//System.out.println("Term: " + term + " Conditional Prob of Term: " + condProbindex.get(term)
				//	+ " Log of Prob: " + docScore);

		}
		BigDecimal finalProb = classProbB.add(docScoreTemp).setScale(9, RoundingMode.HALF_UP);
		return finalProb;
	}

	private static double termFrequencyInClass(String featureWord, NaiveInvertedIndex index) {
		double termFrequency = 0.0;
		//System.out.println(featureWord);
		//System.out.println(index.getPostings(featureWord));
		HashMap<Integer, List<Integer>> docMap = new HashMap<Integer, List<Integer>>();
		List<String> vocabList = index.getVocabList();
		if (vocabList.contains(featureWord)) {
			docMap = index.getPostings(featureWord);
			for (Entry<Integer, List<Integer>> entry : docMap.entrySet()) {
				int value = entry.getValue().size();
				termFrequency += value;
				//System.out.println(" docId: " + key + " size: " + value);
			}
			//System.out.println("Frequency of term: " + featureWord + " : " + termFrequency);
			return termFrequency;
		} else
			return 0.0;
	}

	private static double totalTermsInClass(NaiveInvertedIndex index) {
		List<String> vocabList = index.getVocabList();
		double totalTermCountInIndex = 0.0;
		HashMap<Integer, List<Integer>> docMap = new HashMap<Integer, List<Integer>>();
		for (String term : vocabList) {
			double termFrequency = 0.0;
			docMap = index.getPostings(term);
			for (Entry<Integer, List<Integer>> entry : docMap.entrySet()) {
				int value = entry.getValue().size();
				termFrequency += value;
				// System.out.println(" docId: " + key + " size: " + value);
			}

			// System.out.println("Frequency of term: " + term + " : " +
			// termFrequency);
			totalTermCountInIndex += termFrequency;
		}
		// System.out.println("Total Count of Terms in Index: " +
		// totalTermCountInIndex);
		return totalTermCountInIndex;
	}
	
	public static void NaivesBayesClassifier(NaiveInvertedIndex indexHamilton,NaiveInvertedIndex indexMadison,NaiveInvertedIndex indexJay) throws IOException{
		double nDocsHamilton = indexHamilton.getDocSize().size();
		double nDocsMadison = indexMadison.getDocSize().size();
		double nDocsJay = indexJay.getDocSize().size();
		double totalDocCount = nDocsHamilton + nDocsMadison + nDocsJay;
		String corpusPredict = "horm";
        HashMap<Integer, File> filenames = getFileNames(corpusPredict);
        
		// Get feature words list
		List<String> featureWords = new ArrayList<String>();
		featureWords = FeatureSelection.itcFeatureList(70, indexHamilton, indexMadison, indexJay);
		//System.out.println("Feature WordList " + featureWords.size());
		
		//Get Conditional Probability
		HashMap<String, Double> condProbHamilton = new HashMap<String, Double>();
		HashMap<String, Double> condProbMadison = new HashMap<String, Double>();
		HashMap<String, Double> condProbJay = new HashMap<String, Double>();
		
		condProbHamilton = calculateConditionalProbabilities(indexHamilton, featureWords);
		condProbMadison = calculateConditionalProbabilities(indexMadison, featureWords);
		condProbJay = calculateConditionalProbabilities(indexJay, featureWords);
		Double j = 0.0;
		for(Integer i: filenames.keySet()){
		NaiveInvertedIndex indexPredict = new NaiveInvertedIndex();
		SimpleEngine.indexFile(filenames.get(i).getAbsoluteFile(), indexPredict, i);
		BigDecimal docProbHamilton = calculateDocProbability(indexPredict, indexHamilton, condProbHamilton, featureWords, totalDocCount);
		//System.out.println("Doc Probability for Hamilton: " + docProbHamilton);
		
		BigDecimal docProbMadison = calculateDocProbability(indexPredict, indexMadison, condProbMadison, featureWords, totalDocCount);
		//System.out.println("Doc Probability for Madison: " + docProbMadison);
		
		BigDecimal docProbJay = calculateDocProbability(indexPredict, indexJay, condProbJay, featureWords, totalDocCount);
		//System.out.println("Doc Probability for Jay: " + docProbJay);
		BigDecimal maxClass = maxNumber(docProbHamilton,docProbMadison,docProbJay);
		if(maxClass.equals(docProbHamilton)){
			System.out.println("Class for predicted doc - "+ filenames.get(i).toString() + " : Hamilton" );
		} else if(maxClass.equals(docProbMadison)){
			System.out.println("Class for predicted doc - "+ filenames.get(i).toString()+ " : Madison" );
			j = j + 1;
		}else{
			System.out.println("Class for predicted doc - "+ filenames.get(i).toString() + " : Jay" );

		}
		}
		Double accuracy = j/(double)filenames.keySet().size();
		System.out.println("Accuracy of Naive Bayes Classification Algorithm: " + accuracy*100 + "%");

	}
	private static BigDecimal maxNumber(BigDecimal a, BigDecimal b, BigDecimal c){
		BigDecimal d = a.max(b);
		BigDecimal e = d.max(c);
		return e;
	}
	private static HashMap<Integer, File> getFileNames(String corpusName) throws IOException {
	    final HashMap<Integer, File> fileNames = new HashMap<Integer, File>();
	    final Path currentWorkingPath = Paths.get(corpusName).toAbsolutePath();
	    Files.walkFileTree(currentWorkingPath, new SimpleFileVisitor<Path>() {
	        int mDocumentID = -1;
	
	        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
	            if (currentWorkingPath.equals(dir)) {
	                return FileVisitResult.CONTINUE;
	            }
	            return FileVisitResult.SKIP_SUBTREE;
	        }
	
	        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws FileNotFoundException {
	
	            if (file.getFileName().toString().matches("^.*.txt$")) {
	                mDocumentID++;
	                fileNames.put(mDocumentID, file.getFileName().toFile());
	            }
	            return FileVisitResult.CONTINUE;
	        }
	
	        public FileVisitResult visitFileFailed(Path file, IOException e) {
	
	            return FileVisitResult.CONTINUE;
	        }
	    });
	    return fileNames;
	}

}
