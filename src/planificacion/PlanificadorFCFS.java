package planificacion;

import proceso.Proceso;
import java.util.List;

public class PlanificadorFCFS implements Planificador {
    @Override
    public Proceso seleccionarSiguiente(List<Proceso> colaListos, int tiempoActual, Proceso enEjecucion) {
        // FCFS simple: devuelve el primero (FIFO)
        if (enEjecucion != null && enEjecucion.getTiempoRestante() > 0) {
            return enEjecucion; // no expropiativo
        }
        if (!colaListos.isEmpty()) {
            return colaListos.remove(0);
        }
        return null;
    }
}
