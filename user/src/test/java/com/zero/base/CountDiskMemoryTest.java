package com.zero.base;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: 四个线程获取盘符大小,一个线程统计
 * @author: yezhaoxing
 * @date: 2017/6/20
 */
public class CountDiskMemoryTest {

    @Test
    public void testCount() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(4);
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        int totalMemorySize = 0;
        for (int i = 0; i < 4; i++) {
            int timer = new Random().nextInt(5);
            Thread.sleep(timer);
            int diskSize = new DiskMemory().getMemory();
            System.out.printf("完成统计,耗时%s,大小%s", timer, diskSize);
            totalMemorySize += diskSize;
            countDownLatch.countDown();
            System.out.println("countDown=" + countDownLatch.getCount());
        }
        countDownLatch.await();
        System.out.println("总计:" + totalMemorySize);
        executorService.shutdown();
    }

    private class DiskMemory {

        private int getMemory() {
            return new Random().nextInt(5) * 100;
        }
    }
}
