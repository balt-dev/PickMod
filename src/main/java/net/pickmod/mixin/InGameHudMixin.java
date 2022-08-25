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
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.pickmod.PickMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;


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
        if (PickMod.getBalance() != null && PickMod.config.replaceHungerWithBalance) {
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
        if (PickMod.getBalance() != null && PickMod.config.replaceHungerWithBalance) {
            int old = RenderSystem.getShaderTexture(0);
            int xPosition = this.scaledWidth / 2 + 89 - this.client.textRenderer.getWidth(PickMod.getBalance());
            int yPosition = this.scaledHeight - 39;
            this.client.textRenderer.drawWithShadow(matrices, PickMod.getBalance(), xPosition, yPosition, 0xFFFFFFFF);
            RenderSystem.setShaderTexture(0, old);
        }
    }

    @Redirect(
            method = "renderExperienceBar(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            at=@At(value="FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;experienceProgress:F", ordinal=0)
    )
    private float replaceProgress(ClientPlayerEntity instance) {
        if (PickMod.getBalance() != null && PickMod.config.replaceExperienceWithDepth) {
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
        if (PickMod.getBalance() != null && PickMod.config.replaceExperienceWithDepth) {
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
        if (PickMod.getBalance() != null && PickMod.config.moveOxygenToBubbles) {
            return (int)(PickMod.currentOxygen * 15);
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

        if (PickMod.getBalance() != null && PickMod.config.replaceExperienceWithDepth) {
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
        if (PickMod.getBalance() != null && PickMod.config.replaceExperienceWithDepth) {
            return 20;
        }
        return oldV;
    }

    @ModifyConstant(
            method = "renderExperienceBar(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            constant = @Constant(intValue = 69, ordinal = 0)
    )
    private int replaceVProgress(int oldV) {
        if (PickMod.getBalance() != null && PickMod.config.replaceExperienceWithDepth) {
            return 25;
        }
        return oldV;
    }

    @ModifyConstant(
            method = "renderExperienceBar(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            constant = @Constant(intValue = 8453920, ordinal = 0)
    )
    private int replaceColor(int oldColor) {
        if (PickMod.getBalance() != null && PickMod.config.replaceExperienceWithDepth) {
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
        if (PickMod.getBalance() != null && PickMod.config.replaceExperienceWithDepth) {
            RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
        }
    }
}
