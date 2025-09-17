package planificador;

import proceso.Proceso;
import java.util.Comparator;
import java.util.List;

public class PlanificadorSRTF implements Planificador {
    @Override
    public Proceso seleccionarSiguiente(List<Proceso> colaListos, int tiempoActual, Proceso enEjecucion) {
        Proceso siguiente = null;
        if (enEjecucion != null) {
            colaListos.add(enEjecucion);
        }
        if (!colaListos.isEmpty()) {
            siguiente = colaListos.stream()
                    .min(Comparator.comparingInt(Proceso::getTiempoRestante))
                    .orElse(null);
            colaListos.remove(siguiente);
        }
        return siguiente;
    }
}

