package KorKeywordFix.patches;

import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.FixDescriptionWidthCustomDynamicVariable;
import basemod.patches.com.megacrit.cardcrawl.relics.AbstractRelic.MultiwordKeywords;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import javassist.CtBehavior;

import static KorKeywordFix.patches.CardDescriptionPatch.tokenChar;

public class BaseModPatch {
	@SpirePatch(cls = "basemod.patches.com.megacrit.cardcrawl.relics.AbstractRelic.MultiwordKeywords", method = "replaceMultiWordKeywords", optional = true)
	public static class KORBaseModPatch1 {
		@SpireInsertPatch(locator = BaseModPatchLocator.class)
		public static void Insert(String input, @ByRef String[] ___keyword, String ___trimmedKeyword) {
			String parent = GameDictionary.parentWord.get(___trimmedKeyword);
			if (parent != null && ___trimmedKeyword.length() > parent.length() && ___trimmedKeyword.startsWith(parent)) {
				___keyword[0] = ___keyword[0].substring(0, parent.length()) + tokenChar + ___keyword[0].substring(parent.length());
			}
		}
	}

	@SpirePatch(cls = "basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.FixDescriptionWidthCustomDynamicVariable", method = "Insert", optional = true)
	public static class KORBaseModPatch2 {
		@SpireInsertPatch(locator = BaseModPatchLocator.class)
		public static void Insert(AbstractCard __instance, GlyphLayout[] gl, String[] word) {
			String parent = GameDictionary.parentWord.get(word[0]);
			if (parent != null && word[0].length() > parent.length() && word[0].startsWith(parent)) {
				word[0] = word[0].substring(0, parent.length()) + tokenChar + word[0].substring(parent.length());
			}
		}
	}

	private static class BaseModPatchLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(String.class, "replaceFirst");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
