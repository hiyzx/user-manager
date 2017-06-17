package com.zero.base;

import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/6/17
 */
public class ThreadTest {

    // 死锁
    private static ReentrantLock LOCK1 = new ReentrantLock();
    private static ReentrantLock LOCK2 = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("t1111");
                LOCK1.lock();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t1112");
                LOCK2.lock();
                System.out.println("t1113");
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("t2111");
                LOCK2.lock();
                System.out.println("t2112");
                LOCK1.lock();
                System.out.println("t2113");
            }
        });
        t1.start();
        t2.start();
    }

    // 生产者和消费者
    @Test
    public void testProAndCum() {
        Basket basket = new Basket();
        Produce produce = new Produce(basket);
        Consumer consumer = new Consumer(basket);
        Thread produceThread = new Thread(produce);
        Thread consumerThread = new Thread(consumer);
        produceThread.start();
        consumerThread.start();
    }

    private class Apple {
        private int id;

	    Apple(Integer id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Apple{" + "id=" + id + '}';
        }
    }

    private class Basket {
        private int index = 0;
        private Apple[] apples = new Apple[5];

        synchronized void push(Apple apple) {
            while (index == apples.length) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.notify();
            apples[index] = apple;
            this.index++;
        }

        synchronized Apple pop() {
            while (index == 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.notify();
            index--;
            return apples[index];
        }
    }

    private class Produce implements Runnable {

        private Basket basket = null;

        Produce(Basket basket) {
            this.basket = basket;
        }

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                Apple apple = new Apple(i);
                basket.push(apple);
                System.out.println("生产了" + apple.toString());
                /*try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }

    private class Consumer implements Runnable {
        private Basket basket = null;

        Consumer(Basket basket) {
            this.basket = basket;
        }

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                Apple pop = basket.pop();
                System.out.println("消费了" + pop.toString());
              /*  try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }
}
