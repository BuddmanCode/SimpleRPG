import java.util.ArrayList;
import java.util.List;
public class Cell {
    private Location location;
    ArrayList<Creature> creatureList = new ArrayList<Creature>();
    public Cell(Location location) {
        this.location = location;
        refresh();
    }
    /*public Location getLocation() {
        return location;
    }*/
    public boolean isPassable() {
        return location.isPassable();
    }
    public ArrayList<Creature> getCreatureList() {
        return creatureList;
    }
    public StringBuilder getCreaturesDescription() {
        StringBuilder res = new StringBuilder();
        /*for(int i = 0; i<creatureList.size();i++) {
            res.append(i + 1).append(' ').append(creatureList.get(i).toString());
        }*/
        int i = 0;
        for(Creature creature: creatureList) {
            if( creatureActionable(creature) ) {
                res.append(i + 1);
                i++;
            } else
                res.append('-');
            res.append(' ').append(creature.getDescription());
        }
        return res;
    }
    public ArrayList<Creature> getActionableList() {
        int i = 0;
        ArrayList<Creature> actionable = new ArrayList<Creature>();
        for(Creature creature: creatureList) {
            if( creatureActionable(creature) ) {
                actionable.add(creature);
            }
        }
        return actionable;
    }
    public ArrayList<Creature> getVeryAgressiveList() {
        int i = 0;
        ArrayList<Creature> veryAgressive = new ArrayList<Creature>();
        for(Creature creature: creatureList) {
            if( creature.isVeryAgressive() ) {
                veryAgressive.add(creature);
            }
        }
        return veryAgressive;
    }
    public boolean creatureActionable(Creature creature) {
        return creature.isDamageable() || creature.isLootable() || creature.isTradeable() || creature.isServiceable();
    }
    /*public boolean creatureDamageable(Creature creature) { //можно стукать
        return creature.isDamageable();
    }
    public boolean creatureLootable(Creature creature) { //можно обобрать
        return creature.isLootable();
    }
    public boolean creatureTradeable(Creature creature) { //можно торговать
        return creature.isTradeable();
    }*/
    @Override
    public String toString() {
        return getDescription().toString();
    }
    public StringBuilder getDescription() {
        return location.getDescription().append(!isSafe()?" (опасность)":"");
    }
    public String getLocationName() {
        return location.toString();
    }
    public void refresh() {
        creatureList  = location.makeCreatures();
    }
    public boolean isSafe() {
        for(Creature creature: creatureList) {
            if( creature.isAgressive() && creature.isAlive()) return false;
        }
        return true;
    }
    public boolean canGo() {
        for(Creature creature: creatureList) {
            if( creature.isAlive() && creature.isVeryAgressive() ) return false;
        }
        return true;
    }
}
/*
import java.util.ArrayList;
import java.util.List;
public class Cell {
    private Terrain terrain;
    List<Creature> creatureList = new ArrayList<Creature>();
    public Cell(Terrain terrain) {
        this.terrain = terrain;
    }
    public Terrain getTerrain() {
        return terrain;
    }
    public boolean isPassable() {
        return terrain.isPassable();
    }
    public List<Creature> getCreatureList() {
        return List.copyOf(creatureList);
    }
    public void addCreature(Creature creature) {
        creatureList.add(creature);
    }
    @Override
    public String toString() {
        return terrain.toString();
    }
}
*/