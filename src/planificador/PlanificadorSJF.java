package planificador;

import proceso.Proceso;
import java.util.Comparator;
import java.util.List;

public class PlanificadorSJF implements Planificador {
    @Override
    public Proceso seleccionarSiguiente(List<Proceso> colaListos, int tiempoActual, Proceso enEjecucion) {
        if (!colaListos.isEmpty()) {
            Proceso siguiente = colaListos.stream()
                    .min(Comparator.comparingInt(Proceso::getTiempoCPU))
                    .orElse(null);
            colaListos.remove(siguiente);
            return siguiente;
        }
        return null;
    }
}
