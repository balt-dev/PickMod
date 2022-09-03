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

import static java.lang.Double.NaN;


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
    public void resetValues (MatrixStack matrices, CallbackInfo ci){
        if (bossBars.isEmpty()) {
            PickMod.currentOxygen = 20f;
            PickMod.suitCharge = -1.0f;

        }
    }
    @Redirect(
            at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;", ordinal = 0),
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;)V"
    )
    public Collection<ClientBossBar> redirectValues(@SuppressWarnings("rawtypes") Map instance) {
        Pattern depthPattern = Pattern.compile("Depth: \\d+m - @up to go back");
        Pattern oxygenPattern = Pattern.compile("‹< O₂ (?:\\| Tanks: EMPTY )?(?:\\| (.+): (\\d+(?:\\.\\d)?)s )?>›");
        Pattern miningPattern = Pattern.compile("Mining\\.\\.\\. \\((\\d+(?:\\.\\d+)?)%\\)");
        Pattern chargePattern = Pattern.compile("Suit Charge: ⚡ (\\d+(?:\\.\\d)?)/10");
        Collection<ClientBossBar> filteredValues = new ArrayList<>();
        boolean miningFound = false;
        boolean chargeFound = false;
        for (Object value : instance.values()) {
            ClientBossBar bossBar = (ClientBossBar) value;
            String barName = bossBar.getName().getString();
            Matcher depthMatcher = depthPattern.matcher(barName);
            Matcher oxygenMatcher = oxygenPattern.matcher(barName);
            Matcher miningMatcher = miningPattern.matcher(barName);
            Matcher chargeMatcher = chargePattern.matcher(barName);
            boolean addBar = true;
            if (depthMatcher.find()) {
                addBar = !PickMod.config.visualTweaks.replaceExperienceWithDepth;
            } else if (oxygenMatcher.find()) {
                addBar = !PickMod.config.visualTweaks.moveOxygenToBubbles;
                if (Objects.equals(oxygenMatcher.group(1), "Breath") && Objects.equals(PickMod.currentOxygenHolder, "Tanks")) {
                    assert this.client.player != null;
                    this.client.player.playSound(SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 2.0f, 1.0f);
                }
                try {
                    PickMod.currentOxygenHolder = oxygenMatcher.group(1);
                    PickMod.currentOxygen = Float.parseFloat(oxygenMatcher.group(2));
                } catch(IndexOutOfBoundsException | NullPointerException e) {
                    PickMod.currentOxygenHolder = "Tanks";
                    PickMod.currentOxygen = 0f;
                }
            } else if (chargeMatcher.find()) {
                chargeFound = true;
                addBar = !PickMod.config.visualTweaks.replaceArmorWithEnergy;
                PickMod.suitCharge = Float.parseFloat(chargeMatcher.group(1));
            }/* else if (miningMatcher.find()) {
                miningFound = true;
                if (bossBar.getName() != PickMod.miningLastName) {
                    if (PickMod.miningLastChecked != 0) {
                        double deltaTime = ((double) (System.currentTimeMillis() - PickMod.miningLastChecked)) / 1000;
                        PickMod.miningPercentageMovingAverage[0] = Double.parseDouble(miningMatcher.group(1));
                        double deltaMining = (Arrays.stream(PickMod.miningPercentageMovingAverage).average().getAsDouble() - PickMod.miningPercentageLastMovingAverage) / 100;
                        double secondsLeft = (double) (deltaTime / deltaMining);
                        PickMod.LOGGER.info(deltaTime + " " + deltaMining + " " + secondsLeft);
                    }
                    PickMod.LOGGER.info(Arrays.toString(PickMod.miningPercentageMovingAverage));
                    System.arraycopy(PickMod.miningPercentageMovingAverage, 0, PickMod.miningPercentageMovingAverage, 1, PickMod.miningPercentageMovingAverage.length-1);
                    PickMod.miningPercentageLastMovingAverage = Arrays.stream(PickMod.miningPercentageMovingAverage).average().getAsDouble();
                    PickMod.miningLastChecked = System.currentTimeMillis();
                    PickMod.miningLastName = bossBar.getName();
                }
            }
            TODO: rethink how i'm doing this and actually add it*/
            if (addBar) filteredValues.add(bossBar);
        }
        /*
        if (!miningFound) {
            PickMod.miningPercentageMovingAverage = new double[4];
            PickMod.miningPercentageLastMovingAverage = 0.0;
            PickMod.miningLastChecked = 0;
        }*/
        if (!chargeFound) {
            PickMod.suitCharge = -1.0f;
        }
        return filteredValues;
    }
}
