import java.util.Scanner;
import java.io.*;
public class TicTacToe {
    static int size = 3;
    static char[][] board;
    static String player1 = "Player 1";
    static String player2 = "Player 2";
    static final String SETTINGS_FILE = "settings.txt";
    static final String STATS_FILE = "statistics.txt";
    public static void main(String[] args) {
        loadSettings();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("=== Меню ===");
            System.out.println("1. Грати");
            System.out.println("2. Налаштування");
            System.out.println("3. Переглянути статистику");
            System.out.println("4. Вийти");
            System.out.print("Оберіть пункт: ");
            String choice = sc.nextLine();
            if (choice.equals("1")) {
                playGame(sc);
            } else if (choice.equals("2")) {
                changeSettings(sc);
            } else if (choice.equals("3")) {
                showStatistics();
            } else if (choice.equals("4")) {
                System.out.println("До побачення!");
                break;
            }
        }
        sc.close();
    }

    static void playGame(Scanner sc) {
        board = new char[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                board[i][j] = ' ';
        char current = 'X';
        String winner = null;
        int moves = 0;
        while (true) {
            printBoard();
            String currentName = current == 'X' ? player1 : player2;
            System.out.println(currentName + " (" + current + ") ваш хід.");
            int row, col;
            while (true) {
                System.out.print("Введіть рядок (1-" + size + "): ");
                row = getInt(sc) - 1;
                System.out.print("Введіть стовпець (1-" + size + "): ");
                col = getInt(sc) - 1;
                if (row >= 0 && row < size && col >= 0 && col < size && board[row][col] == ' ') {
                    board[row][col] = current;
                    break;
                } else {
                    System.out.println("Некоректний хід. Спробуйте ще раз.");
                }
            }
            moves++;
            if (checkWin(current)) {
                printBoard();
                winner = currentName;
                System.out.println("Вітаємо, переміг " + winner + "!");
                break;
            }
            if (moves == size * size) {
                printBoard();
                System.out.println("Нічия!");
                break;
            }
            current = (current == 'X') ? 'O' : 'X';
        }
        saveStatistics(winner, moves == size * size ? "Draw" : (current == 'X' ? "X" : "O"));
    }

    static void printBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(" " + board[i][j]);
                if (j < size - 1) System.out.print(" |");
            }
            System.out.println();
            if (i < size - 1) {
                for (int j = 0; j < size; j++) {
                    System.out.print("---");
                    if (j < size - 1) System.out.print("+");
                }
                System.out.println();
            }
        }
    }

    static boolean checkWin(char symbol) {
        // рядки і стовпці
        for (int i = 0; i < size; i++) {
            boolean row = true, col = true;
            for (int j = 0; j < size; j++) {
                if (board[i][j] != symbol) row = false;
                if (board[j][i] != symbol) col = false;
            }
            if (row || col) return true;
        }
        // діагоналі
        boolean diag1 = true, diag2 = true;
        for (int i = 0; i < size; i++) {
            if (board[i][i] != symbol) diag1 = false;
            if (board[i][size - 1 - i] != symbol) diag2 = false;
        }
        return diag1 || diag2;
    }

    static void changeSettings(Scanner sc) {
        System.out.print("Введіть ім'я першого гравця (X): ");
        player1 = sc.nextLine();
        System.out.print("Введіть ім'я другого гравця (O): ");
        player2 = sc.nextLine();
        System.out.print("Введіть розмір поля (3-9): ");
        int sz = getInt(sc);
        if (sz < 3) sz = 3;
        if (sz > 9) sz = 9;
        size = sz;
        saveSettings();
        System.out.println("Налаштування збережено.");
    }

    static void loadSettings() {
        File f = new File(SETTINGS_FILE);
        if (!f.exists()) return;
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                int eq = line.indexOf('=');
                if (eq == -1) continue;
                String key = line.substring(0, eq).trim();
                String val = line.substring(eq + 1).trim();
                if (key.equals("size")) size = Integer.parseInt(val);
                else if (key.equals("player1")) player1 = val;
                else if (key.equals("player2")) player2 = val;
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Помилка читання налаштувань.");
        }
    }

    static void saveSettings() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(SETTINGS_FILE));
            pw.println("size=" + size);
            pw.println("player1=" + player1);
            pw.println("player2=" + player2);
            pw.close();
        } catch (Exception e) {
            System.out.println("Помилка збереження налаштувань.");
        }
    }

    static void saveStatistics(String winner, String symbol) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(STATS_FILE, true));
            String date = java.time.LocalDateTime.now().toString();
            pw.println(date + " | " + "Розмір: " + size + " | " +
                    "Гравець1: " + player1 + " | Гравець2: " + player2 + " | " +
                    "Переможець: " + (winner != null ? winner : "Нічия") +
                    " | Знак: " + symbol);
            pw.close();
        } catch (Exception e) {
            System.out.println("Помилка збереження статистики.");
        }
    }

    static void showStatistics() {
        File f = new File(STATS_FILE);
        if (!f.exists()) {
            System.out.println("Статистика відсутня.");
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            System.out.println("=== Статистика ігор ===");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Помилка читання статистики.");
        }
    }

    static int getInt(Scanner sc) {
        while (true) {
            String s = sc.nextLine();
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                System.out.print("Введіть число: ");
            }
        }
    }
}