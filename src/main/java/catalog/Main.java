package catalog;

import catalog.io.ProductCsvReader;
import catalog.service.ProductCatalog;
import catalog.ui.ConsoleApp;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Console console = System.console();
        try (BufferedReader input = new BufferedReader(console == null
                ? new InputStreamReader(System.in, StandardCharsets.UTF_8) : console.reader());
             PrintWriter output = console == null
                     ? new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true)
                     : console.writer()) {
            new ConsoleApp(input, output, new ProductCatalog(), new ProductCsvReader()).run();
        } catch (IOException exception) {
            System.err.println("Ошибка закрытия консольного ввода: " + exception.getMessage());
        }
    }
}
