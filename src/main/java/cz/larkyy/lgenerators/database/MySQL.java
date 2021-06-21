package cz.larkyy.lgenerators.database;


import cz.larkyy.lgenerators.config.ConfigValues;
import cz.larkyy.lgenerators.generators.GeneratorHandler;
import cz.larkyy.lgenerators.generators.objects.Generator;
import cz.larkyy.lgenerators.generators.objects.GeneratorType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQL implements Database {

    private Connection connection;

    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String database;
    private final String table;

    public MySQL() {
        this.host = ConfigValues.database_host;
        this.port = ConfigValues.database_port;
        this.user = ConfigValues.database_user;
        this.password = ConfigValues.database_password;
        this.database = ConfigValues.database_database;
        this.table = ConfigValues.database_table;

        createConnection();
    }

    private void createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/?useUnicode=true&characterEncoding=utf8&useSSL=false&verifyServerCertificate=false", user, password);

            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + database);
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + database + "." + table + " (location VARCHAR(1000) NOT NULL, type VARCHAR(36) NOT NULL, level MEDIUMINT(255) NOT NULL, PRIMARY KEY ( location ))");

            statement.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveGenerator(Generator g) {
        Location loc = g.getLocation();
        String locStr = loc.getWorld().getName()+";"+loc.getBlockX()+";"+loc.getBlockY()+";"+loc.getBlockZ();
        savePlayer("INSERT INTO " + database + "." + table + " (location, type, level) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE type=?, level=?",
                locStr, g.getName(), g.getUpgrade(), g.getName(), g.getUpgrade());
    }

    private void savePlayer(String sql, Object... objects) {
        try (final PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i + 1, objects[i]);
            }

            stmt.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public Generator getGenerator(Location loc) {
        try (final PreparedStatement stmt =
                     connection.prepareStatement("SELECT * FROM " + database + "." + table + " WHERE location=?")) {
            String locStr = loc.getWorld().getName()+";"+loc.getBlockX()+";"+loc.getBlockY()+";"+loc.getBlockZ();

            stmt.setString(1, locStr);

            if (!stmt.execute()) {
                return null;
            }

            ResultSet rs = stmt.getResultSet();
            GeneratorType type = GeneratorHandler.getType(rs.getString("type"));
            int level = rs.getInt("level");

            return new Generator(loc,type,level);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Generator> getGenerators() {
        final List<Generator> gens = new ArrayList<>();
        try (final PreparedStatement stmt =
                     connection.prepareStatement("SELECT * FROM " + database + "." + table)) {

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
