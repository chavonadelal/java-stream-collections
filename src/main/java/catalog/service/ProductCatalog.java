package catalog.service;

import catalog.collection.DynamicArrayList;
import catalog.model.Product;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Stores products in the custom List and performs stream queries. */
public final class ProductCatalog {
    private final List<Product> products = new DynamicArrayList<>();

    public void add(Product product) {
        products.add(Objects.requireNonNull(product, "Product must not be null."));
    }

    public void addAll(List<Product> imported) {
        Objects.requireNonNull(imported, "Imported list must not be null.");
        imported.forEach(product -> Objects.requireNonNull(product, "Product must not be null."));
        products.addAll(imported);
    }

    public Product get(int index) {
        return products.get(index);
    }

    public Product remove(int index) {
        return products.remove(index);
    }

    public int size() {
        return products.size();
    }

    public List<String> describeAll() {
        return products.stream().map(Product::toString).toList();
    }

    public List<String> process(int minimumQuantity) {
        if (minimumQuantity < 0) {
            throw new IllegalArgumentException("Минимальный остаток не может быть отрицательным.");
        }
        return products.stream()
                .filter(product -> product.getQuantity() >= minimumQuantity)
                .sorted(Comparator.comparing(Product::getPrice))
                .map(Product::toString)
                .toList();
    }
}
