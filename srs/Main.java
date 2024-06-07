import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Перечисление для представления арифметических операций
enum Operation {
    ADDITION('+'), SUBTRACTION('-'), MULTIPLICATION('*'), DIVISION('/');

    private final char symbol;

    Operation(char symbol) {
        this.symbol = symbol;
    }

    // Метод для получения операции по символу
    public static Operation fromChar(char symbol) {
        for (Operation op : values()) {
            if (op.symbol == symbol) {
                return op;
            }
        }
        throw new IllegalArgumentException("Invalid operation symbol: " + symbol);
    }
}

// Класс для работы с римскими числами
class RomanNumeral {
    // Карта для преобразования римских чисел в арабские
    private static final Map<Character, Integer> romanToInt = new HashMap<>();
    // Карта для преобразования арабских чисел в римские
    private static final Map<Integer, String> intToRoman = new HashMap<>();

    static {
        // Инициализация карты для преобразования римских чисел в арабские
        romanToInt.put('I', 1);
        romanToInt.put('V', 5);
        romanToInt.put('X', 10);
        romanToInt.put('L', 50);
        romanToInt.put('C', 100);
        romanToInt.put('D', 500);
        romanToInt.put('M', 1000);

        // Инициализация карты для преобразования арабских чисел в римские
        intToRoman.put(1, "I");
        intToRoman.put(4, "IV");
        intToRoman.put(5, "V");
        intToRoman.put(9, "IX");
        intToRoman.put(10, "X");
        intToRoman.put(40, "XL");
        intToRoman.put(50, "L");
        intToRoman.put(90, "XC");
        intToRoman.put(100, "C");
        intToRoman.put(400, "CD");
        intToRoman.put(500, "D");
        intToRoman.put(900, "CM");
        intToRoman.put(1000, "M");
    }

    // Метод для преобразования римского числа в арабское
    public static int toInt(String roman) {
        int result = 0;
        for (int i = 0; i < roman.length(); i++) {
            // Если текущий символ больше предыдущего, вычитаем предыдущий и добавляем разницу
            if (i > 0 && romanToInt.get(roman.charAt(i)) > romanToInt.get(roman.charAt(i - 1))) {
                result += romanToInt.get(roman.charAt(i)) - 2 * romanToInt.get(roman.charAt(i - 1));
            } else {
                // Иначе просто добавляем текущее значение
                result += romanToInt.get(roman.charAt(i));
            }
        }
        return result;
    }

    // Метод для преобразования арабского числа в римское
    public static String toRoman(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Roman numeral must be greater than 0");
        }

        StringBuilder sb = new StringBuilder();
        // Преобразуем ключи карты в массив и сортируем их по убыванию
        int[] keys = intToRoman.keySet().stream().sorted((a, b) -> b - a).mapToInt(Integer::intValue).toArray();

        // Постепенно уменьшаем число, добавляя соответствующие римские символы
        for (int key : keys) {
            while (number >= key) {
                sb.append(intToRoman.get(key));
                number -= key;
            }
        }
        return sb.toString();
    }

    // Метод для проверки правильности римского числа
    public static boolean isValidRoman(String s) {
        return s.matches("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$");
    }
}

// Главный класс
class Main {
    // Метод для вычисления арифметического выражения
    public static String calc(String input) {
        input = input.trim();
        char[] chars = input.toCharArray();
        int length = chars.length;

        int opIndex = -1;
        // Находим индекс оператора в строке
        for (int i = 0; i < length; i++) {
            if (chars[i] == '+' || chars[i] == '-' || chars[i] == '*' || chars[i] == '/') {
                opIndex = i;
                break;
            }
        }

        if (opIndex == -1) {
            throw new IllegalArgumentException("Invalid input");
        }

        // Разделяем строку на левую и правую части относительно оператора
        String left = input.substring(0, opIndex).trim();
        String right = input.substring(opIndex + 1).trim();
        char operation = chars[opIndex];

        // Проверяем, римские или арабские числа введены
        boolean isRoman = isRoman(left) && isRoman(right);
        boolean isArabic = isArabic(left) && isArabic(right);

        if (!(isRoman || isArabic)) {
            throw new IllegalArgumentException("Mixed number formats or invalid numbers");
        }

        int num1, num2;

        // Преобразуем введенные числа в арабские и проверяем диапазон
        if (isRoman) {
            if (!RomanNumeral.isValidRoman(left) || !RomanNumeral.isValidRoman(right)) {
                throw new IllegalArgumentException("Invalid Roman numeral");
            }
            num1 = RomanNumeral.toInt(left);
            num2 = RomanNumeral.toInt(right);
            if (num1 < 1 || num1 > 10 || num2 < 1 || num2 > 10) {
                throw new IllegalArgumentException("Roman numerals must be between I and X inclusive");
            }
        } else {
            num1 = Integer.parseInt(left);
            num2 = Integer.parseInt(right);
            if (num1 < 1 || num1 > 10 || num2 < 1 || num2 > 10) {
                throw new IllegalArgumentException("Numbers must be between 1 and 10 inclusive");
            }
        }

        int result;

        // Выполняем арифметическую операцию
        switch (Operation.fromChar(operation)) {
            case ADDITION -> result = num1 + num2;
            case SUBTRACTION -> result = num1 - num2;
            case MULTIPLICATION -> result = num1 * num2;
            case DIVISION -> result = num1 / num2; // num2 не может быть 0 из-за предыдущей проверки
            default -> throw new IllegalArgumentException("Unknown operation");
        }

        // Преобразуем результат в римские числа, если на входе были римские
        if (isRoman) {
            if (result < 1) {
                throw new IllegalArgumentException("Roman numerals cannot be less than I");
            }
            return RomanNumeral.toRoman(result);
        } else {
            return String.valueOf(result);
        }
    }

    // Проверяем, является ли строка римским числом
    private static boolean isRoman(String s) {
        return s.matches("[IVXLCDM]+");
    }

    // Проверяем, является ли строка арабским числом
    private static boolean isArabic(String s) {
        return s.matches("\\d+");
    }

    // Главный метод для запуска программы
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите выражение: ");
        String input = scanner.nextLine();

        try {
            String result = calc(input);
            System.out.println("Результат: " + result);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
