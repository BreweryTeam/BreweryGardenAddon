package dev.jsinco.brewery.configuration;

import com.dre.brewery.api.addons.AddonConfigFile;
import com.dre.brewery.configuration.annotation.OkaeriConfigFileOptions;
import com.dre.brewery.depend.okaeri.configs.annotation.Comment;
import com.dre.brewery.depend.okaeri.configs.annotation.Header;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;

@Header({
        "Welcome to the configuration file for the BreweryGarden addon!",
        "The settings below is all you're able to customize. If you'd like to request a feature",
        "or report a bug, join our Discord or make an issue on our Github! https://brewery.lumamc.net/",
        "To add a BreweryGarden item (plant or seeds), use 'Garden:Berry' or 'Garden:Berry_Seeds'.",
        "To see all available PlantTypes, have a look here: https://github.com/BreweryTeam/BreweryGardenAddon/blob/master/src/main/java/dev/jsinco/brewery/constants/PlantType.java#L38"
})
@Getter
@OkaeriConfigFileOptions("gardenConfig.yml")
public class BreweryGardenConfig extends AddonConfigFile {

    @Comment({"How likely it is for a seed to spawn from a broken 'validSeedDropBlocks' block.",
    "Use an integer from 1 to 100."})
    private int seedSpawnChance = 15;

    @Comment({"The integer which determines if a plant is fully grown (has a plant sprouted on it).",
    "A plant's growth stage increases by '1' every 5 minutes. Making '4' equal one full Minecraft day, or 20 minutes."})
    private int fullyGrown = 4;

    @Comment("A list of materials which a seed may drop from.")
    private List<Material> validSeedDropBlocks = List.of(Material.SHORT_GRASS, Material.TALL_GRASS);

    @Comment("A list of materials which a seed may be planted on.")
    private List<Material> plantableBlocks = List.of(Material.GRASS_BLOCK, Material.DIRT, Material.COARSE_DIRT, Material.PODZOL);
}
