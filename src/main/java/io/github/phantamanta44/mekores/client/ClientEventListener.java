package io.github.phantamanta44.mekores.client;

import io.github.phantamanta44.mekores.MekOres;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ClientEventListener {

    private static int[] atlas;
    private static int atlasWidth;

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Post event) {
        MekOres.LOGGER.info("Texture stitch event caught.");
        try {
            int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
            if (width > 16 && height > 16) {
                MekOres.LOGGER.info("Caching {}x{} texture atlas...", width, height);
                IntBuffer buf = ByteBuffer.allocateDirect(Integer.BYTES * width * height).asIntBuffer();
                GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buf);
                atlas = new int[buf.remaining()];
                buf.get(atlas);
                atlasWidth = width;
                ClientProxy.cacheOreColours();
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
            ClientProxy.gameInit(); // Hacky, but works
    }

    public static int[] getAtlas() {
        return atlas;
    }

    public static int getAtlasWidth() {
        return atlasWidth;
    }

}