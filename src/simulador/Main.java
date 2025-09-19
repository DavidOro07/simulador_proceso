package simulador;  
// Define el paquete donde está la clase Main. Sirve para organizar el proyecto.

import javax.swing.SwingUtilities;  
// Importa SwingUtilities, una clase de utilidad para manejar la ejecución
// de interfaces gráficas en el hilo de eventos de Swing (EDT).

public class Main {
    // Método principal: punto de inicio del programa
    public static void main(String[] args) {
        // SwingUtilities.invokeLater(...) asegura que la creación
        // y manipulación de la interfaz gráfica se ejecute en el
        // Event Dispatch Thread (hilo de eventos de Swing).
        SwingUtilities.invokeLater(() -> {
            // Se crea una instancia de la ventana principal de la aplicación
            VentanaPrincipal ventana = new VentanaPrincipal();
            // Se hace visible la ventana
            ventana.setVisible(true);
        });
    }
}
