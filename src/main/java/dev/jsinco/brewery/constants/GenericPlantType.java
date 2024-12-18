package dev.jsinco.brewery.constants;

import org.bukkit.inventory.ItemStack;

public abstract class GenericPlantType {

    public abstract ItemStack getItemStack(int amount);

    @Override
    public abstract String toString();

    public abstract String name();
}
