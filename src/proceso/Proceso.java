package proceso;

public class Proceso {
    private static int contadorPid = 1;

    private final int pid;
    private final String nombre;
    private final int tiempoCPU; // tiempo total requerido
    private int tiempoRestante;  // tiempo que queda
    private final int llegada;   // instante de llegada (unidades)
    private final int quantum;   // usado solo si aplica

    public Proceso(String nombre, int tiempoCPU, int llegada, int quantum) {
        this.pid = contadorPid++;
        this.nombre = nombre;
        this.tiempoCPU = tiempoCPU;
        this.tiempoRestante = tiempoCPU;
        this.llegada = llegada;
        this.quantum = quantum;
    }

    public int getPid() { return pid; }
    public String getNombre() { return nombre; }
    public int getTiempoCPU() { return tiempoCPU; }
    public int getTiempoRestante() { return tiempoRestante; }
    public void setTiempoRestante(int tiempoRestante) { this.tiempoRestante = tiempoRestante; }
    public int getLlegada() { return llegada; }
    public int getQuantum() { return quantum; }

    @Override
    public String toString() {
        return String.format("P%d-%s (R:%d)", pid, nombre, tiempoRestante);
    }
}
