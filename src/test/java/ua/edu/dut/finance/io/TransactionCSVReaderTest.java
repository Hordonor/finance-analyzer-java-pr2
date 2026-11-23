package ua.edu.dut.finance.io;

import org.junit.jupiter.api.Test;
import ua.edu.dut.finance.model.Transaction;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionCSVReaderTest {
    @Test
    void readsBundledCsvFile() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/transactions.csv")) {
            List<Transaction> transactions = TransactionCSVReader.parseTransactions(
                    TransactionCSVReader.readLines(input));

            assertEquals(26, transactions.size());
            assertEquals("Сільпо", transactions.get(0).getDescription());
            assertEquals(-7_850.0, transactions.get(0).getAmount());
            assertEquals("Інші витрати", transactions.get(transactions.size() - 1).getDescription());
        }
    }

    @Test
    void parsesQuotedCommasAndEscapedQuotes() {
        List<Transaction> transactions = TransactionCSVReader.parseTransactions(List.of(
                "Дата,Сума,Категорія", "05-12-2023,-25,\"Кав'ярня, \"\"Центр\"\"\""));

        assertEquals("Кав'ярня, \"Центр\"", transactions.get(0).getDescription());
        assertEquals(-25.0, transactions.get(0).getAmount());
    }
}
