package xyz.amymialee.mialib.values.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import xyz.amymialee.mialib.values.MValueManager;

import java.util.ArrayList;
import java.util.List;

public class MValueMenuScreen extends Screen {
    private final List<Element> mValueChildren = new ArrayList<>();
    private String namespace;
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
        var namespaces = MValueManager.getNamespaces().toArray(new String[0]);
        if (namespaces.length == 0) return;
        this.loadNamespace(namespaces[0]);
    }

    public void loadNamespace(String namespace) {
        if (this.client == null) return;
        this.namespace = namespace;
        for (var element : this.mValueChildren) this.remove(element);
        this.mValueChildren.clear();
        var i = 0;
        for (var value : MValueManager.getValuesByNamespace(namespace)) {
            var mValue = value.getValue();
            var buttonWidget = mValue.createWidget(this.x - 40, this.y - 80 + i * 30, 120);
            this.mValueChildren.add(buttonWidget);
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
        context.drawText(this.textRenderer, this.title, this.x - this.textRenderer.getWidth(this.namespace), this.y - 100, 4210752, false);
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