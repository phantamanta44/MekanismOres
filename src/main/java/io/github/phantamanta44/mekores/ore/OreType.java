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
    private Integer colour = null;

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
        if (colour != null)
            return colour;
        ItemStack stack = OreDictHelper.getStack("ingot" + key, 1);
        if (stack == null)
            stack = OreDictHelper.getStack("dust" + key, 1);
        if (stack == null) {
            colour = -1;
        } else {
            IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null);
            TextureAtlasSprite sprite = model.getParticleTexture();
            IResource res = null;
            try {
                res = Minecraft.getMinecraft().getResourceManager().getResource(TextureMap.LOCATION_BLOCKS_TEXTURE); // FIXME file not found
                try (BufferedInputStream in = new BufferedInputStream(res.getInputStream())) {
                    BufferedImage img = ImageIO.read(in);
                    int red = 0, green = 0, blue = 0;
                    for (int rgb : img.getRGB(sprite.getOriginX(), sprite.getOriginY(), sprite.getIconWidth(), sprite.getIconHeight(), null, 0, 1)) {
                        red += (rgb & 0xFF0000) >> 16;
                        green += (rgb & 0x00FF00) >> 8;
                        blue += rgb & 0x0000FF;
                    }
                    int size = sprite.getIconWidth() * sprite.getIconHeight();
                    colour = ((red / size) << 16) | ((green / size) << 8) | (blue / size);
                }
            } catch (Exception e) {
                e.printStackTrace(); // TODO Log error better
                colour = -1;
            } finally {
                IOUtils.closeQuietly(res);
            }
        }
        return colour;
    }

}
