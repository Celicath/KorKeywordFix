package KorKeywordFix.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import javassist.CtBehavior;

import static KorKeywordFix.patches.CardDescriptionPatch.tokenChar;

public class TipRenderPatch {
	@SpirePatch(
			clz = FontHelper.class,
			method = "renderSmartText",
			paramtypez = {
					SpriteBatch.class,
					BitmapFont.class,
					String.class,
					float.class,
					float.class,
					float.class,
					float.class,
					Color.class
			})
	public static class KORTipRenderPatch {
		private static String remaining = "";
		private static float offset = 0;
		private static GlyphLayout layout = new GlyphLayout();
		private static int nextIndex = 0;
		private static String[] split = null;

		@SpirePrefixPatch
		public static void Prefix(SpriteBatch sb, BitmapFont font, String msg, float x, float y, float lineWidth, float lineSpacing, Color baseColor) {
			nextIndex = 0;
			split = msg.split(" ");
		}

		@SpireInsertPatch(locator = TipIndexCounterLocator.class)
		public static void IndexCounterInsert(SpriteBatch sb, BitmapFont font, String msg, float x, float y, float lineWidth, float lineSpacing, Color baseColor) {
			nextIndex++;
		}

		@SpireInsertPatch(locator = TipPreLocator.class)
		public static void PreInsert(SpriteBatch sb, BitmapFont font, String msg, float x, float y, float lineWidth, float lineSpacing, Color baseColor, @ByRef String[] ___word) {
			if (split != null) {
				int curIndex = nextIndex - 1;
				if (split.length > curIndex && !split[curIndex].startsWith("#")) {
					return;
				}
				if (split.length > nextIndex && split[nextIndex].startsWith(split[curIndex].substring(0, 2))) {
					return;
				}
			}

			String lowerWord = ___word[0].replace(',', ' ').replace('.', ' ').trim().toLowerCase();
			String parent = GameDictionary.parentWord.get(lowerWord);

			int tokenIndex = ___word[0].indexOf(tokenChar);
			if (tokenIndex != -1) {
				parent = lowerWord.substring(0, tokenIndex + 1);
			}

			if (parent != null && lowerWord.startsWith(parent) && lowerWord.length() > parent.length()) {
				remaining = ___word[0].substring(parent.length());
				___word[0] = ___word[0].substring(0, parent.length());
				layout.setText(font, ___word[0]);
				offset = layout.width;
			}
		}

		@SpireInsertPatch(locator = TipPostLocator1.class)
		public static void PostInsert1(SpriteBatch sb, BitmapFont font, String msg, float x, float y, float lineWidth, float lineSpacing, Color baseColor, float ___curHeight) {
			if (remaining != null && !remaining.equals("")) {
				font.setColor(baseColor);
				font.draw(sb, remaining, x + offset, y + ___curHeight);
				remaining = "";
				offset = 0;
			}
		}

		@SpireInsertPatch(locator = TipPostLocator2.class)
		public static void PostInsert2(SpriteBatch sb, BitmapFont font, String msg, float x, float y, float lineWidth, float lineSpacing, Color baseColor, float ___curWidth, float ___curHeight) {
			if (remaining != null && !remaining.equals("")) {
				font.setColor(baseColor);
				font.draw(sb, remaining, x + ___curWidth + offset, y + ___curHeight);
				remaining = "";
				offset = 0;
			}
		}
	}

	private static class TipIndexCounterLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(String.class, "equals");
			int[] all = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			return new int[]{all[0]};
		}
	}

	private static class TipPreLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(BitmapFont.class, "draw");
			int[] all = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			return new int[]{all[0], all[1]};
		}
	}

	private static class TipPostLocator1 extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(BitmapFont.class, "draw");
			int[] all = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			return new int[]{all[0] + 1};
		}
	}

	private static class TipPostLocator2 extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(BitmapFont.class, "draw");
			int[] all = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			return new int[]{all[1] + 1};
		}
	}
}
