import java.util.Map;
import java.util.NoSuchElementException;

public class Trader extends Human implements Lootable {
    public Trader(CreatureStatPack stats, int level, String name) throws NoSuchElementException {
        super(stats, level, name);
        refresh();
        //Заполнить инвентарь, сделать интерфейс торговли
    }
    @Override
    public boolean isTradeable() { //можно торговать
        return !isEmpty() && isAlive() && !isAgressive();
    }
    public void refresh() {
        backpack.clear();
        super.putNumerousItem(new Money(), 100 * getLevel());
        super.putNumerousItem(new HealingItem(2,"Малое зелье лечения", 40,1,25), 10);
    }

    public int sell(Item key, Integer quantity) {
        putNumerousItem(key,quantity);
        return key.getCost() * quantity;
    }

    public int buy(Item key, int quantity) {
        if (quantity > backpack.get(key)) quantity = backpack.get(key);
        if(quantity == backpack.get(key)) backpack.remove(key);
        else backpack.replace(key,backpack.get(key) - quantity);
        this.addMoney((int)(key.getCost() * quantity * 1.2));
        return quantity;
    }
}
