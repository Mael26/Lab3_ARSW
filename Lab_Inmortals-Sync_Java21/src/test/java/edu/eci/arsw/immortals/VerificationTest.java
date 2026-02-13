package edu.eci.arsw.immortals;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class VerificationTest {

    @Test
    void testInvariantWithManyImmortals() throws Exception {
        int n = 100;
        int health = 100;
        int damage = 10;
        long expectedTotalHealth = (long) n * health;

        try (var m = new ImmortalManager(n, "ordered", health, damage)) {
            m.start();

            // Dejarlos pelear un rato
            Thread.sleep(500);

            // Pausar y verificar el invariante
            m.pause();
            long sum = m.totalHealth();
            System.out.println("Paused. Total health: " + sum);
            assertEquals(expectedTotalHealth, sum, "Invariant violated! Total health changed.");

            // Reanudar y pelear más
            m.resume();
            Thread.sleep(500);

            // Pausar de nuevo y verificar
            m.pause();
            sum = m.totalHealth();
            System.out.println("Paused again. Total health: " + sum);
            assertEquals(expectedTotalHealth, sum, "Invariant violated after resume!");
        }
    }

    @Test
    void testDeadImmortalsRemoval() throws Exception {
        // Alto daño, poca salud para asegurar muertes
        int n = 10;
        int health = 25; // Salud irregular para probar la limitación (clamping)
        int damage = 10;

        try (var m = new ImmortalManager(n, "ordered", health, damage)) {
            m.start();

            // Esperar algunas muertes
            Thread.sleep(1000);

            m.pause();
            int alive = m.aliveCount();
            int populationSize = m.populationSnapshot().size();
            System.out.println("Alive: " + alive + ", Population size: " + populationSize);

            // Verificar que algunos inmortales murieron y fueron eliminados de la lista de
            // población
            // Nota: aliveCount itera la población y verifica isAlive().
            // Si son removidos de la población, aliveCount podría retornar un número menor
            // o
            // el tamaño de la población debería disminuir.
            // El requerimiento dice "Remover inmortales muertos".
            // Así que el tamaño de la lista de población debería disminuir.

            assertTrue(populationSize < n, "Population size should decrease as immortals die.");
            assertEquals(alive, populationSize, "Population list should only contain alive immortals (mostly).");

            // ¿Verificar invariante para la población restante?
            // Si son eliminados, su salud desaparece de totalHealth().
            // ¿Entonces la Salud Total disminuirá si los eliminamos?
            // Espera. El invariante "La suma de salud permanece constante" usualmente
            // aplica al
            // sistema cerrado.
            // Si eliminamos inmortales muertos (salud=0), la suma debería seguir siendo
            // válida.
            // Pero si los eliminamos antes de que lleguen exactamente a 0 (ej. paran en
            // <=0),
            // ¿su salud negativa podría afectar la suma?
            // Nuestra lógica: health += damage, other.health -= damage.
            // La suma es constante.
            // Si uno muere (health <= 0) y es removido, la suma de la población RESTANTE
            // será MENOR que el total inicial...
            // Porque la salud fue transferida al ganador.
            // Espera. NO.
            // ganador.health += damage. perdedor.health -= damage.
            // Suma = (Ganador + damage) + (Perdedor - damage) = Ganador + Perdedor.
            // Si el Perdedor sale con health <= 0, y lo removemos.
            // Suma de restantes = Ganador (aumentado).
            // Pero el Perdedor tenía algo de salud antes... revisemos:
            // Inicial: A=100, B=100. Suma=200.
            // Pelea: A golpea a B. B=90, A=110. Suma=200.
            // ...
            // A golpea a B hasta que B=0. A=200. Suma=200. (Si B se queda en la lista con
            // 0).
            // Si B es removido. Suma = A(200). La suma sigue siendo 200.
            // Así que el invariante se mantiene incluso si los muertos (salud 0) son
            // removidos.

            long sum = m.totalHealth();
            assertEquals((long) n * health, sum, "Invariant should hold even after removal (assuming they die at 0).");
        }
    }
}
