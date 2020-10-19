package com.company;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class BarberShop {
    volatile AtomicBoolean shopClosed;
     BlockingQueue<Customer> waitQueue;
     BlockingQueue<Customer> cashierQueue;
    volatile Barber barber = new Barber();
    volatile Cashier cashier = new Cashier();


    BarberShop(final int capacity) {
        shopClosed = new AtomicBoolean(true);
        waitQueue = new ArrayBlockingQueue<>(capacity);
        cashierQueue = new ArrayBlockingQueue<>(capacity);
         new Thread(barber).start();
        new Thread(cashier).start();
    }

    void close() {
        if (!shopClosed.get()) {
            shopClosed.set(true);
        }
    }

    void open() {
        shopClosed.set(false);
    }

    boolean visit(Customer customer) {
        if (shopClosed.get()) {
            return false;
        }
        if (waitQueue.remainingCapacity() > 0) {
            System.out.println("customer allowed access into the barber shop");
            System.out.println(System.identityHashCode(waitQueue));
            waitQueue.offer(customer);
            return true;
        }
        System.out.println("customer denied visit to the barber shop");
        return false;
    }

    public class Barber implements Runnable {
        private void serve(Customer customer) {
            System.out.println("serving customerId:" + customer.getId());
            try {
                Thread.sleep(2);
            } catch (Exception ex) {
                System.out.println("service for customerId was interrupted");
            }
            System.out.println("done serving customerId:" + customer.getId());
        }

        public void run() {
            while (true) {
                System.out.println("waitqueue id in barber: "+System.identityHashCode(waitQueue)+" size:"+waitQueue.size());
                System.out.println("shopclosed in barber: "+System.identityHashCode(shopClosed)+ "value: "+shopClosed.get());
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("barbar looking to service the queue");
                if (waitQueue.size() > 0 && !shopClosed.get()) {
                    Customer customer = waitQueue.poll();
                    serve(customer);
                    cashierQueue.offer(customer);
                } else if (shopClosed.get()) {
                    break;
                }
            }
            System.out.println("barber done for the day");
        }
    }

    public class Cashier implements Runnable {
        private void pay(Customer customer) {
            System.out.println("customerId:" + customer.getId() + " paying and exiting the barbarshop");
        }

        public void run() {

            while (true) {
//                System.out.println("waitqueue id in cashier: "+System.identityHashCode(waitQueue));
//                System.out.println("shopClosed in cashier:"+System.identityHashCode(shopClosed));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                System.out.println("cashier looking to service the queue");
                if (cashierQueue.size()>0) {
                    Customer customer = cashierQueue.poll();
                    pay(customer);
                } else if (shopClosed.get() && waitQueue.size()<=0) {
                    break;
                }
            }
            System.out.println("cashier done for the day");
        }
    }
}
