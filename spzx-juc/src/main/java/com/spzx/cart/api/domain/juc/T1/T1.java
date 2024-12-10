package com.spzx.cart.api.domain.juc.T1;

public class T1 {
    public static void main(String[] args) {
        girl1 girl1 = new girl1();
        gril2 gril2 = new gril2();
        new Thread(()->{
            synchronized (girl1){
                System.out.println(Thread.currentThread().getName()+"我的女朋友gril1");
                synchronized (gril2){
                    System.out.println(Thread.currentThread().getName()+"但是我又爱上了gril2");
                }
            }

        },"神父").start();
       new Thread(()->{
           synchronized (gril2){
               System.out.println(Thread.currentThread().getName()+"我的女朋友gril2");
               synchronized (girl1){
                   System.out.println(Thread.currentThread().getName()+"但是我又爱上了gril1");
               }
       }
       },"圣母").start();


    }

    }

