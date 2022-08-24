package net.pickmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.pickmod.PickMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @ModifyConstant(
            method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V",
            constant = @Constant(
                    intValue = 10,
                    ordinal = 0
            ),
            slice = @Slice(from=@At(value="INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=food"}))
    )
    private int preventForLoop(int oldValue) {
        if (PickMod.getBalance() != null) {
            return 0;
        }
        return oldValue;
    }
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;
    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V",
            at=@At(value="INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=food"})
    )
    private void renderBalance(MatrixStack matrices, CallbackInfo ci) {
        if (PickMod.getBalance() != null) {
            int xPosition = this.scaledWidth / 2 + 89 - this.client.textRenderer.getWidth(PickMod.getBalance());
            int yPosition = this.scaledHeight - 39;
            this.client.textRenderer.drawWithShadow(matrices, PickMod.getBalance(), xPosition, yPosition, 0xFFFFFFFF);
        }
    }
}
