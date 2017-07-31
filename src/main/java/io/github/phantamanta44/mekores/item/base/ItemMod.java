package io.github.phantamanta44.mekores.item.base;

import io.github.phantamanta44.mekores.constant.MOConst;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemMod extends Item {

    public ItemMod(String name) {
        registerItem(name);
    }

    protected void registerItem(String name) {
        setUnlocalizedName(name);
        if (!Item.REGISTRY.containsKey(new ResourceLocation(MOConst.MOD_ID, name))) {
            setRegistryName(name);
            GameRegistry.register(this);
        }
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack stack) {
        return super.getUnlocalizedNameInefficiently(stack).replaceAll("item\\.", "item." + MOConst.MOD_PREF);
    }

}
