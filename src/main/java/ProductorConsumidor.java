//Implementar cola
import java.util.LinkedList;
import java.util.Queue;
//Implementar Semaforo
import java.util.concurrent.Semaphore;

public class ProductorConsumidor {
    private static Queue<Integer> buffer = new LinkedList<>(); //buffer
    private static Semaphore empty = new Semaphore(5); //semaforo que indica cuantos espacios quedan en el buffer
    private static Semaphore full = new Semaphore(0); //semaforo que indica cuantos espacios estan ocupados en el buffer
    private static Semaphore mutex = new Semaphore(1); //mutex para evitar condición de carrera

    public static class Productor implements Runnable{ //Clase productor, implementa Runnable para usar la clase como hilo
        @Override
        public void run() {
            for (int i = 1; i < 16; i++) { // Produce 15 cosas
                try {
                    empty.acquire(); //Espera hasta que hayan espacios vacios y quita uno.

                    mutex.acquire(); //Toma el turno para evitar que otro manipule el buffer al mismo tiempo

                    buffer.add(i); //añade al buffer

                    System.out.println(Thread.currentThread().getName() + " produjo " + i +" buffer: "+ buffer.size()); //Indica quien y que productor produjo, ademas de la cantidad de cosas en el buffer

                    mutex.release(); //Indica que su turno se acabo.

                    full.release(); //Indica que un espacio fue llenado
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static class Consumidor implements Runnable{ //Clase consumidor, implementa Runnable para ser usado como hilo

        @Override
        public void run() {
            for (int i = 0; i < 15; i++) {
                try {
                    full.acquire(); //Espera hasta que un espacio sea llenado y indica que fue vaciado

                    mutex.acquire(); //Toma el turno

                    System.out.println(Thread.currentThread().getName() + " Consumio " +buffer.poll() +" buffer: "+ buffer.size()); //Indica que consumidor consumio que cosa, ademas de indicar la cantidad de cosas en el buffer

                    mutex.release(); //Libera su turno.

                    empty.release(); //Indica que un espacio fue vaciado
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //Crear hilos productores y consumidores
        Thread productor1 = new Thread(new Productor(), "productor1");
        Thread productor2 = new Thread(new Productor(), "productor2");
        Thread consumidor1 = new Thread(new Consumidor(), "consumidor1");
        Thread consumidor2 = new Thread(new Consumidor(), "consumidor2");

        //Iniciar hilos.
        productor2.start();
        productor1.start();
        consumidor1.start();
        consumidor2.start();

    }
}
