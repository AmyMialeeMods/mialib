package xyz.amymialee.mialib.templates;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ArmorMaterialBuilder {
    public static final Map<ArmorItem.Type, Integer> ARMOR_ZERO = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 0);
        map.put(ArmorItem.Type.LEGGINGS, 0);
        map.put(ArmorItem.Type.CHESTPLATE, 0);
        map.put(ArmorItem.Type.HELMET, 0);
        map.put(ArmorItem.Type.BODY, 0);
    });
    public static final List<ArmorMaterial.Layer> EMPTY_LAYER = List.of(new ArmorMaterial.Layer(new Identifier(MiaLib.MOD_ID)));
    public static final Supplier<Ingredient> EMPTY_INGREDIENT = () -> Ingredient.EMPTY;
    private Map<ArmorItem.Type, Integer> defense = ARMOR_ZERO;
    private int enchantability = 0;
    private RegistryEntry<SoundEvent> equipSound = SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
    private Supplier<Ingredient> repairIngredient = EMPTY_INGREDIENT;
    private List<ArmorMaterial.Layer> layers = EMPTY_LAYER;
    private float toughness = 0;
    private float knockbackResistance = 0;

    private ArmorMaterialBuilder() {}

    public static @NotNull ArmorMaterialBuilder create() {
        return new ArmorMaterialBuilder();
    }

    public static @NotNull ArmorMaterialBuilder create(@NotNull ArmorMaterial material) {
        return new ArmorMaterialBuilder()
                .defense(material.defense())
                .enchantability(material.enchantability())
                .equipSound(material.equipSound())
                .repairIngredient(material.repairIngredient())
                .layers(material.layers())
                .toughness(material.toughness())
                .knockbackResistance(material.knockbackResistance());
    }

    public RegistryEntry<ArmorMaterial> build(Identifier id) {
        return Registry.registerReference(Registries.ARMOR_MATERIAL, id, new ArmorMaterial(this.defense, this.enchantability, this.equipSound, this.repairIngredient, this.layers, this.toughness, this.knockbackResistance));
    }

    public ArmorMaterialBuilder defense(@NotNull Map<ArmorItem.Type, Integer> defense) {
        this.defense = defense;
        return this;
    }

    public ArmorMaterialBuilder enchantability(int enchantability) {
        this.enchantability = enchantability;
        return this;
    }

    public ArmorMaterialBuilder equipSound(@NotNull RegistryEntry<SoundEvent> equipSound) {
        this.equipSound = equipSound;
        return this;
    }

    public ArmorMaterialBuilder repairIngredient(@NotNull Supplier<Ingredient> repairIngredient) {
        this.repairIngredient = repairIngredient;
        return this;
    }

    public ArmorMaterialBuilder layers(@NotNull List<ArmorMaterial.Layer> layers) {
        this.layers = layers;
        return this;
    }

    public ArmorMaterialBuilder toughness(float toughness) {
        this.toughness = toughness;
        return this;
    }

    public ArmorMaterialBuilder knockbackResistance(float knockbackResistance) {
        this.knockbackResistance = knockbackResistance;
        return this;
    }
}