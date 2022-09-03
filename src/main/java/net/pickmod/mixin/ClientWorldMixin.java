package net.pickmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.pickmod.PickMod;
import net.pickmod.classes.StatObtainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(at=@At("HEAD"),method = "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V", cancellable = true)
    public void amethystMessage(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance, CallbackInfo ci) {
        if (category == SoundCategory.MASTER &&
                volume == 2.0f &&
                sound == SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP &&
                1.7 <= pitch &&
                PickMod.config.muteAutoSell &&
                StatObtainer.isOnPickaxe()) {
            ci.cancel();
        }
        if (category == SoundCategory.MASTER &&
                volume == 0.4f &&
                sound == SoundEvents.BLOCK_NOTE_BLOCK_PLING &&
                pitch == 2.0f &&
                PickMod.config.muteDoubleDrop &&
                StatObtainer.isOnPickaxe()) {
            ci.cancel();
        }
        if (category == SoundCategory.MASTER &&
                sound == SoundEvents.BLOCK_BELL_RESONATE &&
                volume == 2.0 &&
                pitch == 1.0 &&
                PickMod.config.showAmethystMessage &&
                StatObtainer.isOnPickaxe()) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.Serializer.fromJson("[\"\",{\"text\":\"[\",\"color\":\"dark_gray\"},{\"text\":\"◎\",\"color\":\"#8C13C5\"},{\"text\":\"]\",\"color\":\"dark_gray\"},{\"text\":\" Amethyst drop!\",\"color\":\"#E857E7\"}]"));
        }
    }
}
