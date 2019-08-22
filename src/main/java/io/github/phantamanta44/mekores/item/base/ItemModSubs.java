package io.github.phantamanta44.mekores.item.base;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemModSubs extends ItemMod {

    protected final int subs;

    public ItemModSubs(String name, int subs) {
        super(name);
        setHasSubtypes(true);
        this.subs = subs;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for (int i = 0; i < subs; i++)
            subItems.add(new ItemStack(this, 1, i));
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + stack.getItemDamage();
    }

}
