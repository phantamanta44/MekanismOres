package io.github.phantamanta44.mekores;

import io.github.phantamanta44.mekores.constant.MOConst;
import io.github.phantamanta44.mekores.item.MOItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = MOConst.MOD_ID, version = MOConst.MOD_VERSION)
public class MekOres {

    public static final Logger LOGGER = LogManager.getLogger(MOConst.MOD_ID);

    @Mod.Instance(MOConst.MOD_ID)
    public static MekOres INSTANCE;

    @SidedProxy(
            serverSide = "io.github.phantamanta44.mekores.CommonProxy",
            clientSide = "io.github.phantamanta44.mekores.client.ClientProxy")
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        MOItems.init();
        PROXY.onPreInit();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        PROXY.onInit();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        PROXY.onPostInit();
    }

}
