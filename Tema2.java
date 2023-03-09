import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class Tema2 {
    public static void main(String[] args) throws IOException {
        int P = Integer.parseInt(args[1]);
        java.util.concurrent.Semaphore sem = new java.util.concurrent.Semaphore(0);
        File orders, products;

        orders = new File(args[0] + "/orders.txt");
        products = new File(args[0] + "/order_products.txt");
        BufferedReader br = new BufferedReader(new java.io.FileReader(orders));
        AtomicInteger inQueue = new AtomicInteger(0);
        ExecutorService tpe = Executors.newFixedThreadPool(P);
        ExecutorService tpe2 = Executors.newFixedThreadPool(P);

        BufferedWriter f_writer1 = new BufferedWriter(new FileWriter("order_products_out.txt"));
        BufferedWriter f_writer2 = new BufferedWriter(new FileWriter("orders_out.txt"));

        inQueue.incrementAndGet();
        tpe.submit(new OrderTasks(br, products, f_writer1, tpe, inQueue, P, f_writer2, tpe2, sem));
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            f_writer1.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        try {
            f_writer2.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}