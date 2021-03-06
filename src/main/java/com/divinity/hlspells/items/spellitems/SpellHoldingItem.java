package com.divinity.hlspells.items.spellitems;

import com.divinity.hlspells.HLSpells;
import com.divinity.hlspells.capabilities.spellholdercap.ISpellHolder;
import com.divinity.hlspells.capabilities.spellholdercap.SpellHolderProvider;
import com.divinity.hlspells.network.ClientAccess;
import com.divinity.hlspells.setup.init.EnchantmentInit;
import com.divinity.hlspells.setup.init.SpellInit;
import com.divinity.hlspells.spell.Spell;
import com.divinity.hlspells.spell.SpellAttributes;
import com.divinity.hlspells.util.SpellUtils;
import com.divinity.hlspells.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class SpellHoldingItem extends ProjectileWeaponItem {

    private final boolean isSpellBook;
    private boolean wasHolding;

    public SpellHoldingItem(Properties properties, boolean isSpellBook) {
        super(properties);
        this.isSpellBook = isSpellBook;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab pGroup, NonNullList<ItemStack> pItems) {
        if (isSpellBook) {
            if (this.allowdedIn(pGroup)) {
                for (Spell spell : SpellInit.SPELLS_REGISTRY.get()) {
                    ItemStack stack = new ItemStack(this);
                    stack.getCapability(SpellHolderProvider.SPELL_HOLDER_CAP).ifPresent(cap -> {
                        if (spell != SpellInit.EMPTY.get())
                            cap.addSpell(Objects.requireNonNull(spell.getRegistryName()).toString());
                    });
                    pItems.add(stack);
                }
            }
        }
        else super.fillItemCategory(pGroup, pItems);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> text, TooltipFlag pFlag) {
        stack.getCapability(SpellHolderProvider.SPELL_HOLDER_CAP).ifPresent(cap -> {
            List<String> spells = cap.getSpells();
            if (isSpellBook) {
                Spell spell = SpellUtils.getSpell(stack);
                text.add(spell.getDisplayName().withStyle(spell.getSpellType().getTooltipFormatting()));
            }
            else {
                text.add(1, new TextComponent(ChatFormatting.GOLD + "Spells: "));
                if (spells.isEmpty()) text.add(new TextComponent(ChatFormatting.GRAY + "   Empty"));
                else {
                    spells.forEach(spell -> {
                        Spell currentSpell = SpellUtils.getSpellByID(spell);
                        if (cap.getCurrentSpell().equals(spell)) {
                            text.add(new TextComponent(ChatFormatting.BLUE + "   " + currentSpell.getTrueDisplayName()));
                        }
                        else text.add(new TextComponent(ChatFormatting.GRAY + "   " + currentSpell.getTrueDisplayName()));
                    });
                }
            }
        });
    }

    @Override
    @ParametersAreNonnullByDefault
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        LazyOptional<ISpellHolder> capability = itemstack.getCapability(SpellHolderProvider.SPELL_HOLDER_CAP);
        this.wasHolding = true;
        Spell spell = SpellUtils.getSpell(itemstack);
        List<String> spells = capability.map(ISpellHolder::getSpells).orElse(null);
        if (spells != null && !spells.isEmpty()) {
            player.startUsingItem(hand);
            if (!world.isClientSide()) {
                if (isSpellBook) {
                    world.playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.NEUTRAL, 0.6F, 1.0F);
                }
                if (spell != SpellInit.EMPTY.get() && spell.getSpellType() == SpellAttributes.Type.HELD) {
                    world.playSound(null, player.blockPosition(), SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.NEUTRAL, 0.6F, 1.0F);
                }
            }
            return InteractionResultHolder.success(itemstack);
        }
        else return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity livingEntity, int count) {
        if (livingEntity instanceof Player player) {
            Spell spell = SpellUtils.getSpell(stack);
            if (spell.getSpellType() == SpellAttributes.Type.HELD) spell.execute(player, stack);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int power) {
        if (entity instanceof Player player) {
            LazyOptional<ISpellHolder> capability = stack.getCapability(SpellHolderProvider.SPELL_HOLDER_CAP);
            this.wasHolding = false;
            Spell spell = SpellUtils.getSpell(stack);
            if (this.castTimeCondition(player, stack)) {
                if (spell.getSpellType() == SpellAttributes.Type.CAST) spell.execute(player, stack);
                Util.doParticles(player);
                if (!world.isClientSide()) {
                    capability.filter(p -> !(p.getSpells().isEmpty())).ifPresent(cap -> {
                        world.playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 0.6F, 1.0F);
                        if (stack.getItem() instanceof StaffItem item) {
                            if (item.isGemAmethyst() && SpellUtils.getSpellByID(cap.getCurrentSpell()).getMarkerType() == SpellAttributes.Marker.COMBAT) {
                                player.getCooldowns().addCooldown(stack.getItem(), (int) (HLSpells.CONFIG.cooldownDuration.get() * 20));
                            }
                            else if (!item.isGemAmethyst() && SpellUtils.getSpellByID(cap.getCurrentSpell()).getMarkerType() == SpellAttributes.Marker.UTILITY) {
                                player.getCooldowns().addCooldown(stack.getItem(), (int) (HLSpells.CONFIG.cooldownDuration.get() * 20));
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        if (EnchantedBookItem.getEnchantments(book).size() > 0) {
            for (int i = 0; i < EnchantedBookItem.getEnchantments(book).size(); i++) {
                CompoundTag tag = EnchantedBookItem.getEnchantments(book).getCompound(i);
                ResourceLocation enchantment = EnchantmentHelper.getEnchantmentId(tag);
                if (enchantment != null) {
                    switch (enchantment.toString()) {
                        case "minecraft:mending":
                            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, stack) == 0) {
                                return !isSpellBook || stack.getItem() instanceof StaffItem;
                            }
                        case "minecraft:unbreaking":
                            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack) <= EnchantmentHelper.getEnchantmentLevel(tag)) {
                                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack) != 3) {
                                    return !isSpellBook || stack.getItem() instanceof StaffItem;
                                }
                            }
                        case "hlspells:soul_bond":
                            if (EnchantmentHelper.getItemEnchantmentLevel(EnchantmentInit.SOUL_BOND.get(), stack) == 0) {
                                return !isSpellBook || stack.getItem() instanceof StaffItem;
                            }
                        break;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        List<String> spells = pStack.getCapability(SpellHolderProvider.SPELL_HOLDER_CAP).map(ISpellHolder::getSpells).orElse(null);
        return isSpellBook && SpellUtils.getSpell(pStack) != SpellInit.EMPTY.get() || !isSpellBook && spells != null && !spells.isEmpty() || super.isFoil(pStack);
    }

    // Responsible for syncing capability to client side
    @Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag capTag = new CompoundTag();
        LazyOptional<ISpellHolder> spellHolder = stack.getCapability(SpellHolderProvider.SPELL_HOLDER_CAP);
        if (spellHolder.isPresent()) {
            spellHolder.ifPresent(cap -> capTag.put("spellHolder", stack.serializeNBT()));
        }
        CompoundTag stackTag = stack.getTag();
        if (capTag.isEmpty()) return stackTag;
        else if (stackTag == null) stackTag = new CompoundTag();
        else stackTag = stackTag.copy(); // Because we don't actually want to add this data to the server side ItemStack
        stackTag.put("spellCap", capTag);
        return stackTag;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        stack.setTag(nbt);
        if (nbt != null && nbt.contains("spellCap")) {
            CompoundTag capTags = nbt.getCompound("spellCap");
            if (capTags.contains("spellHolder")) {
                stack.getCapability(SpellHolderProvider.SPELL_HOLDER_CAP).ifPresent(spellHolder -> stack.deserializeNBT(nbt));
            }
        }
    }

    @Override @NotNull public Predicate<ItemStack> getAllSupportedProjectiles() { return ARROW_ONLY; }

    @Override @NotNull public UseAnim getUseAnimation(@NotNull ItemStack pStack) { return isSpellBook ? UseAnim.CROSSBOW : UseAnim.BOW; }

    @Override @Nullable public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) { return new SpellHolderProvider(); }

    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }

    @Override public int getDefaultProjectileRange() { return 8; }

    @Override public int getUseDuration(@NotNull ItemStack pStack) { return 72000; }

    @Override public boolean isEnchantable(@NotNull ItemStack pStack) { return isSpellBook; }

    @Override public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) { return false; }

    @SuppressWarnings("all")
    public boolean isSpellBook() {
        return isSpellBook;
    }

    /**
     * Returns true if wand or staff
     */
    public boolean isWand() {
        return !isSpellBook;
    }

    public boolean isWasHolding() {
        return this.wasHolding;
    }

    public void setWasHolding(boolean wasHolding) {
        this.wasHolding = wasHolding;
    }

    private boolean castTimeCondition(Player player, ItemStack stack) {
        if (stack.getItem() instanceof StaffItem item) {
            return player.getUseItemRemainingTicks() < (72000 - (item.getCastDelay()));
        }
        else if (!this.isSpellBook) {
            return player.getUseItemRemainingTicks() < (72000 - (HLSpells.CONFIG.spellCastTime.get() * 20));
        }
        return player.getUseItemRemainingTicks() < 71988;
    }
}
