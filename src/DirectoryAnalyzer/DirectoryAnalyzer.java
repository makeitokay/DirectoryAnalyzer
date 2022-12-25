package DirectoryAnalyzer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.stream.Stream;

public class DirectoryAnalyzer {
    private final Path directory;
    private final FileParser fileParser;

    private final FileDependencyResolver dependencyResolver;

    public DirectoryAnalyzer(String directoryPath) throws FileNotFoundException {
        this.directory = Path.of(directoryPath);
        if (!Files.isDirectory(this.directory, LinkOption.NOFOLLOW_LINKS)) {
            throw new FileNotFoundException("Directory not found");
        }
        this.fileParser = new FileParser(directory);
        this.dependencyResolver = new FileDependencyResolver();
    }

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
        dependencyResolver.topologicalSort().forEach(System.out::println);
    }
}
