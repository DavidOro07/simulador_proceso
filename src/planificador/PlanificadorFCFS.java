package planificador;

import proceso.Proceso;
import java.util.List;

public class PlanificadorFCFS implements Planificador {
    @Override
    public Proceso seleccionarSiguiente(List<Proceso> colaListos, int tiempoActual, Proceso enEjecucion) {
        if (!colaListos.isEmpty()) {
            return colaListos.remove(0);
        }
        return null;
    }
}

