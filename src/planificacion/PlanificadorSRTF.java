package planificacion;

import proceso.Proceso;
import java.util.Comparator;
import java.util.List;

public class PlanificadorSRTF implements Planificador {
    @Override
    public Proceso seleccionarSiguiente(List<Proceso> colaListos, int tiempoActual, Proceso enEjecucion) {
        // SRTF (Shortest Remaining Time First) es preemptivo:
        // busca entre enEjecucion y la colaListos quien tenga menor remaining.
        Proceso candidato = enEjecucion;

        // considerar todos los listos
        for (Proceso p : colaListos) {
            if (candidato == null || p.getTiempoRestante() < candidato.getTiempoRestante()) {
                candidato = p;
            }
        }

        if (candidato == null) return null;

        // si candidato está en colaListos lo removemos y devolvemos; si es el enEjecucion lo devolvemos tal cual.
        if (colaListos.contains(candidato)) {
            colaListos.remove(candidato);
            // si enEjecucion era distinto, enEjecucion quedará preemptado y volverá a cola en Simulador
        }
        return candidato;
    }
}



