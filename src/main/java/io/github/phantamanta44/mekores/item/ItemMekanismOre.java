package io.github.phantamanta44.mekores.item;

import io.github.phantamanta44.mekores.CommonProxy;
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
import java.util.Objects;

import static io.github.phantamanta44.mekores.ore.OreStage.*;

public class ItemMekanismOre extends ItemModSubs {

    private static final String[] VANILLA_ORE_NAMES = { "Iron", "Gold", "Osmium", "Copper", "Tin", "Silver", "Lead" };

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
        Gas hCl = GasRegistry.getGas("hydrogenchloride");
        Gas oxygen = GasRegistry.getGas("oxygen");
        for (OreType type : OreType.values()) {
            if (type.isValid()) {
                try {
                    MekOres.LOGGER.debug("Adding ore recipes for {}", type.key);
                    Collection<ItemStack> ores = OreDictionary.getOres("ore" + type.key);

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

                    // netherending ores and friends
                    if (CommonProxy.CONFIG.oreNetherMultiplier != 0) {
                        try {
                            NonNullList<ItemStack> netherOres = OreDictionary.getOres("oreNether" + type.key);
                            if (!netherOres.isEmpty()) {
                                MekOres.LOGGER.debug("Adding nether ore recipes for {}", type.key);
                                registerMultRecipes(
                                        type, netherOres, CommonProxy.CONFIG.oreNetherMultiplier, hCl, oxygen);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to register nether ore recipes for ore: " + type, e);
                        }
                    }
                    if (CommonProxy.CONFIG.oreEndMultiplier != 0) {
                        try {
                            NonNullList<ItemStack> endOres = OreDictionary.getOres("oreEnd" + type.key);
                            if (!endOres.isEmpty()) {
                                MekOres.LOGGER.debug("Adding end ore recipes for {}", type.key);
                                registerMultRecipes(
                                        type, endOres, CommonProxy.CONFIG.oreEndMultiplier, hCl, oxygen);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to register end ore recipes for ore: " + type, e);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to register recipes for ore: " + type, e);
                }
            }
        }

        // netherending ores support for ores with vanilla mekanism support
        if (CommonProxy.CONFIG.oreNetherMultiplier != 0) {
            for (String oreName : VANILLA_ORE_NAMES) {
                try {
                    MekOres.LOGGER.debug("Adding nether ore recipes for {}", oreName);
                    registerMultRecipes("oreNether", oreName, CommonProxy.CONFIG.oreNetherMultiplier, hCl, oxygen);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to register nether ore recipes for ore: " + oreName, e);
                }
            }
        }
        if (CommonProxy.CONFIG.oreEndMultiplier != 0) {
            for (String oreName : VANILLA_ORE_NAMES) {
                try {
                    MekOres.LOGGER.debug("Adding end ore recipes for {}", oreName);
                    registerMultRecipes("oreEnd", oreName, CommonProxy.CONFIG.oreEndMultiplier, hCl, oxygen);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to register end ore recipes for ore: " + oreName, e);
                }
            }
        }
    }

    private static void registerMultRecipes(OreType type, List<ItemStack> ores, int mult, Gas hCl, Gas oxygen) {
        registerMultRecipes(type.getName(), type.key, ores, mult, hCl, oxygen);
    }

    private static void registerMultRecipes(String prefix, String name, int mult, Gas hCl, Gas oxygen) {
        registerMultRecipes(name.toLowerCase(), name, OreDictionary.getOres(prefix + name), mult, hCl, oxygen);
    }

    private static void registerMultRecipes(String gasName, String key, List<ItemStack> ores, int mult,
                                            Gas hCl, Gas oxygen) {
        for (ItemStack stack : ores) {
            IMCHelper.addChemicalDissolutionRecipe(stack, GasHelper.gasStack(gasName, 1000 * mult));
        }
        for (ItemStack stack : ores) {
            IMCHelper.addChemicalInjectionRecipe(stack, hCl, Objects.requireNonNull(SHARD.getOre(key, 4 * mult)));
        }
        for (ItemStack stack : ores) {
            IMCHelper.addPurificationRecipe(stack, oxygen, Objects.requireNonNull(CLUMP.getOre(key, 3 * mult)));
        }
        for (ItemStack stack : ores) {
            IMCHelper.addEnrichmentRecipe(stack, Objects.requireNonNull(DUST.getOre(key, 2 * mult)));
        }
    }

}
