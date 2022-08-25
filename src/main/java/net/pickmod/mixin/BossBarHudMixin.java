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
import net.minecraft.util.math.BlockPos;
import net.pickmod.PickMod;
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
    @Shadow
    private final Map<UUID, ClientBossBar> bossBars = Maps.newLinkedHashMap();

    @Shadow public abstract void clear();

    @Redirect(
            at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;", ordinal = 0),
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;)V"
    )
    public Collection<ClientBossBar> redirectValues(Map instance) {
        Pattern depthPattern = Pattern.compile("Depth: (\\d+)m - @up to go back");
        Collection<ClientBossBar> filteredValues = new ArrayList<>();
        for (Object value : instance.values()) {
            ClientBossBar bossBar = (ClientBossBar) value;
            Matcher matcher = depthPattern.matcher(bossBar.getName().getString());
            if (!matcher.find() || !PickMod.config.replaceExperienceWithDepth) {
                filteredValues.add(bossBar);
            }
        }
        return filteredValues;
    }
}
