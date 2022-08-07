package com.lovec.nio.filechannel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TestFiles {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("/root/data.txt");

        Files.exists(path);
        Files.createDirectories(path);

        Path path2 = Paths.get("/root/data-back.txt");
        Files.copy(path, path2);
        Files.copy(path, path2, StandardCopyOption.REPLACE_EXISTING);   // 替换现有的

        Files.move(path, path2, StandardCopyOption.ATOMIC_MOVE);        // 保证文件移动时原子性

        Files.delete(path2); // 可删文件，删除目录。目录中有文件不能删除
    }
}
