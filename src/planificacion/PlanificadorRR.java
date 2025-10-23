package planificacion;

import proceso.Proceso;
import java.util.List;

/**
 * Implementación básica RR que mantiene un contador interno de quantum actual.
 * La clase NO remueve el enEjecucion automáticamente de la cola; Simulador debe
 * manejar el regreso del proceso a la cola si Planificador devuelve otro proceso.
 */
public class PlanificadorRR implements Planificador {
    private final int quantum; // unidades
    private int contadorQuantum = 0;

    public PlanificadorRR(int quantum) {
        this.quantum = Math.max(1, quantum);
    }

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> colaListos, int tiempoActual, Proceso enEjecucion) {
        // Si hay un enEjecucion y aún no ha consumido su quantum, lo mantiene
        if (enEjecucion != null && enEjecucion.getTiempoRestante() > 0) {
            contadorQuantum++;
            if (contadorQuantum < quantum) {
                return enEjecucion;
            } else {
                // quantum expiró: reset y devolver null para que Simulador saque del CPU
                contadorQuantum = 0;
                // Simulador se encargará de devolver enEjecucion a la cola si quedó remanente.
                // Ahora elegimos siguiente de la cola (si hay)
                if (!colaListos.isEmpty()) {
                    return colaListos.remove(0);
                } else {
                    // si no hay otros, seguir con el mismo proceso reiniciando contador
                    contadorQuantum = 0;
                    return enEjecucion; // continúa si es el único
                }
            }
        } else {
            // no hay enEjecucion: tomar el primero de la cola (si existe)
            if (!colaListos.isEmpty()) {
                contadorQuantum = 0;
                return colaListos.remove(0);
            }
        }
        return null;
    }
}




