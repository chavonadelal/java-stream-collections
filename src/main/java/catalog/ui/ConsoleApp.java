package catalog.ui;

import catalog.io.ProductCsvReader;
import catalog.io.ProductParser;
import catalog.model.Product;
import catalog.service.ProductCatalog;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

public final class ConsoleApp {
    private final BufferedReader input;
    private final PrintWriter output;
    private final ProductCatalog catalog;
    private final ProductCsvReader csvReader;

    public ConsoleApp(BufferedReader input, PrintWriter output,
                      ProductCatalog catalog, ProductCsvReader csvReader) {
        this.input = input;
        this.output = output;
        this.catalog = catalog;
        this.csvReader = csvReader;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            try {
                String command = read("Выберите пункт меню: ").strip();
                switch (command) {
                    case "1" -> addManually();
                    case "2" -> importFile();
                    case "3" -> showAll();
                    case "4" -> process();
                    case "5" -> getByIndex();
                    case "6" -> removeByIndex();
                    case "7" -> output.println("Размер коллекции: " + catalog.size());
                    case "0" -> {
                        output.println("Работа завершена.");
                        running = false;
                    }
                    default -> output.println("Неизвестная команда. Введите номер от 0 до 7.");
                }
            } catch (EOFException exception) {
                output.println("Ввод завершен. Работа завершена.");
                return;
            } catch (IllegalArgumentException exception) {
                output.println("Ошибка ввода: " + exception.getMessage());
            } catch (IOException exception) {
                output.println("Ошибка чтения консоли: " + exception.getMessage());
                return;
            }
        }
    }

    private void printMenu() {
        output.println("""

                ===== КАТАЛОГ ТОВАРОВ =====
                1. Добавить товар вручную
                2. Загрузить товары из CSV
                3. Показать коллекцию
                4. Обработать данные через Stream API
                5. Получить товар по индексу
                6. Удалить товар по индексу
                7. Показать размер коллекции
                0. Выход
                """);
    }

    private void addManually() throws IOException {
        String name = read("Название: ");
        String category = read("Категория: ");
        String price = read("Цена в рублях: ");
        String quantity = read("Остаток: ");
        Product product = ProductParser.parse(name, category, price, quantity);
        catalog.add(product);
        output.println("Добавлен товар: " + product);
    }

    private void importFile() throws IOException {
        String fileName = read("Путь к CSV-файлу в UTF-8: ").strip();
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("Путь к файлу не должен быть пустым.");
        }
        try {
            List<Product> imported = csvReader.read(Path.of(fileName));
            catalog.addAll(imported);
            output.println("Загружено товаров: " + imported.size());
        } catch (IOException | InvalidPathException | SecurityException exception) {
            output.println("Ошибка загрузки файла: " + exception.getMessage());
            output.println("Коллекция не изменена.");
        }
    }

    private void showAll() {
        List<String> descriptions = catalog.describeAll();
        if (descriptions.isEmpty()) {
            output.println("Коллекция пуста.");
            return;
        }
        for (int index = 0; index < descriptions.size(); index++) {
            output.println("[" + index + "] " + descriptions.get(index));
        }
        output.println("Всего товаров: " + catalog.size());
    }

    private void process() throws IOException {
        int minimumQuantity = ProductParser.parseQuantity(read("Минимальный остаток: "));
        List<String> result = catalog.process(minimumQuantity);
        output.println("Товары с остатком от " + minimumQuantity + ", цена по возрастанию:");
        if (result.isEmpty()) {
            output.println("Подходящих товаров нет.");
        } else {
            result.forEach(output::println);
        }
    }

    private void getByIndex() throws IOException {
        Integer index = readIndex();
        if (index != null) {
            output.println("Товар [" + index + "]: " + catalog.get(index));
        }
    }

    private void removeByIndex() throws IOException {
        Integer index = readIndex();
        if (index != null) {
            output.println("Удален товар: " + catalog.remove(index));
        }
    }

    private Integer readIndex() throws IOException {
        if (catalog.size() == 0) {
            output.println("Коллекция пуста.");
            return null;
        }
        int index = ProductParser.parseQuantity(read("Индекс (от 0 до " + (catalog.size() - 1) + "): "));
        if (index >= catalog.size()) {
            throw new IllegalArgumentException("Индекс должен быть от 0 до " + (catalog.size() - 1) + ".");
        }
        return index;
    }

    private String read(String prompt) throws IOException {
        output.print(prompt);
        output.flush();
        String line = input.readLine();
        if (line == null) {
            throw new EOFException();
        }
        return line;
    }
}
