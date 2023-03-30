package main.world;

import main.creatures.*;
import main.db.DBConnection;
import main.items.HealingItem;
import main.items.Item;
import main.items.Money;
import main.items.Usable;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
// Здесь содержится структура мира и происходят взаимодействия между его обитателями
public class World {
    //Текущая позиция героя.
    private int xCurrent;
    private int yCurrent;
    //чтоб не выходить за пределы карты.
    private int maxWidth;
    private int maxHeight;
    //Заготовки для генерации существ.
    private HashMap<Integer, CreatureStatPack> statPackMap;
    int heroId;
    private Hero hero;
    //Доступные локации.
    private ArrayList<Location> locations;
    //группы существ. Нужны для выбора конструктора.
    private HashMap<Integer, CreatureGroup> creatureGroups;
    //"структура" мира. Заполняется на основе данных локаций.
    private Cell[][] globalCells;
    //виды поверхности. Используются в локациях.
    private HashMap<Integer, Terrain> terrainsMap;
    // Загружаем из бд всякое разное, формируем карту, исходя из загруженного
    public World() throws SQLException {
        creatureGroups = DBConnection.ReadCreatureGroups(); //Загружаем группы существ
        statPackMap = DBConnection.ReadCreatureStats(creatureGroups); //Загружаем характеристики существ
        terrainsMap = DBConnection.ReadTerrains(statPackMap); //Загружаем виды поверхности
        locations = DBConnection.ReadLocations(terrainsMap); //Загружаем локации
        heroId = DBConnection.findHero(); //Ищем героя
        generateGlobalCellStructure(); //размещаем локации в мире
        for(Location loc: locations) {
            if(loc.isSafe()) {
                placeHero(loc.getXBottomLeft(), loc.getYBottomLeft()); //размещаем героя в первой попавшейся безопасной локации (в норме она будет первой)
                break;
            }
        }
    }
    //наверное, надо было в конструкторе исключение тупо кидать
    public boolean heroExist() {
        if( hero == null) return false;
        return true;
    }
    //Помещение гуроя в указанную локация (если возможно).
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
    //Создание массива ячеек. Размещение ячеек в соответствии с локациями. (локация в данном случае - описательная часть. ходит герой по ячейкам)
    private void generateGlobalCellStructure() {
        int maxWidth = 0, maxHeight = 0;
        for (Location loc: locations) {
            maxWidth = Math.max(maxWidth, (loc.getXBottomLeft() + loc.getWidth()));
            maxHeight = Math.max(maxHeight, (loc.getYBottomLeft() + loc.getHeight()));
        }
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
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
                            throw new ArrayStoreException("GlobalCell at (" + x + "," + y + ") already used. java.world.Location placement filed.");
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
    //Возможность пойти в ячейку. (существование, проходимость)
    public boolean isPassable(int y, int x) {
        if (y < maxHeight && x < maxWidth && x >= 0 && y >= 0) {
            if(globalCells[y][x]!=null){
                return globalCells[y][x].isPassable();
            }
        }
        return false;
    }
    //возможность сделать шаг вверх (вперёд)
    public boolean upAllowed() {
        return isPassable(yCurrent + 1,xCurrent);
    }
    //возможность сделать шаг вниз (назад)
    public boolean downAllowed() {
        return isPassable(yCurrent - 1,xCurrent);
    }
    //возможность сделать шаг влево
    public boolean leftAllowed() {
        return isPassable(yCurrent,xCurrent - 1);
    }
    //возможность сделать шаг вправо
    public boolean rightAllowed() {
        return isPassable(yCurrent,xCurrent + 1);
    }
    //Описание указанной ячейки
    public String description(int y, int x) {
        if (y < maxHeight && x < maxWidth && x >= 0 && y >= 0) {
            if(globalCells[y][x]!=null){
                return globalCells[y][x].toString();
            }
        }
        return "Край земли";// (где-то там затаились древние боги и хтонические чудовища)";
    }
    //Описание происходящего в текущей ячейке, описание окружение
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
    //Предоставляет список существ, доступных для взаимодействия
    public ArrayList<Creature> getAtionableList(){
        return globalCells[yCurrent][xCurrent].getActionableList();
    }
    //Количество используемых вещей у героя (чисто для меню)
    public int heroHasUsable(){
        int i=0;
        for (Map.Entry<Item,Integer> entry: hero.provideBackpack().entrySet()) {
            if(entry.getKey() instanceof Usable)
                i++;
        }
        return i;
    }
    //в текущей ячейке нет противника
    public boolean currentSafe() {
        return globalCells[yCurrent][xCurrent].isSafe();
    }
    //в текущей ячейке нет противника, которые будет препятствовать отступлению
    public boolean canGo() {
        return globalCells[yCurrent][xCurrent].canGo();
    }
    //описание локации, к которой относится текущая ячейка
    public String currentDescription() {
        return globalCells[yCurrent][xCurrent].getLocationName();
    }
    //Описание локации спереди
    public String upDescription() {
        return description(yCurrent + 1, xCurrent);
    }
    //Описание локации  сзади
    public String downDescription() {
        return description(yCurrent - 1, xCurrent);
    }
    //Описание локации справа
    public String rightDescription() {
        return description(yCurrent, xCurrent + 1);
    }
    //Описание локации слева
    public String leftDescription() {
        return description(yCurrent, xCurrent - 1);
    }
    //Попытка побега в зависимости от соотношения характеристик героя и противников, присутствующих в ячейке
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
    //шаг вперёд
    public boolean stepUp() {
        if(upAllowed()) {
            if(tryRun()){
                yCurrent++;
                return true;
            }
        }
        return false;
    }
    //шаг назад
    public boolean stepDown() {
        if(downAllowed()) {
            if(tryRun()){
                yCurrent--;
                return true;
            }
        }
        return false;
    }
    //шаг вправо
    public boolean stepRight() {
        if(rightAllowed()) {
            if(tryRun()){
                xCurrent++;
                return true;
            }
        }
        return false;
    }
    //шаг влево
    public boolean stepLeft() {
        if(leftAllowed()) {
            if(tryRun()){
                xCurrent--;
                return true;
            }
        }
        return false;
    }
    //обновление мира, пересоздание монстров в ячейках, восстановление характеристик героя.
    // (планировалось, что только удаление, а создаваться они будут, когда ячейка становится "видна". Но механику видимости не реализовал.
    public StringBuilder resetWorld() {
        for (Cell[] line: globalCells) {
            for (Cell cell: line) {
                cell.refresh();
            }
        }
        hero.setStatsToLevel();
        return new StringBuilder("Батя готовит кашу!\nПрошло время.\nТорговцы пополнили свои запасы, монстры снова расползлись по миру.\nЗдоровье героя восстановлено.");
    }
    //Описание существ текущей ячейки для главного меню
    public StringBuilder getCreaturesDescription() {
        return globalCells[yCurrent][xCurrent].getCreaturesDescription();
    }
    //Стукать
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
    //Огребать ото всех существ в ячейке
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
    //Проверка живости героя
    public boolean isHeroAlive() {
        return hero.isAlive();
    }
    //Передать герою содержимое рюкзака указанного существа
    public StringBuilder loot(Creature creature) {
        return hero.loot(creature);
    }
    //Вывод списка используемых предметов из инвентаря героя, запрос, какой из них нужно использовать. Использование.
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
    //Меню торговли. На данный момент реализовано дендрофекальным методом.
    // Сначала возможность продать один пункт из инвентаря героя, потом возможность купить один пункт из инвентаря торговца.
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
                                    if (hero.provideBackpack().get(new Money()) >= traderItem.getKey().getCost() * quantity * 1.2) {
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
