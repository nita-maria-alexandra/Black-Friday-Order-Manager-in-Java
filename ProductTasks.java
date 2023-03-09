import java.io.File;
import java.io.BufferedWriter;
import java.util.concurrent.Semaphore;
import java.io.BufferedReader;

public class ProductTasks implements Runnable {
    private final File products_file;
    private final String order;
    private final BufferedWriter output;
    private final Semaphore sem;

    public ProductTasks(File products_file, String order, BufferedWriter output, Semaphore sem) {
        this.products_file = products_file;
        this.order = order;
        this.output = output;
        this.sem = sem;
    }

    @Override
    public void run() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new java.io.FileReader(products_file));
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = null;
        while(true) {
            try {
                if ((line = br.readLine()) == null) break;
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            String[] splitLine = line.split(",");
            if (java.util.Objects.equals(splitLine[0], order)) {
                    try {
                        output.write(splitLine[0] + "," + splitLine[1] + ",shipped\n");
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        sem.release();
    }
}
