package edu.eci.arsw.concurrency;

import java.util.concurrent.Phaser;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public final class PauseController {
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition unpaused = lock.newCondition();
  private volatile boolean paused = false;

  // Usar un Phaser para rastrear los hilos que alcanzan el estado de pausa
  // Registramos el hilo principal más todos los hilos trabajadores
  private final Phaser phaser = new Phaser(1); // 1 para el controlador mismo (hilo principal)

  public void register() {
    phaser.register();
  }

  public void unregister() {
    phaser.arriveAndDeregister();
  }

  public void pause() {
    lock.lock();
    try {
      paused = true;
    } finally {
      lock.unlock();
    }
    // Esperar a que todas las partes registradas lleguen a la barrera
    phaser.arriveAndAwaitAdvance();
  }

  public void resume() {
    lock.lock();
    try {
      paused = false;
      unpaused.signalAll();
    } finally {
      lock.unlock();
    }
  }

  public boolean paused() {
    return paused;
  }

  public void awaitIfPaused() throws InterruptedException {
    // Si está pausado, llegar a la barrera para señalar que estamos pausados
    if (paused) {
      phaser.arrive();
      lock.lockInterruptibly();
      try {
        while (paused) {
          unpaused.await();
        }
      } finally {
        lock.unlock();
      }
    }
  }
}
