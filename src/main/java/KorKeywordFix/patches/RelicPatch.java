package KorKeywordFix.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

import static KorKeywordFix.Utils.fixKorKeyword;

public class RelicPatch {
	@SpirePatch(clz = AbstractRelic.class, method = "initializeTips")
	public static class KORRelicTipPatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractRelic __instance) {
			for (int i = 0; i < __instance.tips.size(); i++) {
				__instance.tips.get(i).body = fixKorKeyword(__instance.tips.get(i).body);
			}
		}
	}

	@SpirePatch(clz = CharacterOption.class, method = "renderRelics")
	public static class KORCharacterSelectRelicDescriptionPatch {
		@SpireInsertPatch(locator = CharacterOptionLocator.class, localvars = {"relicString"})
		public static void Insert(CharacterOption __instance, SpriteBatch sb, @ByRef String[] relicString) {
			relicString[0] = fixKorKeyword(relicString[0]);
		}

		public static class CharacterOptionLocator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(FontHelper.class, "renderSmartText");

				return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[1]};
			}
		}
	}

	@SpirePatch(clz = SingleRelicViewPopup.class, method = "renderDescription")
	public static class KORSingleRelicViewPopupPatch {
		@SpirePrefixPatch
		public static void Prefix(SingleRelicViewPopup __instance, SpriteBatch sb) {
			TipRenderPatch.activated = true;
		}

		@SpirePostfixPatch
		public static void Postfix(SingleRelicViewPopup __instance, SpriteBatch sb) {
			TipRenderPatch.activated = false;
		}
	}
}
