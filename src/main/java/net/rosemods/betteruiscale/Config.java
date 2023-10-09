package net.rosemods.betteruiscale;

import com.google.gson.*;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();

    public static final float FONT_SMOOTHING_SCALE = 4f;

    private final SimpleOption<Integer> fontSmoothing = new SimpleOption<>("betteruiscale.options.font_smoothing",
        SimpleOption.emptyTooltip(),
        (optionText, value) -> getPercentValueText(optionText, value / FONT_SMOOTHING_SCALE),
        new SimpleOption.ValidatingIntSliderCallbacks(0, (int)(2*FONT_SMOOTHING_SCALE)),
        (int) FONT_SMOOTHING_SCALE,
        value -> {
            MinecraftClient client = MinecraftClient.getInstance();
            client.execute(() -> {
                ShaderProgram textShader = GameRenderer.getRenderTypeTextProgram();
                if(textShader != null) {
                    textShader.getUniformOrDefault("betteruiscale_smoothness").set(value.floatValue() / FONT_SMOOTHING_SCALE);
                }
            });
        });


    public SimpleOption<Integer> fontSmoothing() {
        return fontSmoothing;
    }

    public Map<String, SimpleOption<?>> getOptions() {
        Map<String, SimpleOption<?>> map = new HashMap<>();
        map.put("fontSmoothing", fontSmoothing());
        return map;
    }

    private static Text getPercentValueText(Text prefix, double value) {
        return Text.translatable("options.percent_value", prefix, (int)(value * 100.0));
    }

    public void save(Path path) {
        try {
            JsonObject root = new JsonObject();
            serialize(root);
            String jsonString = GSON.toJson(root);
            Files.writeString(path, jsonString, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            Main.LOGGER.error("Cannot write config!", e);
        }
        Main.LOGGER.info("Updated config '{}'.", path);
    }

    public static Config load(Path path) {
        if(!Files.exists(path)) return new Config();

        Config config = new Config();
        try (
            FileReader fileReader = new FileReader(path.toFile());
        ) {
            JsonElement root = JsonParser.parseReader(fileReader);
            config.deserialize(root);
            Main.LOGGER.info("Loaded config '{}'.", path);
            return config;
        } catch (Exception e) {
            Main.LOGGER.error("Failed load config '{}'!", path, e);
        }

        return config;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void deserialize(JsonElement jsonElement) throws JsonParseException {
        Map<String, JsonElement> jsonEntries = jsonElement.getAsJsonObject().asMap();
        for (Map.Entry<String, SimpleOption<?>> entry : getOptions().entrySet()) {
            SimpleOption option = entry.getValue();
            String key = entry.getKey();
            JsonElement value = jsonEntries.getOrDefault(key, null);
            if(value == null) continue;
            DataResult<?> dataResult = option.getCodec().parse(JsonOps.INSTANCE, value);
            dataResult.error().ifPresent(partialResult -> Main.LOGGER.error("Error parsing option value " + value + " for option " + option + ": " + partialResult.message()));
            dataResult.result().ifPresent(option::setValue);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void serialize(JsonObject root) {
        for (Map.Entry<String, SimpleOption<?>> entry : getOptions().entrySet()) {
            SimpleOption option = entry.getValue();
            String key = entry.getKey();
            DataResult<JsonElement> dataResult = option.getCodec().encodeStart(JsonOps.INSTANCE, option.getValue());
            dataResult.error().ifPresent(partialResult -> Main.LOGGER.error("Error saving option " + option + ": " + partialResult));
            dataResult.result().ifPresent(element -> {
                root.add(key, element);
            });
        }
    }
}
