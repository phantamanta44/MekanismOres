package io.github.phantamanta44.mekores.item;

import io.github.phantamanta44.mekores.client.ClientProxy;
import io.github.phantamanta44.mekores.constant.LangConst;
import io.github.phantamanta44.mekores.item.base.ItemModSubs;
import io.github.phantamanta44.mekores.ore.OreStage;
import io.github.phantamanta44.mekores.ore.OreType;
import io.github.phantamanta44.mekores.ore.SpecificOreStage;
import io.github.phantamanta44.mekores.util.GasHelper;
import io.github.phantamanta44.mekores.util.IMCHelper;
import io.github.phantamanta44.mekores.util.OreDictHelper;
import mekanism.api.gas.GasRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;

public class ItemMekanismOre extends ItemModSubs {

    private static SpecificOreStage[] registry;

    public static SpecificOreStage getStage(ItemStack stack) {
        return registry[stack.getMetadata()];
    }

    private static int buildRegistry() { // TODO Configurability
        registry = Arrays.stream(OreType.values())
                .flatMap(t -> Arrays.stream(OreStage.values())
                        .filter(s -> !s.exists(t))
                        .map(s -> new SpecificOreStage(t, s)))
                .toArray(SpecificOreStage[]::new);
        return registry.length;
    }

    public ItemMekanismOre() {
        super(LangConst.ITEM_MEK_ORE_NAME, buildRegistry());
        for (int i = 0; i < registry.length; i++) {
            SpecificOreStage stage = registry[i];
            OreDictionary.registerOre(stage.getEntry(), new ItemStack(this, 1, i));
            switch (stage.stage) {
                case CRYSTAL:
                    ClientProxy.registerItemModel(this, i, "crystal");
                    break;
                case SHARD:
                    ClientProxy.registerItemModel(this, i, "shard");
                    break;
                case CLUMP:
                    ClientProxy.registerItemModel(this, i, "clump");
                    break;
                case DIRTY_DUST:
                    ClientProxy.registerItemModel(this, i, "dirty_dust");
                    break;
                case DUST:
                    ClientProxy.registerItemModel(this, i, "dust");
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return getStage(stack).getLocalizedName();
    }

    public void registerRecipes() {
        for (SpecificOreStage stage : registry) {
            if (stage.type.isValid()) {
                switch (stage.stage) {
                    case CRYSTAL:
                        GasHelper.registerOreGas(stage.type);
                        for (ItemStack stack : OreDictionary.getOres("ore" + stage.type.key)) {
                            IMCHelper.addChemicalDissolutionRecipe(
                                    stack, GasHelper.gasStack(stage.type.getName(), 1000));
                        }
                        IMCHelper.addChemicalWasherRecipe(
                                GasHelper.gasStack(stage.type.getName(), 1),
                                GasHelper.gasStack("clean" + stage.type.getName(), 1));
                        IMCHelper.addChemicalCrystalizationRecipe(
                                GasHelper.gasStack("clean" + stage.type.getName(), 200),
                                stage.getOre(1));
                        IMCHelper.addChemicalInjectionRecipe(
                                stage.getOre(1),
                                GasRegistry.getGas("hydrogenchloride"),
                                nextStage(stage, 1));
                        for (ItemStack stack : OreDictionary.getOres("ore" + stage.type.key)) {
                            IMCHelper.addChemicalInjectionRecipe(
                                    stack, GasRegistry.getGas("hydrogenchloride"), nextStage(stage, 4));
                        }
                        break;
                    case SHARD:
                        IMCHelper.addPurificationRecipe(
                                stage.getOre(1),
                                GasRegistry.getGas("oxygen"),
                                nextStage(stage, 1));
                        for (ItemStack stack : OreDictionary.getOres("ore" + stage.type.key)) {
                            IMCHelper.addPurificationRecipe(
                                    stack, GasRegistry.getGas("oxygen"), nextStage(stage, 3));
                        }
                        break;
                    case CLUMP:
                        IMCHelper.addCrusherRecipe(
                                stage.getOre(1),
                                nextStage(stage, 1));
                        break;
                    case DIRTY_DUST:
                        IMCHelper.addEnrichmentRecipe(stage.getOre(1), nextStage(stage, 1));
                        for (ItemStack stack : OreDictionary.getOres("ore" + stage.type.key))
                            IMCHelper.addEnrichmentRecipe(stack, nextStage(stage, 2));
                        break;
                    case DUST:
                        if (OreDictHelper.exists("ingot" + stage.type.key)) {
                            GameRegistry.addSmelting(
                                    stage.getOre(1), OreDictHelper.getStack("ingot" + stage.type.key, 1), 0);
                        }
                        break;
                }
            }
        }
    }

    private static ItemStack nextStage(SpecificOreStage stage, int count) {
        return stage.stage.next().getOre(stage.type.key, count);
    }

}
