import java.util.Map;
import java.util.NoSuchElementException;
//Герой. Может получать опыт при убийстве врагов.
public class Hero extends Human implements Experienceable {
    int experience;
    public Hero(CreatureStatPack stats, int level, String name, int experience) throws NoSuchElementException {
        super(stats, level, name);
        this.experience = experience;
    }
    public int nextLevelXp() {
        return 10 * this.getLevel();
    }
    //получает опыт при убийстве врагов.
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
    //Повышение уровня при получении достаточного количества опыта
    @Override
    public void getExperience(int experience) {
        this.experience += experience;
        while(this.experience >= nextLevelXp()) {
            this.experience -= nextLevelXp();
            if(getLevel() < 50) {
                setLevel(getLevel()+1);
            } else {
                System.out.println("Не хочу стать, как One Punch Man. Притворюсь, что никакого левелапа не было.");
            }
        }
    }
    //забирает содержимое рюкзака цели. Возвращает описание полученных предметов и их количество.
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
