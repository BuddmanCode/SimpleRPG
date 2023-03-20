import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class Location {
    private final String name;
    private final int level;
    //private Cell[][] cellsArrayList;
    private final int height;
    private final int width;
    private final int yBottomLeft;
    private final int xBottomLeft;
    private Terrain terrain;
    private boolean isSafe;
    private boolean spawnAll;

    public Location(String name, int level, int width, int height, int xBottomLeft, int yBottomLeft, boolean isSafe, boolean spawnAll, Terrain terrain) {
        this.name = name;
        this.level = level;
        this.width = width;
        this.height = height;
        this.xBottomLeft = xBottomLeft;
        this.yBottomLeft = yBottomLeft;
        this.isSafe = isSafe;
        this.spawnAll = spawnAll;
        this.terrain = terrain;
    }
    public String getName() {
        return name;
    }
    public int getLevel() {
        return level;
    }
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
    public int getYBottomLeft() {
        return yBottomLeft;
    }
    public int getXBottomLeft() {
        return xBottomLeft;
    }
    public boolean isSafe() {
        return isSafe;
    }
    public boolean isPassable() {
        return terrain.isPassable();
    }
    public ArrayList<Creature> makeCreatures() {
        Random rand = new Random(System.currentTimeMillis());
        ArrayList<Creature> creatureList = new ArrayList<Creature>();
        List<CreatureStatPack> monsterTypeList = terrain.getMonsterTypeList();
        if(spawnAll){
            for(CreatureStatPack stats: monsterTypeList) {
                creatureList.add(makeNewCreature(stats, level - 4  + rand.nextInt(level + 6)));
            }
        } else {
            if(monsterTypeList.size() != 0)
                creatureList.add( makeNewCreature( monsterTypeList.get( rand.nextInt(monsterTypeList.size()) ), level - 4  + rand.nextInt(level + 6) )
            );
        }
        return creatureList;
    }
    public Creature makeNewCreature(CreatureStatPack stats, int level) throws NoSuchElementException {
        switch(stats.group.name) {
            case "Monster": return new Monster(stats,level);
            case "Trader": return new Trader(stats,level, "Акаши"); //AzurLane, привет )
            case "Hotelier": return new Hotelier(stats,level, "Иван Иванович"); //Продумать, откуда брать имена
            default: throw new NoSuchElementException("No constructor for group " + stats.group);
        }

    }
    @Override
    public String toString(){
        return getDescription().toString();
    }
    public StringBuilder getDescription(){
        return new StringBuilder(name).append(" [").append(terrain.toString()).append("] ").append(level).append(" уровня");
    }

}
/*
import java.util.Random;

public class Location {
    private final String name;
    private final int level;
    private Cell[][] cellsArrayList;
    private final int height;
    private final int width;
    private final int yBottomLeft;
    private final int xBottomLeft;
    private Terrain terrain;
    private boolean isSafe;
    public Location(String name, int level, int width, int height, int xBottomLeft, int yBottomLeft, boolean isSafe, Terrain terrain) {
        this.name = name;
        this.level = level;
        this.width = width;
        this.height = height;
        this.xBottomLeft = xBottomLeft;
        this.yBottomLeft = yBottomLeft;
        this.isSafe = isSafe;
        this.terrain = terrain;
        cellsArrayList = new Cell[height][width]; //ArrayList<ArrayList<Cell>>(height);
        Random rand = new Random(System.currentTimeMillis());
        for(int y = 0;y<height;y++) {
            for(int x = 0;x<width;x++) {
                cellsArrayList[y][x] = new Cell(terrain);
                if (isSafe) {
                    cellsArrayList[y][x].addCreature(new Trader("Trader", this.level,"Акаши")); //AzurLane, привет )))
                    //В норме мы должны выяснять у Terrian, что там может спавниться, но пока заглушка
                    //Да и уровень должен быть +-. Зря я скейлы статов подбирал, что-ли...
                    if(rand.nextInt(2) == 0) {
                        cellsArrayList[y][x].addCreature(new Monster("Goblin", this.level));
                    } else {
                        cellsArrayList[y][x].addCreature(new Monster("Skeleton", this.level));
                    }
                }
            }
        }
    }
    public String getName() {
        return name;
    }
    public int getLevel() {
        return level;
    }
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
    public int getYBottomLeft() {
        return yBottomLeft;
    }
    public int getXBottomLeft() {
        return xBottomLeft;
    }
    public boolean isSafe() {
        return isSafe;
    }
    public Cell[][] getCellsArrayList() {
        return cellsArrayList;
    }
    public Cell getCellsArrayList(int x, int y) {
        return cellsArrayList[y][x];
    }
    @Override
    public String toString(){
        return new StringBuilder(name).append(" [").append(terrain.toString()).append("] ").append(level).append(" уровня (").append(isSafe?"безопасно)":"опасность)").toString();
    }

    public boolean isPassable(int y, int x) {
        return cellsArrayList[y][x].isPassable();
    }

    public String toStringWithCell(int y, int x) {
        //return new StringBuilder(name).append(" [").append(cellsArrayList[y][x].toString()).append("] ").append(level).append(" уровня (").append(isSafe?"безопасно)":"опасность)").toString();
        //Здесь будет предоставляться информация о существах в ячейке и их состоянии.
        return toString();
    }
    //Ячейки надо бы как-то генерировать, чтобы они имели или не имели необходиых интерфейсов...
}
*/