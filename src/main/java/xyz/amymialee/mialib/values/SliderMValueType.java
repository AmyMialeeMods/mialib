package xyz.amymialee.mialib.values;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public abstract class SliderMValueType<T> implements MValueManager.MValueType<T> {
    protected T value;
    protected double sliderValue = 0;

    @Override
    public ClickableWidget createWidget(MValueManager.MValue<T> mValue, int x, int y, int width, int height) {
        return new SliderWidget(x, y, width, height, null, SliderMValueType.this.sliderValue) {
            @Override
            protected void updateMessage() {
                this.setMessage(SliderMValueType.this.getWidgetText(mValue, this.value));
            }

            @Override
            protected void applyValue() {
                SliderMValueType.this.onValueChanged(this.value);
            }
        };
    }

    protected abstract Text getWidgetText(MValueManager.MValue<T> mValue, double value);
    protected abstract void onValueChanged(double value);

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public double getSliderValue() {
        return this.sliderValue;
    }

    public void setSliderValue(double sliderValue) {
        this.sliderValue = sliderValue;
    }
}