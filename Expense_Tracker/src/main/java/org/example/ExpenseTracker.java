package org.example;

import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class ExpenseTracker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Expense> expenses = ExpenseStorage.loadExpenses();

        while (true) {
            System.out.println("\nExpense Tracker Menu:");
            System.out.println("1. Add Expense");
            System.out.println("2. Update Expense");
            System.out.println("3. Delete Expense");
            System.out.println("4. View All Expense");
            System.out.println("5. View Summary of All Expenses");
            System.out.println("6. View Summary of Expenses for a specific month");
            System.out.println("7. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addExpense(scanner, expenses);
                    break;
                case 2:
                    updateExpense(scanner, expenses);
                    break;
                case 3:
                    deleteExpense(scanner, expenses);
                    break;
                case 4:
                    viewAllExpenses(expenses);
                    break;
                case 5:
                    viewSummary(expenses);
                    break;
                case 6:
                    viewMonthlySummary(scanner, expenses);
                    break;
                case 7:
                    ExpenseStorage.saveExpenses(expenses);
                    System.out.println("Expenses saved. Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }


    private static void addExpense(Scanner scanner, ArrayList<Expense> expenses) {
        System.out.println("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.println("Enter description: ");
        String description = scanner.nextLine();
        System.out.println("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter category: ");
        String category = scanner.nextLine();
        expenses.add(new Expense(date, description, amount, category));
        System.out.println("Expense added.");
    }

    private static void updateExpense(Scanner scanner, ArrayList<Expense> expenses) {
        System.out.println("Enter the index of the expense to update: ");
        int index = scanner.nextInt();
        scanner.nextLine();
        if (index >= 0 && index < expenses.size()) {
            System.out.println("Enter new date (YYYY-MM-DD): ");
            String date = scanner.nextLine();
            System.out.println("Enter new description: ");
            String description = scanner.nextLine();
            System.out.println("Enter new amount: ");
            double amount = scanner.nextDouble();
            System.out.println("Enter new category: ");
            String category = scanner.nextLine();
            expenses.set(index, new Expense(date, description, amount, category));
            System.out.println("Expense updated.");
        } else {
            System.out.println("Invalid index.");
        }
    }

    private static void deleteExpense(Scanner scanner, ArrayList<Expense> expenses) {
        System.out.println("Enter the index of the expense to delete: ");
        int index = scanner.nextInt();
        scanner.nextLine();
        if (index >= 0 && index < expenses.size()) {
            expenses.remove(index);
            System.out.println("Expense deleted.");
        } else {
            System.out.println("Invalid index.");
        }
    }

    private static void viewAllExpenses(ArrayList<Expense> expenses) {
        for (int i = 0; i < expenses.size(); i++) {
            System.out.println(i + ": " + expenses.get(i));
        }
    }

    private static void viewSummary(ArrayList<Expense> expenses) {
        double total = 0;
        for (Expense e : expenses) {
            total += e.getAmount();
        }
        System.out.println("Total expenses: " + total);
    }

    private static void viewMonthlySummary(Scanner scanner, ArrayList<Expense> expenses) {
        System.out.println("Enter month (MM): ");
        String month = scanner.nextLine();
        double total = 0;
        for (Expense e : expenses) {
            if (e.getDate().substring(5, 7).equals(month)) {
                total += e.getAmount();
            }
        }
        System.out.println("Total expenses for month " + month + ": " + total);
    }
}