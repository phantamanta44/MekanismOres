package io.github.phantamanta44.mekores.ore;

import com.google.common.collect.Sets;
import io.github.phantamanta44.mekores.MekOres;
import io.github.phantamanta44.mekores.client.ClientEventListener;
import io.github.phantamanta44.mekores.constant.LangConst;
import io.github.phantamanta44.mekores.util.OreDictHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Set;

public enum OreType {

    // big reactors
    URANIUM("Uranium"),
    YELLORIUM("Yellorium"),

    // thermal
    NICKEL("Nickel"),
    PLATINUM("Platinum"),
    IRIDIUM("Iridium"),
    MITHRIL("Mithril"),
    ALUMINUM("Aluminum"),

    // tcon
    COBALT("Cobalt"),
    ARDITE("Ardite"),

    // astral sorc
    ASTRAL_STARMETAL("AstralStarmetal"),

    // draconic evo
    DRACONIUM("Draconium"),

    // nuclearcraft
    BERYLLIUM("Beryllium"),
    BORON("Boron"),
    LITHIUM("Lithium"),
    MAGNESIUM("Magnesium"),
    THORIUM("Thorium"),
    ZIRCONIUM("Zirconium"),

    // gc
    TITANIUM("Titanium"),
    DESH("Desh"),

    // extra planets
    CARBON("Carbon"),
    DARK_IRON("DarkIron"),
    TUNGSTEN("Tungsten"),
    ZINC("Zinc"),
    MERCURY("Mercury"),
    PALLADIUM("Palladium");

    public final String key;

    @Nullable
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
        return valid == null ? (valid = isKeyValid(key)) : valid;
    }

    public void setValid() {
        valid = true;
    }

    public int getColour() {
        return colour;
    }

    @Nullable
    public static OreType getByKey(String key) {
        return Arrays.stream(values())
                .filter(v -> v.key.equals(key))
                .findAny().orElse(null);
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
                                int index = (y + sprite.getOriginY()) * ClientEventListener.getAtlasWidth() + x + sprite.getOriginX();
                                int value = ((atlas[index]) & 0xFF) << 24
                                        | ((atlas[index] >> 8) & 0xFF) << 16
                                        | ((atlas[index] >> 16) & 0xFF) << 8
                                        | ((atlas[index] >> 24) & 0xFF);
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

    private static final Set<String> BLACKLIST;

    static {
        BLACKLIST = Sets.newHashSet(
                "Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Osmium",
                "Coal", "Sulfur", "Sulphur", "Redstone", "Electrotine");
    }

    public static boolean isKeyValid(String key) {
        if (OreDictHelper.exists("gem" + key) || BLACKLIST.contains(key))
            return false;
        return OreDictHelper.exists("ore" + key) &&
                (OreDictHelper.exists("ingot" + key) || OreDictHelper.exists("dust" + key));
    }

}
