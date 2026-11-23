package ua.edu.dut.finance.report;

import ua.edu.dut.finance.analysis.TransactionAnalyzer;
import ua.edu.dut.finance.model.Transaction;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Stateless text report formatting; static methods avoid report-generator instances. */
public abstract class TransactionReportGenerator {
    public static final double UAH_PER_STAR = 1_000.0;

    private TransactionReportGenerator() {
    }

    public static void printBalanceReport(double totalBalance) {
        System.out.printf(Locale.ROOT, "Загальний баланс: %.2f грн%n", totalBalance);
    }

    public static void printTransactionsCountByMonth(String monthYear, int count) {
        System.out.printf(Locale.ROOT, "Кількість транзакцій за %s: %d%n", monthYear, count);
    }

    public static void printTopExpensesReport(List<Transaction> topExpenses) {
        System.out.println("10 найбільших витрат:");
        if (topExpenses.isEmpty()) {
            System.out.println("Витрат не знайдено.");
            return;
        }
        topExpenses.forEach(expense -> System.out.printf(Locale.ROOT, "%s: %.2f грн%n",
                expense.getDescription(), expense.getAmount()));
    }

    public static void printExpensesByPeriodReport(String monthYear, List<Transaction> transactions) {
        System.out.println("Витрати за " + monthYear + ":");
        printExtreme("Найбільша витрата", TransactionAnalyzer.findLargestExpenseByMonth(transactions, monthYear));
        printExtreme("Найменша витрата", TransactionAnalyzer.findSmallestExpenseByMonth(transactions, monthYear));
    }

    public static String generateTextExpenseReport(List<Transaction> transactions) {
        StringBuilder report = new StringBuilder("Витрати за категоріями (1 * = 1000 грн):\n");
        appendBars(report, TransactionAnalyzer.totalExpensesByCategory(transactions));
        report.append("\nВитрати за місяцями (1 * = 1000 грн):\n");
        appendBars(report, TransactionAnalyzer.totalExpensesByMonth(transactions));
        return report.toString();
    }

    public static void printTextExpenseReport(List<Transaction> transactions) {
        System.out.print(generateTextExpenseReport(transactions));
    }

    private static void appendBars(StringBuilder report, Map<String, Double> totals) {
        if (totals.isEmpty()) {
            report.append("Витрат не знайдено.\n");
            return;
        }
        totals.forEach((label, total) -> report.append(String.format(Locale.ROOT, "%s: %.2f грн %s%n",
                label, total, "*".repeat((int) (total / UAH_PER_STAR))
        )));
    }

    private static void printExtreme(String label, Optional<Transaction> expense) {
        expense.ifPresentOrElse(transaction -> System.out.printf(Locale.ROOT, "%s: %s — %.2f грн%n",
                        label, transaction.getDescription(), transaction.getAmount()),
                () -> System.out.println(label + ": витрат не знайдено."));
    }
}
