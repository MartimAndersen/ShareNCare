package pt.unl.fct.di.apdc.sharencare.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BadWordsUtil {

	private List<String> swear = new ArrayList<String>();

	public BadWordsUtil() {
		swear.add("parvo");
		swear.add("parva");
		swear.add("idiota");
		swear.add("stupid");
	}

	public boolean hasBadWords(String comment) {
		boolean res = false;
		for (String badWord : swear) {
			if(comment.toLowerCase().contains(badWord.toLowerCase())){
				res = true;
			}
		}
		return res;
	}

}