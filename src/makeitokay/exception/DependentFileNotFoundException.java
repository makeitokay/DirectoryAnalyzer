package makeitokay.exception;

import java.io.FileNotFoundException;
import java.nio.file.Path;

public class DependentFileNotFoundException extends FileNotFoundException {
    private final Path parentFile;
    private final Path dependentFile;

    public DependentFileNotFoundException(Path parentFile, Path dependentFile) {
        this.parentFile = parentFile;
        this.dependentFile = dependentFile;
    }

    public Path getParentFile() {
        return parentFile;
    }

    public Path getDependentFile() {
        return dependentFile;
    }
}
