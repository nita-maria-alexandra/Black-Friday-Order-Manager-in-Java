import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.util.concurrent.Semaphore;

public class OrderTasks implements Runnable {
    private final BufferedReader orders;
    private final File products;
    private final BufferedWriter output;
    private final BufferedWriter output2;
    private final ExecutorService tpe;
    private final ExecutorService tpe2;
    private final AtomicInteger inQueue;
    private final Integer threads;
    private final Semaphore sem2;

    public OrderTasks(BufferedReader orders, File products, BufferedWriter output, ExecutorService tpe,
                      AtomicInteger inQueue, Integer threads, BufferedWriter output2,
                      ExecutorService tpe2, java.util.concurrent.Semaphore sem2) {
        this.orders = orders;
        this.products = products;
        this.output = output;
        this.tpe = tpe;
        this.inQueue = inQueue;
        this.threads = threads;
        this.output2 = output2;
        this.tpe2 = tpe2;
        this.sem2 = sem2;
    }

    @Override
    public void run() {
        String [] splitLine;
        String line;
        Semaphore sem = new Semaphore(0);

        try {
            if((line = orders.readLine()) != null) {
                splitLine = line.split(",");

                if (Integer.parseInt(splitLine[1]) != 0) {
                    tpe2.submit(new ProductTasks(products, splitLine[0], output, sem));

                    inQueue.incrementAndGet();
                    tpe.submit(new OrderTasks(orders, products, output, tpe, inQueue, threads, output2,
                            tpe2, sem2));
                    try {
                        sem.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                        try {
                            output2.write(splitLine[0] + "," + splitLine[1] + ",shipped\n");
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }

                } else {
                    inQueue.incrementAndGet();
                    tpe.submit(new OrderTasks(orders, products, output, tpe, inQueue, threads, output2,
                            tpe2, sem2));
                }

        }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        int left = inQueue.decrementAndGet();

        if (left == 0) {
            tpe.shutdown();
            tpe2.shutdown();
            sem2.release();
        }
    }
}
