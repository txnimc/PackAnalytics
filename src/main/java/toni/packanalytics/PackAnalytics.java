package toni.packanalytics;

import toni.lib.utils.PlatformUtils;
import toni.lib.utils.VersionUtils;
import toni.packanalytics.foundation.config.AllConfigs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


#if FABRIC
    import net.fabricmc.api.ClientModInitializer;
    import net.fabricmc.api.ModInitializer;
    import net.fabricmc.loader.api.FabricLoader;
    import net.fabricmc.api.EnvType;
    #if after_21_1
    import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
    import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
    import net.neoforged.neoforge.client.gui.ConfigurationScreen;
    #endif

    #if current_20_1
    import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
    #endif
#endif

#if FORGE
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
#endif


#if NEO
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.api.distmarker.Dist;
#endif

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

#if FORGELIKE
@Mod("packanalytics")
#endif
public class PackAnalytics #if FABRIC implements ModInitializer, ClientModInitializer #endif
{
    public static final String MODNAME = "Pack Analytics";
    public static final String ID = "packanalytics";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);

    public PackAnalytics(#if NEO IEventBus modEventBus, ModContainer modContainer #endif) {
        #if FORGE
        var context = FMLJavaModLoadingContext.get();
        var modEventBus = context.getModEventBus();
        #endif

        #if FORGELIKE
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        AllConfigs.register((type, spec) -> {
            #if FORGE
            ModLoadingContext.get().registerConfig(type, spec);
            #elif NEO
            modContainer.registerConfig(type, spec);
            #endif
        });
        #endif
    }


    #if FABRIC @Override #endif
    public void onInitialize() {
        #if FABRIC
            AllConfigs.register((type, spec) -> {
                #if AFTER_21_1
                NeoForgeConfigRegistry.INSTANCE.register(PackAnalytics.ID, type, spec);
                #else
                ForgeConfigRegistry.INSTANCE.register(PackAnalytics.ID, type, spec);
                #endif
            });
        #endif

        startKeepAliveTask();
    }

    #if FABRIC @Override #endif
    public void onInitializeClient() {

    }

    private void startKeepAliveTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::sendKeepAliveRequest, 0, AllConfigs.common().updateRate.get(), TimeUnit.SECONDS);
    }

    private void sendKeepAliveRequest() {
        var packID = AllConfigs.common().packID.get();
        String uri = AllConfigs.common().endpoint_url.get();
        if (packID.isEmpty() || uri.isEmpty()) {
            return;
        }

        String packIDParam = "packID=" + packID;
        if (!uri.endsWith("/"))
            uri += "/";

        String packVersion = "1.0.0";
        if (PlatformUtils.isModLoaded("bcc"))
            packVersion = BCCCompat.getBCCVersion();

        if (packVersion.equals("CHANGE_ME"))
            packVersion = "1.0.0";

        String urlString = uri + "keepalive?" + packIDParam + "&server=" + isDedicatedServer() + "&version=" + packVersion;

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                LOGGER.warn("Failed to send keepalive request, response code: " + responseCode);
            }
        } catch (Exception e) {
            LOGGER.error("Error sending keepalive request: " + e.getMessage(), e);
        }
    }

    public static boolean isDedicatedServer() {
        #if FABRIC
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
        #elif NEO
        return FMLLoader.getDist() == Dist.DEDICATED_SERVER;
        #else
        return FMLLoader.getDist() == Dist.DEDICATED_SERVER;
        #endif
    }

    // Forg event stubs to call the Fabric initialize methods, and set up cloth config screen
    #if FORGELIKE
    public void commonSetup(FMLCommonSetupEvent event) { onInitialize(); }
    public void clientSetup(FMLClientSetupEvent event) { onInitializeClient(); }
    #endif
}
