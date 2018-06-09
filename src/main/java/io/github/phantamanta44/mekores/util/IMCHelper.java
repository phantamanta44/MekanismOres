package io.github.phantamanta44.mekores.util;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class IMCHelper {

    private static final String MOD_ID_MEKANISM = "mekanism";

    public static void addChemicalCrystalizationRecipe(GasStack input, ItemStack output) {
        message("ChemicalCrystallizerRecipe", new BuildableTagCompound()
                .withGasStack("input", input)
                .withItemStack("output", output));
    }

    public static void addChemicalDissolutionRecipe(ItemStack input, GasStack output) {
        message("ChemicalDissolutionChamberRecipe", new BuildableTagCompound()
                .withItemStack("input", input)
                .withGasStack("output", output));
    }

    public static void addChemicalInjectionRecipe(ItemStack inputItem, Gas inputGas, ItemStack output) {
        message("ChemicalInjectionChamberRecipe", new BuildableTagCompound()
                .withItemStack("input", inputItem)
                .withSerializable("gasType", inputGas::write)
                .withItemStack("output", output));
    }

    public static void addChemicalWasherRecipe(GasStack input, GasStack output) {
        message("ChemicalWasherRecipe", new BuildableTagCompound()
                .withGasStack("input", input)
                .withGasStack("output", output));
    }

    public static void addCrusherRecipe(ItemStack input, ItemStack output) {
        message("CrusherRecipe", new BuildableTagCompound()
                .withItemStack("input", input)
                .withItemStack("output", output));
    }

    public static void addEnrichmentRecipe(ItemStack input, ItemStack output) {
        message("EnrichmentChamberRecipe", new BuildableTagCompound()
                .withItemStack("input", input)
                .withItemStack("output", output));
    }

    public static void addPurificationRecipe(ItemStack inputItem, Gas inputGas, ItemStack output) {
        message("PurificationChamberRecipe", new BuildableTagCompound()
                .withItemStack("input", inputItem)
                .withSerializable("gasType", inputGas::write)
                .withItemStack("output", output));
    }

    private static void message(String method, NBTTagCompound nbt) {
        FMLInterModComms.sendMessage(MOD_ID_MEKANISM, method, nbt);
    }

}