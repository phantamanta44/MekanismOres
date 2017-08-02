package io.github.phantamanta44.mekores;

import io.github.phantamanta44.mekores.item.MOItems;

public class CommonProxy {

    public void onPreInit() {
        // NO-OP
    }

    public void onInit() {
        MOItems.mekanismOre.registerRecipes();
    }

    public void onPostInit() {
        // NO-OP
    }

}
