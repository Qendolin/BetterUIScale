package net.rosemods.betteruiscale.mixin;

import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;
import net.rosemods.betteruiscale.ScaleFactorUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Window.class)
public class MixinWindow {
    @Shadow
    private int framebufferWidth;

    @Shadow
    private int framebufferHeight;

    @Shadow
	private double scaleFactor;

    @Shadow
	private int scaledWidth;

    @Shadow
	private int scaledHeight;


	/**
     * @author Rose
	 * @author Qendolin
	 * @reason Modifies gui scaling
     */
    @Overwrite
	public int calculateScaleFactor(int guiScale, boolean forceUnicodeFont) {
		return ScaleFactorUtil.calcScaleFactor(guiScale, forceUnicodeFont, framebufferWidth, framebufferHeight);
	}

	/**
	 * @author Rose
	 * @author Qendolin
	 * @reason Modifies gui scaling
	 */
	@Overwrite
	public void setScaleFactor(double internalScaleFactor) {
		this.scaleFactor = ScaleFactorUtil.fromInternalScaleFactor(internalScaleFactor);
		scaledWidth = ScaleFactorUtil.scaleInternal(framebufferWidth, internalScaleFactor);
		scaledHeight = ScaleFactorUtil.scaleInternal(framebufferHeight, internalScaleFactor);
	}
}
