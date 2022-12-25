package makeitokay;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

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
        for (var file : files) {
            List<String> dependencyLog = new ArrayList<>();
            if (visited.get(file) == VisitState.Unvisited && hasCyclicDependency(file, visited, dependencyLog)) {
                return dependencyLog;
            }
        }
        return null;
    }

    public List<Path> topologicalSort() {
        var result = new Stack<String>();
        var visited = new HashMap<String, VisitState>();
        var files = adjacencyMap.keySet();
        for (var file : files) {
            visited.put(file, VisitState.Unvisited);
        }

        for (var file : files) {
            if (visited.get(file) == VisitState.Unvisited) {
                topologicalSort(file, visited, result);
            }
        }
        return result.stream().map(Path::of).toList();
    }

    private void topologicalSort(String file, HashMap<String, VisitState> visited, Stack<String> result) {
        visited.put(file, VisitState.Visited);

        for (var child : adjacencyMap.get(file)) {
            if (visited.get(child) == VisitState.Unvisited) {
                topologicalSort(child, visited, result);
            }
        }
        result.push(file);
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

