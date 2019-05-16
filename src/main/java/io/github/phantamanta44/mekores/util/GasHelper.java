package io.github.phantamanta44.mekores.util;

import io.github.phantamanta44.mekores.constant.LangConst;
import io.github.phantamanta44.mekores.ore.OreType;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.OreGas;

public class GasHelper {

    public static GasStack gasStack(String name, int count) {
        return new GasStack(GasRegistry.getGas(name), count);
    }

    public static void registerOreGas(OreType type) {
        Gas cleanGas = GasRegistry.getGas("clean" + type.getName());
        if (cleanGas == null) {
            cleanGas = GasRegistry.register(new MekanismOreGas(type, true)).setVisible(false);
        }
        if (GasRegistry.getGas(type.getName()) == null) {
            OreGas dirtyGas = new MekanismOreGas(type, false);
            if (cleanGas instanceof OreGas) {
                dirtyGas.setCleanGas((OreGas)cleanGas);
            }
            GasRegistry.register(dirtyGas.setVisible(false));
        }
    }

    private static class MekanismOreGas extends OreGas {

        private final OreType type;

        MekanismOreGas(OreType type, boolean clean) {
            super(clean ? "clean" + type.getName() : type.getName(), type.getName());
            this.type = type;
        }

        @Override
        public String getOreName() {
            return LangConst.get(LangConst.MISC_ORE, type.getLocalizedName());
        }

        @Override
        public String getLocalizedName() {
            return LangConst.get(LangConst.MISC_SLURRY, type.getLocalizedName());
        }

        @Override
        public int getTint() {
            return type.getColour();
        }

    }

}
