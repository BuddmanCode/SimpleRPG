package main.world;

import main.creatures.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
//Информация о локации, поверхностьи
public class Location {
    //Название
    private final String name;
    //уровень
    private final int level;
    //размеры
    private final int height;
    private final int width;
    //координаты левого нижнего угла
    private final int yBottomLeft;
    private final int xBottomLeft;
    //Поверхность. Содержит информацию о возможных существах и можно ли по "этому" ходить
    private Terrain terrain;
    //базовое состояние локации. Планировалось, что в зависимости от этого будут спавниться разные NPC, но не реализовано
    private boolean isSafe;
    //Должны ли спавниться все противники сразу или рандомно один из.
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
    //безоавсность локации. так как безопасноть на данный момент не учитывается при заполнении мобами, то использовать не целесообразно.
    public boolean isSafe() {
        return isSafe;
    }
    //можно ли здесь пройти
    public boolean isPassable() {
        return terrain.isPassable();
    }
    //создание существ для размещения в ячейке
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
    //выбор конструктора в зависимости от группы существа
    public Creature makeNewCreature(CreatureStatPack stats, int level) throws NoSuchElementException {
        switch(stats.group.name) {
            case "Monster": return new Monster(stats,level);
            case "Trader": return new Trader(stats,level, "Акаши"); //AzurLane, привет )
            case "Hotelier": return new Hotelier(stats,level, "Иван Иванович"); //Продумать, откуда брать имена
            default: throw new NoSuchElementException("No constructor for group " + stats.group);
        }
    }
    //описание локации
    @Override
    public String toString(){
        return getDescription().toString();
    }
    //описание локации
    public StringBuilder getDescription(){
        return new StringBuilder(name).append(" [").append(terrain.toString()).append("] ").append(level).append(" уровня");
    }

}