package com.heromotion.hlspells.init;

import com.heromotion.hlspells.HLSpells;
import com.heromotion.hlspells.items.SpellBookItem;
import com.heromotion.hlspells.items.TotemOfGriefing;
import com.heromotion.hlspells.items.TotemOfKeeping;
import com.heromotion.hlspells.items.TotemOfReturning;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            HLSpells.MODID);

    // Items
    public static final RegistryObject<Item> TOTEM_OF_GRIEFING = ITEMS.register("totem_of_griefing", TotemOfGriefing::new);
    public static final RegistryObject<Item> TOTEM_OF_KEEPING = ITEMS.register("totem_of_keeping", TotemOfKeeping::new);
    public static final RegistryObject<Item> TOTEM_OF_RETURNING = ITEMS.register("totem_of_returning", TotemOfReturning::new);
    public static final RegistryObject<Item> SPELL_BOOK = ITEMS.register("spell_book", SpellBookItem::new);
}