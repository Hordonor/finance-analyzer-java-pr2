package ua.edu.dut.finance.analysis;

import org.junit.jupiter.api.Test;
import ua.edu.dut.finance.model.Transaction;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionAnalyzerTest {
    @Test
    void calculatesTotalBalance() {
        assertEquals(200.0, TransactionAnalyzer.calculateTotalBalance(List.of(
                tx("01-01-2023", 100, "Дохід"), tx("02-01-2023", -50, "Витрата"),
                tx("03-01-2023", 150, "Дохід"))));
    }

    @Test
    void countsTransactionsByMonth() {
        List<Transaction> transactions = List.of(tx("01-02-2023", 50, "Дохід"),
                tx("15-02-2023", -20, "Витрата"), tx("05-03-2023", 100, "Дохід"));
        assertEquals(2, TransactionAnalyzer.countTransactionsByMonth(transactions, "02-2023"));
        assertEquals(1, TransactionAnalyzer.countTransactionsByMonth(transactions, "03-2023"));
    }

    @Test
    void returnsTenLargestExpensesSortedByAmount() {
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            transactions.add(tx("01-01-2024", -i * 100.0, "Витрата " + i));
        }
        transactions.add(tx("02-01-2024", 50_000, "Дохід"));

        List<Transaction> result = TransactionAnalyzer.findTopExpenses(transactions);
        assertEquals(10, result.size());
        assertEquals(-1_200.0, result.get(0).getAmount());
        assertEquals(-300.0, result.get(result.size() - 1).getAmount());
    }

    @Test
    void findsLargestAndSmallestExpenseInSelectedMonth() {
        List<Transaction> transactions = List.of(tx("02-01-2024", -900, "Оренда"),
                tx("03-01-2024", -50, "Кава"), tx("04-02-2024", -700, "Інший місяць"));

        assertEquals("Оренда", TransactionAnalyzer.findLargestExpenseByMonth(transactions, "01-2024")
                .orElseThrow().getDescription());
        assertEquals("Кава", TransactionAnalyzer.findSmallestExpenseByMonth(transactions, "01-2024")
                .orElseThrow().getDescription());
        assertFalse(TransactionAnalyzer.findLargestExpenseByMonth(transactions, "03-2024").isPresent());
    }

    @Test
    void aggregatesExpensesByCategoryAndMonth() {
        List<Transaction> transactions = List.of(tx("02-01-2024", -900, "Оренда"),
                tx("03-01-2024", -50, "Кава"), tx("04-02-2024", -700, "Оренда"),
                tx("05-02-2024", 1_000, "Зарплата"));

        assertEquals(1_600.0, TransactionAnalyzer.totalExpensesByCategory(transactions).get("Оренда"));
        assertEquals(950.0, TransactionAnalyzer.totalExpensesByMonth(transactions).get("2024-01"));
        assertEquals(700.0, TransactionAnalyzer.totalExpensesByMonth(transactions).get("2024-02"));
    }

    private static Transaction tx(String date, double amount, String description) {
        return new Transaction(date, amount, description);
    }
}
