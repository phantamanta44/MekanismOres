package io.github.phantamanta44.mekores.util;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class IMCHelper {

    private static final String MOD_ID_MEKANISM = "Mekanism";

    public static void addAmbientAccumulatorRecipe(int dimension, GasStack output) {
        message("AmbientAccumulatorRecipe", new BuildableTagCompound()
                .withInt("input", dimension)
                .withGasStack("output", output));
    }

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

    public static void addChemicalInfuserRecipe(GasStack inputA, GasStack inputB, GasStack output) {
        message("ChemicalInfuserRecipe", new BuildableTagCompound()
                .withGasStack("leftInput", inputA)
                .withGasStack("rightInput", inputB)
                .withGasStack("output", output));
    }

    public static void addChemicalInjectionRecipe(ItemStack inputItem, Gas inputGas, ItemStack output) {
        message("ChemicalInjectionChamberRecipe", new BuildableTagCompound()
                .withItemStack("input", inputItem)
                .withSerializable("gasType", inputGas::write)
                .withItemStack("output", output));
    }

    public static void addChemicalOxidizerRecipe(ItemStack input, GasStack output) {
        message("ChemicalOxidizerRecipe", new BuildableTagCompound()
                .withItemStack("input", input)
                .withGasStack("output", output));
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

    public static void addElectrolyticSeparatorRecipe(FluidStack input, GasStack outputA, GasStack outputB) {
        message("ElectrolyticSeparatorRecipe", new BuildableTagCompound()
                .withFluidStack("input", input)
                .withGasStack("leftOutput", outputA)
                .withGasStack("rightOutput", outputB));
    }

    public static void addEnrichmentRecipe(ItemStack input, ItemStack output) {
        message("EnrichmentChamberRecipe", new BuildableTagCompound()
                .withItemStack("input", input)
                .withItemStack("output", output));
    }

    public static void removeEnrichmentRecipe(ItemStack input, ItemStack output) {
        message("RemoveEnrichmentChamberRecipe", new BuildableTagCompound()
                .withItemStack("input", input)
                .withItemStack("output", output));
    }

    public static void addMetallurgicInfuserRecipe(ItemStack inputStack, String inputInfuse, int inputInfuseAmount, ItemStack output) {
        message("MetallurgicInfuserRecipe", new BuildableTagCompound()
                .withItemStack("input", inputStack)
                .withStr("infuseType", inputInfuse)
                .withInt("infuseAmount", inputInfuseAmount)
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