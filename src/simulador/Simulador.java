package simulador;

import proceso.Proceso;
import planificacion.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Clase Simulador:
 * Controla el reloj, las llegadas de procesos, la cola de listos,
 * la ejecución en CPU y el historial de procesos terminados.
 * 
 * Usa javax.swing.Timer para simular el paso del tiempo en la GUI.
 */
public class Simulador {
    // Listas principales del simulador
    private final List<Proceso> procesos = new ArrayList<>();   // todos los procesos creados (entrada)
    private final List<Proceso> colaListos = new ArrayList<>(); // procesos que están listos para ejecutar
    private final List<Proceso> historial = new ArrayList<>();  // procesos que ya terminaron

    private Proceso enEjecucion = null; // proceso actualmente en CPU
    private Planificador planificador;  // algoritmo de planificación seleccionado
    private javax.swing.Timer timer;    // reloj del simulador (usa Swing Timer)
    private int tiempo = 0;             // contador de tiempo en unidades
    private final VentanaPrincipal ventana; // referencia a la GUI
    private int milisegundosPorUnidad = 1000; // tiempo real por unidad simulada

    // Constructor: recibe la ventana principal para actualizar la GUI
    public Simulador(VentanaPrincipal ventana) {
        this.ventana = ventana;
    }

    // Permite cambiar la velocidad de simulación (ms por unidad de tiempo)
    public void setMilisegundosPorUnidad(int ms) { 
        this.milisegundosPorUnidad = ms; 
    }

    // Agregar proceso a la lista inicial de procesos
    public void agregarProceso(Proceso p) {
        procesos.add(p);
    }

    // Métodos de acceso
    public List<Proceso> getColaListos() { return colaListos; }
    public Proceso getEnEjecucion() { return enEjecucion; }
    public List<Proceso> getHistorial() { return historial; }
    public int getTiempo() { return tiempo; }

    // Configuración del algoritmo de planificación
    public void configurarPlanificador(String algoritmo, int quantum) {
        switch (algoritmo) {
            case "FCFS" -> planificador = new PlanificadorFCFS();
            case "SJF" -> planificador = new PlanificadorSJF();
            case "SRTF" -> planificador = new PlanificadorSRTF();
            case "Round Robin" -> planificador = new PlanificadorRR(Math.max(1, quantum));
            default -> planificador = new PlanificadorFCFS();
        }
    }

    // Inicia la simulación (empieza el reloj)
    public void iniciar() {
        if (planificador == null) planificador = new PlanificadorFCFS();
        if (timer != null && timer.isRunning()) timer.stop();

        // Cada "tick" ocurre cada milisegundosPorUnidad
        timer = new javax.swing.Timer(milisegundosPorUnidad, e -> tick());
        timer.start();
    }

    // Detiene la simulación (pausa el reloj)
    public void detener() {
        if (timer != null) timer.stop();
    }

    // Lógica de un "tick" de tiempo (una unidad simulada)
    private void tick() {
        // 1) Revisar procesos que llegan en este instante
        for (Iterator<Proceso> it = procesos.iterator(); it.hasNext(); ) {
            Proceso p = it.next();
            if (p.getLlegada() == tiempo) {
                colaListos.add(p);
                ventana.agregarFilaCola(p); // actualizar tabla de listos en la GUI
                // Nota: no se elimina de "procesos" para conservar registro
            }
        }

        // 2) Seleccionar siguiente proceso según el planificador
        Proceso seleccionado = planificador.seleccionarSiguiente(colaListos, tiempo, enEjecucion);

        // Manejo de cambios de proceso (preempción si aplica)
        if (seleccionado != null && enEjecucion != null && seleccionado != enEjecucion) {
            // Si el proceso interrumpido aún no terminó, vuelve a la cola
            if (enEjecucion.getTiempoRestante() > 0) {
                colaListos.add(enEjecucion);
                ventana.agregarFilaCola(enEjecucion);
            }
            enEjecucion = seleccionado;
        } else if (seleccionado == null && enEjecucion == null) {
            // CPU libre y sin procesos disponibles
            enEjecucion = null;
        } else {
            enEjecucion = seleccionado;
        }

        // 3) Ejecutar una unidad de CPU si hay proceso en ejecución
        if (enEjecucion != null) {
            int rem = enEjecucion.getTiempoRestante() - 1;
            enEjecucion.setTiempoRestante(Math.max(0, rem));
            ventana.actualizarProcesoEnCPU(enEjecucion);

            if (enEjecucion.getTiempoRestante() == 0) {
                // Proceso terminó → mover a historial
                historial.add(enEjecucion);
                ventana.agregarFilaHistorial(enEjecucion, tiempo + 1); // registra tiempo de fin
                enEjecucion = null;
                ventana.limpiarCPU();
            }
        } else {
            // CPU vacía en este tick
            ventana.limpiarCPU();
        }

        // 4) Refrescar la cola de listos en la interfaz
        ventana.refrescarTablaCola(colaListos);

        // 5) Avanzar tiempo y actualizar display
        tiempo++;
        ventana.actualizarTiempo(tiempo);
    }
}
