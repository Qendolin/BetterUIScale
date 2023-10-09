package net.rosemods.betteruiscale;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ConfigScreen
    extends Screen {
    protected final Screen parent;
    protected final Config config;
    private ButtonListWidget list;

    public ConfigScreen(Screen parent, Config config) {
        super(Text.translatable("betteruiscale.options.title"));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        list = new ButtonListWidget(client, width, height, 32, height - 32, 25);
        list.addAll(config.getOptions().values().toArray(SimpleOption[]::new));
        addSelectableChild(list);
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, button -> {
            config.save(Main.configPath());
            client.setScreen(parent);
        }));
    }

    @Override
    public void removed() {
        config.save(Main.configPath());
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
        renderBackground(stack);
        list.render(stack, mouseX, mouseY, tickDelta);
        DrawableHelper.drawCenteredTextWithShadow(stack, textRenderer, title.asOrderedText(), width / 2, 20, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, tickDelta);
    }
}

