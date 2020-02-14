package KorKeywordFix.Utils;

import com.megacrit.cardcrawl.helpers.GameDictionary;

public class Utils {
	public static char tokenChar = '\u2028';

	public static String getParent(String keyword) {
		String parent = GameDictionary.parentWord.get(keyword);
		if (parent == null) {
			parent = AdditionalKeyword.parentWord.get(keyword);
		}
		return parent;
	}

	public static String fixKorKeyword(String input) {
		StringBuilder builder = new StringBuilder();
		String[] tokens = input.split(" ");
		for (int i = 0; i < tokens.length; i++) {
			String word = tokens[i];
			String thisWord = word;

			if (word.length() >= 2 && !word.contains(":") && word.charAt(0) == '#') {
				String keyword = word.substring(2);
				String lowerWord = keyword.replace(',', ' ').replace('.', ' ').trim().toLowerCase();
				String parent = getParent(lowerWord);
				if (parent != null && lowerWord.startsWith(parent)) {
					if (lowerWord.length() > parent.length()) {
						thisWord = word.substring(0, 2) + keyword.substring(0, parent.length()) + tokenChar + keyword.substring(parent.length());
					} else if (lowerWord.equals(parent) && i < tokens.length - 1) {
						if (lowerWord.equals(getParent(lowerWord + tokens[i + 1]))) {
							thisWord = word + tokenChar + tokens[i + 1];
							i++;
						}
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
