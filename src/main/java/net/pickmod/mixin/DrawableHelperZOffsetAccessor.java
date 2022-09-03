package net.pickmod.mixin;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DrawableHelper.class)
public interface DrawableHelperZOffsetAccessor {
    @Accessor
    int getZOffset();
}
