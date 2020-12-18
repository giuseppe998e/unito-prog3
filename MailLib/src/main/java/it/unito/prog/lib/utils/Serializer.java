package it.unito.prog.lib.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

/**
 * MIT License
 * <p>
 * Copyright (c) 2020 Giuseppe Eletto
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p>
 * ------------------------------------------------------------------------------
 * <p>
 * This utility class allows you to read and write a serializable class using the Java.NIO library.
 * In particular using "FileChannel" and "FileLock" (Exclusive for writing, Shared for reading).
 * <p>
 * Based on StackOverflow answer : https://stackoverflow.com/a/22931238/8277574
 */
public final class Serializer {
    public static boolean writeToFile(File file, Serializable serializable) {
        return writeToFile(file.toPath(), serializable);
    }

    public static boolean writeToFile(Path path, Serializable serializable) {
        try (FileChannel writeChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             FileLock ignored = writeChannel.lock(0L, Long.MAX_VALUE, false)) {

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                    objectOutputStream.writeObject(serializable);
                    objectOutputStream.flush();
                }

                byte[] objectBytes = byteArrayOutputStream.toByteArray();
                ByteBuffer buffer = ByteBuffer.wrap(objectBytes);
                writeChannel.write(buffer);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static <T extends Serializable> T readFromFile(File file) {
        return readFromFile(file.toPath());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T readFromFile(Path path) {
        Object objectClass = null;
        try (FileChannel readChannel = FileChannel.open(path, StandardOpenOption.READ);
             FileLock ignored = readChannel.lock(0L, Long.MAX_VALUE, true)) {

            byte[] bytes = new byte[5120]; // chunk of 5KB
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                int bytesRead;
                ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
                while ((bytesRead = readChannel.read(buffer)) != -1) {
                    buffer.flip();
                    buffer.get(bytes, 0, bytesRead);
                    buffer.clear();
                    byteArrayOutputStream.write(bytes, 0, bytesRead);
                }

                byte[] objectBytes = byteArrayOutputStream.toByteArray();
                try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objectBytes);
                     ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                    objectClass = objectInputStream.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return (T) objectClass;
    }

    public static String writeToB64(Serializable serializable) {
        String base64Class = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            objectOutputStream.writeObject(serializable);
            base64Class = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64Class;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T readFromB64(String base64) {
        Object objectClass = null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            try (ObjectInputStream objStreamIn = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                objectClass = objStreamIn.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (T) objectClass;
    }
}
