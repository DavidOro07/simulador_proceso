package planificacion;

import proceso.Proceso;
import java.util.List;

/**
 * Planificador Round Robin (RR)
 *
 * Basado en la implementación JavaScript original, adaptado para integrarse
 * con el modelo de simulador existente que gestiona la cola y el CPU.
 */
public class PlanificadorRR implements Planificador {
    private final int quantum; // duración del quantum en unidades de tiempo
    private int contadorQuantum = 0;

    public PlanificadorRR(int quantum) {
        this.quantum = Math.max(1, quantum);
    }

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> colaListos, int tiempoActual, Proceso enEjecucion) {
        // Si hay proceso ejecutándose actualmente
        if (enEjecucion != null) {
            // El proceso todavía tiene tiempo restante
            if (enEjecucion.getTiempoRestante() > 0) {
                contadorQuantum++;

                // Si no se ha agotado el quantum, continuar con el mismo proceso
                if (contadorQuantum < quantum) {
                    return enEjecucion;
                } else {
                    // Quantum expiró → reiniciar contador
                    contadorQuantum = 0;

                    // Si el proceso aún no ha terminado, el simulador lo reencola
                    // y el planificador elige el siguiente proceso disponible
                    if (!colaListos.isEmpty()) {
                        return colaListos.remove(0);
                    } else {
                        // Si no hay más procesos, continuar con el mismo
                        return enEjecucion;
                    }
                }
            } else {
                // Proceso terminó → reiniciar contador y elegir otro
                contadorQuantum = 0;
                if (!colaListos.isEmpty()) {
                    return colaListos.remove(0);
                }
                return null;
            }
        } else {
            // No hay proceso en ejecución → tomar el primero de la cola
            if (!colaListos.isEmpty()) {
                contadorQuantum = 0;
                return colaListos.remove(0);
            }
        }

        // No hay proceso disponible
        return null;
    }
}
