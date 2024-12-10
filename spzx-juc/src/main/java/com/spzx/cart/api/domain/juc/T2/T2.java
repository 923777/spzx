package com.spzx.cart.api.domain.juc.T2;

public class T2 {

    public static void main(String[] args) {    Tree tree = new Tree();
//        tree.pick();

//        for (int i = 1; i < 11; i++) {
            new Thread(() -> {
                 tree.pick();
            }, "号小朋友").start();
//
//        }
        tree.pick();
    }
//    @Test]]
//    public void Test() {
//        Tree tree = new Tree();
//tree.pick();
//    }
}
