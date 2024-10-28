package toni.packanalytics;

import dev.wuffs.bcc.data.BetterStatusServerHolder;

public class BCCCompat {
    public static String getBCCVersion() {
        return BetterStatusServerHolder.INSTANCE.getStatus().version();
    }
}
