import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.List;
import java.io.IOException;
import java.lang.Math;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class FeatureSelection {

	private static double docsWithTermNotInClass(NaiveInvertedIndex secondIndex, NaiveInvertedIndex thirdIndex,
			String term) {
		// Calculate number of documents that have the term and are not in the
		// class N10
		Set<Integer> tempDocIdSetSecond = secondIndex.getDocumentId(term);
		Set<Integer> tempDocIdSetThird = thirdIndex.getDocumentId(term);
		//tempDocIdSet.addAll(thirdIndex.getDocumentId(term));
		double tempDocIdSet = tempDocIdSetSecond.size() + tempDocIdSetThird.size();
		return tempDocIdSet;
	}

	private static double docsWithoutTermInClass(NaiveInvertedIndex primaryIndex, String term) {
		// Calculate number of documents that do not have the term but are in
		// the class N01
		int tempDocsWithTerm = primaryIndex.getDocumentId(term).size();
		int tempTotalDocs = primaryIndex.getDocSize().size();
		// System.out.println("Total Number of Documents in class: " +
		// tempTotalDocs);
		int tempDocsWithoutTerm = tempTotalDocs - tempDocsWithTerm;
		return tempDocsWithoutTerm;
	}

	private static double docsWithoutTermNotInClass(NaiveInvertedIndex secondIndex, NaiveInvertedIndex thirdIndex,
			String term) {
		// Calculate number of documents that do not have the term and are not
		// in the class N00
		double tempTotalDocsSecondIndex = secondIndex.getDocSize().size();
		double tempTotalDocsThirdIndex = thirdIndex.getDocSize().size();
		double tempTotalDocsNotInClass = tempTotalDocsSecondIndex + tempTotalDocsThirdIndex;
		double tempTotalDocsWithTermNotInClass = docsWithTermNotInClass(secondIndex, thirdIndex, term);
		double tempTotalDocsWithoutTermNotInClass = tempTotalDocsNotInClass - tempTotalDocsWithTermNotInClass;
		// System.out.println("Total Number of Documents outside class: " +
		// tempTotalDocsNotInClass);
		return tempTotalDocsWithoutTermNotInClass;
	}

	private static BigDecimal calculateITC(NaiveInvertedIndex primaryIndex, NaiveInvertedIndex secondIndex,
			NaiveInvertedIndex thirdIndex, String term) {
		// Calculate Importance of Term in a Class
		double n00 = docsWithoutTermNotInClass(secondIndex, thirdIndex, term);
		double n11 = primaryIndex.getDocumentId(term).size();
		double n01 = docsWithoutTermInClass(primaryIndex, term);
		double n10 = docsWithTermNotInClass(secondIndex, thirdIndex, term);
		double n0x = n00 + n01;
		double n1x = n10 + n11;
		double nx0 = n00 + n10;
		double nx1 = n01 + n11;
		double n = primaryIndex.getDocSize().size() + secondIndex.getDocSize().size() + thirdIndex.getDocSize().size();
		BigDecimal tempExp11 = new BigDecimal(log(((n * n11 + 1) / (n1x * nx1 + 1)), 2)).setScale(9,
				RoundingMode.HALF_UP);
		BigDecimal tempExp10 = new BigDecimal(log(((n * n10 + 1) / (n1x * nx0 + 1)), 2)).setScale(9,
				RoundingMode.HALF_UP);
		BigDecimal tempExp01 = new BigDecimal(log(((n * n01 + 1) / (n0x * nx1 + 1)), 2)).setScale(9,
				RoundingMode.HALF_UP);
		BigDecimal tempExp00 = new BigDecimal(log(((n * n00 + 1) / (n0x * nx0 + 1)), 2)).setScale(9,
				RoundingMode.HALF_UP);
		BigDecimal tempClass11 = new BigDecimal(n11 / n).setScale(9, RoundingMode.HALF_UP);
		BigDecimal tempClass10 = new BigDecimal(n10 / n).setScale(9, RoundingMode.HALF_UP);
		BigDecimal tempClass01 = new BigDecimal(n01 / n).setScale(9, RoundingMode.HALF_UP);
		BigDecimal tempClass00 = new BigDecimal(n00 / n).setScale(9, RoundingMode.HALF_UP);
		BigDecimal finalExp11 = tempExp11.multiply(tempClass11).setScale(9, RoundingMode.HALF_UP);
		BigDecimal finalExp10 = tempExp10.multiply(tempClass10).setScale(9, RoundingMode.HALF_UP);
		BigDecimal finalExp01 = tempExp01.multiply(tempClass01).setScale(9, RoundingMode.HALF_UP);
		BigDecimal finalExp00 = tempExp00.multiply(tempClass00).setScale(9, RoundingMode.HALF_UP);
		BigDecimal itc = finalExp11.add(finalExp10).add(finalExp01).add(finalExp00).setScale(9, RoundingMode.HALF_UP);
		// System.out.println("ITC score for a "+ term +" in primaryIndex class:
		// " + itc);
		return itc;
	}

	private static double log(double number, double base) {
		return Math.log10(number) / Math.log10(base);
	}

	public static List<String> itcFeatureList(int pollItems, NaiveInvertedIndex indexHamilton,
		NaiveInvertedIndex indexMadison, NaiveInvertedIndex indexJay) throws IOException {
		PriorityQueue<Pair> queue = new PriorityQueue<Pair>();
		List<String> featureList = new ArrayList<String>();

		// Get Vocabulary list for each class
		String[] vocabHamilton = indexHamilton.getDictionary();
		String[] vocabMadison = indexMadison.getDictionary();
		String[] vocabJay = indexJay.getDictionary();

		// Calculating ITC values for Hamilton class and store it in
		// PriorityQueue
		//System.out.println(" Vocab size for Hamilton class: " + vocabHamilton.length);
		for (String term : vocabHamilton) {
			// System.out.println("Hamilton class: " + "Document Id: " +
			// indexHamilton.getDocumentId(term) + " Term: " + term);
			BigDecimal termITC = calculateITC(indexHamilton, indexMadison, indexJay, term);
			if (!queue.contains(termITC))
				queue.offer(new Pair(term, termITC));
		}

		// Calculating ITC values for Madison class and store it in
		// PriorityQueue
		//System.out.println(" Vocab size for Madison class: " + vocabMadison.length);

		for (String term : vocabMadison) {
			BigDecimal termITC = calculateITC(indexMadison, indexHamilton, indexJay, term);
			if (!queue.contains(termITC))
				queue.offer(new Pair(term, termITC));
		}

		// Calculating ITC values for Jay class and store it in PriorityQueue
		//System.out.println(" Vocab size for Jay class: " + vocabJay.length);
		for (String term : vocabJay) {
			// System.out.println("Working on Jay class: " + "Term: " + term);
			BigDecimal termITC = calculateITC(indexJay, indexMadison, indexHamilton, term);
			if (!queue.contains(termITC))
				queue.offer(new Pair(term, termITC));
		}
		//System.out.println("Queue size: " + queue.size());
		// Poll number of items specified by user into a featureList and return
		// it.
		while (pollItems != 0) {
			Pair pr = queue.poll();
			//System.out.println("{PQ Term: " + pr.term + "} {PQ ITC: " + pr.termItc + "}");
			if (!featureList.contains(pr.term))
				featureList.add(pr.term);
			pollItems--;
		}

		return featureList;
	}
}
