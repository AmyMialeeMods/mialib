package xyz.amymialee.mialib.interfaces;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MText {
	Text mialib$withColor(int color);
	Text mialib$withItalics(boolean italics);
	Text mialib$withBold(boolean bold);
	Text mialib$withUnderline(boolean underline);
	Text mialib$withStrikethrough(boolean strikethrough);
	Text mialib$withObfuscated(boolean obfuscated);
	Text mialib$withInsertion(String insertion);

	static Text withColor(@NotNull Text text, int color) {
		return repack(text.getWithStyle(text.getStyle().withColor(color)));
	}

	static Text withItalics(@NotNull Text text, boolean italics) {
		return repack(text.getWithStyle(text.getStyle().withItalic(italics)));
	}

	static Text withBold(@NotNull Text text, boolean bold) {
		return repack(text.getWithStyle(text.getStyle().withBold(bold)));
	}

	static Text withUnderline(@NotNull Text text, boolean underline) {
		return repack(text.getWithStyle(text.getStyle().withUnderline(underline)));
	}

	static Text withStrikethrough(@NotNull Text text, boolean strikethrough) {
		return repack(text.getWithStyle(text.getStyle().withStrikethrough(strikethrough)));
	}

	static Text withObfuscated(@NotNull Text text, boolean obfuscated) {
		return repack(text.getWithStyle(text.getStyle().withObfuscated(obfuscated)));
	}

	static Text withInsertion(@NotNull Text text, String insertion) {
		return repack(text.getWithStyle(text.getStyle().withInsertion(insertion)));
	}

	static Text repack(@NotNull List<Text> textList) {
		if (textList.isEmpty()) {
			return Text.literal("");
		} else {
			var first = textList.get(0);
			for (var i = 1; i < textList.size(); i++) {
				first = first.copy().append(textList.get(i));
			}
			return first;
		}
	}
}