package io.github.phantamanta44.mekores.client;

public class ClientEventListener {

    private static byte[] atlas;

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Post event) {
        if (event.getMap().basePath.equals("items")) { // FIXME find a way to make sure we're on the items atlas
            int tex = event.getMap().getGlTextureId();
            ByteBuffer buf = ByteBuffer.allocate(); // FIXME find some hacky way to get the texture size; maybe bind a delegating logger to System.out and intercept the log message?
            GL11.glGetTexImage(tex, 0, GL11.GL_RGBA, GL11.GL_BYTE, buf);
            atlas = buf.toArray();
        }
    }

    public static byte[] getAtlas() {
        return atlas;
    }

}