package net.rosemods.betteruiscale;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
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

    @Override
    public void onInitializeClient() {
        config = Config.load(configPath());
        config.save(configPath());

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(MODID, "shader-injector");
            }

            @Override
            public void reload(ResourceManager manager) {
                config.setFontSmoothingUniform();
            }
        });
    }

    public static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(MODID + ".json");
    }
}
