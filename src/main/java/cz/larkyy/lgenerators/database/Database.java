package cz.larkyy.lgenerators.database;

import cz.larkyy.lgenerators.generators.objects.Generator;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface Database {
    void saveGenerator(Generator g);

    Generator getGenerator(Location loc);

    List<Generator> getGenerators();
}
