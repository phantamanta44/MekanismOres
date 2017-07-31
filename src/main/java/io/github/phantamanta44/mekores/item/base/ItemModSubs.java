package io.github.phantamanta44.mekores.item.base;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemModSubs extends ItemMod {

    protected final int subs;

    public ItemModSubs(String name, int subs) {
        super(name);
        setHasSubtypes(true);
        this.subs = subs;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < subs; i++)
            subItems.add(new ItemStack(item, 1, i));
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + stack.getItemDamage();
    }

}
