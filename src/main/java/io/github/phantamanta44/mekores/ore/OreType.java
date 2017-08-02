package io.github.phantamanta44.mekores.ore;

import io.github.phantamanta44.mekores.MekOres;
import io.github.phantamanta44.mekores.client.ClientEventListener;
import io.github.phantamanta44.mekores.constant.LangConst;
import io.github.phantamanta44.mekores.util.OreDictHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

public enum OreType {

    ALUMINIUM("Aluminum"),
    NICKEL("Nickel"),
    PLATINUM("Platinum"),
    IRIDIUM("Iridium"),
    MITHRIL("Mithril"),
    DRACONIUM("Draconium"),
    COBALT("Cobalt"),
    ARDITE("Ardite"),
    YELLORIUM("Yellorium"),
    URANIUM("Uranium"),
    TITANIUM("Titanium"),
    DESH("Desh"),
    CHROME("Chrome"),
    TUNGSTEN("Tungsten"),
    ZINC("Zinc"),
    THORIUM("Thorium"),
    BORON("Boron"),
    LITHIUM("Lithium"),
    MAGNESIUM("Magnesium"),
    CADMIUM("Cadmium"),
    MANGANESE("Manganese"),
    PLUTONIUM("Plutonium"),
    RUTILE("Rutile"),
    TANTALUM("Tantalum"),
    ZIRCONIUM("Zirconium")/*,
    IGNATIUS("Ignatius"),
    SHADOWIRON("Shadow Iron"),
    LEMURITE("Lemurite"),
    MIDASIUM("Midasium"),
    VYROXERES("Vyroxeres"),
    CERUCLASE("Ceruclase"),
    ADLUORITE("Adluorite"),
    KALENDRITE("Kalendrite"),
    VULCANITE("Vulcanite"),
    SANGUINITE("Sanguinite"),
    PROMETHEUM("Prometheum"),
    DEEPIRON("Deep Iron"),
    INFUSCOLIUM("Infuscolium"),
    OURECLASE("Oureclase"),
    AREDRITE("Aredrite"),
    ASTALSILVER("Astral Silver"),
    CARMOT("Carmot"),
    RUBRACIUM("Rubracium"),
    ORICHALCUM("Orichalcum"),
    ADAMANTINE("Adamantine"),
    ATLARUS("Atlarus"),
    EXIMITE("Eximite"),
    MEUTOITE("Meutoite")*/;

    public final String key;

    private Boolean valid = null;
    private int colour = -1;

    OreType(String key) {
        this.key = key;
    }

    public String getName() {
        return name().toLowerCase();
    }

    public String getLocalizedName() {
        return LangConst.get(LangConst.ORE_KEY + getName());
    }

    public boolean isValid() {
        return valid != null ? valid : (valid
                = OreDictHelper.exists("ore" + key) && (OreDictHelper.exists("ingot" + key) || OreDictHelper.exists("dust" + key)));
    }

    public int getColour() {
        return colour;
    }

    public static void cacheColours() {
        MekOres.LOGGER.info("Caching ore colours...");
        for (OreType type : values()) {
            if (type.isValid()) {
                ItemStack stack = OreDictHelper.getStack("ingot" + type.key, 1);
                if (stack == null)
                    stack = OreDictHelper.getStack("dust" + type.key, 1);
                if (stack != null) {
                    try {
                        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null);
                        TextureAtlasSprite sprite = model.getParticleTexture();
                        int[] atlas = ClientEventListener.getAtlas();
                        int rBin = 0, gBin = 0, bBin = 0, total = 0;
                        for (int y = 0; y < sprite.getIconHeight(); y++) {
                            for (int x = 0; x < sprite.getIconWidth(); x++) {
                                int index = (y + sprite.getOriginY()) * 4096 + x + sprite.getOriginX();
                                int b1 = (atlas[index]) & 0xFF;
                                int b2 = (atlas[index] >> 8) & 0xFF;
                                int b3 = (atlas[index] >> 16) & 0xFF;
                                int b4 = (atlas[index] >> 24) & 0xFF;
                                int value = b1 << 24 | b2 << 16 | b3 << 8 | b4;
                                int alpha = (value >>> 24) & 0xFF;
                                if (alpha > 127) {
                                    rBin += (value >>> 16) & 0xFF;
                                    gBin += (value >>> 8) & 0xFF;
                                    bBin += value & 0xFF;
                                    total++;
                                }
                            }
                        }
                        type.colour = ((rBin / total) << 16) | ((gBin / total) << 8) | (bBin / total);
                    } catch (Exception e) {
                        MekOres.LOGGER.warn("Failed to calculate colour for " + type.name(), e);
                    }
                }
            }
        }
    }

}
