package io.github.phantamanta44.mekores;

import io.github.phantamanta44.mekores.constant.MOConst;
import io.github.phantamanta44.mekores.item.MOItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MOConst.MOD_ID, version = MOConst.MOD_VERSION)
public class MekOres {

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

}
