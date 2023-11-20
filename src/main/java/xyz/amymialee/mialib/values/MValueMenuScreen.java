package xyz.amymialee.mialib.values;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class MValueMenuScreen extends Screen {
    protected MValueMenuScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        if (this.client == null) return;
        var i = 0;
        for (var value : MValueManager.getValues().entrySet()) {
            var mValue = value.getValue();
            var buttonWidget = mValue.createWidget(this.client.options, this.width / 2 - 40, 80 + i * 60, 80);
            this.addDrawableChild(buttonWidget);
            i++;
        }
    }
}