package simulador;

import planificador.*;
import proceso.Proceso;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class VentanaPrincipal extends JFrame {
    private Simulador simulador;
    private JTextArea areaCola, areaEjecucion, areaHistorial;
    private JComboBox<String> comboAlgoritmo;
    private Timer timer;

    // ✅ Constructor
    public VentanaPrincipal() {
        setTitle("Simulador de Planificación de Procesos");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior
        JPanel panelControl = new JPanel(new GridLayout(6, 2, 5, 5));
        panelControl.setBorder(BorderFactory.createTitledBorder("Configuración de procesos"));
        panelControl.setBackground(new Color(230, 240, 250)); // azul claro

        JTextField txtNombre = new JTextField();
        JTextField txtCPU = new JTextField();
        JTextField txtLlegada = new JTextField();
        JTextField txtQuantum = new JTextField("2");

        comboAlgoritmo = new JComboBox<>(new String[]{"FCFS", "SJF", "SRTF", "Round Robin"});
        JButton btnAgregar = new JButton("Agregar Proceso");
        JButton btnIniciar = new JButton("Iniciar Simulación");

        panelControl.add(new JLabel("Nombre:")); panelControl.add(txtNombre);
        panelControl.add(new JLabel("Tiempo CPU:")); panelControl.add(txtCPU);
        panelControl.add(new JLabel("Llegada:")); panelControl.add(txtLlegada);
        panelControl.add(new JLabel("Quantum (RR):")); panelControl.add(txtQuantum);
        panelControl.add(new JLabel("Algoritmo:")); panelControl.add(comboAlgoritmo);
        panelControl.add(btnAgregar); panelControl.add(btnIniciar);

        add(panelControl, BorderLayout.NORTH);

        // Áreas de texto
        areaCola = new JTextArea();
        areaCola.setBorder(BorderFactory.createTitledBorder("Cola de procesos"));
        areaEjecucion = new JTextArea();
        areaEjecucion.setBorder(BorderFactory.createTitledBorder("En ejecución"));
        areaHistorial = new JTextArea();
        areaHistorial.setBorder(BorderFactory.createTitledBorder("Historial"));

        Font font = new Font("Consolas", Font.PLAIN, 14);
        areaCola.setFont(font);
        areaEjecucion.setFont(font);
        areaHistorial.setFont(font);

        // Divisiones ajustables
        JSplitPane split1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(areaCola), new JScrollPane(areaEjecucion));
        split1.setResizeWeight(0.3);

        JSplitPane split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                split1, new JScrollPane(areaHistorial));
        split2.setResizeWeight(0.6);

        add(split2, BorderLayout.CENTER);

        // Eventos
        btnAgregar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText();
                int cpu = Integer.parseInt(txtCPU.getText());
                int llegada = Integer.parseInt(txtLlegada.getText());
                int quantum = Integer.parseInt(txtQuantum.getText());
                if (simulador != null) {
                    simulador.agregarProceso(new Proceso(nombre, cpu, llegada, quantum));
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Primero inicia la simulación.",
                            "Atención", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: Ingresa valores numéricos válidos.",
                        "Entrada inválida", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnIniciar.addActionListener(e -> iniciarSimulacion(txtQuantum));
    }

    // ✅ Método iniciar
    private void iniciarSimulacion(JTextField txtQuantum) {
        String algoritmo = (String) comboAlgoritmo.getSelectedItem();
        switch (algoritmo) {
            case "FCFS" -> simulador = new Simulador(new PlanificadorFCFS());
            case "SJF" -> simulador = new Simulador(new PlanificadorSJF());
            case "SRTF" -> simulador = new Simulador(new PlanificadorSRTF());
            case "Round Robin" -> simulador =
                    new Simulador(new PlanificadorRR(Integer.parseInt(txtQuantum.getText())));
        }

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                simulador.tick();
                SwingUtilities.invokeLater(() -> actualizarUI());
            }
        }, 0, 2000); // cada 2 segundos
    }

    // ✅ Método actualizar
    private void actualizarUI() {
        areaCola.setText("Cola:\n" + simulador.getColaListos());
        areaEjecucion.setText("En Ejecución:\n" + simulador.getEnEjecucion());
        areaHistorial.setText("Historial:\n" + simulador.getHistorial());
    }
}


