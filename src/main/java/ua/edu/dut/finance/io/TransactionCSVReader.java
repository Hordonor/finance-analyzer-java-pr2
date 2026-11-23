package ua.edu.dut.finance.io;

import ua.edu.dut.finance.model.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Reads CSV text and converts its rows into transactions without instance state. */
public abstract class TransactionCSVReader {
    private TransactionCSVReader() {
    }

    /** Reads a local path or an HTTP(S) URL. */
    public static List<Transaction> readTransactions(String source) throws IOException {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Джерело CSV не може бути порожнім.");
        }
        if (source.startsWith("http://") || source.startsWith("https://")) {
            URLConnection connection = URI.create(source).toURL().openConnection();
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
            try (InputStream input = connection.getInputStream()) {
                return parseTransactions(readLines(input));
            }
        }
        return readTransactions(Path.of(source));
    }

    public static List<Transaction> readTransactions(Path path) throws IOException {
        try (InputStream input = Files.newInputStream(path)) {
            return parseTransactions(readLines(input));
        }
    }

    /** Source-independent reading stage: turns any UTF-8 stream into lines. */
    public static List<String> readLines(InputStream input) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    /** Source-independent processing stage: parses CSV lines into the domain model. */
    public static List<Transaction> parseTransactions(Iterable<String> lines) {
        List<Transaction> transactions = new ArrayList<>();
        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            List<String> columns = parseCsvLine(line);
            if (lineNumber == 1 && !columns.isEmpty()
                    && columns.get(0).strip().replace("\uFEFF", "").equalsIgnoreCase("Дата")) {
                continue;
            }
            if (columns.size() != 3) {
                throw new IllegalArgumentException("Рядок CSV " + lineNumber + " має містити рівно 3 стовпці.");
            }
            try {
                String date = columns.get(0).strip();
                double amount = Double.parseDouble(columns.get(1).strip());
                String description = columns.get(2).strip();
                transactions.add(new Transaction(date, amount, description));
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Некоректна сума в рядку CSV " + lineNumber + ".", exception);
            }
        }
        return transactions;
    }

    private static List<String> parseCsvLine(String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (current == ',' && !quoted) {
                columns.add(value.toString());
                value.setLength(0);
            } else {
                value.append(current);
            }
        }
        if (quoted) {
            throw new IllegalArgumentException("CSV рядок містить незакриті лапки.");
        }
        columns.add(value.toString());
        return columns;
    }
}
