package KorKeywordFix;

import com.megacrit.cardcrawl.helpers.GameDictionary;

public class Utils {
	public static char tokenChar = '\u2028';

	public static String fixKorKeyword(String input) {
		StringBuilder builder = new StringBuilder();
		for (String word : input.split(" ")) {
			String thisWord = word;

			if (word.length() >= 2 && !word.contains(":") && word.charAt(0) == '#') {
				String keyword = word.substring(2);
				String lowerWord = keyword.replace(',', ' ').replace('.', ' ').trim().toLowerCase();
				if (GameDictionary.keywords.containsKey(lowerWord)) {
					String parent = GameDictionary.parentWord.get(lowerWord);

					if (parent != null && lowerWord.startsWith(parent) && lowerWord.length() > parent.length()) {
						thisWord = word.substring(0, 2) + keyword.substring(0, parent.length()) + tokenChar + keyword.substring(parent.length());
					}
				}
			}
			if (builder.length() != 0) {
				builder.append(' ');
			}
			builder.append(thisWord);
		}
		return builder.toString();
	}

}
