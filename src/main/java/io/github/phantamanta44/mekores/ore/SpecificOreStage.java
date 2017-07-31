package io.github.phantamanta44.mekores.ore;

import net.minecraft.item.ItemStack;

public class SpecificOreStage {

    public final OreType type;
    public final OreStage stage;

    public SpecificOreStage(OreType type, OreStage stage) {
        this.type = type;
        this.stage = stage;
    }

    public String getEntry() {
        return stage.getEntry(type.key);
    }

    public ItemStack getOre(int qty) {
        return stage.getOre(type.key, qty);
    }

    public String getLocalizedName() {
        return stage.getLocalizedName(type);
    }

}
