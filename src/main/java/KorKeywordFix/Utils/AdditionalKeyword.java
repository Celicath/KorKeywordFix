package KorKeywordFix.Utils;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;

import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public class AdditionalKeyword {

	public String PARENT;
	public String[] NAMES;

	public AdditionalKeyword() {
	}

	public static final TreeMap<String, String> parentWord;

	static {
		parentWord = new TreeMap<>();

		Gson gson = new Gson();

		try {
			String json = Gdx.files.internal("KorKeyword/localization/" + Settings.language.name().toLowerCase() + "/keywords.json").readString(
					String.valueOf(StandardCharsets.UTF_8));
			AdditionalKeyword[] keywords = gson.fromJson(json, AdditionalKeyword[].class);
			for (AdditionalKeyword keyword : keywords) {
				for (String name : keyword.NAMES) {
					parentWord.put(name, keyword.PARENT);
				}
				parentWord.put(keyword.PARENT, keyword.PARENT);
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
}
