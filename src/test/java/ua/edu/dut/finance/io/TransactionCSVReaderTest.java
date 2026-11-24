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
    void readsTheIntroductoryCsvExampleFromThePresentation() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/transactions-intro-example.csv")) {
            List<Transaction> transactions = TransactionCSVReader.parseTransactions(
                    TransactionCSVReader.readLines(input));

            assertEquals(14, transactions.size());
            assertEquals("Transaction{date='05-12-2023', amount=-450.0, description='Сільпо'}",
                    transactions.get(0).toString());
            assertEquals("Transaction{date='05-01-2024', amount=-500.0, description='Сільпо'}",
                    transactions.get(transactions.size() - 1).toString());
        }
    }

    @Test
    void parsesQuotedCommasAndEscapedQuotes() {
        List<Transaction> transactions = TransactionCSVReader.parseTransactions(List.of(
                "Дата,Сума,Категорія", "05-12-2023,-25,\"Кав'ярня, \"\"Центр\"\"\""));

        assertEquals("Кав'ярня, \"Центр\"", transactions.get(0).getDescription());
        assertEquals(-25.0, transactions.get(0).getAmount());
    }

    @Test
    void formatsTransactionLikeThePresentation() {
        Transaction transaction = new Transaction("05-12-2023", -450.0, "Сільпо");

        assertEquals("Transaction{date='05-12-2023', amount=-450.0, description='Сільпо'}",
                transaction.toString());
    }
}
