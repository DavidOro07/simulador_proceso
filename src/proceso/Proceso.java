package proceso;

public class Proceso {
    private static int contador = 1;
    private int pid;
    private String nombre;
    private int tiempoCPU;
    private int tiempoRestante;
    private int llegada;
    private int quantum;

    public Proceso(String nombre, int tiempoCPU, int llegada, int quantum) {
        this.pid = contador++;
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
        return "PID:" + pid + " " + nombre + " (Restante: " + tiempoRestante + ")";
    }
}
