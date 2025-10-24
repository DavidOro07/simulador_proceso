package proceso;

/**
 * Representa un proceso en el simulador de planificación.
 */
public class Proceso {
    private static int contador = 1;

    private final int pid;          // Identificador único
    private String nombre;
    private int tiempoCPU;          // Tiempo total requerido en CPU
    private int tiempoRestante;     // Tiempo restante
    private int llegada;            // Instante de llegada
    private int quantum;            // Quantum (solo usado en Round Robin)

    public Proceso(String nombre, int tiempoCPU, int llegada, int quantum) {
        this.pid = contador++;
        this.nombre = nombre;
        this.tiempoCPU = tiempoCPU;
        this.tiempoRestante = tiempoCPU;
        this.llegada = llegada;
        this.quantum = quantum;
    }

    // ================= Getters & Setters =================

    public int getPid() {
        return pid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTiempoCPU() {
        return tiempoCPU;
    }

    public void setTiempoCPU(int tiempoCPU) {
        this.tiempoCPU = tiempoCPU;
        this.tiempoRestante = tiempoCPU; // actualizar también restante
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    public int getLlegada() {
        return llegada;
    }

    public void setLlegada(int llegada) {
        this.llegada = llegada;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    // ================= Utilidades =================

    /** Reinicia el proceso para nueva simulación (reutilización de datos). */
    public void reiniciar() {
        this.tiempoRestante = this.tiempoCPU;
    }

    @Override
    public String toString() {
        return String.format("P%d (%s): CPU=%d, Llegada=%d, Restante=%d",
                pid, nombre, tiempoCPU, llegada, tiempoRestante);
    }
}
