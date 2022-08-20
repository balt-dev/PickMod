package net.pickmod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class CooldownToast implements Toast {
    private final Text title;
    private final ItemStack stack;
    @Nullable
    private final Toast.Visibility visibility = Toast.Visibility.SHOW;
    private final long delta;
    private final Runnable postCooldown;
    private boolean hasRunPost = false;
    public enum DrawDirection {
        FORWARD,
        BACKWARD
    }
    private final DrawDirection drawDirection;
    private int offset = 0;
    public CooldownToast(ItemStack display, Text title, long deltaMilliseconds, @Nullable DrawDirection drawDirection, Runnable postCooldown) {
        this.stack = display;
        this.title = title;
        this.delta = deltaMilliseconds;
        this.postCooldown = postCooldown;
        this.drawDirection = drawDirection;
    }
    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long currentTime) { //currentTime is in ms
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        manager.drawTexture(matrices, 0, 0, 0, 0, this.getWidth(), this.getHeight());
        // draw progress bar
        double f = Math.min((double)(currentTime + this.offset) / (double)this.delta,1);
        double drawnDelta = f;
        if (this.drawDirection == DrawDirection.BACKWARD) {
            drawnDelta = 1 - f;
        }
        DrawableHelper.fill(matrices, 4, 28, (int) (4.0f + 153.0f * (drawnDelta)), 29, 0xFFFFFFFF);
        manager.getClient().textRenderer.draw(matrices, this.title, 30.0f, 7.0f, 0xFFFFFFFF);
        String timeLeft = DurationFormatUtils.formatDurationWords(Math.max(this.delta-currentTime,0), true, true);
        timeLeft = timeLeft.replaceAll(" ?days ?","d")
                .replaceAll(" ?hours ?","h")
                .replaceAll(" ?minutes ?","m")
                .replaceAll(" ?seconds ?","s") + " left";
        manager.getClient().textRenderer.draw(matrices, Text.of(timeLeft), 30.0f, 18.0f, 0xFFFFFF);
        manager.getClient().getItemRenderer().renderInGui(this.stack, 8, 8);
        if (f >= 1 && !hasRunPost) {
            this.postCooldown.run();
            hasRunPost = true;
        }
        return f >= 1 ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }
    public void setOffset(int offset) { //forge toast is planned
        this.offset = offset;
    }
}
