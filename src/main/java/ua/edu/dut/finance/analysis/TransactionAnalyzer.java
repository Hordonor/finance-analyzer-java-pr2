package ua.edu.dut.finance.analysis;

import ua.edu.dut.finance.model.Transaction;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Stateless financial calculations; static methods avoid analyzer instances. */
public abstract class TransactionAnalyzer {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd-MM-uuuu", Locale.ROOT).withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter
            .ofPattern("MM-uuuu", Locale.ROOT).withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM", Locale.ROOT);

    private TransactionAnalyzer() {
    }

    public static double calculateTotalBalance(List<Transaction> transactions) {
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }

    public static int countTransactionsByMonth(List<Transaction> transactions, String monthYear) {
        YearMonth requested = parseMonth(monthYear);
        return (int) transactions.stream()
                .filter(transaction -> YearMonth.from(parseDate(transaction)).equals(requested))
                .count();
    }

    public static List<Transaction> findTopExpenses(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction.getAmount() < 0)
                .sorted(Comparator.comparingDouble(Transaction::getAmount))
                .limit(10)
                .toList();
    }

    /** Largest expense means the greatest amount spent (the most negative transaction). */
    public static Optional<Transaction> findLargestExpenseByMonth(List<Transaction> transactions, String monthYear) {
        return expensesInMonth(transactions, monthYear).min(Comparator.comparingDouble(Transaction::getAmount));
    }

    /** Smallest expense means the lowest amount spent (closest to zero). */
    public static Optional<Transaction> findSmallestExpenseByMonth(List<Transaction> transactions, String monthYear) {
        return expensesInMonth(transactions, monthYear).max(Comparator.comparingDouble(Transaction::getAmount));
    }

    public static Map<String, Double> totalExpensesByCategory(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction.getAmount() < 0)
                .collect(Collectors.groupingBy(Transaction::getDescription, TreeMap::new,
                        Collectors.summingDouble(transaction -> -transaction.getAmount())));
    }

    public static Map<String, Double> totalExpensesByMonth(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction.getAmount() < 0)
                .collect(Collectors.groupingBy(transaction -> YearMonth.from(parseDate(transaction))
                                .format(YEAR_MONTH_FORMAT), TreeMap::new,
                        Collectors.summingDouble(transaction -> -transaction.getAmount())));
    }

    private static java.util.stream.Stream<Transaction> expensesInMonth(List<Transaction> transactions, String monthYear) {
        YearMonth requested = parseMonth(monthYear);
        return transactions.stream().filter(transaction -> transaction.getAmount() < 0
                && YearMonth.from(parseDate(transaction)).equals(requested));
    }

    private static LocalDate parseDate(Transaction transaction) {
        return LocalDate.parse(transaction.getDate(), DATE_FORMAT);
    }

    private static YearMonth parseMonth(String monthYear) {
        return YearMonth.parse(monthYear, MONTH_FORMAT);
    }
}
