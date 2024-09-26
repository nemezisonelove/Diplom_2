package model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Класс представляет заказ в Stellar Burgers, содержащий список ингредиентов.
 * <p>
 * Позволяет получить или изменить список ингредиентов для заказа.
 */
public class OrderStellar {

    /**
     * Массив ингредиентов, которые содержатся в заказе.
     */
    @NotNull
    private ArrayList<String> ingredients;

    /**
     * Конструктор для создания заказа с указанным списком ингредиентов.
     *
     * @param ingredients Список ингредиентов для заказа. Не может быть null.
     */
    public OrderStellar(@NotNull ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * Возвращает список ингредиентов заказа.
     *
     * @return Список ингредиентов. Никогда не возвращает null.
     */
    @NotNull
    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    /**
     * Заменяет текущий список ингредиентов на новый.
     *
     * @param ingredients Новый список ингредиентов. Может быть null для удаления всех ингредиентов.
     */
    public void setIngredients(@Nullable ArrayList<String> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }
}
