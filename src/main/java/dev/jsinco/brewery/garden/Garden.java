package dev.jsinco.brewery.garden;

import com.dre.brewery.recipe.PluginItem;
import dev.jsinco.brewery.garden.commands.GardenCommand;
import dev.jsinco.brewery.garden.configuration.BreweryGardenConfig;
import dev.jsinco.brewery.garden.listener.BlockEventListener;
import dev.jsinco.brewery.garden.listener.EventListeners;
import dev.jsinco.brewery.garden.integration.BreweryGardenIngredient;
import dev.jsinco.brewery.garden.persist.Database;
import dev.jsinco.brewery.garden.persist.GardenPlantDataType;
import dev.jsinco.brewery.garden.plant.GardenPlant;
import dev.jsinco.brewery.garden.plant.GrowthManager;
import dev.jsinco.brewery.garden.plant.PlantType;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Garden extends JavaPlugin {

    @Getter
    private static Garden instance;
    @Getter
    private static PlantRegistry gardenRegistry;
    @Getter
    private BreweryGardenConfig pluginConfiguration;
    private Database database;
    @Getter
    private GardenPlantDataType gardenPlantDataType;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.database = new Database();
        try {
            database.init(this.getDataFolder());
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
        gardenRegistry = new PlantRegistry();
        gardenPlantDataType = new GardenPlantDataType(database);

        try {
            PluginItem.registerForConfig(this.getName(), BreweryGardenIngredient::new);
        } catch (NoClassDefFoundError ignored) {

        }
        this.pluginConfiguration = compileConfig();
        for (World world : Bukkit.getWorlds()) {
            List<GardenPlant> gardenPlants = gardenPlantDataType.fetch(world);
            gardenPlants.forEach(gardenRegistry::registerPlant);
        }
        Bukkit.getPluginManager().registerEvents(new EventListeners(gardenRegistry, gardenPlantDataType), this);
        Bukkit.getPluginManager().registerEvents(new BlockEventListener(gardenRegistry, gardenPlantDataType), this);
        this.registerPlantRecipes();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, GardenCommand::register);
        Bukkit.getScheduler().runTaskTimer(this, new GrowthManager(gardenRegistry, gardenPlantDataType)::tick, 0, 200);
    }

    public void reload() {
        this.pluginConfiguration = compileConfig();
    }

    private BreweryGardenConfig compileConfig() {
        return ConfigManager.create(BreweryGardenConfig.class, it -> {
            it.withConfigurer(new YamlBukkitConfigurer());
            it.withBindFile(new File(this.getDataFolder(), "config.yml"));
            it.saveDefaults();
            it.load(true);
        });
    }

    private void registerPlantRecipes() {
        for (PlantType plantType : GardenRegistry.PLANT_TYPE.values()) {
            NamespacedKey namespacedKey = plantType.key();
            if (Bukkit.getRecipe(namespacedKey) != null) {
                Bukkit.removeRecipe(namespacedKey);
            }

            ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, plantType.newSeeds().newItem(4));
            recipe.addIngredient(plantType.newFruit().newItem(1));
            Bukkit.addRecipe(recipe);
        }
    }
}