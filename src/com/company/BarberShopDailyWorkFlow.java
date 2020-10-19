package com.company;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

public class BarberShopDailyWorkFlow {
    volatile BarberShop barberShop;

    BarberShopDailyWorkFlow() {
        barberShop = new BarberShop(5);
    }

    class CustomerArrival extends TimerTask {
        Customer customer;

        CustomerArrival() {
            IntStream limit = new Random().ints(97, 122 + 1).limit(4);
            StringBuilder builder = limit.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append);
            customer = new Customer(builder.toString());
        }

        public void run() {
            barberShop.visit(customer);
            System.out.println("waitqueue id in CustomerArrival" + System.identityHashCode(barberShop.waitQueue));
            System.out.println(barberShop.waitQueue.size());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BarberShopDailyWorkFlow workFlow = new BarberShopDailyWorkFlow();
        workFlow.barberShop.open();
        Timer timer = new Timer();

        int count = 2;
        while (count > 0) {
            count--;
            TimerTask timerTask = workFlow.new CustomerArrival();
            timer.schedule(timerTask, 200);
        }

        Thread.sleep(10000);
        timer.cancel();
        timer.purge();
        workFlow.barberShop.close();
    }
}
