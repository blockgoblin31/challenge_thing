package com.blockgoblin31.challengemodthing.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.EntityHelper;
import vazkii.botania.common.item.BlackLotusItem;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.XplatAbstractions;

public class BlackestLotusItem extends BlackLotusItem {

    public BlackestLotusItem(Properties props) {
        super(props);
    }

    @Override
    public void onDissolveTick(ManaPool pool, ItemEntity item) {
        if (!pool.isFull() && pool.getCurrentMana() != 0) {
            BlockPos pos = pool.getManaReceiverPos();
            if (!item.level().isClientSide) {
                pool.receiveMana(1000000);
                EntityHelper.shrinkItem(item);
                XplatAbstractions.INSTANCE.sendToTracking(item, new BotaniaEffectPacket(EffectType.BLACK_LOTUS_DISSOLVE, (double)pos.getX(), (double)pos.getY() + 0.5, (double)pos.getZ(), new int[0]));
            }

            item.playSound(BotaniaSounds.blackLotus, 1.0F, 1.5F);
        }
    }
}
