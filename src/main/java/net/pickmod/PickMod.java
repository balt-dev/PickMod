package net.pickmod;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.pickmod.classes.PeriodicBackup;
import net.pickmod.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PickMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("pickmod");
	public static float currentOxygen = 20;
	public static String currentOxygenHolder = "Breath";
	public static float suitCharge = 0;
	public static ModConfig config;
	/*
	public static double[] miningPercentageMovingAverage = new double[4];
	public static double miningPercentageLastMovingAverage = 0.0;
	public static long miningLastChecked = 0;
	public static Text miningLastName = Text.of("");
	 */

	@Override
	public void onInitialize() {
		PickKeybinds.register();
		PeriodicBackup.register();
		AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}
}
