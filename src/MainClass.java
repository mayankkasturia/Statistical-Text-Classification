import java.io.IOException;

public class MainClass {
	public static void main(String[] args) throws IOException {
		// Declare three indexes for each class
		NaiveInvertedIndex indexHamilton = new NaiveInvertedIndex();
		NaiveInvertedIndex indexMadison = new NaiveInvertedIndex();
		NaiveInvertedIndex indexJay = new NaiveInvertedIndex();
        NaiveInvertedIndex indexAll = new NaiveInvertedIndex();
                
		// Specify the folder for the class
		String corpusHamilton = "hamilton";
		String corpusJay = "jay";
		String corpusMadison = "madison";
        String corpusAll="all";

		// Build index for each class
		indexHamilton = SimpleEngine.buildIndex(corpusHamilton);
		indexMadison = SimpleEngine.buildIndex(corpusMadison);
		indexJay = SimpleEngine.buildIndex(corpusJay);		
		indexAll=SimpleEngine.buildIndex(corpusAll);
          
		System.out.println("Classification of Unknow Federalist Papers");
		System.out.println("a) Naive Bayes Classification");
		NaiveBayesClassification.NaivesBayesClassifier(indexHamilton,indexMadison, indexJay);
		System.out.println("\n");
        
		System.out.println("b) Rocchio Classification");     
        Rocchio.rocchioClassification(indexAll);
		
	}
}
	
	
