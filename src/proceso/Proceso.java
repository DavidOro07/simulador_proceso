package proceso; 
// Paquete donde se encuentra la clase. Sirve para organizar el código.

public class Proceso {
    // ============================
    // Atributos de clase y objeto
    // ============================

    private static int contadorPid = 1; 
    // Variable estática que actúa como contador global para asignar
    // automáticamente identificadores únicos (pid) a cada proceso creado.

    private final int pid;         // Identificador único del proceso (auto-asignado)
    private final String nombre;   // Nombre del proceso
    private final int tiempoCPU;   // Tiempo total de CPU requerido por el proceso
    private int tiempoRestante;    // Tiempo que falta por ejecutar
    private final int llegada;     // Instante en que el proceso llega al sistema (unidades de tiempo)
    private final int quantum;     // Quantum asignado (solo se usa en algoritmos con reparto por turnos como Round Robin)

    // ============================
    // Constructor
    // ============================
    public Proceso(String nombre, int tiempoCPU, int llegada, int quantum) {
        this.pid = contadorPid++;   // Asigna un identificador único y lo incrementa para el siguiente proceso
        this.nombre = nombre;       // Asigna el nombre del proceso
        this.tiempoCPU = tiempoCPU; // Tiempo total que requiere de CPU
        this.tiempoRestante = tiempoCPU; // Inicialmente, el tiempo restante es igual al total
        this.llegada = llegada;     // Instante de llegada
        this.quantum = quantum;     // Quantum (en caso de aplicar algoritmos con rebanado de tiempo)
    }

    // ============================
    // Métodos de acceso (Getters y Setter)
    // ============================

    public int getPid() { return pid; }                     // Devuelve el ID del proceso
    public String getNombre() { return nombre; }            // Devuelve el nombre
    public int getTiempoCPU() { return tiempoCPU; }         // Devuelve el tiempo total de CPU requerido
    public int getTiempoRestante() { return tiempoRestante; } // Devuelve cuánto tiempo queda por ejecutar
    public void setTiempoRestante(int tiempoRestante) { this.tiempoRestante = tiempoRestante; } // Actualiza el tiempo restante
    public int getLlegada() { return llegada; }             // Devuelve el instante de llegada
    public int getQuantum() { return quantum; }             // Devuelve el quantum asignado

    // ============================
    // Representación en texto del proceso
    // ============================
    @Override
    public String toString() {
        // Muestra el proceso en formato:
        // P[id]-[nombre] (R:[tiempo restante])
        return String.format("P%d-%s (R:%d)", pid, nombre, tiempoRestante);
    }
}
