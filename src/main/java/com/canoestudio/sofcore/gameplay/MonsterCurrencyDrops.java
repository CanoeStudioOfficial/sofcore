package com.canoestudio.sofcore.gameplay;

import com.canoestudio.sofcore.SOFConfig;
import com.canoestudio.sofcore.SOFcore;
import com.canoestudio.sofcore.items.SOFCurrencyItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = SOFcore.MOD_ID)
public final class MonsterCurrencyDrops {

    private MonsterCurrencyDrops() {
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        SOFConfig.MonsterCurrencyDrops config = SOFConfig.monsterCurrencyDrops;
        EntityLivingBase entity = event.getEntityLiving();
        World world = entity.world;

        if (!config.enabled || world.isRemote || entity instanceof EntityPlayer || !(entity instanceof IMob)) {
            return;
        }

        if (config.requirePlayerKill && !wasKilledByPlayer(event.getSource())) {
            return;
        }

        if (config.dropChance < 1.0D && world.rand.nextDouble() > config.dropChance) {
            return;
        }

        int amount = calculateAmount(entity, config);
        if (amount <= 0) {
            return;
        }

        dropCurrencyAmount(world, entity.posX, entity.posY, entity.posZ, amount);
    }

    private static boolean wasKilledByPlayer(DamageSource source) {
        Entity trueSource = source.getTrueSource();
        if (trueSource instanceof EntityPlayer && !(trueSource instanceof FakePlayer)) {
            return true;
        }

        Entity immediateSource = source.getImmediateSource();
        if (immediateSource instanceof EntityArrow) {
            Entity shooter = ((EntityArrow) immediateSource).shootingEntity;
            return shooter instanceof EntityPlayer && !(shooter instanceof FakePlayer);
        }

        return false;
    }

    private static int calculateAmount(EntityLivingBase entity, SOFConfig.MonsterCurrencyDrops config) {
        double amount = entity.getMaxHealth() / config.mobDivisionValue;
        return Math.max(0, (int) Math.floor(amount));
    }

    private static void dropCurrencyAmount(World world, double x, double y, double z, int amount) {
        int remaining = amount;

        remaining = dropDenomination(world, x, y, z, SOFCurrencyItems.CURRENCY_10000, 10000, remaining);
        remaining = dropDenomination(world, x, y, z, SOFCurrencyItems.CURRENCY_5000, 5000, remaining);
        remaining = dropDenomination(world, x, y, z, SOFCurrencyItems.CURRENCY_2000, 2000, remaining);
        remaining = dropDenomination(world, x, y, z, SOFCurrencyItems.CURRENCY_1000, 1000, remaining);
        remaining = dropDenomination(world, x, y, z, SOFCurrencyItems.CURRENCY_500, 500, remaining);
        remaining = dropDenomination(world, x, y, z, SOFCurrencyItems.CURRENCY_100, 100, remaining);
        remaining = dropDenomination(world, x, y, z, SOFCurrencyItems.CURRENCY_25, 25, remaining);
        remaining = dropDenomination(world, x, y, z, SOFCurrencyItems.CURRENCY_10, 10, remaining);
        remaining = dropDenomination(world, x, y, z, SOFCurrencyItems.CURRENCY_5, 5, remaining);
        dropDenomination(world, x, y, z, SOFCurrencyItems.CURRENCY_1, 1, remaining);
    }

    private static int dropDenomination(World world, double x, double y, double z, Item item, int value, int amount) {
        if (item == null || amount < value) {
            return amount;
        }

        int count = amount / value;
        dropItemStacks(world, x, y, z, item, count);
        return amount - (count * value);
    }

    private static void dropItemStacks(World world, double x, double y, double z, Item item, int amount) {
        int remaining = amount;

        while (remaining > 0) {
            int stackSize = Math.min(remaining, Math.max(1, item.getItemStackLimit(new ItemStack(item))));
            EntityItem drop = new EntityItem(world, x, y, z, new ItemStack(item, stackSize));
            world.spawnEntity(drop);
            remaining -= stackSize;
        }
    }
}
