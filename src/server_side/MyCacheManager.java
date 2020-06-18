package server_side;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;

public class MyCacheManager<T extends Serializable, U extends Serializable> implements CacheManager<T, U> {
    private static final String cacheDirectoryName = "caches";

    private final File cacheFile;
    private final HashMap<T, U> memoryCache = new HashMap<>();

    public MyCacheManager(String cacheFileName) {
        cacheFile = Paths.get(cacheDirectoryName, cacheFileName).toFile();
    }

    @Override
    public boolean existsSolution(T problem) {
        return existsSolutionInMemory(problem) || existsSolutionInFile(problem);
    }

    @Override
    public U getSolution(T problem) {
        if (memoryCache.containsKey(problem))
            return memoryCache.get(problem);

        if (!cacheFile.exists())
            return null;

        try {
            String problemInBase64 = serializeToBase64String(problem);
            Optional<String[]> problemAndSolution = Files.lines(cacheFile.toPath())
                    .map(s -> s.split(","))
                    .filter(strings -> strings[0].equals(problemInBase64))
                    .findFirst();

            if (problemAndSolution.isPresent())
                return deserializeFromBase64String(problemAndSolution.get()[1]);
            else
                return null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveSolution(T problem, U solution) {
        if (existsSolutionInMemory(problem)) return;

        memoryCache.put(problem, solution);

        if (!existsSolutionInFile(problem)) {
            try {
                if (!cacheFile.exists())
                    resetCache();

                String toWrite = serializeToBase64String(problem) + "," + serializeToBase64String(solution) + "\r\n";
                Files.write(cacheFile.toPath(), toWrite.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean existsSolutionInMemory(T problem) {
        return memoryCache.containsKey(problem);
    }

    private boolean existsSolutionInFile(T problem) {
        if (!cacheFile.exists())
            return false;

        try {
            String problemInBase64 = serializeToBase64String(problem);
            return Files.lines(cacheFile.toPath())
                    .map(s -> s.split(","))
                    .anyMatch(strings -> strings[0].equals(problemInBase64));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void resetCache() throws IOException {
        cacheFile.getParentFile().mkdirs();
        cacheFile.createNewFile();
    }

    private static <R> R deserializeFromBase64String(String s) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(s)))) {
            //noinspection unchecked
            return (R) input.readObject();
        }
    }

    private static String serializeToBase64String(Serializable object) throws IOException {
        try (ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
             ObjectOutputStream objectOutput = new ObjectOutputStream(byteArrayOutput)) {
            objectOutput.writeObject(object);
            return Base64.getEncoder().encodeToString(byteArrayOutput.toByteArray());
        }
    }
}
