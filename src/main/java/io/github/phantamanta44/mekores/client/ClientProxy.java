package io.github.phantamanta44.mekores.client;

import io.github.phantamanta44.mekores.CommonProxy;
import io.github.phantamanta44.mekores.constant.MOConst;
import io.github.phantamanta44.mekores.item.ItemMekanismOre;
import io.github.phantamanta44.mekores.item.MOItems;
import io.github.phantamanta44.mekores.ore.OreType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.LinkedList;

public class ClientProxy extends CommonProxy {

    private final Collection<Triple<Item, Integer, ModelResourceLocation>> rendersToRegister = new LinkedList<>();

    private boolean gameInit = false;

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

    @Override
    public void onPostInit() {
        super.onPostInit();
    }

    @Override
    public void registerItemModel(Item item, int meta, String name) {
        rendersToRegister.add(Triple.of(item, meta, new ModelResourceLocation(MOConst.MOD_PREF + name, "inventory")));
    }

    @Override
    public void gameInit() {
        if (!gameInit) {
            gameInit = true;
            cacheOreColours();
        }
    }

    @Override
    public void cacheOreColours() {
        if (gameInit)
            OreType.cacheColours();
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        rendersToRegister.forEach(r ->
                ModelLoader.setCustomModelResourceLocation(r.getLeft(), r.getMiddle(), r.getRight()));
        rendersToRegister.clear();
    }

}
