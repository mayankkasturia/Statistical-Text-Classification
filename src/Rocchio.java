 
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mayankkasturia
 */
public class Rocchio {

    public static void rocchioClassification(NaiveInvertedIndex indexAll) throws IOException {
        HashMap<Integer, ArrayList<Double>> docIndex = new HashMap<Integer, ArrayList<Double>>();
        ArrayList<Integer> docIdHamilton = new ArrayList<Integer>();
        ArrayList<Integer> docIdMadison = new ArrayList<Integer>();
        ArrayList<Integer> docIdHorM = new ArrayList<Integer>();
        ArrayList<Double> centroidHamilton = new ArrayList<Double>();
        ArrayList<Double> centroidMadison = new ArrayList<Double>();
        ArrayList<Double> check = new ArrayList<Double>();
        Integer sizeOfVector = 0;
        String a[] = indexAll.getDictionary();
        for (int j = 0; j < a.length; j++) {
            for (int i = 0; i < indexAll.getDocSize().size(); i++) {

                //System.out.println(a[j]+":"+indexAll.getDocumentId(a[j]+":"+indexAll.getPostings(a[j])));
                HashMap<Integer, List<Integer>> finalList = (indexAll.getPostings(a[j]));
                //System.out.println(a[j] + " : " + finalList);
                //System.out.println(" : "+finalList.values().size());
                Iterator itr = finalList.entrySet().iterator();
                HashMap<Integer, Double> docFre = new HashMap<Integer, Double>();
                int finalDocumentNumber = indexAll.getDocSize().size();
                double finalTermFrequency = 0.0;
                while (itr.hasNext()) {
                    Map.Entry entry = (Map.Entry) itr.next();
                    int documentNumber = Integer.parseInt(entry.getKey().toString());
                    double termFrequency = ((List) entry.getValue()).size();
                    docFre.put(documentNumber, termFrequency);
                    //System.out.println(documentNumber + " I am document number ");
                    //System.out.println(termFrequency+" Term frequency");
                }
                Iterator docfreItr = docFre.entrySet().iterator();
                while (docfreItr.hasNext()) {
                    Map.Entry entry = (Map.Entry) docfreItr.next();
                    if (Integer.parseInt(entry.getKey().toString()) == i) {
                        finalDocumentNumber = Integer.parseInt(entry.getKey().toString());
                        finalTermFrequency = Double.parseDouble(entry.getValue().toString());
                        //System.out.println("I am in complex loop");
//                        System.out.println(finalDocumentNumber);
//                        System.out.println(finalTermFrequency);
                    }
//                    System.out.println(documentNumber + " I am document number ");
//                    System.out.println(termFrequency+" Term frequency");
                }
                if (!docIndex.containsKey(i)) {
                    ArrayList<Double> docVocab = new ArrayList<Double>();
                    if (i == finalDocumentNumber) {
                        double freqValue = 1 + Math.log(finalTermFrequency);
                        docVocab.add(freqValue);
                        //System.out.println("Adding "+freqValue+" to "+i+ " at"+j+"th position");
                        //System.out.println(freqValue);
                        docIndex.put(finalDocumentNumber, docVocab);
                    } else {
                        docVocab.add(finalTermFrequency);
                        //System.out.println("Adding 0.0 to "+i+ " at"+j+"th position");
                        //System.out.println(finalTermFrequency);
                        docIndex.put(i, docVocab);
                    }

                } else if (docIndex.containsKey(i)) {
                    if (i == finalDocumentNumber) {
                        double freqValue = 1 + Math.log(finalTermFrequency);
                        docIndex.get(i).add(freqValue);
                        //System.out.println("Adding "+freqValue+" to "+i+ " at"+j+"th position");
                    } else {
                        docIndex.get(i).add(finalTermFrequency);
                        // System.out.println("Adding 0.0 to "+i+ " at"+j+"th position");
                    }

                }
            }

        }

        for (int l = 0; l < indexAll.getDocSize().size(); l++) {
            double lengthNorm = 0.0;
            for (int m = 0; m < a.length; m++) {
                lengthNorm = lengthNorm + Math.pow(docIndex.get(l).get(m), 2);
            }
            double lengthNormalize = Math.sqrt(lengthNorm);
            //System.out.println(lengthNormalize);
            for (int m = 0; m < a.length; m++) {
                double temp = docIndex.get(l).get(m) / lengthNormalize;
                docIndex.get(l).remove(m);
                docIndex.get(l).add(m, temp);

            }

        }
        Iterator itr = docIndex.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
//            System.out.println("Documnet number : " + entry.getKey());
//            System.out.println("Size of arraylist: " + entry.getValue());
            sizeOfVector = ((List) entry.getValue()).size();
//            System.out.println("Size of arraylist: " + sizeOfVector);
        }
//        int totalDocAll = indexAll.getDocSize().size();
//        System.out.println("Total number of documents: " + totalDocAll);

//        int totalTermsAll = indexAll.getTermCount();
//        System.out.println("Total number of terms: " + totalTermsAll);
        HashMap<Integer, String> fileNamesHamilton = getFileNames("hamilton");
        HashMap<Integer, String> fileNamesMadison = getFileNames("madison");
        HashMap<Integer, String> fileNamesAll = getFileNames("all");
        HashMap<Integer, String> fileNamesHorM = getFileNames("HAMILTON OR MADISON");
        double j = 0.0;
        double accuracy = 0.0;
        Iterator itrAll = fileNamesAll.entrySet().iterator();
        while (itrAll.hasNext()) {
            Map.Entry entryAll = (Map.Entry) itrAll.next();
            Iterator itrHamilton = fileNamesHamilton.entrySet().iterator();
            while (itrHamilton.hasNext()) {
                Map.Entry entryHamilton = (Map.Entry) itrHamilton.next();
                if (entryAll.getValue().equals(entryHamilton.getValue())) {
                    docIdHamilton.add(Integer.parseInt(entryAll.getKey().toString()));
                }
            }
        }

        Iterator itrAll2 = fileNamesAll.entrySet().iterator();
        while (itrAll2.hasNext()) {
            Map.Entry entryAll = (Map.Entry) itrAll2.next();
            Iterator itrMadison = fileNamesMadison.entrySet().iterator();
            while (itrMadison.hasNext()) {
                Map.Entry entryMadison = (Map.Entry) itrMadison.next();
                if (entryAll.getValue().equals(entryMadison.getValue())) {
                    docIdMadison.add(Integer.parseInt(entryAll.getKey().toString()));
                    //System.out.println(entryAll.getKey()+" and value is "+entryAll.getValue());
                }
            }
        }
        Iterator itrAll3 = fileNamesAll.entrySet().iterator();
        while (itrAll3.hasNext()) {
            Map.Entry entryAll = (Map.Entry) itrAll3.next();
            Iterator itrHorM = fileNamesHorM.entrySet().iterator();
            while (itrHorM.hasNext()) {
                Map.Entry entryHorM = (Map.Entry) itrHorM.next();
                if (entryAll.getValue().equals(entryHorM.getValue())) {
                    docIdHorM.add(Integer.parseInt(entryAll.getKey().toString()));
                    //System.out.println(entryAll.getKey()+" and value is "+entryAll.getValue());
                }
            }
        }
        if (!docIdHamilton.isEmpty()) {

            for (int g = 0; g < sizeOfVector; g++) {
                double sum = 0;
                for (Integer temp : docIdHamilton) {
                    sum = sum + docIndex.get(temp).get(g);
                }

                centroidHamilton.add(sum / docIdHamilton.size());
            }
        }
        if (!docIdMadison.isEmpty()) {

            for (int g = 0; g < sizeOfVector; g++) {
                double sum = 0;
                for (Integer temp : docIdMadison) {
                    sum = sum + docIndex.get(temp).get(g);
                }

                centroidMadison.add(sum / docIdMadison.size());
            }
        }
//        System.out.println("Hamilton"+centroidHamilton);
//        System.out.println("Madison"+centroidMadison);
        for (Integer temp : docIdHorM) {
            ArrayList<Double> tempCheckH = new ArrayList<Double>();
            ArrayList<Double> tempCheckM = new ArrayList<Double>();
            double sumH = 0;
            double sumM = 0;
            check = docIndex.get(temp);
           
            for (int h = 0; h < sizeOfVector; h++) {
                tempCheckH.add(Math.pow(centroidHamilton.get(h) - check.get(h), 2));
                tempCheckM.add(Math.pow(centroidMadison.get(h) - check.get(h), 2));
            }
            for (Double tempH : tempCheckH) {
                sumH = sumH + tempH;
            }
            sumH = Math.sqrt(sumH);
            for (Double tempM : tempCheckM) {
                sumM = sumM + tempM;
            }
            sumM = Math.sqrt(sumM);
            if (sumH > sumM) {
                System.out.println("Class for predicted doc - " + fileNamesAll.get(temp) + " : Madison");
                //System.out.println("Madison"+sumM);
                //System.out.println("Hamilton"+sumH);
//                    System.out.println(temp);
                j = j+1;
            }
            if (sumH < sumM) {
                System.out.println("Class for predicted doc - " + fileNamesAll.get(temp)+ " : Hamilton");
//                     System.out.println(temp);
               // System.out.println("Madison"+sumM);
               // System.out.println("Hamilton"+sumH);
            }

        }
        accuracy = j/(double)fileNamesHorM.size();
        System.out.println("Accuracy of Rocchio Classification Algorithm: " + Math.round(accuracy*100) + "%");
        
        
        
        System.out.println("c) KNN");
        for (Integer temp : docIdHorM) {
            System.out.println("Name of File: "+fileNamesAll.get(temp));
            Map<String,Double> kNN=new HashMap<String,Double>();
            double sumH = 0;
            double sumM = 0;
            check = docIndex.get(temp);
            int i=0;
           for(Integer tempHamilton: docIdHamilton){
               
               String string="H"+i;
            ArrayList<Double> tempCheckH = new ArrayList<Double>();
               for (int h = 0; h < sizeOfVector; h++) {
                tempCheckH.add(Math.pow(docIndex.get(tempHamilton).get(h) - check.get(h), 2));
            }
           for (Double tempH : tempCheckH) {
                sumH = sumH + tempH;
            }
            sumH = Math.sqrt(sumH);
            kNN.put(string, sumH);
            i++;
           }
            for(Integer tempMadison: docIdMadison){
                String string="M"+i;
            ArrayList<Double> tempCheckM = new ArrayList<Double>();
               for (int h = 0; h < sizeOfVector; h++) {
                tempCheckM.add(Math.pow(docIndex.get(tempMadison).get(h) - check.get(h), 2));
            }
           for (Double tempM : tempCheckM) {
                sumM = sumM + tempM;
            }
            sumM = Math.sqrt(sumM);
            kNN.put(string, sumM);
            i++;
           }
//             Map<String, Double> sortedMap = sortByValue(kNN);
//        printMap(sortedMap);

               int n = 3;
        List<Entry<String, Double>> greatest = findGreatest(kNN, 3);
        System.out.println("Top "+n+" entries:");
        for (Entry<String, Double> entry : greatest)
        {
            ArrayList<String> s = new ArrayList<String>();
//            System.out.println(entry);
            if(entry.toString().startsWith("H")){
                s.add("Hamilton");
            System.out.println("Hamilton");
            }
            if(entry.toString().startsWith("M")){
                s.add("Madison");
            System.out.println("Madison");
            }
//            for(String d:s){
//            
//            }
        }

              } 
  
        
    }//end of rocchioClassification
   

    private static <K, V extends Comparable<? super V>> List<Entry<K, V>> 
        findGreatest(Map<K, V> map, int n)
    {
        Comparator<? super Entry<K, V>> comparator = 
            new Comparator<Entry<K, V>>()
        {
            @Override
            public int compare(Entry<K, V> e0, Entry<K, V> e1)
            {
                V v0 = e0.getValue();
                V v1 = e1.getValue();
                return v0.compareTo(v1);
            }
        };
        PriorityQueue<Entry<K, V>> highest = 
            new PriorityQueue<Entry<K,V>>(n, comparator);
        for (Entry<K, V> entry : map.entrySet())
        {
            highest.offer(entry);
           
        }

        List<Entry<K, V>> result = new ArrayList<Map.Entry<K,V>>();
        for(int j=0;j<n;j++){
            result.add(highest.poll());
            }
        return result;
    }
        

        
        
        
    public static HashMap<Integer, String> getFileNames(String corpusName) throws IOException {
        final HashMap<Integer, String> fileNames = new HashMap<Integer, String>();
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
                    fileNames.put(mDocumentID, file.getFileName().toString());
                }
                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult visitFileFailed(Path file, IOException e) {

                return FileVisitResult.CONTINUE;
            }
        });
        return fileNames;
    }

}// end of class
