package net.pickmod.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "pickmod")
public class ModConfig implements ConfigData {
    public boolean disableUnackedError = true;
    public boolean muteAutoSell = true;
    public boolean muteDoubleDrop = true;
    public boolean showAmethystMessage = true;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min=9,max=120)
    public long minutesBetweenAutoBackup = 9;
    @ConfigEntry.Gui.Excluded
    public long lastBackup = 0;
    @ConfigEntry.Gui.CollapsibleObject
    public VisualTweaksCategory visualTweaks = new VisualTweaksCategory();
}
