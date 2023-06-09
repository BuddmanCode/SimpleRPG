package main.items;

import java.util.*;
//Предмет
public class Item {
    private int id; //из бд
    private String name; //название
    private int cost; //базовая цена, цена продажи.
    private int level; //уровень предмета. Должен использоваться для определени возможности появления в монстрах
    private int groupId; //Это нужно при создании а не здесь
    private boolean isSingle; //предмет не стакается
    private static WeakHashMap uniqueCheck = new WeakHashMap<UUID, Item>(); //Не уверен, что это действительно нужно. Но это будет использоваться для проверки уникальности uuid.
    private UUID uuid; //используется для проверки уникальности предметов, которые не стакаются.
    Item(int itemID, String name, int cost, int level, boolean isSingle) {
        this.id = itemID;
        this.name = name;
        this.cost = cost;
        this.level = level;
        this.isSingle = isSingle;
        if(this.isSingle) {
            synchronized (uniqueCheck) {
                do {
                    uuid = UUID.randomUUID();
                } while (uniqueCheck.containsKey(uuid));
                uniqueCheck.put(uuid, this);
            }
        }
    }
    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("[").append(id);
        if(isSingle) res = res.append("s");
        return res.append(" - ").append(name).append("]").toString();

    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return (id == item.id)&&(uuid == item.uuid);
    }
    @Override
    public int hashCode() {
        if (isSingle)  return Objects.hash(id, uuid);
        return Objects.hash(id);
    }
    public int getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }

    public int getGroupId() {
        return groupId;
    }
    public boolean getSingle() {
        return isSingle;
    }
}
