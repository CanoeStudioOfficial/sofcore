package com.canoestudio.sofcore.proxy;

import com.canoestudio.sofcore.SOFcore;
import com.canoestudio.sofcore.compat.WaystonesToXaeroCompat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);


    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        if (Loader.isModLoaded("waystones") && Loader.isModLoaded("xaerominimap")) {
            MinecraftForge.EVENT_BUS.register(WaystonesToXaeroCompat.class);
            SOFcore.LOGGER.info("Enabled Waystones to Xaero waypoint compatibility.");
        }
    }
}
