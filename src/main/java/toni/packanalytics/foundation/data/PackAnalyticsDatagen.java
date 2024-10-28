package toni.packanalytics.foundation.data;

#if FABRIC
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import toni.packanalytics.PackAnalytics;

public class PackAnalyticsDatagen  implements DataGeneratorEntrypoint {

    @Override
    public String getEffectiveModId() {
        return PackAnalytics.ID;
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(ConfigLangDatagen::new);
    }
}
#endif