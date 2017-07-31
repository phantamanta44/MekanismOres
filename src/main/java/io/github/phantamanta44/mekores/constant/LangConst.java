package io.github.phantamanta44.mekores.constant;

import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public class LangConst {

    public static final String ITEM_MEK_ORE_NAME = "mekanismOre";

    public static final String MISC_KEY = MOConst.MOD_ID + ".misc.";
    public static final String MISC_ORE = MISC_KEY + "ore";
    public static final String MISC_SLURRY = MISC_KEY + "slurry";

    public static final String ORE_KEY = MOConst.MOD_ID + ".ore.";

    public static String get(String key, Object... args) {
        return String.format(I18n.translateToLocal(key), args);
    }

}
