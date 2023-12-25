package org.example.seminar_5.five_philosopher;

import java.util.concurrent.Semaphore;

public class DiningPhilosophers {
    private static final int NUM_PHILOSOPHERS = 5;
    private static final int NUM_EATS = 3;

    public static void main(String[] args) {
        Semaphore[] forks = new Semaphore[NUM_PHILOSOPHERS];
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new Semaphore(1); // инициализация семафоров для вилок
        }

        Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i] = new Philosopher(i, forks);
            philosophers[i].start();
        }

        try {
            for (Philosopher philosopher : philosophers) {
                philosopher.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Philosopher extends Thread {
        private final int id;
        private final Semaphore[] forks;

        public Philosopher(int id, Semaphore[] forks) {
            this.id = id;
            this.forks = forks;
        }

        private void eat() throws InterruptedException {
            // Проверяем, доступны ли обе вилки для текущего философа
            if (forks[id].tryAcquire()) {
                if (forks[(id + 1) % NUM_PHILOSOPHERS].tryAcquire()) {
                    System.out.println("Philosopher " + id + " is eating.");
                    Thread.sleep(500);
                    forks[(id + 1) % NUM_PHILOSOPHERS].release(); // освобождаем вилки после еды
                    forks[id].release();
                } else {
                    forks[id].release();
                    // Философ ожидает освобождения вилки справа
                    // для предотвращения блокировки и дедлока
                    Thread.sleep(100);
                }
            } else {
                // Философ ожидает освобождения вилки слева
                // для предотвращения блокировки и дедлока
                Thread.sleep(100);
            }
        }

        private void think() throws InterruptedException {
            System.out.println("Philosopher " + id + " is thinking.");
            Thread.sleep(500);
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < NUM_EATS; i++) {
                    eat();
                    think();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
