import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class World {
    private int xCurrent;
    private int yCurrent;
    private int maxWidth;
    private int maxHeight;
    private HashMap<Integer, CreatureStatPack> statPackMap;
    int heroId;
    private Hero hero;

    private ArrayList<Location> locations;
    private HashMap<Integer, CreatureGroup> creatureGroups;
    //private Location[][] globalLocations; //Система ячеек меня не удовлетворяет. Нужны именно ссылки на локацию с координатами.
    private Cell[][] globalCells;
    private HashMap<Integer, Terrain> terrainsMap;
    public World() throws SQLException {
        creatureGroups = DBConnection.ReadCreatureGroups(); //Загружаем группы существ
        statPackMap = DBConnection.ReadCreatureStats(creatureGroups); //Загружаем характеристики существ
        terrainsMap = DBConnection.ReadTerrains(statPackMap); //Загружаем виды поверхности
        locations = DBConnection.ReadLocations(terrainsMap); //Загружаем локации
        heroId = DBConnection.findHero(); //Ищем героя
        generateGlobalCellStructure(); //размещаем локации в мире
        for(Location loc: locations) {
            if(loc.isSafe()) {
                placeHero(loc.getXBottomLeft(), loc.getYBottomLeft());
                break;
            }
        }
    }
    public boolean heroExist() {
        if( hero == null) return false;
        return true;
    }
    public boolean placeHero(int x, int y) {
        if(isPassable(yCurrent,xCurrent)) {
            if (hero == null)
                hero = new Hero(statPackMap.get(heroId), 1, "НеВан Панчманович", 0);
            xCurrent = x;
            yCurrent = y;
            return true;
        }
        return false;
    }
    private void generateGlobalCellStructure() {
        int maxWidth = 0, maxHeight = 0;
        for (Location loc: locations) {
            maxWidth = Math.max(maxWidth, (loc.getXBottomLeft() + loc.getWidth()));
            maxHeight = Math.max(maxHeight, (loc.getYBottomLeft() + loc.getHeight()));
        }
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        //globalCells = new Cell[maxHeight][maxWidth];
        globalCells = new Cell[maxHeight][maxWidth];
        int x,y;
        ArrayList<Location> locationsToDelete = new ArrayList<Location>();
        for (Location loc: locations) {
            x = loc.getXBottomLeft();
            y = loc.getYBottomLeft();
            try {
                for(int yLoc = 0;yLoc < loc.getHeight();yLoc++,y++) {
                    for(int xLoc = 0;xLoc < loc.getWidth();xLoc++,x++) {
                        if (globalCells[y][x] != null)
                            throw new ArrayStoreException("GlobalCell at (" + x + "," + y + ") already used. Location placement filed.");
                        globalCells[y][x] = new Cell(loc);
                    }
                }
            } catch (ArrayStoreException e) {
                x--;
                e.printStackTrace();
                for (int i = loc.getYBottomLeft(); i < y; i++) { //Убираем части локации, которые не удалось загрузить
                    for (int j = loc.getXBottomLeft(); j < x; j++) {
                        globalCells[y][x] = null;
                    }
                }
                locationsToDelete.add(loc);
            }
        }
        for (Location locDel : locationsToDelete) {
            locations.remove(locDel);
        }
    }
    public boolean isPassable(int y, int x) {
        if (y < maxHeight && x < maxWidth && x >= 0 && y >= 0) {
            if(globalCells[y][x]!=null){
                return globalCells[y][x].isPassable();
            }
        }
        return false;
    }
    public boolean upAllowed() {
        return isPassable(yCurrent + 1,xCurrent);
    }
    public boolean downAllowed() {
        return isPassable(yCurrent - 1,xCurrent);
    }
    public boolean leftAllowed() {
        return isPassable(yCurrent,xCurrent - 1);
    }
    public boolean rightAllowed() {
        return isPassable(yCurrent,xCurrent + 1);
    }
    public String description(int y, int x) {
        if (y < maxHeight && x < maxWidth && x >= 0 && y >= 0) {
            if(globalCells[y][x]!=null){
                return globalCells[y][x].toString();
            }
        }
        return "Край земли";// (где-то там затаились древние боги и хтонические чудовища)";
    }
    public StringBuilder getDescription() {
        StringBuilder res = hero.getShortDescription().append("\n").append(currentDescription());
        if(!currentSafe()) res.append(", идёт бой");
        if(!canGo()) res.append(" (просто так не уйти)");
        res.append("\nСлева: ");
        res.append(leftDescription());
        res.append("\nСправа: ");
        res.append(rightDescription());
        res.append("\nСпереди: ");
        res.append(upDescription());
        res.append("\nСзади: ");
        res.append(downDescription());
        return res;
    }
    public int getCreatureQuantity(){
        return globalCells[yCurrent][xCurrent].getCreatureList().size();
    }
    public ArrayList<Creature> getAtionableList(){
        return globalCells[yCurrent][xCurrent].getActionableList();
    }
    public int heroHasUsable(){
        int i=0;
        for (Map.Entry<Item,Integer> entry: hero.provideBackpack().entrySet()) {
            if(entry.getKey() instanceof Usable)
                i++;
        }
        return i;
    }
    public boolean currentSafe() { //в текущей ячейке нет противника
        return globalCells[yCurrent][xCurrent].isSafe();
    }
    public boolean canGo() { //в текущей ячейке нет агрессивного противника
        return globalCells[yCurrent][xCurrent].canGo();
    }
    public String currentDescription() {
        return globalCells[yCurrent][xCurrent].getLocationName();
    }
    public String upDescription() {
        return description(yCurrent + 1, xCurrent);
    }
    public String downDescription() {
        return description(yCurrent - 1, xCurrent);
    }
    public String rightDescription() {
        return description(yCurrent, xCurrent + 1);
    }
    public String leftDescription() {
        return description(yCurrent, xCurrent - 1);
    }
    private boolean tryRun() {
        if(canGo())
            return true;
        else {
            int enemyAgility = 0;
            int enemyPower = 0;
            ArrayList<Creature> veryAgressive = globalCells[yCurrent][xCurrent].getVeryAgressiveList();
            for(Creature creature: veryAgressive) {
                enemyAgility += creature.getAgility();
                enemyPower += creature.getPower();
            }
            if(hero.getAgility() > enemyAgility) { //по хорошему это бы сделать nodamage
                return true;
            }
            if(hero.getPower() > enemyPower) {
                return true;
            }
            return false;
        }
    }
    public boolean stepUp() {
        if(upAllowed()) {
            if(tryRun()){
                yCurrent++;
                return true;
            }
        }
        return false;
    }
    public boolean stepDown() {
        if(downAllowed()) {
            if(tryRun()){
                yCurrent--;
                return true;
            }
        }
        return false;
    }
    public boolean stepRight() {
        if(rightAllowed()) {
            if(tryRun()){
                xCurrent++;
                return true;
            }
        }
        return false;
    }
    public boolean stepLeft() {
        if(leftAllowed()) {
            if(tryRun()){
                xCurrent--;
                return true;
            }
        }
        return false;
    }
    public StringBuilder resetWorld() {
        for (Cell[] line: globalCells) {
            for (Cell cell: line) {
                cell.refresh();
            }
        }
        hero.setStatsToLevel();
        return new StringBuilder("Батя готовит кашу!\nПрошло время.\nТорговцы пополнили свои запасы, монстры снова расползлись по миру.\nЗдоровье героя восстановлено.");
    }
    public StringBuilder getCreaturesDescription() {
        return globalCells[yCurrent][xCurrent].getCreaturesDescription();
    }
    public StringBuilder attack(Creature target) {
        StringBuilder res = new StringBuilder();
        res.append("Герой ударил ");
        res.append(target.getShortDescription());
        int damage = hero.attack(target);
        if(damage > 0) {
            res.append(" на ");
            res.append(damage);
        } else {
            res.append(", но промахнулся");
        }
        return res;
    }
    public StringBuilder rake() {
        StringBuilder res = new StringBuilder();
        int damage;
        for(Creature creature: globalCells[yCurrent][xCurrent].getCreatureList()) {
            if( creature.isAgressive() && creature.isAlive() ) {
                res.append("\n");
                res.append(creature.getShortDescription());
                damage = creature.attack(hero);
                if(damage > 0) {
                    res.append(" ударил героя на ");
                    res.append(damage);
                } else {
                    res.append(" промахнулся");
                }
                if(!hero.isAlive()) {
                    res.append("\nГерой умер смертью мёртвых. Не надо так.");
                }
            }
        }
        return res;
    }
    /*public StringBuilder linger() {

    }*/
    public boolean isHeroAlive() {
        return hero.isAlive();
    }

    public StringBuilder loot(Creature creature) {
        return hero.loot(creature);
    }

    public void useItems() throws IOException  {
        StringBuilder builder = new StringBuilder();
        Map<Item, Integer> backpack = hero.provideBackpack();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<Item> usableList = new ArrayList<Item>();
        int i = 1;
        for(Map.Entry<Item, Integer> backpackItem: backpack.entrySet()) {
            if(backpackItem.getKey() instanceof Usable) {
                builder.append("\n").append(i).append(" - ").append(backpackItem.getKey().getName()).append(" (").
                        append(backpackItem.getKey().getCost()).append(" монет) - ").append(backpackItem.getValue()).append(" шт.");
                usableList.add(backpackItem.getKey());
                i++;
            }
        }
        System.out.println(builder);
        String str = reader.readLine();
        if (str.length() > 0) {
            i = 0;
            for (; i < str.length(); i++) {
                if (str.charAt(i) < '0' || str.charAt(i) > '9') break;
            }
            if (i > 0) {
                i = Integer.parseInt(str.substring(0, i));
            }
        }
        if(i>0) {
            i--;
            if(backpack.get(usableList.get(i)) > 0) {
                ((HealingItem)usableList.get(i)).use(hero);
                if(backpack.get(usableList.get(i)) > 1) {
                    backpack.replace(usableList.get(i), backpack.get(usableList.get(i)) - 1);
                } else {
                    backpack.remove(usableList.get(i));
                }
            }
        }

    }
    public void trade(Trader target) throws IOException { //Всё очень плохо (((
        StringBuilder builder = new StringBuilder();
        Map<Item, Integer> backpack = hero.provideBackpack();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String str;
        boolean repeat;
        for(Map.Entry<Item, Integer> backpackItem: backpack.entrySet()) {
            if(!(backpackItem.getKey() instanceof Money)) {
                repeat = true;
                while(repeat) {
                    builder.append("\n").append(backpackItem.getKey().getName()).append(" (").
                            append(backpackItem.getKey().getCost()).append(" монет) - ").append(backpackItem.getValue()).append(" шт.");
                    System.out.println(builder.append("\nПродать[Y/N] (опционально количество)?->"));
                    str = reader.readLine();
                    switch (str) {
                        case "н":
                        case "Н":
                        case "Y":
                        case "y": {
                            int quantity = 0;
                            if(str.length() > 1) {
                                int i = 0;
                                str = str.substring(1).trim();
                                for (; i < str.length(); i++) {
                                    if (str.charAt(i) < '0' || str.charAt(i) > '9') break;
                                }
                                if (i > 0) {
                                    quantity = Integer.parseInt(str.substring(0, i));
                                }
                            }
                            if(quantity != 0) {
                                hero.addMoney(((Trader)target).sell(backpackItem.getKey(),quantity));
                                backpack.replace(backpackItem.getKey(), backpackItem.getValue() - quantity);
                            } else {
                                hero.addMoney(((Trader) target).sell(backpackItem.getKey(), backpackItem.getValue()));
                                backpack.remove(backpackItem);
                            }
                        }
                        case "т":
                        case "Т":
                        case "n":
                        case "N": {
                            repeat = false;
                        }

                    }
                }
            }
        }
        for(Map.Entry<Item, Integer> traderItem: target.provideBackpack().entrySet()) {
            if (!(traderItem.getKey() instanceof Money)) {
                repeat = true;
                while (repeat) {
                    builder.append("\n").append(traderItem.getKey().getName()).append(" (").
                            append(traderItem.getKey().getCost()).append(" монет) - ").append(traderItem.getValue()).append(" шт.");
                    System.out.println(builder.append("\nКупить[Y/N] (опционально количество)?->"));
                    str = reader.readLine();
                    if(str.length()>0) {
                        switch (str.charAt(0)) {
                            case 'н':
                            case 'Н':
                            case 'Y':
                            case 'y': {
                                int quantity = 0;
                                if (str.length() > 1) {
                                    int i = 0;
                                    str = str.substring(1).trim();
                                    for (; i < str.length(); i++) {
                                        if (str.charAt(i) < '0' || str.charAt(i) > '9') break;
                                    }
                                    if (i > 0) {
                                        quantity = Integer.parseInt(str.substring(0, i));
                                    }
                                } else {
                                    quantity = traderItem.getValue();
                                }
                                if (quantity != 0) {
                                    if (quantity > traderItem.getValue()) quantity = traderItem.getValue();
                                    //костыль ппц, да ту всё костыль. Надо полностью переделать
                                    if (hero.backpack.get(new Money()) >= traderItem.getKey().getCost() * quantity * 1.2) {
                                        int newMoney = ((Trader) target).buy(traderItem.getKey(), quantity);
                                        hero.putNumerousItem(traderItem.getKey(), newMoney);
                                    }
                                } else System.out.println("Не хватает денег");
                            }
                            case 'т':
                            case 'Т':
                            case 'n':
                            case 'N': {
                                repeat = false;
                            }

                        }
                    }
                }
            }
        }
    }
}
