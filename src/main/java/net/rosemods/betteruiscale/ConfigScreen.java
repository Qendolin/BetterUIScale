package net.rosemods.betteruiscale;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ConfigScreen
    extends Screen {
    protected final Screen parent;
    protected final Config config;
    private OptionListWidget list;

    public ConfigScreen(Screen parent, Config config) {
        super(Text.translatable("betteruiscale.options.title"));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        list = new OptionListWidget(client, width, height, 32, height - 32, 25);
        list.addAll(config.getOptions().values().toArray(SimpleOption[]::new));
        addSelectableChild(list);
        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
            config.save(Main.configPath());
            client.setScreen(parent);
        }).dimensions(width / 2 - 100, height - 27, 200, 20).build());
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
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        renderBackground(context);
        list.render(context, mouseX, mouseY, tickDelta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, tickDelta);
    }
}

