package io.github.phantamanta44.mekores.client;

import com.google.common.base.Preconditions;
import io.github.phantamanta44.mekores.MekOres;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ClientEventListener {

    @Nullable
    private static CachedAtlas cachedAtlas = null;

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Post event) {
        MekOres.LOGGER.info("Texture stitch event caught.");
        try {
            GlStateManager.bindTexture(event.getMap().getGlTextureId());
            int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
            if (width > 16 && height > 16) {
                MekOres.LOGGER.info("Caching {}x{} texture atlas...", width, height);
                IntBuffer buf = ByteBuffer.allocateDirect(Integer.BYTES * width * height).asIntBuffer();
                GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buf);
                cachedAtlas = new CachedAtlas(new int[buf.remaining()], width);
                buf.get(cachedAtlas.data);
                MekOres.PROXY.cacheOreColours();
            } else {
                MekOres.LOGGER.info("Ignoring {}x{} atlas stitch event.", width, height);
            }
        } catch (Exception e) {
            MekOres.LOGGER.error("Failed to cache texture atlas!", e);
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiMainMenu)
            MekOres.PROXY.gameInit(); // Hacky, but works
    }

    public static CachedAtlas getAndEvictCachedAtlas() {
        Preconditions.checkState(cachedAtlas != null, "No texture atlas is cached!");
        CachedAtlas atlas = cachedAtlas;
        cachedAtlas = null;
        return atlas;
    }

}
