package xyz.amymialee.mialib.util;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MText {
	static Text withColor(@NotNull Text text, int color) {
		return repack(text.getWithStyle(text.getStyle().withColor(color)));
	}

	static Text withItalics(@NotNull Text text, boolean italics) {
		return repack(text.getWithStyle(text.getStyle().withItalic(italics)));
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