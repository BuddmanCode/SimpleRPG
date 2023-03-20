import java.util.Map;
import java.util.NoSuchElementException;

public class Human extends Creature implements Healable {
    String name;

    public Human(CreatureStatPack stats, int level, String name) throws NoSuchElementException { //, CreatureStatPack stats
        //this.getClass().getSimpleName();
        super(stats, level);
        this.name = name;
    }
    @Override
    public void applyHeal(int healEffect) {
        if (healEffect > 0) {
            if((getHealth() + healEffect) > 0 &&  (getHealth() + healEffect) < getHealthMax())
                this.setHealth(getHealth() + healEffect);
            else
                this.setHealth(getHealthMax());
        }
    }
    @Override
    public String getName() {
        return super.getName() + " " + name;
    }
    public void addMoney(int money) {
        Money tmp = new Money();
        if (backpack.containsKey(tmp)) {
            backpack.replace(tmp, backpack.get(tmp) + money);
            return;
        }
        backpack.put(tmp,money);
    }

}
