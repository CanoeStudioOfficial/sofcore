package com.canoestudio.sofcore.items;

import com.canoestudio.sofcore.SOFcore;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class SOFCurrencyItem extends Item {

    public SOFCurrencyItem(String name) {
        setRegistryName(SOFcore.MOD_ID, name);
        setTranslationKey(SOFcore.MOD_ID + "." + name);
        setCreativeTab(CreativeTabs.MISC);
    }
}
