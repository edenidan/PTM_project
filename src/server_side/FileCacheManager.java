package server_side;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Optional;

public class FileCacheManager<T extends Serializable, U extends Serializable> implements CacheManager<T, U> {
    private static final String cacheDirectoryName = "caches";

    private File cacheFile;

    public FileCacheManager(String cacheFileName) {
        cacheFile = Paths.get(cacheDirectoryName, cacheFileName).toFile();
    }

    @Override
    public boolean existsSolution(T problem) {
        if (!cacheFile.exists())
            return false;

        try {
            return Files.lines(cacheFile.toPath())
                    .map(s -> s.split(","))
                    .anyMatch(strings -> strings[0].equals(problem));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public U getSolution(T problem) {
        if (!cacheFile.exists())
            return null;

        try {
            Optional<String[]> problemAndSolution = Files.lines(cacheFile.toPath())
                    .map(s -> s.split(","))
                    .filter(strings -> strings[0].equals(problem))
                    .findFirst();

            if (problemAndSolution.isPresent())
                return (U) deserializeFromBase64String(problemAndSolution.get()[1]);
            else
                return null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveSolution(T problem, U solution) {
        if (existsSolution(problem)) return;

        try {
            if (!cacheFile.exists())
                resetCache();

            String toWrite = serializeToBase64String(problem) + "," + serializeToBase64String(solution) + "\r\n";
            Files.write(cacheFile.toPath(), toWrite.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetCache() throws IOException {
        cacheFile.getParentFile().mkdirs();
        Files.write(cacheFile.toPath(), new byte[0]);
    }

    private static Object deserializeFromBase64String(String s) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(s)))) {
            return input.readObject();
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
