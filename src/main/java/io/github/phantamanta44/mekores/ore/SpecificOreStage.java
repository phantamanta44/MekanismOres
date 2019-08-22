package io.github.phantamanta44.mekores.ore;

import io.github.phantamanta44.mekores.MekOres;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

public class SpecificOreStage {

    public final OreType type;
    public final OreStage stage;
    @Nullable
    private Boolean valid = null;

    public SpecificOreStage(OreType type, OreStage stage) {
        this.type = type;
        this.stage = stage;
    }

    public String getEntry() {
        return stage.getEntry(type.key);
    }

    public ItemStack getOre(int qty) {
        return Objects.requireNonNull(stage.getOre(type.key, qty));
    }

    public String getLocalizedName() {
        return stage.getLocalizedName(type);
    }

    public boolean isValid() {
        if (!MekOres.PROXY.isInitialized()) {
            MekOres.LOGGER.warn("Validation occurring early for stage {}{}!", stage.prefix, type.key);
            MekOres.LOGGER.warn("You may get inconsistent results!", new IllegalStateException());
            return OreType.validateKey(type.key).isValid() && !stage.exists(type);
        }
        return valid == null ? (valid = isValid0()) : valid;
    }

    private boolean isValid0() {
        return type.isValid() && !stage.exists(type);
    }

}
