import java.io.*;
import java.util.ArrayList;
//Консольный интерфейс. Здесь происходит основное взаимодействие с пользователем
public class UserInterfaceConsole extends Thread {
    World world;
    boolean inGame;
    //Создаётся мир, проверяется удалось ли создать героя
    public UserInterfaceConsole() {
        try{
            world = new World();
            if (!world.heroExist()) throw new Exception("Hero not exists");
        } catch (Exception e) { //SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //Здесь генерируется описание местонахождения, окружения и возможности перемещения
    private String getMenu() {
        StringBuilder menu = new StringBuilder("\n=======================================================================\n");
        menu.append(world.getDescription());
        menu.append("\n");
        menu.append(world.getCreaturesDescription());
        //inventoryActions
        menu.append("\n-----------------------------------------------------------------------\n");
        if(world.getAtionableList().size() > 0) {
            menu.append("T - выбрать цель\n");
        }
        if(world.canGo()){
            if(world.leftAllowed()) {
                menu.append("A - пойти влево\n");
            }
            if(world.rightAllowed()) {
                menu.append("D - пойти вправо\n");
            }
            if(world.upAllowed()) {
                menu.append("W - пойти вперёд\n");
            }
            if(world.downAllowed()) {
                menu.append("S - пойти назад\n");
            }
        } else {
            if(world.leftAllowed()) {
                menu.append("A - попытаться сбежать влево\n");
            }
            if(world.rightAllowed()) {
                menu.append("D - попытаться сбежать вправо\n");
            }
            if(world.upAllowed()) {
                menu.append("W - попытаться сбежать вперёд\n");
            }
            if(world.downAllowed()) {
                menu.append("S - попытаться сбежать назад\n");
            }
        }
        if(world.heroHasUsable() > 0)
            menu.append("J - инвентарь\n");
        menu.append("Q - выйти из игры\n");
        menu.append("->");
        return menu.toString();
    }
    //Здесь вызывается взаимодействия с инвентарём героя.
    private void inventory() throws IOException {
        world.useItems();
    }
    // Здесь обрабатывается взаимодействие с существами, находящимися рядом и предоставляющих соответствующие интерфейсы
    private void targetAction() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        int targetNumber = 0;
        String str = null;
        ArrayList<Creature> actionableCreatures = world.getAtionableList();
        if(actionableCreatures.size() > 0) {
            if (actionableCreatures.size() == 1)
                targetNumber = 1;
            else {
                while (targetNumber <= 0 || targetNumber > actionableCreatures.size()) {
                    writer.write("Выберите цель (1-" + String.valueOf(actionableCreatures.size()) + ")->");
                    writer.flush();
                    str = reader.readLine();
                    if (str.length() > 0) {
                        int i = 0;
                        for (; i < str.length(); i++) {
                            if (str.charAt(i) < '0' || str.charAt(i) > '9') break;
                        }
                        if (i > 0) {
                            targetNumber = Integer.parseInt(str.substring(0, i));
                        }
                    }
                }
            }
            targetNumber--;
            boolean repeat = true;
            while (repeat) {
                StringBuilder builder = actionableCreatures.get(targetNumber).getShortDescription();
                builder.append("\n");
                if (actionableCreatures.get(targetNumber).isDamageable()) builder.append("F - атаковать\n");
                if (actionableCreatures.get(targetNumber).isTradeable()) builder.append("G - торговать\n");
                if (actionableCreatures.get(targetNumber).isServiceable()) builder.append("H - отдохнуть\n");
                if (actionableCreatures.get(targetNumber).isLootable()) builder.append("L - обобрать\n");
                builder.append("->");
                writer.write(builder.toString());
                writer.flush();
                str = reader.readLine();
                switch (str) {
                    case "а":
                    case "А":
                    case "f":
                    case "F":
                        if (actionableCreatures.get(targetNumber).isDamageable()) {
                            writer.write(world.attack(actionableCreatures.get(targetNumber)).toString());
                            repeat = false;
                            break;
                        }
                    case "п":
                    case "П":
                    case "g":
                    case "G":
                        if (actionableCreatures.get(targetNumber).isTradeable()) {
                            world.trade((Trader) actionableCreatures.get(targetNumber));
                            repeat = false;
                            break;
                        }
                    case "р":
                    case "Р":
                    case "h":
                    case "H":
                        if (actionableCreatures.get(targetNumber).isServiceable()) {
                            writer.write(world.resetWorld().toString());
                            repeat = false;
                            break;
                        }
                    case "д":
                    case "Д":
                    case "l":
                    case "L":
                        if (actionableCreatures.get(targetNumber).isLootable()) {
                            writer.write(world.loot(actionableCreatures.get(targetNumber)).toString());
                            repeat = false;
                            break;
                        }
                }
            }
        } else {
            writer.write("Ничего не получилось\n");
        }
        writer.flush();
        //return str;
    }
    // Обработка выборов основного меню
    @Override
    public void run() {
        inGame = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        while(world.isHeroAlive() && inGame) {
            try {
                writer.write(getMenu());
                writer.flush();
                String str = reader.readLine();
                writer.write("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv\n");
                writer.flush();
                switch (str.charAt(0)) {
                    case 'е':
                    case 'Е':
                    case 't':
                    case 'T': {
                        targetAction();
                        //writer.write("\n");
                        writer.write(world.rake().toString());
                        break;
                    }
                    case 'ф':
                    case 'Ф':
                    case 'a':
                    case 'A': {
                        writer.write("\n");
                        writer.write(world.rake().toString());
                        if(world.leftAllowed()) world.stepLeft();
                        break;
                    }
                    case 'в':
                    case 'В':
                    case 'd':
                    case 'D': {
                        writer.write("\n");
                        writer.write(world.rake().toString());
                        if(world.rightAllowed()) world.stepRight();
                        break;
                    }
                    case 'ц':
                    case 'Ц':
                    case 'w':
                    case 'W': {
                        writer.write("\n");
                        writer.write(world.rake().toString());
                        if(world.upAllowed()) world.stepUp();
                        break;
                    }
                    case 'ы':
                    case 'Ы':
                    case 's':
                    case 'S': {
                        writer.write("\n");
                        writer.write(world.rake().toString());
                        if(world.downAllowed()) world.stepDown();
                        break;
                    }
                    case 'о' :
                    case 'О' :
                    case 'j' :
                    case 'J' :
                        if(world.heroHasUsable() > 0) {
                            inventory();
                            writer.write(world.rake().toString());
                            break;
                        }
                    case 'й':
                    case 'Й':
                    case 'q':
                    case 'Q': {
                        inGame = false;
                        writer.write("Герой внезапно ушёл на пенсию.");
                        break;
                    }
                    default: {
                        if(!world.currentSafe()) writer.write("Герой замешкался");
                        writer.write("\n");
                        writer.write(world.rake().toString());
                        break;
                    }
                }
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
