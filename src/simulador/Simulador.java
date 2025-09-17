package simulador;

import planificador.*;
import proceso.Proceso;

import java.util.*;

public class Simulador {
    private List<Proceso> procesos;
    private List<Proceso> colaListos;
    private List<Proceso> historial;
    private Planificador planificador;
    private Proceso enEjecucion;
    private int tiempo;

    public Simulador(Planificador planificador) {
        this.planificador = planificador;
        this.procesos = new ArrayList<>();
        this.colaListos = new ArrayList<>();
        this.historial = new ArrayList<>();
        this.tiempo = 0;
    }

    public void agregarProceso(Proceso p) {
        procesos.add(p);
    }

    public void tick() {
        // Llegan procesos
        for (Proceso p : procesos) {
            if (p.getLlegada() == tiempo) {
                colaListos.add(p);
            }
        }

        // Selección de proceso
        enEjecucion = planificador.seleccionarSiguiente(colaListos, tiempo, enEjecucion);

        // Ejecutar
        if (enEjecucion != null) {
            enEjecucion.setTiempoRestante(enEjecucion.getTiempoRestante() - 1);
            if (enEjecucion.getTiempoRestante() <= 0) {
                historial.add(enEjecucion);
                enEjecucion = null;
            }
        }

        tiempo++;
    }

    public List<Proceso> getColaListos() { return colaListos; }
    public List<Proceso> getHistorial() { return historial; }
    public Proceso getEnEjecucion() { return enEjecucion; }
    public int getTiempo() { return tiempo; }
}

