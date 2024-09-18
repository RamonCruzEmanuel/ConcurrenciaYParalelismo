import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;

class Worker implements Runnable {
    private String name;
    private int delay;
    private CountDownLatch latch;
    public static int contador = 0; // Recurso compartido
    private static final Object lock = new Object(); // Objeto para sincronización

    // Constructor
    public Worker(String name, int delay, CountDownLatch latch) {
        this.name = name;
        this.delay = delay;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            System.out.println(name + " iniciando.");
            Thread.sleep(delay);

            // Sección crítica: modificación del recurso compartido
            synchronized (lock) {
                System.out.println(name + " entrando a la sección crítica.");
                contador++;
                System.out.println(name + " incrementó contador a: " + contador);
                System.out.println(name + " saliendo de la sección crítica.");
            }

        } catch (InterruptedException e) {
            System.out.println(name + " fue interrumpido.");
        } finally {
            // Reduce el contador del CountDownLatch
            latch.countDown();
        }
    }
}

public class MutexExample {
    public static void main(String[] args) {
        // Número de hilos a gestionar
        int numThreads = 3;

        // Crear un CountDownLatch para sincronizar los hilos
        CountDownLatch latch = new CountDownLatch(numThreads);

        // Crear un pool de hilos con un tamaño fijo
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Iniciar los hilos
        for (int i = 1; i <= numThreads; i++) {
            Worker worker = new Worker("Hilo-" + i, i * 2000, latch);
            executor.execute(worker);
        }

        try {
            // Esperar a que todos los hilos terminen
            latch.await();
        } catch (InterruptedException e) {
            System.out.println("El hilo principal fue interrumpido.");
        }

        // Apagar el pool de hilos
        executor.shutdown();

        System.out.println("Todos los hilos han terminado.");
        System.out.println("Valor final del contador: " + Worker.contador);
    }
}
