package catalog.io;

import catalog.model.Product;

import java.math.BigDecimal;

/** Shared parsing rules for the console and CSV import. */
public final class ProductParser {
    private ProductParser() {
    }

    public static Product parse(String name, String category, String price, String quantity) {
        return new Product(name, category, parsePrice(price), parseQuantity(quantity));
    }

    private static BigDecimal parsePrice(String value) {
        if (value == null || !value.strip().matches("[0-9]+([.,][0-9]{1,2})?")) {
            throw new IllegalArgumentException("Цена: введите положительное число, максимум два знака после запятой.");
        }
        return new BigDecimal(value.strip().replace(',', '.'));
    }

    public static int parseQuantity(String value) {
        if (value == null || !value.strip().matches("[0-9]+")) {
            throw new IllegalArgumentException("Количество должно быть целым неотрицательным числом.");
        }
        try {
            return Integer.parseInt(value.strip());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Количество не должно превышать " + Integer.MAX_VALUE + ".",
                    exception);
        }
    }
}
