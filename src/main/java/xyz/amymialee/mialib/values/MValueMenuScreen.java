package xyz.amymialee.mialib.values;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class MValueMenuScreen extends Screen {
    private int x;
    private int y;

    public MValueMenuScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        this.x = (this.width / 2);
        this.y = (this.height / 2);
        if (this.client == null) return;
        var i = 0;
        for (var value : MValueManager.getValues().entrySet()) {
            var mValue = value.getValue();
            var buttonWidget = mValue.createWidget(this.client.options, this.x - 40, 80 + i * 60, 120);
            this.addDrawableChild(buttonWidget);
            i++;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.setShaderColor(1, 1, 1, 0.5f);
        context.fillGradient(0, 0, this.width, this.height, 0x00000000, 0x00000000);
        context.setShaderColor(1, 1, 1, 1);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}