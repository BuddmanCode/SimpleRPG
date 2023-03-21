import java.util.ArrayList;
//Единица мира. Создаётся на основе локации. Оттуда получает информацию о существах и местности.
public class Cell {
    private Location location;
    //список существ, присутствующих в данный момент в ячейке
    ArrayList<Creature> creatureList = new ArrayList<Creature>();
    public Cell(Location location) {
        this.location = location;
        refresh();
    }
    ////по локации можно ходить
    public boolean isPassable() {
        return location.isPassable();
    }
    //предоставляет полный список существ
    public ArrayList<Creature> getCreatureList() {
        return creatureList;
    }
    //генерирует описание всех существ с нумерацией существ, доступных для взаимодействия
    public StringBuilder getCreaturesDescription() {
        StringBuilder res = new StringBuilder();
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
    //предоставляет список существ, доступных для взаимодействия
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
    //список существ, которые будут препятствовать побегу
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
    //доступно ли существо для взаимодействия
    public boolean creatureActionable(Creature creature) {
        return creature.isDamageable() || creature.isLootable() || creature.isTradeable() || creature.isServiceable();
    }
    //описание локации + текущее состояние ячейки
    @Override
    public String toString() {
        return getDescription().toString();
    }
    //описание локации + текущее состояние ячейки
    public StringBuilder getDescription() {
        return location.getDescription().append(!isSafe()?" (опасность)":"");
    }
    //описание локации
    public String getLocationName() {
        return location.toString();
    }
    //создаёт новых существ вместо старых
    public void refresh() {
        creatureList  = location.makeCreatures();
    }
    //безопасность локации. В небезопасной игрока будут атаковать
    public boolean isSafe() {
        for(Creature creature: creatureList) {
            if( creature.isAgressive() && creature.isAlive()) return false;
        }
        return true;
    }
    //будут ли игроку мешать отступать
    public boolean canGo() {
        for(Creature creature: creatureList) {
            if( creature.isAlive() && creature.isVeryAgressive() ) return false;
        }
        return true;
    }
}