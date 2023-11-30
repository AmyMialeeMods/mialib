package xyz.amymialee.mialib.mixin;

import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.interfaces.MText;

@Mixin(Text.class)
public class TextMixin implements MText {
    @Override
    public Text mialib$withColor(int color) {
        return MText.withColor((Text) this, color);
    }

    @Override
    public Text mialib$withItalics(boolean italics) {
        return MText.withItalics((Text) this, italics);
    }

    @Override
    public Text mialib$withBold(boolean bold) {
        return MText.withBold((Text) this, bold);
    }

    @Override
    public Text mialib$withUnderline(boolean underline) {
        return MText.withUnderline((Text) this, underline);
    }

    @Override
    public Text mialib$withStrikethrough(boolean strikethrough) {
        return MText.withStrikethrough((Text) this, strikethrough);
    }

    @Override
    public Text mialib$withObfuscated(boolean obfuscated) {
        return MText.withObfuscated((Text) this, obfuscated);
    }

    @Override
    public Text mialib$withInsertion(String insertion) {
        return MText.withInsertion((Text) this, insertion);
    }
}