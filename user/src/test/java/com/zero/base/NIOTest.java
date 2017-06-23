package com.zero.base;

import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/6/20
 */
public class NIOTest {

    @Test
    public void testNIO() throws IOException {
        RandomAccessFile file = new RandomAccessFile("E:\\test.txt", "rw");
        FileChannel channel = file.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(48);
        int byteRead = channel.read(buffer);
        while (byteRead != -1) {
            System.out.println("read" + byteRead);
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.println((char) buffer.get());
            }
            buffer.clear();
            byteRead = channel.read(buffer);
        }
        file.close();
    }
}
