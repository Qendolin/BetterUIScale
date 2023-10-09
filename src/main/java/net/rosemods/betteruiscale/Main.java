package net.rosemods.betteruiscale;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import org.apache.logging.log4j.LogManager;

import java.nio.file.Path;

public class Main implements ClientModInitializer {
    public static final String MODID = "advanced-ui-scale";
    public static final boolean IS_DEV = FabricLoader.getInstance().isDevelopmentEnvironment();
    public static final NamedLogger LOGGER = new NamedLogger(LogManager.getLogger(MODID), !IS_DEV);

    private static Config config;

    public static Config config() {
        return config;
    }

    public static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(MODID+".json");
    }

    @Override
    public void onInitializeClient() {
        config = Config.load(configPath());
        config.save(configPath());

        ShaderStage.Type.FRAGMENT.getLoadedShaders().get("rendertype_text");
    }
}
