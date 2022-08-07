package com.lovec.nio.filechannel;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestPaths {
    public static void main(String[] args) {
        Paths.get("1.txt");                         // 相对路径
        Paths.get("d:\\1.txt");                     // 绝对路径， \\  转义'/'
        Paths.get("d:/1.txt");                      // 和上面一样
        Paths.get("d:/data", "projects");    // d:/data/projects

        /*
            path.normalize() d:/data/projects/a/../b -> /d:/data/projects/b

            d:
                |- data
                    |- projects
                        |- a
                        |- b

         */

        Path path = Paths.get("d:\\data\\projects\\a\\..\\b");
        Path normalize = path.normalize(); // d:/data/projects/b
    }
}
