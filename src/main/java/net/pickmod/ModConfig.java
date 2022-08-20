package net.pickmod;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Config(name = "pickmod")
public class ModConfig implements ConfigData {
    public enum NotifySounds {
        BELL_RING("block.bell.use"),
        LEVEL_UP("block.bell.use"),
        EXP_COLLECT("entity.experience_orb.pickup"),
        NOTE_BELL("block.note_block.bell"),
        NOTE_CHIME("block.note_block.chime"),
        NOTE_FLUTE("block.note_block.flute"),
        NOTE_GUITAR("block.note_block.guitar"),
        NOTE_HARP("block.note_block.harp"),
        NOTE_PLING("block.note_block.pling"),
        NOTE_XYLO("block.note_block.xylophone"),
        NOTE_IRON_XYLO("block.note_block.iron_xylophone"),
        NOTE_COWBELL("block.note_block.cow_bell"),
        NOTE_BIT("block.note_block.bit"),
        NOTE_BANJO("block.note_block.banjo"),
        CHEST_OPEN("block.chest.open");
        private final String id;
        NotifySounds(String id) {
            this.id = id;
        }
        public String getId() {
            return this.id;
        }
    }
    public boolean showChest = true;
    public boolean showLock = true;
    public boolean disableUnackedError = true;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
    public NotifySounds chestCooldownExpireSound = NotifySounds.CHEST_OPEN;
    public float chestCooldownExpirePitch = 1.2f;
}