package xyz.amymialee.mialib.values;

import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.amymialee.mialib.MiaLib;

public class IntegerMValueType extends SliderMValueType<Integer> {
    private int min;
    private int max;

    @Override
    public MValueManager.MValue<Integer> fromJson(JsonObject json, MValueManager.MValue<Integer> mValue, Identifier id) {
        try {
            mValue.value = json.get(id.toString()).getAsInt();
        } catch (Exception e) {
            MiaLib.LOGGER.error("Failed to load config value " + id.toString() + " as integer, using existing value of " + mValue.value + " instead.", e);
        }
        return mValue;
    }

    @Override
    public JsonObject appendToJson(JsonObject json, MValueManager.MValue<Integer> mValue, Identifier id) {
        json.addProperty(id.toString(), mValue.value);
        return json;
    }

    @Override
    protected Text getWidgetText(MValueManager.MValue<Integer> mValue, double value) {
        return mValue.name.copy().append(": " + this.value);
    }

    @Override
    protected void onValueChanged(double value) {
        this.value = (int) (((this.max - this.min) * value) + this.min);
    }
}