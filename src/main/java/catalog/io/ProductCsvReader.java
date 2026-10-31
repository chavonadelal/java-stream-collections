package catalog.io;

import catalog.collection.DynamicArrayList;
import catalog.model.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Imports a documented semicolon-separated CSV subset, without quoted fields. */
public final class ProductCsvReader {
    private static final String HEADER = "name;category;price;quantity";

    public List<Product> read(Path path) throws IOException {
        List<Product> imported = new DynamicArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            validateHeader(reader.readLine());
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.isBlank()) {
                    imported.add(parseLine(line, lineNumber));
                }
            }
        }
        return imported;
    }

    private void validateHeader(String header) throws CsvFormatException {
        if (header != null && header.startsWith("\uFEFF")) {
            header = header.substring(1);
        }
        if (header == null || !HEADER.equals(header.strip())) {
            throw new CsvFormatException(1, "Ожидается заголовок " + HEADER);
        }
    }

    private Product parseLine(String line, int lineNumber) throws CsvFormatException {
        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new CsvFormatException(lineNumber, "Ожидаются четыре поля, разделенные символом ;");
        }
        try {
            return ProductParser.parse(fields[0], fields[1], fields[2], fields[3]);
        } catch (IllegalArgumentException exception) {
            throw new CsvFormatException(lineNumber, exception.getMessage());
        }
    }
}
