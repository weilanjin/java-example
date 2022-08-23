package com.lovec.network.nio.filechannel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFileWalk {
    public static void main(String[] args) throws IOException {
        String source = "C:\\Users\\mr\\Desktop\\可视化大屏";
        String target = "C:\\Users\\mr\\Desktop\\可视化大屏a";
        Files.walk(Paths.get(source)).forEach(path -> {
            String targetName = path.toString().replace(source, target);
            Path pathTarget = Paths.get(targetName);
            try {
                if (Files.isDirectory(path)) {
                    Files.createDirectory(pathTarget);
                }
                if (Files.isRegularFile(path)) {
                    Files.copy(path, pathTarget);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
