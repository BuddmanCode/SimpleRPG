package main.items;

import main.creatures.Human;

import java.util.Optional;
//многократно используемый предмет, например меч
//не стоило разделять однократно и многократно используемые предметы
public abstract class MultiUsableItem extends Item implements Usable {
    private int effectValue;
    private Item wasteItem;
    private int remainingUses;
    public MultiUsableItem(int itemID, String name, int cost, int level, int effectValue, int remainingUses, Item wasteItem) {
        super(itemID, name, cost, level, true);
        this.effectValue = effectValue;
        this.wasteItem = wasteItem;
        this.remainingUses = remainingUses;
    }
    public MultiUsableItem(int itemID, String name, int cost, int level, int effectValue, int remainingUses) {
        this(itemID, name, cost, level, effectValue, remainingUses, null);
    }
    public int getEffectValue() {
        return effectValue;
    }
    //эффект, оказыыаемый предметом
    abstract <T> void effect(T target);
    //интерфейс использования. проверяет возможность использования, вызывает эффект
    @Override
    public <T extends Human> Optional<Item> use(T target){
        if(remainingUses > 0) {
            remainingUses--;
            effect(target);
        }
        if (remainingUses<=0) {
            return Optional.ofNullable(wasteItem);
        }
        return Optional.ofNullable(this);
    }

    /*
    * 1) герой должен понимать, куда использовать. Это его ответственность.
    * 2) Предмет отвечает за эффект.
    * 3) Не используемый предмет не имеет смысла извлекать из рюкзака.
    * 4) Ну или можно взять и перед изменениями количеств сравнить с результатом.
    * 5) Предмет может удалиться, замениться и сохраниться.
    * 6) Не стоит вообще предоставлять возможность использовать неиспользуемое.
    * 7) Применять можно разное и по-разному. Мечь не извлекается из руки при атаке.
    * 8) То есть при использовании ничего не возвращается, пока мечь не сломался.*/
}
