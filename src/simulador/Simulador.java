package simulador;

import proceso.Proceso;
import planificacion.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Simulador: controla el reloj, las llegadas, la cola de listos, ejecución y el historial.
 * Usa javax.swing.Timer (sin importar java.util.Timer).
 */
public class Simulador {
    private final List<Proceso> procesos = new ArrayList<>(); // todos los procesos creados (para llegada)
    private final List<Proceso> colaListos = new ArrayList<>(); // procesos listos para ejecutar
    private final List<Proceso> historial = new ArrayList<>(); // procesos terminados
    private Proceso enEjecucion = null;

    private Planificador planificador;
    private javax.swing.Timer timer; // javax.swing.Timer
    private int tiempo = 0; // tiempo en unidades
    private final VentanaPrincipal ventana;
    private int milisegundosPorUnidad = 3000; // Cada unidad de tiempo = 3 segundos (por requerimiento)

    // Para Round Robin necesitamos pasar quantum al planificador cuando se crea
    public Simulador(VentanaPrincipal ventana) {
        this.ventana = ventana;
    }

    public void setMilisegundosPorUnidad(int ms) { this.milisegundosPorUnidad = ms; }

    public void agregarProceso(Proceso p) {
        procesos.add(p);
    }

    public List<Proceso> getColaListos() { return colaListos; }
    public Proceso getEnEjecucion() { return enEjecucion; }
    public List<Proceso> getHistorial() { return historial; }
    public int getTiempo() { return tiempo; }

    public void configurarPlanificador(String algoritmo, int quantum) {
        switch (algoritmo) {
            case "FCFS" -> planificador = new PlanificadorFCFS();
            case "SJF" -> planificador = new PlanificadorSJF();
            case "SRTF" -> planificador = new PlanificadorSRTF();
            case "Round Robin" -> planificador = new PlanificadorRR(Math.max(1, quantum));
            default -> planificador = new PlanificadorFCFS();
        }
    }

    public void iniciar() {
        if (planificador == null) planificador = new PlanificadorFCFS();
        if (timer != null && timer.isRunning()) timer.stop();

        // Cada tick = 3 segundos de tiempo real
        timer = new javax.swing.Timer(milisegundosPorUnidad, e -> tick());
        timer.start();
    }

    public void detener() {
        if (timer != null) timer.stop();
    }

    // Nuevo método para reiniciar el estado manteniendo procesos
    public void reiniciarConMismosProcesos(String nuevoAlgoritmo, int quantum) {
        // Detener el timer
        if (timer != null) timer.stop();

        // Resetear estado
        tiempo = 0;
        colaListos.clear();
        historial.clear();
        enEjecucion = null;

        // Restaurar tiempoRestante de los procesos a su tiempoCPU original
        for (Proceso p : procesos) {
            p.setTiempoRestante(p.getTiempoCPU());
        }

        // Configurar el nuevo planificador
        configurarPlanificador(nuevoAlgoritmo, quantum);

        // Actualizar UI
        ventana.limpiarCPU();
        ventana.refrescarTablaCola(new ArrayList<>());
        ventana.actualizarTiempo(tiempo);
        ventana.reiniciarTablas();
    }

    private void tick() {
        // 1) agregar procesos que llegan en este instante
        for (Iterator<Proceso> it = procesos.iterator(); it.hasNext(); ) {
            Proceso p = it.next();
            if (p.getLlegada() == tiempo) {
                colaListos.add(p);
                ventana.agregarFilaCola(p); // actualiza tabla inmediatamente cuando llega
            }
        }

        // 2) seleccionar siguiente proceso (planificador puede devolver enEjecucion o uno nuevo)
        Proceso seleccionado = planificador.seleccionarSiguiente(colaListos, tiempo, enEjecucion);

        // Si planificador devolvió distinto al que estaba, manejar preempción / retorno a cola
        if (seleccionado != null && enEjecucion != null && seleccionado != enEjecucion) {
            if (enEjecucion.getTiempoRestante() > 0) {
                colaListos.add(enEjecucion);
                ventana.agregarFilaCola(enEjecucion);
            }
            enEjecucion = seleccionado;
        } else if (seleccionado == null && enEjecucion == null) {
            enEjecucion = null;
        } else {
            enEjecucion = seleccionado;
        }

        // 3) ejecutar 1 unidad si hay proceso
        if (enEjecucion != null) {
            int rem = enEjecucion.getTiempoRestante() - 1;
            enEjecucion.setTiempoRestante(Math.max(0, rem));
            ventana.actualizarProcesoEnCPU(enEjecucion);

            if (enEjecucion.getTiempoRestante() == 0) {
                // Proceso termina
                historial.add(enEjecucion);
                ventana.agregarFilaHistorial(enEjecucion, tiempo + 1); // finish time = tiempo+1
                ventana.agregarFilaMetricas(enEjecucion, tiempo + 1);  // agrega métricas a tabla
                enEjecucion = null;
                ventana.limpiarCPU();
            }
        } else {
            ventana.limpiarCPU();
        }

        // 4) actualizar tabla de cola (remover procesos con tiempo 0 si quedaron)
        ventana.refrescarTablaCola(colaListos);

        // 5) incrementar tiempo y actualizar label
        tiempo++;
        ventana.actualizarTiempo(tiempo);

        // 6) Si no quedan procesos → detener simulación y mostrar diálogo
        if (enEjecucion == null && colaListos.isEmpty()) {
            boolean quedanPorLlegar = procesos.stream()
                    .anyMatch(p -> p.getLlegada() > tiempo && !historial.contains(p));
            if (!quedanPorLlegar) {
                detener();
                ventana.limpiarCPU();
                SwingUtilities.invokeLater(() -> {
                    ventana.resaltarMasEficiente();
                    ventana.mostrarDialogoNuevoAlgoritmo();
                });
            }
        }
    }
}
