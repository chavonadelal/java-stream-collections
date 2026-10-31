package catalog.io;

import java.io.IOException;

/** A readable import error with a physical line number. */
public final class CsvFormatException extends IOException {
    private static final long serialVersionUID = 1L;

    public CsvFormatException(int lineNumber, String message) {
        super("Строка " + lineNumber + ": " + message);
    }
}
