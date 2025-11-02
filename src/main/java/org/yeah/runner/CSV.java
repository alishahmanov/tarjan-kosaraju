package org.yeah.runner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class CSV {
    public static void writeHeader(Path file, String header) throws Exception {
        if (!Files.exists(file.getParent())) Files.createDirectories(file.getParent());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile(), false))) {
            bw.write(header);
            bw.newLine();
        }
    }
    public static void append(Path file, String row) throws Exception {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile(), true))) {
            bw.write(row);
            bw.newLine();
        }
    }
}
