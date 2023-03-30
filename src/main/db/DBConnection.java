package main.db;

import main.creatures.CreatureGroup;
import main.creatures.CreatureStatPack;
import main.world.Location;
import main.world.Terrain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class DBConnection {
    public static Connection conn;
    public static Statement statement;
    public static ResultSet resSet;
    public static void Conn() throws ClassNotFoundException, SQLException
    {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:src/sqlite.db");
        statement = conn.createStatement();
        //System.out.println("База Подключена!");
    }
    public static void CloseDB() throws SQLException
    {
        conn.close();
        statement.close();
        resSet.close();
    }
    public static HashMap<Integer, CreatureGroup> ReadCreatureGroups() throws SQLException {
        resSet = statement.executeQuery("SELECT * FROM creatureGroups;");
        HashMap<Integer, CreatureGroup> creatureGroups = new HashMap<Integer, CreatureGroup>();
        while(resSet.next())
        {
            creatureGroups.put(resSet.getInt("id"),new CreatureGroup(resSet.getString("name"), resSet.getInt("relationshipId")));
        }
        return creatureGroups;
    }
    public static HashMap<Integer, CreatureStatPack> ReadCreatureStats(HashMap<Integer,CreatureGroup> creatureGroups) throws SQLException
    {
        resSet = statement.executeQuery("SELECT * FROM Creatures;");
        HashMap<Integer,CreatureStatPack> statPackMap = new HashMap<Integer, CreatureStatPack>();
        while(resSet.next())
        {
            statPackMap.put( resSet.getInt("id"), new CreatureStatPack(resSet.getString("type"), creatureGroups.get(resSet.getInt("groupId")),
                            resSet.getInt("healthBase"), resSet.getInt("powerBase"), resSet.getInt("agilityBase"),
                            resSet.getFloat("healthScale"), resSet.getFloat("powerScale"), resSet.getFloat("agilityScale")
                    )
            );
        }
        return statPackMap;
    }
    public static HashMap<Integer, Terrain> ReadTerrains(HashMap<Integer, CreatureStatPack> statPackMap) throws SQLException {
        resSet = statement.executeQuery("SELECT * FROM TerrainCreatures;");
        HashMap<Integer,ArrayList<CreatureStatPack>> terrainCreatures = new HashMap<Integer,ArrayList<CreatureStatPack>>();
        while(resSet.next())
        {
            if(!terrainCreatures.containsKey( resSet.getInt("terrainId") ))
                terrainCreatures.put( resSet.getInt("terrainId"), new ArrayList<CreatureStatPack>() );
            terrainCreatures.get( resSet.getInt("terrainId") ).add( statPackMap.get( resSet.getInt("creatureId") ) );
        }
        resSet = statement.executeQuery("SELECT * FROM Terrains;");
        HashMap<Integer,Terrain> terrainsMap = new HashMap<Integer,Terrain>();
        while(resSet.next())
        {
            terrainsMap.put( resSet.getInt("id"),
                    new Terrain( resSet.getString("name"), resSet.getInt("vision"),
                            resSet.getInt("passable") != 0, terrainCreatures.get(resSet.getInt("id")))
            );
        }
        return terrainsMap;
    }
    public static ArrayList<Location> ReadLocations(HashMap<Integer, Terrain> terrainsMap) throws SQLException {
        resSet = statement.executeQuery("SELECT * FROM Locations;");
        ArrayList<Location> locationsArrayList = new ArrayList<Location>();
        while(resSet.next())
        {
            locationsArrayList.add( new Location(
                            resSet.getString("name"), resSet.getInt("level"), resSet.getInt("width"),
                            resSet.getInt("height"), resSet.getInt("xBottomLeft"),resSet.getInt("yBottomLeft"),
                            resSet.getInt("isSafe") != 0, resSet.getInt("spawnAll") != 0,
                            terrainsMap.get(resSet.getInt("terrainId"))
                    )
            );
        }
        return locationsArrayList;
    }
    public static int findHero() throws SQLException {
        resSet = statement.executeQuery("SELECT cr.id FROM Creatures as cr INNER JOIN creatureGroups as cg on cg.id = cr.groupId and cg.name = 'Hero';");
        if(resSet.next()) {
            return resSet.getInt("id");
        } else throw new RuntimeException("DB does not contain java.creatures.Hero");
    }
}

/* //Check creature stats
WITH RECURSIVE
TEST AS (
SELECT 1 as lvl
UNION ALL
SELECT lvl+1 as lvl FROM TEST
LIMIT 100
)
SELECT
  lvl
, h.healthBase + h.healthScale*lvl as Herohealth
, h.powerBase + h.powerScale*lvl as Heropower
, h.agilityBase + h.agilityScale*lvl as Heroagility

, s.healthBase + s.healthScale*lvl as Skeletonhealth
, s.powerBase + s.powerScale*lvl as Skeletonpower
, s.agilityBase + s.agilityScale*lvl as Skeletonagility

, g.healthBase + g.healthScale*lvl as Goblinhealth
, g.powerBase + g.powerScale*lvl as Goblinpower
, g.agilityBase + g.agilityScale*lvl as Goblinagility
FROM TEST
LEFT JOIN CreatureStats as h on h.type = 'java.creatures.Hero'
LEFT JOIN CreatureStats as s on s.type = 'Skeleton'
LEFT JOIN CreatureStats as g on g.type = 'Goblin'
;
 */