package com.divinity.hlspells.spell.spells;

import com.divinity.hlspells.capabilities.playercap.PlayerCapProvider;
import com.divinity.hlspells.spell.Spell;
import com.divinity.hlspells.spell.SpellAttributes;
import com.divinity.hlspells.spell.SpellConsumer;
import com.divinity.hlspells.util.Util;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class RespirationSpell extends Spell {

    public RespirationSpell(String displayName, int xpCost, int tickDelay, boolean treasureOnly) {
        super(SpellAttributes.Type.HELD, SpellAttributes.Rarity.UNCOMMON, SpellAttributes.Tier.ONE, SpellAttributes.Marker.UTILITY, displayName, xpCost, tickDelay, treasureOnly);
    }

    @Override
    public SpellConsumer<Player> getAction() {
        return p -> {
            List<Player> players = Util.getEntitiesInRange(p, Player.class, 10, 4, 10);
            p.getCapability(PlayerCapProvider.PLAYER_CAP).ifPresent(cap -> {
                cap.setSpellTimer(cap.getSpellTimer() + 1);
                for (Player player : players) {
                    if (player.isUnderWater() && cap.getSpellTimer() == 10) {
                        player.setAirSupply(player.getAirSupply() + 15);
                        if (player.getAirSupply() > player.getMaxAirSupply()) {
                            player.setAirSupply(player.getMaxAirSupply());
                        }
                        cap.setSpellTimer(0);
                    }
                }
            });
            return true;
        };
    }
}
