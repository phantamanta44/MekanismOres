package io.github.phantamanta44.mekores;

import io.github.phantamanta44.mekores.item.MOItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.LinkedList;

public class CommonProxy {

    private final Collection<Item> itemsToRegister = new LinkedList<>();
    private final Collection<Pair<ItemStack, String>> oresToRegister = new LinkedList<>();

    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onInit() {
        MOItems.mekanismOre.registerRecipes();
    }

    public void onPostInit() {
        // NO-OP
    }

    public void queueRegistration(Item item) {
        itemsToRegister.add(item);
    }

    public void queueRegistration(ItemStack stack, String key) {
        oresToRegister.add(Pair.of(stack, key));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        itemsToRegister.forEach(event.getRegistry()::register);
        itemsToRegister.clear();
        oresToRegister.forEach(p -> OreDictionary.registerOre(p.getRight(), p.getLeft()));
    }

}
