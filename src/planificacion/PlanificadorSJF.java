package planificacion;

import proceso.Proceso;
import java.util.Comparator;
import java.util.List;

public class PlanificadorSJF implements Planificador {
    @Override
    public Proceso seleccionarSiguiente(List<Proceso> colaListos, int tiempoActual, Proceso enEjecucion) {
        // SJF no expropiativo: si hay uno en ejecución, lo deja terminar.
        if (enEjecucion != null && enEjecucion.getTiempoRestante() > 0) {
            return enEjecucion;
        }
        if (colaListos.isEmpty()) return null;

        // elegir por menor tiempo total requerido (tiempoCPU)
        Proceso elegido = colaListos.stream()
                .min(Comparator.comparingInt(Proceso::getTiempoCPU))
                .orElse(null);
        if (elegido != null) {
            colaListos.remove(elegido);
        }
        return elegido;
    }
}

