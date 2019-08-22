package io.github.phantamanta44.mekores.item.base;

import io.github.phantamanta44.mekores.MekOres;
import io.github.phantamanta44.mekores.constant.MOConst;
import io.github.phantamanta44.mekores.item.MOItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemMod extends Item {

    public ItemMod(String name) {
        registerItem(name);
        setCreativeTab(MOItems.CREATIVE_TAB);
    }

    protected void registerItem(String name) {
        setTranslationKey(name);
        if (!Item.REGISTRY.containsKey(new ResourceLocation(MOConst.MOD_ID, name))) {
            setRegistryName(name);
            MekOres.PROXY.queueRegistration(this);
        }
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack stack) {
        return super.getUnlocalizedNameInefficiently(stack).replaceAll("item\\.", "item." + MOConst.MOD_PREF);
    }

}
