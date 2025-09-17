package planificador;

import proceso.Proceso;
import java.util.List;

public interface Planificador {
    Proceso seleccionarSiguiente(List<Proceso> colaListos, int tiempoActual, Proceso enEjecucion);
}
