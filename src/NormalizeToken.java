import java.util.ArrayList;

/**
 *
 * @author manikhanuja
 */

@SuppressWarnings("rawtypes")
public class NormalizeToken {
    public static ArrayList normalizeToken(String token) {
        //convert the token to lower case
    	ArrayList<String> term = new ArrayList<>();
    	if(token!=null){
        token = token.toLowerCase();
        // remove all non-alphanumeric characters from beginning
        token = token.replaceAll("^[^a-zA-Z0-9\\s]*", "");
        // remove all non-alphanumeric characters from end
        token = token.replaceAll("[^a-zA-Z0-9\\s]*$", "");
        // remove all apostrophes (single quotes)
        token = token.replaceAll("[']", "");
        if (token.contains("-")) {     
            String[] str=token.split("-");
            for(int i=0;i<str.length;i++){
            String p=str[i];
            term.add(p);
            }
            token = token.replaceAll("[-]", "");
        }
        
            term.add(token);
    	}
        return term;
    }
}
