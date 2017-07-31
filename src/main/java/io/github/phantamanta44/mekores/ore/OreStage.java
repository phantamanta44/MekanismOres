package io.github.phantamanta44.mekores.ore;

import io.github.phantamanta44.mekores.constant.LangConst;
import io.github.phantamanta44.mekores.constant.MOConst;
import io.github.phantamanta44.mekores.util.OreDictHelper;
import net.minecraft.item.ItemStack;

public enum OreStage {

    CRYSTAL("crystal"),
    SHARD("shard"),
    CLUMP("clump"),
    DIRTY_DUST("dustDirty"),
    DUST("dust");

    public final String prefix;

    OreStage(String prefix) {
        this.prefix = prefix;
    }

    public String getEntry(String ore) {
        return prefix + ore;
    }

    public ItemStack getOre(String ore, int qty) {
        return OreDictHelper.getStack(getEntry(ore), qty);
    }

    public OreStage next() {
        return values()[ordinal() + 1];
    }

    public boolean exists(OreType type) {
        return OreDictHelper.exists(getEntry(type.key));
    }

    public String getLocalizedName(OreType type) {
        return LangConst.get("item." + MOConst.MOD_PREF + prefix + ".name", type.getLocalizedName());
    }

}