package io.github.phantamanta44.mekores;

import io.github.phantamanta44.mekores.constant.MOConst;
import io.github.phantamanta44.mekores.item.MOItems;
import io.github.phantamanta44.mekores.ore.OreType;
import io.github.phantamanta44.mekores.ore.OreValidity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class CommonProxy {

    private final Collection<Item> itemsToRegister = new LinkedList<>();
    private final Collection<Pair<ItemStack, String>> oresToRegister = new LinkedList<>();
    private boolean initialized = false;

    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onInit() {
        initialized = true;
        MOItems.mekanismOre.registerOreDict();
        MOItems.mekanismOre.registerRecipes();
    }

    public void onPostInit() {
        MekOres.LOGGER.info("Searching for valid Mekanism ores...");
        Set<String> modIds = new HashSet<>();
        int valid = 0, invalid = 0;
        for (String name : OreDictionary.getOreNames()) {
            if (name.startsWith("ore")) {
                String key = name.substring(3);
                OreType type = OreType.getByKey(name.substring(3));
                if (type != null) {
                    OreValidity validity = type.getValidity();
                    if (validity.isValid()) {
                        reportOre(type.key, modIds);
                        ++valid;
                    } else if (OreType.validateKey(key).isValid()) {
                        reportOre(type.key, modIds);
                        MekOres.LOGGER.warn("Ore {} was marked as {}! This might be a bug.", type.key, validity);
                        ++invalid;
                    } else {
                        MekOres.LOGGER.debug("Ore {} was marked {}.", type.key, validity);
                    }
                } else {
                    OreValidity validity = OreType.validateKey(key);
                    if (validity.isValid()) {
                        reportOre(key, modIds);
                        MekOres.LOGGER.warn("Unknown ore {}!", key);
                        ++invalid;
                    } else {
                        MekOres.LOGGER.trace("Discovered ore {} with validity {}.", key, validity);
                    }
                }
            }
        }
        MekOres.LOGGER.info("Found {} valid, {} invalid ores from mod(s) {}", valid, invalid, String.join(", ", modIds));
    }

    private static void reportOre(String key, Set<String> modIds) {
        String mods = OreDictionary.getOres("ore" + key).stream()
                .map(o -> Objects.requireNonNull(o.getItem().getRegistryName()).getNamespace())
                .peek(modIds::add)
                .collect(Collectors.joining(", "));
        MekOres.LOGGER.info("Found ore {} from mod(s) {}", key, mods);
    }

    public boolean isInitialized() {
        return initialized;
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

    @Config(modid = MOConst.MOD_ID)
    public static class CONFIG {

        @Config.RequiresMcRestart
        @Config.Comment({
                "A list of ore types to disable processing for.",
                "Entries should be formatted as oredict suffixes (e.g. Copper, DeepIron, AstralStarmetal)"
        })
        public static String[] blacklist = new String[0];

        @Config.RequiresMcRestart
        @Config.Comment({
                "The multiplier for processing nether ores. Set to 0 to disable direct processing.",
                "If disabled, nether ores must be smelted into overworld ores before processing."
        })
        @Config.RangeInt(min = 0)
        public static int oreNetherMultiplier = 0;

        @Config.RequiresMcRestart
        @Config.Comment({
                "The multiplier for processing end ores. Set to 0 to disable direct processing.",
                "If disabled, end ores must be smelted into overworld ores before processing."
        })
        @Config.RangeInt(min = 0)
        public static int oreEndMultiplier = 0;

    }

}
