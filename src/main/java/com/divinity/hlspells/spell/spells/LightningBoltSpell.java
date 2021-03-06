package com.divinity.hlspells.spell.spells;

import com.divinity.hlspells.spell.Spell;
import com.divinity.hlspells.spell.SpellAttributes;
import com.divinity.hlspells.spell.SpellConsumer;
import com.divinity.hlspells.util.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LightningBoltSpell extends Spell {

    public LightningBoltSpell(String displayName, int xpCost, boolean treasureOnly) {
        super(SpellAttributes.Type.CAST, SpellAttributes.Rarity.RARE, SpellAttributes.Tier.THREE, SpellAttributes.Marker.COMBAT, displayName, xpCost, treasureOnly);
    }

    @Override
    public SpellConsumer<Player> getAction() {
        return p -> {
            HitResult rayTraceResult = Util.lookAt(p, 25D, 1F, false);
            Vec3 location = rayTraceResult.getLocation();
            int stepX = 0;
            int stepY = 0;
            int stepZ = 0;
            if (rayTraceResult instanceof BlockHitResult) {
                Direction rayTraceDirection = ((BlockHitResult) rayTraceResult).getDirection();
                stepX = rayTraceDirection.getStepX();
                stepY = rayTraceDirection.getStepY();
                stepZ = rayTraceDirection.getStepZ();
            }
            double dx = location.x() + stepX;
            double dy = location.y() + stepY - 1;
            double dz = location.z() + stepZ;
            LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, p.level);
            lightning.moveTo(dx, dy, dz);
            p.level.addFreshEntity(lightning);
            return true;
        };
    }
}
