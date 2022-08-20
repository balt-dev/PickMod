package net.pickmod.mixin;

import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.pickmod.CooldownToast;
import net.pickmod.PickMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;


@Mixin(ChatHud.class)
public class ChestCooldownMixin {
    String chestMessageAsJson = "{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"dark_gray\",\"text\":\"[\"},{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#99552E\",\"text\":\"✉\"},{\"italic\":false,\"color\":\"dark_gray\",\"text\":\"] \"},{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#CC893D\",\"text\":\"You found a chest!\"}],\"text\":\"\"}";
    MinecraftClient client = MinecraftClient.getInstance();
    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;IIZ)V")
    public void onAddMessage(Text message, int messageId, int timestamp, boolean refresh, CallbackInfo ci) {
        if (Objects.equals(Text.Serializer.toJson(message), chestMessageAsJson) && PickMod.config.showChest) {
            PickMod.LOGGER.info("Chest found!");
            CooldownToast chestToast = new CooldownToast(Items.CHEST.getDefaultStack(), new TranslatableText("text.pickmod.chest_title"), 480000L, CooldownToast.DrawDirection.BACKWARD, (()->{
                if (client.player != null) {
                    client.player.playSound(Registry.SOUND_EVENT.get(new Identifier(PickMod.config.chestCooldownExpireSound.getId())), SoundCategory.PLAYERS, 2.0f, PickMod.config.chestCooldownExpirePitch);
                }
                PickMod.LOGGER.info("Chest cooldown expired!");
            }));
            client.getToastManager().add(chestToast);
        }
    }
}
