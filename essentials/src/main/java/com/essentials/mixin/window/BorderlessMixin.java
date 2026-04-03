package com.essentials.mixin.window;

import com.essentials.config.EssentialsConfig;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class BorderlessMixin {

    @Shadow
    private long handle;

    @Shadow
    private boolean fullscreen;

    @Shadow
    private int x;

    @Shadow
    private int y;

    @Shadow
    private int width;

    @Shadow
    private int height;

    @Shadow
    private boolean isResized;

    @Shadow
    @Final
    private ScreenManager screenManager;

    @Shadow
    public abstract Monitor findBestMonitor();

    @Shadow
    private int windowedX;

    @Shadow
    private int windowedY;

    @Shadow
    private int windowedWidth;

    @Shadow
    private int windowedHeight;

    @Inject(method = "setMode", at = @At("HEAD"), cancellable = true)
    private void onSetMode(CallbackInfo ci) {
        if (!EssentialsConfig.borderlessEnabled) {
            return;
        }

        if (!this.fullscreen) {
            // Exiting fullscreen — restore decorations and windowed position
            GLFW.glfwSetWindowAttrib(this.handle, GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
            this.x = this.windowedX;
            this.y = this.windowedY;
            this.width = this.windowedWidth;
            this.height = this.windowedHeight;
            this.isResized = true;
            GLFW.glfwSetWindowMonitor(this.handle, 0L, this.x, this.y, this.width, this.height, -1);
            ci.cancel();
            return;
        }

        // Intercept fullscreen and do borderless instead
        Monitor monitor = this.findBestMonitor();
        if (monitor == null) {
            return;
        }

        VideoMode mode = monitor.getCurrentMode();
        int monX = monitor.getX();
        int monY = monitor.getY();
        int monW = mode.getWidth();
        int monH = mode.getHeight();

        // Remove decorations and position at monitor origin
        GLFW.glfwSetWindowAttrib(this.handle, GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
        // Use windowed mode (monitor = 0) but sized to fill the screen
        GLFW.glfwSetWindowMonitor(this.handle, 0L, monX, monY, monW, monH, GLFW.GLFW_DONT_CARE);

        this.x = monX;
        this.y = monY;
        this.width = monW;
        this.height = monH;
        this.isResized = true;

        ci.cancel();
    }
}
