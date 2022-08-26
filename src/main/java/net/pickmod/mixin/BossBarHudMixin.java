package net.pickmod.mixin;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.pickmod.PickMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Mixin(BossBarHud.class)
public abstract class BossBarHudMixin {
    @Final
    @Shadow
    final Map<UUID, ClientBossBar> bossBars = Maps.newLinkedHashMap();


    @Shadow public abstract void clear();

    @Shadow @Final private MinecraftClient client;

    @Inject(
            at = @At(value = "HEAD"),
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;)V"
    )
    public void resetOxygen(MatrixStack matrices, CallbackInfo ci){
        if (bossBars.isEmpty()) PickMod.currentOxygen = 20f;
    }
    @Redirect(
            at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;", ordinal = 0),
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;)V"
    )
    public Collection<ClientBossBar> redirectValues(@SuppressWarnings("rawtypes") Map instance) {
        Pattern depthPattern = Pattern.compile("Depth: \\d+m - @up to go back");
        Pattern oxygenPattern = Pattern.compile("‹< O₂ (?:\\| Tanks: EMPTY )?\\| (.+): (\\d+(?:\\.\\d)?)s >›");
        Collection<ClientBossBar> filteredValues = new ArrayList<>();
        for (Object value : instance.values()) {
            ClientBossBar bossBar = (ClientBossBar) value;
            String barName = bossBar.getName().getString();
            Matcher depthMatcher = depthPattern.matcher(barName);
            Matcher oxygenMatcher = oxygenPattern.matcher(barName);
            boolean addBar = true;
            if (depthMatcher.find()) {
                addBar = !PickMod.config.replaceExperienceWithDepth;
            } else if (oxygenMatcher.find()) {
                addBar = !PickMod.config.moveOxygenToBubbles;
                if (Objects.equals(oxygenMatcher.group(1), "Breath") && Objects.equals(PickMod.currentOxygenHolder, "Tanks")) {
                    assert this.client.player != null;
                    this.client.player.playSound(SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 2.0f, 1.0f);
                }
                PickMod.currentOxygenHolder = oxygenMatcher.group(1);
                PickMod.currentOxygen = Float.parseFloat(oxygenMatcher.group(2));
            }
            if (addBar) filteredValues.add(bossBar);
        }
        return filteredValues;
    }
}
