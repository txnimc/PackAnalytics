package toni.packanalytics.foundation.config;

import toni.lib.config.ConfigBase;

#if FABRIC
    import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
    #if after_21_1
    import net.neoforged.fml.config.ModConfig;
    import net.neoforged.neoforge.common.ModConfigSpec;
    import net.neoforged.neoforge.common.ModConfigSpec.*;
    #else
    import net.minecraftforge.fml.config.ModConfig;
    import net.minecraftforge.common.ForgeConfigSpec;
    import net.minecraftforge.common.ForgeConfigSpec.*;
    #endif
#endif

#if FORGE
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.fml.config.ModConfig;
#endif

#if NEO
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
#endif

public class CCommon extends ConfigBase {

    public final CValue<String, ConfigValue<String>> endpoint_url = new CValue<>("Metrics Endpoint URL", builder -> builder.define("Metrics Endpoint URL", ""), "URL to send data to.");
    public final CValue<String, ConfigValue<String>> packID = new CValue<>("Pack ID", builder -> builder.define("Pack ID", ""), "Identifier for this modpack");
    public final ConfigInt updateRate = i(30, "Update Rate", "Interval between keepalive requests, in minutes.");

    @Override
    public String getName() {
        return "common";
    }
}
