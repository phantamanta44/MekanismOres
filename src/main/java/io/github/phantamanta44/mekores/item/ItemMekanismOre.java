package io.github.phantamanta44.mekores.item;

import io.github.phantamanta44.mekores.MekOres;
import io.github.phantamanta44.mekores.constant.LangConst;
import io.github.phantamanta44.mekores.item.base.ItemModSubs;
import io.github.phantamanta44.mekores.ore.OreStage;
import io.github.phantamanta44.mekores.ore.OreType;
import io.github.phantamanta44.mekores.ore.SpecificOreStage;
import io.github.phantamanta44.mekores.util.GasHelper;
import io.github.phantamanta44.mekores.util.IMCHelper;
import io.github.phantamanta44.mekores.util.OreDictHelper;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.github.phantamanta44.mekores.ore.OreStage.*;

public class ItemMekanismOre extends ItemModSubs {

    @SuppressWarnings("NullableProblems")
    private static SpecificOreStage[] registry;

    public static SpecificOreStage getStage(ItemStack stack) {
        return registry[stack.getMetadata()];
    }

    private static int buildRegistry() {
        registry = Arrays.stream(OreType.values())
                .peek(GasHelper::registerOreGas)
                .flatMap(t -> Arrays.stream(OreStage.values()).map(s -> new SpecificOreStage(t, s)))
                .toArray(SpecificOreStage[]::new);
        return registry.length;
    }

    ItemMekanismOre() {
        super(LangConst.ITEM_MEK_ORE_NAME, buildRegistry());
        for (int i = 0; i < registry.length; i++) {
            SpecificOreStage stage = registry[i];
            switch (stage.stage) {
                case CRYSTAL:
                    MekOres.PROXY.registerItemModel(this, i, "crystal");
                    break;
                case SHARD:
                    MekOres.PROXY.registerItemModel(this, i, "shard");
                    break;
                case CLUMP:
                    MekOres.PROXY.registerItemModel(this, i, "clump");
                    break;
                case DIRTY_DUST:
                    MekOres.PROXY.registerItemModel(this, i, "dirty_dust");
                    break;
                case DUST:
                    MekOres.PROXY.registerItemModel(this, i, "dust");
            }
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (tab == MOItems.CREATIVE_TAB) {
            for (int i = 0; i < subs; i++) {
                ItemStack stack = new ItemStack(this, 1, i);
                if (getStage(stack).isValid()) {
                    subItems.add(stack);
                }
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return getStage(stack).getLocalizedName();
    }

    public void registerOreDict() {
        for (int i = 0; i < registry.length; i++) {
            ItemStack stack = new ItemStack(this, 1, i);
            SpecificOreStage stage = getStage(stack);
            if (stage.isValid()) {
                OreDictionary.registerOre(stage.getEntry(), stack);
            }
        }
    }

    public void registerRecipes() {
        for (OreType type : OreType.values()) {
            if (type.isValid()) {
                try {
                    Collection<ItemStack> ores = OreDictionary.getOres("ore" + type.key);
                    Gas hCl = GasRegistry.getGas("hydrogenchloride");
                    Gas oxygen = GasRegistry.getGas("oxygen");

                    // ore to crystals
                    for (ItemStack stack : ores) {
                        IMCHelper.addChemicalDissolutionRecipe(stack, GasHelper.gasStack(type.getName(), 1000));
                    }
                    IMCHelper.addChemicalWasherRecipe(
                            GasHelper.gasStack(type.getName(), 1), GasHelper.gasStack("clean" + type.getName(), 1));
                    IMCHelper.addChemicalCrystalizationRecipe(
                            GasHelper.gasStack("clean" + type.getName(), 200), CRYSTAL.oreForType(type, 1));

                    // crystals to shards
                    IMCHelper.addChemicalInjectionRecipe(CRYSTAL.oreForType(type, 1), hCl, SHARD.oreForType(type, 1));

                    // ore to shards
                    for (ItemStack stack : ores) {
                        IMCHelper.addChemicalInjectionRecipe(stack, hCl, SHARD.oreForType(type, 4));
                    }

                    // shards to clumps
                    IMCHelper.addPurificationRecipe(SHARD.oreForType(type, 1), oxygen, CLUMP.oreForType(type, 1));

                    // ore to clumps
                    for (ItemStack stack : ores) {
                        IMCHelper.addPurificationRecipe(stack, oxygen, CLUMP.oreForType(type, 3));
                    }

                    // clumps to dirty dust
                    IMCHelper.addCrusherRecipe(CLUMP.oreForType(type, 1), DIRTY_DUST.oreForType(type, 1));

                    // dirty dust to dust
                    IMCHelper.addEnrichmentRecipe(DIRTY_DUST.oreForType(type, 1), DUST.oreForType(type, 1));

                    // ore to dust
                    for (ItemStack stack : ores) {
                        IMCHelper.addEnrichmentRecipe(stack, DUST.oreForType(type, 2));
                    }

                    // dust to ingot
                    List<ItemStack> dusts = OreDictionary.getOres(DUST.getEntry(type.key));
                    // only add the recipe if no other mods add a dust for this metal type
                    // deals with the possible case where dust-to-ingot processing involves more than a furnace
                    if (dusts.stream().allMatch(is -> is.getItem() instanceof ItemMekanismOre)) {
                        ItemStack ingotStack = OreDictHelper.getStack("ingot" + type.key, 1);
                        if (ingotStack != null) {
                            GameRegistry.addSmelting(DUST.oreForType(type, 1), ingotStack, 0);
                        }
                    }

                    // dust to ore
                    ores.stream().findAny().ifPresent(oreStack -> {
                        ItemStack cobble = new ItemStack(Blocks.COBBLESTONE, 1);
                        for (ItemStack dust : dusts) {
                            IMCHelper.addCombiningRecipe(ItemHandlerHelper.copyStackWithSize(dust, 8), cobble, oreStack);
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException("Failed to register recipes for ore: " + type, e);
                }
            }
        }
    }

}
