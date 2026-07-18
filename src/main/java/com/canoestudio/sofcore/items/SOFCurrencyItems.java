package com.canoestudio.sofcore.items;

import com.canoestudio.sofcore.SOFcore;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = SOFcore.MOD_ID)
public final class SOFCurrencyItems {

    public static final Item CURRENCY_1 = new SOFCurrencyItem("currency_1");
    public static final Item CURRENCY_5 = new SOFCurrencyItem("currency_5");
    public static final Item CURRENCY_10 = new SOFCurrencyItem("currency_10");
    public static final Item CURRENCY_25 = new SOFCurrencyItem("currency_25");
    public static final Item CURRENCY_100 = new SOFCurrencyItem("currency_100");
    public static final Item CURRENCY_500 = new SOFCurrencyItem("currency_500");
    public static final Item CURRENCY_1000 = new SOFCurrencyItem("currency_1000");
    public static final Item CURRENCY_2000 = new SOFCurrencyItem("currency_2000");
    public static final Item CURRENCY_5000 = new SOFCurrencyItem("currency_5000");
    public static final Item CURRENCY_10000 = new SOFCurrencyItem("currency_10000");

    public static final Item[] ALL = new Item[] {
            CURRENCY_1,
            CURRENCY_5,
            CURRENCY_10,
            CURRENCY_25,
            CURRENCY_100,
            CURRENCY_500,
            CURRENCY_1000,
            CURRENCY_2000,
            CURRENCY_5000,
            CURRENCY_10000
    };

    private SOFCurrencyItems() {
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ALL);
    }
}
