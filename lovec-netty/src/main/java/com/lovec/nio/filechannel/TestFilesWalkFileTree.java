package com.lovec.nio.filechannel;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

// 遍历目录文件
public class TestFilesWalkFileTree {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("C:\\Program Files\\Java\\jdk-11");
        fileCount(path, ".jar");
        //  each(path);

//         rm(Paths.get("C:\\Users\\mr\\Desktop\\可视化大屏a")); // rm -rf /data/a
    }

    // 删除多级目录
    private static void rm (Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir); // visitFile 删除完当下目录的文件，在删除当前目录
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    private static void fileCount(Path path, String fileType) throws IOException {
        AtomicInteger jarCount = new AtomicInteger();
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(fileType)) {
                    System.out.println(file);
                    jarCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("jar file count: " +  jarCount);
    }

    // 遍历目录下所有目录和文件
    private static void each(Path path) throws IOException {
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();

        // 匿名类访问外部局部变量 局部变量相当于 final 不能改变（++）
        Files.walkFileTree(path, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println(dir); // 打印找到的目录
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file); // 打印找到的文件
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("dir count: " + dirCount);
        System.out.println("file count: " +  fileCount);
    }
}
