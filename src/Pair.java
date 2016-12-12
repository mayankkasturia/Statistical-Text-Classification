import java.math.BigDecimal;

public class Pair  implements Comparable<Object> {
	    String term;
	    BigDecimal termItc;
	    
	    public Pair(String t, BigDecimal termItc){
	        term = t;
	        this.termItc = termItc;
	    }

	   @Override
        public int compareTo(Object o) {
	    	Pair pr = (Pair)o;
            if(termItc.compareTo(pr.termItc) == 1)
                return -1;
            else
                return 1;
        }

	}

