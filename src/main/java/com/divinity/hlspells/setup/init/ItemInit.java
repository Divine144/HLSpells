package com.divinity.hlspells.setup.init;

import com.divinity.hlspells.HLSpells;
import com.divinity.hlspells.items.spellitems.SpellHoldingItem;
import com.divinity.hlspells.items.spellitems.StaffItem;
import com.divinity.hlspells.items.armor.material.WizardArmorMaterial;
import com.divinity.hlspells.items.armor.WizardHatArmorItem;
import com.divinity.hlspells.items.totems.EscapingTotem;
import com.divinity.hlspells.items.totems.GriefingTotem;
import com.divinity.hlspells.items.totems.KeepingTotem;
import com.divinity.hlspells.items.totems.ReturningTotem;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HLSpells.MODID);

    public static final RegistryObject<Item> TOTEM_OF_ESCAPING = ITEMS.register("totem_of_escaping", EscapingTotem::new);
    public static final RegistryObject<Item> TOTEM_OF_RETURNING = ITEMS.register("totem_of_returning", ReturningTotem::new);
    public static final RegistryObject<Item> TOTEM_OF_GRIEFING = ITEMS.register("totem_of_griefing", GriefingTotem::new);
    public static final RegistryObject<Item> TOTEM_OF_KEEPING = ITEMS.register("totem_of_keeping", KeepingTotem::new);
    public static final RegistryObject<Item> SPELL_BOOK = ITEMS.register("spell_book", () -> new SpellHoldingItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).durability(300), true));
    public static final RegistryObject<Item> WAND = ITEMS.register("wand", () -> new SpellHoldingItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).durability(700), false));
    public static final RegistryObject<Item> AMETHYST_WAND = ITEMS.register("wand_amethyst", () -> new SpellHoldingItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).durability(700), false));
    public static final RegistryObject<ArmorItem> WIZARD_HAT = ITEMS.register("wizard_hat", () -> new WizardHatArmorItem(WizardArmorMaterial.WIZHAT, EquipmentSlot.HEAD, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<BlockItem> ALTAR_ITEM = ITEMS.register("altar_of_attunement", () -> new BlockItem(BlockInit.ALTAR_OF_ATTUNEMENT_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));

    // Staffs
    public static final RegistryObject<Item> WOODEN_STAFF = ITEMS.register("wood_staff", () ->
            new StaffItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).durability(70),
                    2D, -3.231D, HLSpells.CONFIG.woodStaffCastTime.get() * 20, false));

    public static final RegistryObject<Item> GOLDEN_STAFF = ITEMS.register("golden_staff", () ->
            new StaffItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).durability(170),
                    5D, -2.572D, HLSpells.CONFIG.goldStaffCastTime.get() * 20, false));

    public static final RegistryObject<Item> NETHER_STAFF = ITEMS.register("netherite_staff", () ->
            new StaffItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).durability(700),
                    7D, -3D, HLSpells.CONFIG.netheriteStaffCastTime.get() * 20, false));

    // Amethyst Variants
    public static final RegistryObject<Item> WOODEN_STAFF_AMETHYST = ITEMS.register("wood_staff_amethyst", () ->
            new StaffItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).durability(70),
                    2D, -3.231D, HLSpells.CONFIG.woodStaffCastTime.get() * 20, true));

    public static final RegistryObject<Item> GOLDEN_STAFF_AMETHYST = ITEMS.register("golden_staff_amethyst", () ->
            new StaffItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).durability(170),
                    5D, -2.572D, HLSpells.CONFIG.goldStaffCastTime.get() * 20, true));

    public static final RegistryObject<Item> NETHER_STAFF_AMETHYST = ITEMS.register("netherite_staff_amethyst", () ->
            new StaffItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).durability(700),
                    7D, -3D, HLSpells.CONFIG.netheriteStaffCastTime.get() * 20, true));

    public static final List<RegistryObject<Item>> STAFFS = Lists.newArrayList(WOODEN_STAFF, GOLDEN_STAFF, NETHER_STAFF, WOODEN_STAFF_AMETHYST, GOLDEN_STAFF_AMETHYST, NETHER_STAFF_AMETHYST);

    // Totems should be ordered from: Activates Before Death -> Cancels Death -> Requires Death
    public static final List<RegistryObject<Item>> TOTEMS = Lists.newArrayList(TOTEM_OF_GRIEFING,
                                                                               TOTEM_OF_ESCAPING,
                                                                               TOTEM_OF_RETURNING,
                                                                               TOTEM_OF_KEEPING);
 }