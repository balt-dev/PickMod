package net.pickmod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.pickmod.PickMod;
import net.pickmod.StatObtainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static net.pickmod.StatObtainer.joinTexts;


@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper{
    @ModifyConstant(
            method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V",
            constant = @Constant(
                    intValue = 10,
                    ordinal = 0
            ),
            slice = @Slice(from=@At(value="INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=food"}))
    )
    private int preventForLoop(int oldValue) {
        if (StatObtainer.isOnPickaxe() && PickMod.config.replaceHungerWithBalance) {
            return 0;
        }
        return oldValue;
    }
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;
    @Shadow @Final private MinecraftClient client;

    private static final Identifier BARS_TEXTURE = new Identifier("textures/gui/bars.png");

    @Inject(
            method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V",
            at=@At(value="INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=food"}, shift= At.Shift.AFTER)
    )
    private void renderBalance(MatrixStack matrices, CallbackInfo ci) {
        if (StatObtainer.isOnPickaxe() && PickMod.config.replaceHungerWithBalance) {
            int old = RenderSystem.getShaderTexture(0);
            int balanceXPosition = (this.scaledWidth / 2) + (89 - this.client.textRenderer.getWidth(StatObtainer.getStat(StatObtainer.Stats.BALANCE)));
            int balanceYPosition = this.scaledHeight - 39;
            int bubbleOffset;
            if (PickMod.config.moveOxygenToBubbles) {
                bubbleOffset = PickMod.currentOxygen < 20 ? 10 : 0;
            } else {
                assert this.client.player != null;
                bubbleOffset = this.client.player.isSubmergedInWater() ? 8 : 0;
            }
            if (bubbleOffset != 0 && PickMod.config.moveOxygenToBubbles) { // this code isn't very readable :/
                int oxygenXPosition = (this.scaledWidth / 2) + (89 - this.client.textRenderer.getWidth(Text.of("O₂ " + PickMod.currentOxygen)));
                this.client.textRenderer.drawWithShadow(matrices, joinTexts(
                        Text.of("O₂ ").copy().fillStyle(Style.EMPTY.withColor(0xB3FAFF)),
                        Text.of(String.valueOf(PickMod.currentOxygen)),
                        Text.of(" [").copy().fillStyle(Style.EMPTY.withColor(TextColor.parse("dark_gray"))),
                        Text.of(PickMod.currentOxygenHolder).copy().fillStyle(Style.EMPTY.withColor(0xB3FAFF)),
                        Text.of("]").copy().fillStyle(Style.EMPTY.withColor(TextColor.parse("dark_gray")))
                ), oxygenXPosition, balanceYPosition - 10, 0xFFFFFFFF);
            }
            int breakingSpeedXPosition = (this.scaledWidth / 2) + (89 + this.client.textRenderer.getWidth(Text.of(" ")) - this.client.textRenderer.getWidth(StatObtainer.getStat(StatObtainer.Stats.BREAKING_SPEED)));
            //int breakingPowerXPosition = (this.scaledWidth / 2) + (89 - this.client.textRenderer.getWidth(StatObtainer.getStat(StatObtainer.Stats.BREAKING_POWER)));
            this.client.textRenderer.drawWithShadow(matrices, StatObtainer.getStat(StatObtainer.Stats.BALANCE), balanceXPosition, balanceYPosition, 0xFFFFFFFF);
            this.client.textRenderer.drawWithShadow(matrices, StatObtainer.getStat(StatObtainer.Stats.BREAKING_SPEED), breakingSpeedXPosition, balanceYPosition-bubbleOffset-10, 0xFFFFFFFF);
            //this.client.textRenderer.drawWithShadow(matrices, StatObtainer.getStat(StatObtainer.Stats.BREAKING_POWER), breakingPowerXPosition, balanceYPosition-bubbleOffset-20, 0xFFFFFFFF);
            RenderSystem.setShaderTexture(0, old);
        }
    }

    @Redirect(
            method = "renderExperienceBar(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            at=@At(value="FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;experienceProgress:F", ordinal=0)
    )
    private float replaceProgress(ClientPlayerEntity instance) {
        if (StatObtainer.isOnPickaxe() && PickMod.config.replaceExperienceWithDepth) {
            assert this.client.player != null;
            return Math.max((float) (1 - (this.client.player.getY() / 200)),0);
        }
        return instance.experienceProgress;
    }

    @Redirect(
            method = "renderExperienceBar(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            at=@At(value="FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;experienceLevel:I") //ord -1 works here
    )
    private int replaceLevel(ClientPlayerEntity instance) {
        if (StatObtainer.isOnPickaxe() && PickMod.config.replaceExperienceWithDepth) {
            assert this.client.player != null;
            return Math.max(0,200 - (int)Math.floor(this.client.player.getY() < 199.90625 ? this.client.player.getY() : 200 /*handle grass path at top*/));
        }
        return instance.experienceLevel;
    }

    @Redirect(
            method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V",
            at=@At(value="INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAir()I", ordinal = 0)
    )
    private int replaceAir(PlayerEntity instance) {
        if (StatObtainer.isOnPickaxe() && PickMod.config.moveOxygenToBubbles) {
            return 0;
        }
        return instance.getAir();
    }

    @Inject(
            method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V",
            at=@At(value="HEAD")
    )
    private void fixTexture(MatrixStack matrices, CallbackInfo ci) {
        RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
    }

    //All this does is replace the XP bar with the depth bar.
    @Redirect(
            method = "renderExperienceBar(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            at = @At(value="INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", ordinal = 0)
    )
    private void replaceTexture(int i, Identifier identifier) {

        if (StatObtainer.isOnPickaxe() && PickMod.config.replaceExperienceWithDepth) {
            RenderSystem.setShaderTexture(i, BARS_TEXTURE); //probably a bad way to use redirect but whatever lmao
        } else {

            RenderSystem.setShaderTexture(i, identifier);
        }
    }

    @ModifyConstant(
            method = "renderExperienceBar(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            constant = @Constant(intValue = 64, ordinal = 0)
    )
    private int replaceV(int oldV) {
        if (StatObtainer.isOnPickaxe() && PickMod.config.replaceExperienceWithDepth) {
            return 20;
        }
        return oldV;
    }

    @ModifyConstant(
            method = "renderExperienceBar(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            constant = @Constant(intValue = 69, ordinal = 0)
    )
    private int replaceVProgress(int oldV) {
        if (StatObtainer.isOnPickaxe() && PickMod.config.replaceExperienceWithDepth) {
            return 25;
        }
        return oldV;
    }

    @ModifyConstant(
            method = "renderExperienceBar(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            constant = @Constant(intValue = 8453920, ordinal = 0)
    )
    private int replaceColor(int oldColor) {
        if (StatObtainer.isOnPickaxe() && PickMod.config.replaceExperienceWithDepth) {
            return Objects.requireNonNull(TextColor.parse("gray")).getRgb();
        }
        return oldColor;
    }

    //Fix texture
    @Inject(
            method = "renderExperienceBar(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            at = @At(value="INVOKE", target="Lnet/minecraft/util/profiler/Profiler;pop()V")
    )
    private void fixTexture(MatrixStack matrices, int x, CallbackInfo ci) {
        if (StatObtainer.isOnPickaxe() && PickMod.config.replaceExperienceWithDepth) {
            RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
        }
    }
}
