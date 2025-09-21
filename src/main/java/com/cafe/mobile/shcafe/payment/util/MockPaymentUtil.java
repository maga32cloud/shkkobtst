package com.cafe.mobile.shcafe.payment.util;

import java.util.Random;

public class MockPaymentUtil {
    // mock 결제테스트
    public static String makePayment() throws Exception {
        Thread.sleep((long)(Math.random() * 1000));
        Random random = new Random();
        if (random.nextInt() % 10 == 1) {
            throw new Exception("Failed");
        }
        // 성공시 거래ID return
        return "Success";
    }
}
