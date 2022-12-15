package DirectoryAnalyzer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.stream.Stream;

public class DirectoryAnalyzer {
    private final Path path;

    public DirectoryAnalyzer(String directoryPath) throws FileNotFoundException {
        path = Path.of(directoryPath);
        if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            throw new FileNotFoundException("Directory not found");
        }
    }

    public void analyze() throws IOException {
        try (Stream<Path> stream = Files.walk(path)) {
            stream.filter(Files::isRegularFile)
                    .forEach(System.out::println);
        }
    }
}
