package com.divinity.hlspells.compat;

import com.divinity.hlspells.HLSpells;
import com.divinity.hlspells.setup.init.ItemInit;
import com.divinity.hlspells.capabilities.spellholdercap.ISpellHolder;
import com.divinity.hlspells.capabilities.spellholdercap.SpellHolderProvider;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override @NotNull public ResourceLocation getPluginUid() { return new ResourceLocation(HLSpells.MODID, HLSpells.MODID); }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ItemInit.SPELL_BOOK.get(), (ingredient, context) ->
                ingredient.getCapability(SpellHolderProvider.SPELL_HOLDER_CAP).map(ISpellHolder::getCurrentSpell).orElse(""));
    }
}
