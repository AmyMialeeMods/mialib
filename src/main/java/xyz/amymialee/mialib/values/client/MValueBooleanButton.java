package xyz.amymialee.mialib.values.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.values.MValue;

import java.util.List;

@Environment(EnvType.CLIENT)
public class MValueBooleanButton extends CyclingButtonWidget<Boolean> {
    private final MValue<Boolean> mValue;

    public MValueBooleanButton(@NotNull MValue<Boolean> mValue, int x, int y, int width) {
        super(x, y, width, 20, Text.empty(), Text.empty(), mValue.getValue() ? 1 : 0, mValue.getValue(), Values.of(List.of(false, true)), (b) -> mValue.getValueTextFactory().apply(mValue, b), (b) -> Text.empty(), (v, c) -> mValue.setValue(c), (v) -> Tooltip.of(mValue.getTooltipFactory().apply(mValue, v)), true);
        this.mValue = mValue;
        this.setMessage(mValue.getValueTextFactory().apply(mValue, this.getValue()));
    }

    @Override
    public void setValue(Boolean value) {
        super.setValue(value);
        this.mValue.setValue(value);
        this.mValue.sendValue();
    }
}