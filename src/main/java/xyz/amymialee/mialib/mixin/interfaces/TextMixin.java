package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.util.interfaces.MText;

@SuppressWarnings("UnnecessarilyQualifiedStaticUsage")
@Mixin(Text.class)
public interface TextMixin extends MText {
    @Override
    default Text mialib$withItalics(boolean italics) {
        return MText.withItalics((Text) this, italics);
    }

    @Override
    default Text mialib$withBold(boolean bold) {
        return MText.withBold((Text) this, bold);
    }

    @Override
    default Text mialib$withUnderline(boolean underline) {
        return MText.withUnderline((Text) this, underline);
    }

    @Override
    default Text mialib$withStrikethrough(boolean strikethrough) {
        return MText.withStrikethrough((Text) this, strikethrough);
    }

    @Override
    default Text mialib$withObfuscated(boolean obfuscated) {
        return MText.withObfuscated((Text) this, obfuscated);
    }

    @Override
    default Text mialib$withInsertion(String insertion) {
        return MText.withInsertion((Text) this, insertion);
    }
}