package io.github.phantamanta44.mekores.item;

import io.github.phantamanta44.mekores.constant.MOConst;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@SuppressWarnings("NullableProblems")
public class MOItems {

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MOConst.MOD_ID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.IRON_INGOT);
        }
    };

    public static ItemMekanismOre mekanismOre;

    public static void init() {
        mekanismOre = new ItemMekanismOre();
    }

}
