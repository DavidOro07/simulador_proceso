package simulador;

import proceso.Proceso;
import planificacion.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Clase Simulador:
 * - Controla el reloj del sistema.
 * - Administra la llegada de procesos, la cola de listos, el proceso en ejecución y el historial.
 * - Utiliza javax.swing.Timer para simular el paso del tiempo.
 */
public class Simulador {
    // Lista de procesos creados (los que van a llegar en el futuro)
    private final List<Proceso> procesos = new ArrayList<>();
    // Cola de listos: procesos preparados para ejecutarse
    private final List<Proceso> colaListos = new ArrayList<>();
    // Historial: procesos que ya terminaron
    private final List<Proceso> historial = new ArrayList<>();
    // Proceso que actualmente se está ejecutando en la CPU
    private Proceso enEjecucion = null;

    // Algoritmo de planificación en uso (FCFS, SJF, SRTF, RR, etc.)
    private Planificador planificador;
    // Temporizador que avanza el reloj (usa javax.swing.Timer, no java.util.Timer)
    private javax.swing.Timer timer;
    // Tiempo actual de la simulación
    private int tiempo = 0;
    // Referencia a la ventana para actualizar la interfaz gráfica
    private final VentanaPrincipal ventana;
    // Cantidad de milisegundos que representa una unidad de tiempo en la simulación
    private int milisegundosPorUnidad = 1000;

    // Constructor: necesita la ventana principal para poder actualizar la interfaz
    public Simulador(VentanaPrincipal ventana) {
        this.ventana = ventana;
    }

    // Cambiar la velocidad de simulación (ms por unidad de tiempo)
    public void setMilisegundosPorUnidad(int ms) { this.milisegundosPorUnidad = ms; }

    // Agregar un nuevo proceso a la lista de procesos (se activará cuando llegue su tiempo)
    public void agregarProceso(Proceso p) {
        procesos.add(p);
    }

    // Métodos getters para acceder al estado desde la interfaz
    public List<Proceso> getColaListos() { return colaListos; }
    public Proceso getEnEjecucion() { return enEjecucion; }
    public List<Proceso> getHistorial() { return historial; }
    public int getTiempo() { return tiempo; }

    // Configurar el planificador según el algoritmo seleccionado en la interfaz
    public void configurarPlanificador(String algoritmo, int quantum) {
        switch (algoritmo) {
            case "FCFS" -> planificador = new PlanificadorFCFS();
            case "SJF" -> planificador = new PlanificadorSJF();
            case "SRTF" -> planificador = new PlanificadorSRTF();
            case "Round Robin" -> planificador = new PlanificadorRR(Math.max(1, quantum)); // asegura quantum >= 1
            default -> planificador = new PlanificadorFCFS();
        }
    }

    // Iniciar la simulación
    public void iniciar() {
        if (planificador == null) planificador = new PlanificadorFCFS(); // si no hay, usa FCFS
        if (timer != null && timer.isRunning()) timer.stop(); // detiene un timer anterior si existía

        // Crea el temporizador que llama a tick() cada X ms
        timer = new javax.swing.Timer(milisegundosPorUnidad, e -> tick());
        timer.start(); // comienza la simulación
    }

    // Detener la simulación
    public void detener() {
        if (timer != null) timer.stop();
    }

    // Método tick(): se ejecuta en cada unidad de tiempo
    private void tick() {
        // 1) Agregar procesos que llegan en este instante
        for (Iterator<Proceso> it = procesos.iterator(); it.hasNext(); ) {
            Proceso p = it.next();
            if (p.getLlegada() == tiempo) {
                colaListos.add(p); // lo mete en la cola de listos
                ventana.agregarFilaCola(p); // actualiza tabla en GUI
                // Nota: no se elimina de "procesos" para poder reutilizarlo si se quiere
            }
        }

        // 2) Seleccionar el siguiente proceso según el planificador
        Proceso seleccionado = planificador.seleccionarSiguiente(colaListos, tiempo, enEjecucion);

        // Manejo de preempción o cambio de proceso
        if (seleccionado != null && enEjecucion != null && seleccionado != enEjecucion) {
            // Si el proceso anterior aún no terminó, regresa a la cola de listos
            if (enEjecucion.getTiempoRestante() > 0) {
                colaListos.add(enEjecucion);
                ventana.agregarFilaCola(enEjecucion);
            }
            enEjecucion = seleccionado; // se asigna el nuevo proceso
        } else if (seleccionado == null && enEjecucion == null) {
            // CPU libre y no hay nada para ejecutar
            enEjecucion = null;
        } else {
            enEjecucion = seleccionado; // mantiene el mismo proceso
        }

        // 3) Ejecutar una unidad de tiempo en el proceso en ejecución
        if (enEjecucion != null) {
            int rem = enEjecucion.getTiempoRestante() - 1; // resta 1 unidad
            enEjecucion.setTiempoRestante(Math.max(0, rem));
            ventana.actualizarProcesoEnCPU(enEjecucion); // muestra en GUI

            // Si el proceso terminó
            if (enEjecucion.getTiempoRestante() == 0) {
                historial.add(enEjecucion); // lo pasa al historial
                ventana.agregarFilaHistorial(enEjecucion, tiempo + 1); // lo agrega a la tabla de historial
                enEjecucion = null; // CPU queda libre
                ventana.limpiarCPU();
            }
        } else {
            ventana.limpiarCPU(); // si no hay proceso, muestra CPU libre
        }

        // 4) Actualizar la tabla de la cola en la interfaz
        ventana.refrescarTablaCola(colaListos);

        // 5) Incrementar el tiempo y actualizar la etiqueta en GUI
        tiempo++;
        ventana.actualizarTiempo(tiempo);
    }
}
