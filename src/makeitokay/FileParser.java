package makeitokay;

import makeitokay.exception.DependentFileNotFoundException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Класс-утилита для чтения файлов.
 */
public class FileParser {
    private static final Pattern dependencyRequirePattern = Pattern.compile("^require ['\"<`‘](?<dependencyPath>.*)['\">`’]");

    /**
     * Ищет зависимости файла.
     * @param file файл, в котором необходимо осуществить поиск.
     * @return список зависимостей файла.
     * @throws IOException если невозможно корректно прочитать файл или зависимости не существуют.
     */
    public List<Path> getFileDependencies(Path file) throws IOException {
        try (var reader = new BufferedReader(new FileReader(file.toFile()))) {
            var dependencies = new ArrayList<Path>();
            String line;
            while ((line = reader.readLine()) != null) {
                var matcher = dependencyRequirePattern.matcher(line);
                if (!matcher.matches()) {
                    continue;
                }
                var dependencyPath = matcher.group("dependencyPath");
                if (dependencyPath.isEmpty()) {
                    continue;
                }
                var dependency = file.getParent().resolve(dependencyPath);
                if (!Files.exists(dependency)) {
                    throw new DependentFileNotFoundException(file, dependency);
                }
                dependencies.add(dependency);
            }
            return dependencies;
        }
    }

    /**
     * Печатает содержимое файла в консоль.
     * @param file файл, содержимое которого необходимо вывести.
     * @throws IOException если невозможно прочитать файл.
     */
    public void printFile(Path file) throws IOException {
        try (var reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
