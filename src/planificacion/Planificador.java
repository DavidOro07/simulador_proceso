package planificacion;

import proceso.Proceso;
import java.util.List;

public interface Planificador {
    /**
     * Selecciona el siguiente proceso a ejecutar.
     * - colaListos es la lista de procesos listos (ordenada como desee el planificador).
     * - tiempoActual es el tick actual (unidad).
     * - enEjecucion puede ser null o el proceso en CPU.
     *
     * Debe devolver el proceso que se ejecutará en el próximo ciclo (puede devolver enEjecucion).
     * Implementaciones pueden modificar colaListos (por ejemplo remover el seleccionado).
     */
    Proceso seleccionarSiguiente(List<Proceso> colaListos, int tiempoActual, Proceso enEjecucion);
}
