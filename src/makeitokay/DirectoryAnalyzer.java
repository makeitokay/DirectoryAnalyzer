package makeitokay;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Анализатор заданной директории на наличие циклических зависимостей.
 */
public class DirectoryAnalyzer {
    private final Path directory;
    private final FileParser fileParser;

    private final FileDependencyResolver dependencyResolver;

    /**
     * Конструирует анализатор директории.
     * @param directoryPath путь (абсолютный или относительный) директории для анализа.
     * @throws FileNotFoundException если переданная директория не найдена.
     */
    public DirectoryAnalyzer(String directoryPath) throws FileNotFoundException {
        this.directory = Path.of(directoryPath);
        if (!Files.isDirectory(this.directory, LinkOption.NOFOLLOW_LINKS)) {
            throw new FileNotFoundException("Directory not found");
        }
        this.fileParser = new FileParser();
        this.dependencyResolver = new FileDependencyResolver();
    }

    /**
     * Анализирует директорию. Выводит в консоль сообщение о циклической зависимости, если она была найдена.
     * Иначе выводит содержимое анализируемых файлов, топологически отсортированных в соответствии с зависимостями.
     * @throws IOException если невозможно корректно прочитать файлы или зависимости.
     */
    public void analyze() throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            for (var file: stream.filter(Files::isRegularFile).toList()) {
                dependencyResolver.addFile(file, fileParser.getFileDependencies(file));
            }
        }

        var cyclicDependencyLog = dependencyResolver.getCyclicDependencyLog();
        if (cyclicDependencyLog != null) {
            cyclicDependencyLog.forEach(System.out::println);
            return;
        }
        for (var file : dependencyResolver.topologicalSort()) {
            fileParser.printFile(file);
        }
    }
}
