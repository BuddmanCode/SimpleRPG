package main.items;


import main.creatures.Healable;

//Предмет, оказывающий эффект лечения
public class HealingItem extends OnceUsableItem {

    public HealingItem(int itemID, String name, int cost, int level, int effectValue, Item wasteItem) {
        super(itemID, name, cost, level, effectValue, false, wasteItem);
    }
    public HealingItem(int itemID, String name, int cost, int level, int effectValue) {
        this(itemID, name, cost, level, effectValue, null);
    }
    //применяет лечение к выбранной цели
    @Override
    <T> void effect(T target) throws ClassCastException {
        if(target instanceof Healable) {
            ((Healable) target).applyHeal(getEffectValue());
            return;
        }
        throw new ClassCastException("Target is not instance of java.Healable");
    }
}