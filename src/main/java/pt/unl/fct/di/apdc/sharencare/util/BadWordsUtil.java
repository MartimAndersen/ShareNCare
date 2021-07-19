package pt.unl.fct.di.apdc.sharencare.util;

import java.util.ArrayList;
import java.util.List;

public class BadWordsUtil {

	private List<String> swear = new ArrayList<String>();

	public BadWordsUtil() {
		swear.add("parvo");
		swear.add("parva");
		swear.add("idiota");
		swear.add("stupid");
	}

	public boolean hasBadWords(String comment) {
		String[] words = comment.split("\\s+");
		boolean hasBadWords = false;
		for(int l = 0; l < words.length; l++) {
			for (int i = 0; i < swear.size(); i++) {
				if (swear.get(i).equalsIgnoreCase(words[l])) {
					hasBadWords =  true;
					break;
				}
			}
		}
		return hasBadWords;

	}

}