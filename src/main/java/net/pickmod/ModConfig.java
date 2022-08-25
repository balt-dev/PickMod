package net.pickmod;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Config(name = "pickmod")
public class ModConfig implements ConfigData {
    public boolean showLock = true;
    public boolean disableUnackedError = true;
    public boolean replaceHungerWithBalance = true;
    public boolean replaceExperienceWithDepth = true;
    public boolean moveOxygenToBubbles = true;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min=0,max=12)
    public long hoursBetweenAutoBackup = 0;
    @ConfigEntry.Gui.Excluded
    public long lastBackup = 0;
}