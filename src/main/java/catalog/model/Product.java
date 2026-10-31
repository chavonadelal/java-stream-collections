package catalog.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Immutable domain object. Validation also applies to programmatic creation. */
public final class Product {
    private final String name;
    private final String category;
    private final BigDecimal price;
    private final int quantity;

    public Product(String name, String category, BigDecimal price, int quantity) {
        this.name = validateText(name, "Название", 100);
        this.category = validateText(category, "Категория", 50);
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("Цена должна быть больше нуля.");
        }
        try {
            this.price = price.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("Цена должна содержать не более двух знаков после запятой.",
                    exception);
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Остаток не может быть отрицательным.");
        }
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "%s | %s | %s руб. | остаток: %d"
                .formatted(name, category, price.toPlainString(), quantity);
    }

    private static String validateText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + ": значение не должно быть пустым.");
        }
        String normalized = value.strip();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(field + ": максимум " + maxLength + " символов.");
        }
        if (normalized.indexOf(';') >= 0 || normalized.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException(field + ": недопустимый разделитель или управляющий символ.");
        }
        return normalized;
    }
}
