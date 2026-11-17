package com.tpw.carpetaddition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.utils.Translations;
import net.fabricmc.api.ModInitializer;

import java.util.Map;

/**
 * Entry point and Carpet extension registration for TPW Carpet Addition.
 */
public class TpwCarpetAdditionMod implements ModInitializer, CarpetExtension {
    public static final String MOD_ID = "tpw-carpet-addition";
    public static final String VERSION = "1.1.0+1.20.4";

    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(this);
    }

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(TpwCarpetSettings.class);
    }

    @Override
    public String version() {
        return VERSION;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return Translations.getTranslationFromResourcePath("assets/" + MOD_ID + "/lang/%s.json".formatted(lang));
    }
}
