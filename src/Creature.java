import java.util.*;
//Базовый класс существа
public abstract class Creature implements Fightable, Damageable, Lootable {
    //private static HashMap<String, CreatureStatPack> statPackMap;
    //private static boolean init = false;
    //String type;
    private int health;
    private int healthMax;
    private int agility;
    private int power;
    private int level;
    //начальные статы и скейл от уровня
    private CreatureStatPack stats;
    //инвентарь
    protected HashMap<Item, Integer> backpack = new HashMap<Item, Integer>();
    //свой лично илициализированный рандом. Все ведь по разному удачливы...
    protected Random rand = new Random(System.currentTimeMillis());
    public Creature(CreatureStatPack stats, int level) throws NoSuchElementException {
        this.stats = stats;
        this.level = level<1?1:level;
        setStatsToLevel();
    }
    //Установка характеристик в соответствие с уровнем
    public void setStatsToLevel() {
        healthMax = (int) (stats.healthBase + stats.healthScale*level);
        agility = (int) (stats.agilityBase + stats.agilityScale*level);
        power = (int) (stats.powerBase + stats.powerScale*level);
        health = healthMax;
    }
    //изменение уровня
    protected void setLevel(int level) {
        this.level = level;
        setStatsToLevel();
    }
    //добавление одного предмета
    public boolean putItem(Item item) {
        return putNumerousItem(item,1);
    }
    //добавление некоторого количества предмета
    public boolean putNumerousItem(Item item, int quantity) {
        synchronized (backpack) {
            if (backpack.containsKey(item)) {
                if (!item.getSingle()) {
                    backpack.replace(item, ((Integer) backpack.get(item)) + quantity);
                    return true;
                }
                return false;
            }
            backpack.put(item, quantity);
            return true;
        }
    }
    //получение уроня
    @Override
    public void receiveDamage(int damage){
        if( health > damage) {
            health -= damage;
            return;
        }
        health = 0;
    }
    //Атака цели
    @Override
    public int attack(Damageable target) {
        if( agility * 3 > rand.nextInt(101) ){
            target.receiveDamage(power);
            return power;
        }
        return 0;
    }
    //предоставление содержимого рюкзака
    public Map<Item, Integer> provideBackpack() {
        return backpack;
    }
    public int getHealth() {
        return health;
    }
    protected void setHealth(int health) {
        this.health = health;
    }
    public int getHealthMax() {
        return healthMax;
    }
    public int getAgility() {
        return agility;
    }
    public int getPower() {
        return power;
    }
    public int getLevel() {
        return level;
    }
    //существо будет атаковать (что-то типа защиты территории)
    public boolean isAgressive() {
        if(stats.group.relationshipId > 0 && stats.group.relationshipId <3) {
            return true;
        }
        return false;
    }
    //существо будет атаковать и мешать побегу
    public boolean isVeryAgressive() {
        if(stats.group.relationshipId == 1) {
            return true;
        }
        return false;
    }
    public boolean isEmpty() {
        return backpack.isEmpty();
    }
    public boolean isAlive() {
        return health > 0;
    }
    @Override
    public String toString() {
        return getShortDescription().toString();
    }
    //описание существа для формирования информации об окружении
    public StringBuilder getShortDescription() {
        StringBuilder res = new StringBuilder(getName()).append(" ").append(level).append(" lvl ");
        if(isAlive()) {
            if(isAgressive() || stats.group.relationshipId == 0) res.append(health).append(" hp");
        } else {
            res.append(" мертвый");
        }
        return res;
    }
    //описание существа находящегося рядом
    public StringBuilder getDescription() {
        return new StringBuilder(getName()).append(" ").append(level).append(" lvl ").append(isAgressive()&&isAlive()?(String.valueOf(health)+ " hp"):"").append(isAlive()?
                isVeryAgressive()?" !нападает!":
                        isAgressive()?" защищается!":""
                :" мертвый").append(isEmpty()&&!isServiceable()?" (пустой)":"");
    }
    public boolean isDamageable() { //можно стукать
        return isAlive() && isAgressive();
    }
    public boolean isLootable() { //можно обобрать
        return !isEmpty() && !isAlive();
    }
    public boolean isTradeable() { //можно торговать
        return false;
    }
    public boolean isServiceable() { //предоставляет услуги
        return false;
    }
    public String getName() {
        return this.stats.type;
    }

}
