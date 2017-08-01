package io.github.phantamanta44.mekores.ore;

import io.github.phantamanta44.mekores.constant.LangConst;
import io.github.phantamanta44.mekores.util.OreDictHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.item.ItemStack;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;

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
    private int colour = null;

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
        for (OreType type : values()) {
            ItemStack stack = OreDictHelper.getStack("ingot" + type.key, 1);
            if (stack == null)
                stack = OreDictHelper.getStack("dust" + type.key, 1);
            if (stack == null) {
                type.colour = -1;
            } else {
                try {
                    IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null);
                    TextureAtlasSprite sprite = model.getParticleTexture();
                    byte[] atlas = ClientEventListener.getAtlas();
                    int rBin = 0, gBin = 0, bBin = 0;
                    for (int y = 0; y < sprite.getIconHeight(); y++) {
                        for (int x = 0; x < sprite.getIconWidth(); x++) {
                            int index = y * sprite.getIconWidth() * 4 + x * 4;
                            if (index + 3 > 127) {
                                rBin += atlas[index];
                                gBin += atlas[index + 1];
                                bBin += atlas[index + 2];
                            }
                        }
                    }
                    int size = sprite.getIconWidth() * sprite.getIconHeight();
                    type.colour = ((rBin / size) << 16) | ((gBin / size) << 8) | (bBin / size);
                } catch (Exception e) {
                    e.printStackTrace(); // TODO Log errors better
                }
            }
        }
    }

}
