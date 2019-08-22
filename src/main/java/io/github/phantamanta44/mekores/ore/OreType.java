package io.github.phantamanta44.mekores.ore;

import com.google.common.collect.Sets;
import io.github.phantamanta44.mekores.CommonProxy;
import io.github.phantamanta44.mekores.MekOres;
import io.github.phantamanta44.mekores.client.ClientEventListener;
import io.github.phantamanta44.mekores.constant.LangConst;
import io.github.phantamanta44.mekores.util.OreDictHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// only append to this enum!
// otherwise, worlds from older versions will have items swapped around!
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
    PALLADIUM("Palladium"),

    // libvulpes/advanced rocketry
    DILITHIUM("Dilithium"),

    // metallurgy
    ADAMANTINE("Adamantine"),
    ALDUORITE("Alduorite"),
    ASTRAL_SILVER("AstralSilver"),
    ATLARUS("Atlarus"),
    CARMOT("Carmot"),
    CERUCLASE("Ceruclase"),
    DEEP_IRON("DeepIron"),
    EXIMITE("Eximite"),
    IGNATIUS("Ignatius"),
    INFUSCOLIUM("Infuscolium"),
    KALENDRITE("Kalendrite"),
    LEMURITE("Lemurite"),
    MANGANESE("Manganese"),
    MEUTOITE("Meutoite"),
    MIDASIUM("Midasium"),
    ORICHALCUM("Orichalcum"),
    OURECLASE("Oureclase"),
    PROMETHEUM("Prometheum"),
    RUBRACIUM("Rubracium"),
    SANGUINITE("Sanguinite"),
    SHADOW_IRON("ShadowIron"),
    VULCANITE("Vulcanite"),
    VYROXERES("Vyroxeres"),

    // taiga
    TIBERIUM("Tiberium"),
    AURORIUM("Aurorium"),
    DURANITE("Duranite"),
    VALYRIUM("Valyrium"),
    VIBRANIUM("Vibranium"),
    KARMESINE("Karmesine"),
    OVIUM("Ovium"),
    JAUXUM("Jauxum"),
    URU("Uru"),
    OSRAM("Osram"),
    EEZO("Eezo"),
    ABYSSUM("Abyssum"),

    // zoesteria
    VANADIUM("Vanadium"),

    // advent of ascension
    BARONYTE("Baronyte"),
    BLAZIUM("Blazium"),
    ELECANIUM("Elecanium"),
    EMBERSTONE("Emberstone"),
    GHASTLY("Ghastly"),
    GHOULISH("Ghoulish"),
    LIMONITE("Limonite"),
    LYON("Lyon"),
    MYSTITE("Mystite"),
    ROSITE("Rosite"),
    SHYRESTONE("Shyrestone"),
    VARSIUM("Varsium"),

    // matter overdrive
    TRITANIUM("Tritanium");

    public final String key;

    @Nullable
    private OreValidity validity = null;
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
        return getValidity().isValid();
    }

    public OreValidity getValidity() {
        return validity == null ? (validity = validateKey(key)) : validity;
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
        long time = -System.currentTimeMillis();
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
                        List<int[]> rgbData = new ArrayList<>();
                        List<float[]> hsvData = new ArrayList<>();
                        float hMean = 0F, sMean = 0F, bMean = 0F, weightTotal = 0;
                        for (int y = 0; y < sprite.getIconHeight(); y++) {
                            for (int x = 0; x < sprite.getIconWidth(); x++) {
                                int index = (y + sprite.getOriginY()) * ClientEventListener.getAtlasWidth() + x + sprite.getOriginX();
                                if ((atlas[index] & 0xFF) > 127) {
                                    int[] rgb = new int[] {
                                            (atlas[index] >> 8) & 0xFF,
                                            (atlas[index] >> 16) & 0xFF,
                                            (atlas[index] >> 24) & 0xFF
                                    };
                                    rgbData.add(rgb);
                                    float[] hsb = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], null);
                                    float weight = calcWeight(hsb);
                                    hsvData.add(hsb);
                                    hMean += hsb[0] * weight;
                                    sMean += hsb[1] * weight;
                                    bMean += hsb[2] * weight;
                                    weightTotal += weight;
                                }
                            }
                        }
                        if (hsvData.size() == 0) {
                            MekOres.LOGGER.warn("Using fallback colour; no suitably opaque pixels: " + type.name());
                            type.colour = computeFallbackColour(atlas, sprite);
                            continue;
                        }
                        hMean /= weightTotal;
                        double hStdDev = weightedStdDev(hsvData, 0, hMean, weightTotal);
                        sMean /= weightTotal;
                        double sStdDev = weightedStdDev(hsvData, 1, sMean, weightTotal);
                        bMean /= weightTotal;
                        double bStdDev = weightedStdDev(hsvData, 2, bMean, weightTotal);
                        int rBin = 0, gBin = 0, bBin = 0, total = 0;
                        for (int i = 0; i < hsvData.size(); i++) {
                            float[] hsv = hsvData.get(i);
                            if (withinStdDev(hsv[0], hMean, hStdDev)
                                    && withinStdDev(hsv[1], sMean, sStdDev)
                                    && withinStdDev(hsv[2], bMean, bStdDev)) {
                                int[] rgb = rgbData.get(i);
                                rBin += rgb[0];
                                gBin += rgb[1];
                                bBin += rgb[2];
                                ++total;
                            }
                        }
                        if (total == 0) {
                            MekOres.LOGGER.warn("Using fallback colour; no pixels in 1 stddev: " + type.name());
                            type.colour = computeFallbackColour(atlas, sprite);
                            continue;
                        }
                        type.colour = ((rBin / total) << 16) | ((gBin / total) << 8) | (bBin / total);
                    } catch (Exception e) {
                        MekOres.LOGGER.warn("Failed to calculate colour for " + type.name(), e);
                    }
                }
            }
        }
        time += System.currentTimeMillis();
        MekOres.LOGGER.info("Computed ore colours in {} ms", time);
    }

    private static float calcWeight(float[] hsb) {
        // kind of gross step function but it seems to produce okay results
        return hsb[2] > 0.8F ? 1F : (hsb[2] > 0.5F ? 0.5F : 0.1F);
    }

    private static double weightedStdDev(List<float[]> data, int index, float mean, float weightTotal) {
        float acc = 0;
        for (float[] datum : data) {
            float diff = datum[index] - mean;
            acc += diff * diff * calcWeight(datum);
        }
        return Math.sqrt(acc * data.size() / ((data.size() - 1) * weightTotal));
    }

    private static boolean withinStdDev(float datum, float mean, double stdDev) {
        return Math.abs(datum - mean) <= stdDev;
    }

    private static int computeFallbackColour(int[] atlas, TextureAtlasSprite sprite) {
        int rBin = 0, gBin = 0, bBin = 0, total = 0;
        for (int y = 0; y < sprite.getIconHeight(); y++) {
            for (int x = 0; x < sprite.getIconWidth(); x++) {
                int index = (y + sprite.getOriginY()) * ClientEventListener.getAtlasWidth() + x + sprite.getOriginX();
                if ((atlas[index] & 0xFF) > 0) {
                    rBin += (atlas[index] >> 8) & 0xFF;
                    gBin += (atlas[index] >> 16) & 0xFF;
                    bBin += (atlas[index] >> 24) & 0xFF;
                    ++total;
                }
            }
        }
        if (total == 0) {
            MekOres.LOGGER.warn("Using a bland shade of grey; no non-empty pixels!");
            return 0xFF424242;
        }
        return ((rBin / total) << 16) | ((gBin / total) << 8) | (bBin / total);
    }

    private static final Set<String> BLACKLIST;
    private static final Set<String> GEM_WHITELIST;

    static {
        BLACKLIST = Sets.newHashSet(
                "Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Osmium", "Bitumen", "Potash",
                "Coal", "Sulfur", "Sulphur", "Redstone", "Electrotine", "CertusQuartz");
        BLACKLIST.addAll(Arrays.asList(CommonProxy.CONFIG.blacklist));
        GEM_WHITELIST = Stream.of(DILITHIUM).map(t -> t.key).collect(Collectors.toSet());
    }

    public static OreValidity validateKey(String key) {
        if (BLACKLIST.contains(key)) {
            return OreValidity.INVALID_BLACKLIST;
        }
        if (!GEM_WHITELIST.contains(key)) {
            List<ItemStack> gems = OreDictionary.getOres("gem" + key);
            if (!gems.isEmpty()) {
                return new OreValidity(OreValidity.Reason.INVALID_GEM, gems);
            }
        }
        if (!OreDictHelper.exists("ore" + key)) {
            return OreValidity.INVALID_NO_ORE;
        }
        if (!OreDictHelper.exists("ingot" + key) && !OreDictHelper.exists("dust" + key)) {
            return OreValidity.INVALID_NO_RESULT;
        }
        List<ItemStack> dustsDirty = OreDictionary.getOres("dustDirty" + key);
        if (dustsDirty.isEmpty()) {
            return OreValidity.VALID;
        }
        List<ItemStack> clumps = OreDictionary.getOres("clump" + key);
        if (clumps.isEmpty()) {
            return OreValidity.VALID;
        }
        List<ItemStack> shards = OreDictionary.getOres("shard" + key);
        if (shards.isEmpty()) {
            return OreValidity.VALID;
        }
        List<ItemStack> crystals = OreDictionary.getOres("crystal" + key);
        if (crystals.isEmpty()) {
            return OreValidity.VALID;
        }
        return new OreValidity(OreValidity.Reason.INVALID_EXISTING_INTEGRATION,
                Stream.of(crystals, shards, clumps, dustsDirty).flatMap(List::stream).collect(Collectors.toList()));
    }

}
