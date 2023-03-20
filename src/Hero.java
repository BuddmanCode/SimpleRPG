import java.util.Map;
import java.util.NoSuchElementException;

public class Hero extends Human implements Experienceable {
    int experience;
    public Hero(CreatureStatPack stats, int level, String name, int experience) throws NoSuchElementException {
        super(stats, level, name);
        this.experience = experience;
        System.out.println();
    }
    public int nextLevelXp() {
        return 10 * this.getLevel();
    }
    @Override
    public int attack(Damageable target) {
        int damageDealed = super.attack(target);

        if(damageDealed > 0 && target instanceof Creature) {
            if(!((Creature) target).isAlive()) {
                int xpByAverageMonster = (int) (nextLevelXp() / (10 + 0.41 * (getLevel() - 1)));
                int coeff = ((Creature) target).getLevel() - getLevel();
                coeff += 5;
                if (coeff > 10) coeff = 10;
                if (coeff < 0) coeff = 0;
                getExperience((int) ((xpByAverageMonster) * (coeff / 5)));
            }
        }
        return damageDealed;
    }
    @Override
    public void getExperience(int experience) {
        //System.err.println(experience);
        this.experience += experience;
        while(this.experience >= nextLevelXp()) {
            this.experience -= nextLevelXp();
            if(getLevel() < 50) {
                setLevel(getLevel()+1);
            } else {
                System.out.println("Не хочу стать, как One Punch Man. Притворюсь, что никакого левелапа не было.");
            }
        }
        System.err.println(this.experience);
    }
    public StringBuilder loot(Lootable target) {
        StringBuilder res = new StringBuilder("Получено:");
        for (Map.Entry<Item,Integer> entry: target.provideBackpack().entrySet()) {
            res.append("\n");
            res.append(entry.getKey().getName());
            res.append(" - ");
            res.append(entry.getValue().toString());
            res.append(" шт");
            putNumerousItem(entry.getKey(),entry.getValue());
        }
        target.provideBackpack().clear();
        return res;
    }
}
