package KorKeywordFix.patches;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;

import java.util.ArrayList;

import static KorKeywordFix.Utils.Utils.getParent;
import static KorKeywordFix.Utils.Utils.tokenChar;

public class CardDescriptionPatch {
	@SpirePatch(clz = AbstractCard.class, method = "initializeDescription")
	public static class KORInitializeDescriptionPatch {
		static int nextIndex = 0;
		static String[] words;
		static boolean removeNext;

		@SpireInsertPatch(locator = PrepLocator.class)
		public static void Prep(AbstractCard __instance, String ___rawDescription) {
			nextIndex = 0;
			words = ___rawDescription.split(" ");
			removeNext = false;
		}

		@SpireInsertPatch(locator = TokenInitLocator.class)
		public static void PreInsert(AbstractCard __instance, @ByRef String[] ___word) {
			nextIndex++;
			if (removeNext) {
				___word[0] = "";
			}
		}

		@SpireInsertPatch(locator = KeywordMergeLocator.class)
		public static void MergeInsert(AbstractCard __instance, @ByRef String[] ___word, GlyphLayout ___gl, StringBuilder ___sbuilder2, boolean ___isKeyword) {
			if (removeNext) {
				___gl.width = 0;
				___sbuilder2.setLength(0);
				removeNext = false;
			} else if (nextIndex < words.length && (___isKeyword || ___word[0].startsWith("*"))) {
				String keyword = ___word[0];
				if (!___isKeyword) {
					keyword = ___word[0].substring(1);
				}

				if (keyword.equals(getParent(keyword)) && keyword.equals(getParent(keyword + words[nextIndex]))) {
					___gl.width += (new GlyphLayout(FontHelper.cardDescFont_N, words[nextIndex])).width;
					___word[0] += tokenChar + words[nextIndex];
					removeNext = true;
				}
			}
		}

		@SpireInsertPatch(locator = KeywordLocator.class)
		public static void Insert(AbstractCard __instance, @ByRef String[] ___word, String ___keywordTmp) {
			if (___keywordTmp != null && ___word[0].length() > ___keywordTmp.length() && ___word[0].startsWith(___keywordTmp)) {
				___word[0] = ___word[0].substring(0, ___keywordTmp.length()) + tokenChar + ___word[0].substring(___keywordTmp.length());
			}
		}
	}
	private static class PrepLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "clear");
			int[] all = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			return new int[]{all[0]};
		}
	}

	private static class TokenInitLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(StringBuilder.class, "setLength");
			int[] all = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			return new int[]{all[1]};
		}
	}

	private static class KeywordMergeLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
		{
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "DESC_BOX_WIDTH");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}

	private static class KeywordLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(StringBuilder.class, "append");
			int[] all = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			return new int[]{all[5]};
		}
	}

	@SpirePatch(clz = AbstractCard.class, method = "renderDescription")
	public static class KORRenderDescriptionPatch {
		@SpireInsertPatch(locator = RenderLocator.class)
		public static void Insert(AbstractCard __instance, SpriteBatch sb, @ByRef String[] ___tmp, @ByRef String[] ___punctuation) {
			int index = ___tmp[0].indexOf(tokenChar);
			if (index >= 0) {
				___punctuation[0] = ___tmp[0].substring(index + 1) + ___punctuation[0];
				___tmp[0] = ___tmp[0].substring(0, index);
			}
		}
	}

	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescription")
	public static class KORSingleRenderDescriptionPatch {
		@SpireInsertPatch(locator = RenderLocator.class)
		public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb, @ByRef String[] ___tmp, @ByRef String[] ___punctuation) {
			if (Settings.language == Settings.GameLanguage.KOR) {
				int index = ___tmp[0].indexOf(tokenChar);
				if (index >= 0) {
					___punctuation[0] = ___tmp[0].substring(index + 1) + ___punctuation[0];
					___tmp[0] = ___tmp[0].substring(0, index);
				}
			}
		}
	}

	private static class RenderLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(GlyphLayout.class, "setText");
			int[] all = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			return new int[]{all[0]};
		}
	}
}
