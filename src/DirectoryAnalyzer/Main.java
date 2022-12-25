package DirectoryAnalyzer;

import DirectoryAnalyzer.exception.DependentFileNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        System.out.println("Чтобы выйти из программы, введите 0");
        while (true) {
            System.out.println("Введите путь до директории (отн. или абс.):");
            var directory = scanner.next();
            if (Objects.equals(directory, "0")) {
                return;
            }
            try {
                var analyzer = new DirectoryAnalyzer(directory);
                analyzer.analyze();
            }
            catch (DependentFileNotFoundException e) {
                System.out.println("Зависимость " + e.getDependentFile() + " для файла " + e.getParentFile() + " не найдена.");
            }
            catch (FileNotFoundException e) {
                System.out.println("Указанная директория не найдена");
            }
            catch (IOException | UncheckedIOException e) {
                System.out.println("Произошла ошибка во время чтения директории");
            }
        }
    }
}