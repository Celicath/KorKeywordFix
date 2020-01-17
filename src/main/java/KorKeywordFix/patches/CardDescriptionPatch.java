package KorKeywordFix.patches;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;

public class CardDescriptionPatch {
	public static char tokenChar = '\u2028';

	@SpirePatch(clz = AbstractCard.class, method = "initializeDescription")
	public static class KORInitializeDescriptionPatch {
		@SpireInsertPatch(locator = InitLocator.class)
		public static void Insert(AbstractCard __instance, @ByRef String[] ___word, String ___keywordTmp) {
			if (___keywordTmp != null && ___word[0].length() > ___keywordTmp.length() && ___word[0].startsWith(___keywordTmp)) {
				___word[0] = ___word[0].substring(0, ___keywordTmp.length()) + tokenChar + ___word[0].substring(___keywordTmp.length());
			}
		}
	}

	private static class InitLocator extends SpireInsertLocator {
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
