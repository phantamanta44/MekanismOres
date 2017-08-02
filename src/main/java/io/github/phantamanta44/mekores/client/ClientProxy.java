package io.github.phantamanta44.mekores.client;

import io.github.phantamanta44.mekores.CommonProxy;
import io.github.phantamanta44.mekores.constant.MOConst;
import io.github.phantamanta44.mekores.item.ItemMekanismOre;
import io.github.phantamanta44.mekores.item.MOItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void onPreInit() {
        super.onPreInit();
        ClientEventListener events = new ClientEventListener();
        MinecraftForge.EVENT_BUS.register(events);
    }

    @Override
    public void onInit() {
        super.onInit();
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
            (stack, tint) -> ItemMekanismOre.getStage(stack).type.getColour(), MOItems.mekanismOre);
    }

    public static void registerItemModel(Item item, int meta, String name) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(MOConst.MOD_PREF + name));
    }

}
