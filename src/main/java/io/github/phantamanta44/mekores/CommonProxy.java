package io.github.phantamanta44.mekores;

import io.github.phantamanta44.mekores.item.MOItems;
import io.github.phantamanta44.mekores.ore.OreType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class CommonProxy {

    private final Collection<Item> itemsToRegister = new LinkedList<>();
    private final Collection<Pair<ItemStack, String>> oresToRegister = new LinkedList<>();

    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onInit() {
        MOItems.mekanismOre.registerOreDict();
        MOItems.mekanismOre.registerRecipes();
    }

    public void onPostInit() {
        MekOres.LOGGER.info("Searching for valid Mekanism ores...");
        Set<String> modIds = new HashSet<>();
        for (String name : OreDictionary.getOreNames()) {
            if (name.startsWith("ore")) {
                String key = name.substring(3);
                if (OreType.isKeyValid(key)) {
                    String mods = OreDictionary.getOres("ore" + key).stream()
                            .map(o -> Objects.requireNonNull(o.getItem().getRegistryName()).getResourceDomain())
                            .peek(modIds::add)
                            .collect(Collectors.joining(", "));
                    MekOres.LOGGER.info("Found ore {} from mod(s) {}", key, mods);
                    OreType ore = OreType.getByKey(key);
                    if (ore != null)
                        ore.setValid();
                    else
                        MekOres.LOGGER.warn("Unknown ore {}!", key);
                }
            }
        }
        MekOres.LOGGER.info("Found ores from mod(s) {}", String.join(", ", modIds));
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
        oresToRegister.clear();
    }

    public void registerItemModel(Item item, int meta, String name) {
        // NO-OP
    }

    public void gameInit() {
        // NO-OP
    }

    public void cacheOreColours() {
        // NO-OP
    }

}
