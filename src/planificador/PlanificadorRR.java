package planificador;

import proceso.Proceso;
import java.util.List;

public class PlanificadorRR implements Planificador {
    private int quantum;
    private int contadorQuantum = 0;

    public PlanificadorRR(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> colaListos, int tiempoActual, Proceso enEjecucion) {
        if (enEjecucion != null) {
            if (contadorQuantum < quantum - 1 && enEjecucion.getTiempoRestante() > 0) {
                contadorQuantum++;
                return enEjecucion;
            } else {
                contadorQuantum = 0;
                if (enEjecucion.getTiempoRestante() > 0) {
                    colaListos.add(enEjecucion);
                }
            }
        }
        if (!colaListos.isEmpty()) {
            contadorQuantum = 0;
            return colaListos.remove(0);
        }
        return null;
    }
}

