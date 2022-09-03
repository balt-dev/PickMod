package net.pickmod.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.text.TranslatableText;

@Config(name = "pickmod_visualtweaks")
public class VisualTweaksCategory implements ConfigData {



    public enum PotionDisplayAlignment {
        LEFT_ALIGNED,
        RIGHT_ALIGNED;

        public String toString() {
            return new TranslatableText("text.autoconfig.pickmod.option.visualTweaks.potionDisplayAlignment." + this.name()).getString();
        }
    }

    public enum PotionDisplayAnchor {
        TOP_LEFT,
        TOP_MIDDLE,
        TOP_RIGHT,
        MIDDLE_LEFT,
        MIDDLE,
        MIDDLE_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_MIDDLE,
        BOTTOM_RIGHT;

        public String toString() {
            return new TranslatableText("text.autoconfig.pickmod.option.visualTweaks.potionDisplayAnchor." + this.name()).getString();
        }
    }
    public boolean replaceHungerWithBalance = true;
    public boolean replaceExperienceWithDepth = true;
    public boolean replaceArmorWithEnergy = true;
    public boolean moveOxygenToBubbles = true;
    public boolean potionDisplay = true;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public PotionDisplayAlignment potionDisplayAlignment = PotionDisplayAlignment.RIGHT_ALIGNED;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public PotionDisplayAnchor potionDisplayAnchor = PotionDisplayAnchor.TOP_RIGHT;
    public int potionDisplayOffsetX = 4;
    public int potionDisplayOffsetY = 4;
    public boolean showLock = true;

    public void validatePostLoad() {
        moveOxygenToBubbles = moveOxygenToBubbles && replaceExperienceWithDepth;
    }
}
