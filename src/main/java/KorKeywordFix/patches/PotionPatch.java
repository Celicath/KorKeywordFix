package KorKeywordFix.patches;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.StancePotion;

import static KorKeywordFix.Utils.fixKorKeyword;

public class PotionPatch {
	@SpirePatch(
			clz = AbstractPotion.class,
			method = SpirePatch.CONSTRUCTOR,
			paramtypez = {
					String.class,
					String.class,
					AbstractPotion.PotionRarity.class,
					AbstractPotion.PotionSize.class,
					AbstractPotion.PotionEffect.class,
					Color.class,
					Color.class,
					Color.class
			}
	)
	public static class KORPotionConstructorPatch1 {
		@SpirePostfixPatch
		public static void Postfix(AbstractPotion __instance, String name, String id, AbstractPotion.PotionRarity rarity, AbstractPotion.PotionSize size, AbstractPotion.PotionEffect effect, Color liquidColor, Color hybridColor, Color spotsColor) {
			for (int i = 0; i < __instance.tips.size(); i++) {
				__instance.tips.get(i).body = fixKorKeyword(__instance.tips.get(i).body);
			}
		}
	}

	@SpirePatch(
			clz = AbstractPotion.class,
			method = SpirePatch.CONSTRUCTOR,
			paramtypez = {
					String.class,
					String.class,
					AbstractPotion.PotionRarity.class,
					AbstractPotion.PotionSize.class,
					AbstractPotion.PotionColor.class
			}
	)
	public static class KORPotionConstructorPatch2 {
		@SpirePostfixPatch
		public static void Postfix(AbstractPotion __instance, String name, String id, AbstractPotion.PotionRarity rarity, AbstractPotion.PotionSize size, AbstractPotion.PotionColor color) {
			for (int i = 0; i < __instance.tips.size(); i++) {
				__instance.tips.get(i).body = fixKorKeyword(__instance.tips.get(i).body);
			}
		}
	}

	@SpirePatch(clz = StancePotion.class, method = SpirePatch.CONSTRUCTOR)
	public static class KORStancePotionConstructorPatch {
		@SpirePostfixPatch
		public static void Postfix(StancePotion __instance) {
			for (int i = 0; i < __instance.tips.size(); i++) {
				__instance.tips.get(i).body = fixKorKeyword(__instance.tips.get(i).body);
			}
		}
	}
}
