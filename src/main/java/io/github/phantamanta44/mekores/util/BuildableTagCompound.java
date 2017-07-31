package io.github.phantamanta44.mekores.util;

import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.function.Consumer;

public class BuildableTagCompound extends NBTTagCompound {

    public BuildableTagCompound withTag(String key, NBTTagCompound tag) {
        super.setTag(key, tag);
        return this;
    }

    public BuildableTagCompound withInt(String key, int value) {
        super.setInteger(key, value);
        return this;
    }

    public BuildableTagCompound withByte(String key, int value) {
        super.setByte(key, (byte)value);
        return this;
    }

    public BuildableTagCompound withBool(String key, boolean value) {
        super.setBoolean(key, value);
        return this;
    }

    public BuildableTagCompound withStr(String key, String value) {
        super.setString(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends NBTBase> NBTTagCompound withList(String key, T... tags) {
        super.setTag(key, new NBTTagList());
        Arrays.stream(tags).forEach(super.getTagList(key, tags[0].getId())::appendTag);
        return this;
    }

    public BuildableTagCompound withItemStack(String key, ItemStack value) {
        return withSerializable(key, value::writeToNBT);
    }

    public BuildableTagCompound withFluidStack(String key, FluidStack value) {
        return withSerializable(key, value::writeToNBT);
    }

    public BuildableTagCompound withGasStack(String key, GasStack value) {
        return withSerializable(key, value::write);
    }

    public BuildableTagCompound withSerializable(String key, Consumer<NBTTagCompound> writer) {
        super.setTag(key, new NBTTagCompound());
        writer.accept(super.getCompoundTag(key));
        return this;
    }

}