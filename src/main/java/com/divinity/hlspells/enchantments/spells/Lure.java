package com.divinity.hlspells.enchantments.spells;

import com.divinity.hlspells.enchantments.ISpell;
import com.divinity.hlspells.init.ItemInit;
import com.divinity.hlspells.init.SpellInit;
import com.divinity.hlspells.util.SpellUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class Lure extends Enchantment implements ISpell {
    public Lure(EquipmentSlotType... slots) {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentType.BREAKABLE, slots);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return SpellUtils.getSpell(stack) == SpellInit.EMPTY.get();
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() == ItemInit.SPELL_BOOK.get() || super.canEnchant(stack);
    }

    @Override
    public int getMinCost(int value) {
        return 1;
    }

    @Override
    public int getMaxCost(int value) {
        return 33;
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        return !(enchantment instanceof ISpell);
    }

    @Override
    public boolean isTreasureOnly() {
        return false;
    }

    @Override
    public boolean isCurse() {
        return false;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }

    @Override
    public String getSpellRegistryName() {
        ResourceLocation registryName = SpellInit.LURE.get().getRegistryName();
        return registryName != null ? registryName.toString() : "null";
    }
}
