package DirectoryAnalyzer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileDependencyResolver {
    private final HashMap<String, List<String>> adjacencyMap;

    public FileDependencyResolver() {
        adjacencyMap = new HashMap<>();
    }

    public void addFile(Path file, List<Path> dependencies) {
        adjacencyMap.put(file.normalize().toAbsolutePath().toString(),
                dependencies.stream().map(path -> path.normalize().toAbsolutePath().toString()).toList());
    }

    public List<String> getCyclicDependencyLog() {
        var visited = new HashMap<String, VisitState>();

        var files = adjacencyMap.keySet();
        for (var file : files) {
            visited.put(file, VisitState.Unvisited);
        }
        for (var file : adjacencyMap.keySet()) {
            List<String> dependencyLog = new ArrayList<>();
            if (visited.get(file) == VisitState.Unvisited && hasCyclicDependency(file, visited, dependencyLog)) {
                return dependencyLog;
            }
        }
        return null;
    }

    private boolean hasCyclicDependency(String file, HashMap<String, VisitState> visited, List<String> dependencyLog) {
        visited.put(file, VisitState.Visiting);
        dependencyLog.add("Посещаем файл " + file + ".");

        for (var child : adjacencyMap.get(file)) {
            if (visited.get(child) == VisitState.Visiting) {
                dependencyLog.add("Посещаемый файл имеет зависимость " + child + ", посещение которого еще не завершено" +
                        " -> циклическая зависимость.");
                return true;
            } else if (visited.get(child) != VisitState.Visited && hasCyclicDependency(child, visited, dependencyLog)) {
                return true;
            }
        }

        visited.put(file, VisitState.Visited);
        dependencyLog.add("Посещение завершено.");
        return false;
    }
}

