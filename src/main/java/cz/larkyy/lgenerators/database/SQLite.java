package cz.larkyy.lgenerators.database;

import cz.larkyy.lgenerators.generators.GeneratorHandler;
import cz.larkyy.lgenerators.generators.objects.Generator;
import cz.larkyy.lgenerators.generators.objects.GeneratorType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLite implements Database {

    private Connection connection;
    private final JavaPlugin main;

    public SQLite(JavaPlugin main) {
        this.main = main;

        createConnection();
    }

    private void createConnection() {
        try {

            connection = DriverManager.getConnection("jdbc:sqlite:"+main.getDataFolder()+"/data.db");

            final Statement stmt = connection.createStatement();

            final String sql = "CREATE TABLE IF NOT EXISTS lGenerators (location VARCHAR(1000) NOT NULL, type VARCHAR(36) NOT NULL, level MEDIUMINT(255) NOT NULL, PRIMARY KEY ( location ))";
            stmt.executeUpdate(sql);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveGenerator(Generator g) {
        Location loc = g.getLocation();
        String locStr = loc.getWorld().getName()+";"+loc.getBlockX()+";"+loc.getBlockY()+";"+loc.getBlockZ();
        savePlayer("INSERT OR REPLACE INTO lGenerators (location, type, level) VALUES (?, ?, ?)",
                locStr, g.getName(), g.getUpgrade());
    }

    private void savePlayer(String sql, Object... objects) {
        try(final PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i+1,objects[i]);
            }

            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Generator getGenerator(Location loc) {
        try(final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lGenerators WHERE location=?")) {
            String locStr = loc.getWorld().getName()+";"+loc.getBlockX()+";"+loc.getBlockY()+";"+loc.getBlockZ();
            stmt.setString(1, locStr);

            if (!stmt.execute()) {
                return null;
            }

            ResultSet rs = stmt.getResultSet();

            GeneratorType type = GeneratorHandler.getType(rs.getString("type"));
            int level = rs.getInt("level");

            return new Generator(loc,type,level);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Generator> getGenerators() {
        final List<Generator> gens = new ArrayList<>();
        try (final PreparedStatement stmt =
                     connection.prepareStatement("SELECT * FROM lGenerators")) {

            if (!stmt.execute()) {
                return null;
            }

            ResultSet rs = stmt.getResultSet();

            while(rs.next()) {
                String[] locArray = rs.getString("location").split(";");
                Location loc = new Location(
                        Bukkit.getWorld(locArray[0]),
                        Double.parseDouble(locArray[1]),
                        Double.parseDouble(locArray[2]),
                        Double.parseDouble(locArray[3])
                );
                GeneratorType type = GeneratorHandler.getType(rs.getString("type"));
                int level = rs.getInt("level");
                gens.add(new Generator(loc,type,level));
            }

            return gens;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return gens;
    }
}
