package KorKeywordFix.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

import static KorKeywordFix.Utils.Utils.fixKorKeyword;

public class PowerPatch {
	@SpirePatch(clz = AbstractCreature.class, method = "renderPowerTips")
	public static class KORPowerTipCreaturePatch {
		@SpireInsertPatch(locator = RenderPowerLocator.class, localvars = {"tips"})
		public static void Insert(AbstractCreature __instance, SpriteBatch sb, ArrayList<PowerTip> tips) {
			for (PowerTip tip : tips) {
				tip.body = fixKorKeyword(tip.body);
			}
		}
	}

	@SpirePatch(clz = AbstractPlayer.class, method = "renderPowerTips")
	public static class KORPowerTipPlayerPatch {
		@SpireInsertPatch(locator = RenderPowerLocator.class, localvars = {"tips"})
		public static void Insert(AbstractPlayer __instance, SpriteBatch sb, ArrayList<PowerTip> tips) {
			for (PowerTip tip : tips) {
				tip.body = fixKorKeyword(tip.body);
			}
		}
	}

	@SpirePatch(clz = AbstractMonster.class, method = "renderTip")
	public static class KORPowerTipMonsterPatch {
		@SpireInsertPatch(locator = RenderPowerLocator.class, localvars = {"tips"})
		public static void Insert(AbstractMonster __instance, SpriteBatch sb, ArrayList<PowerTip> tips) {
			for (PowerTip tip : tips) {
				tip.body = fixKorKeyword(tip.body);
			}
		}
	}

	public static class RenderPowerLocator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty");

			return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
		}
	}
}
