package com.spzx.cart.api.domain.juc.T2;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Tree {
    private Integer apple = 9;
    private Boolean b = false;

    public void pick() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要摘的苹果数量:");
        Integer num = scanner.nextInt();
    //true 公平锁 false 非公平锁
        ReentrantLock lock = new ReentrantLock();
        b = lock.tryLock();
        try {

            if (b) {
                if (apple > 0) {

                    apple--;
                    System.out.println(Thread.currentThread().getName() + "摘苹果，还剩下：" + apple);
                    try {
                        TimeUnit.SECONDS.sleep(new Random().nextInt(3) + 1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println(Thread.currentThread().getName() + "没摘到苹果");
                }
            } else {
                System.out.println(Thread.currentThread().getName() + "不抢了");
            }

        } finally {
//            if (b) {
//                lock.unlock();
//            }
        }


    }

}
