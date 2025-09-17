package simulador;

import proceso.Proceso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Ventana principal con diseño más limpio y usable.
 * - Formulario en la izquierda
 * - Controles arriba/derecha
 * - Tablas centrales para Cola y Historial
 * - Panel inferior para CPU (proceso en ejecución) y tiempo
 */
public class VentanaPrincipal extends JFrame {
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtCPU = new JTextField();
    private final JTextField txtLlegada = new JTextField();
    private final JTextField txtQuantum = new JTextField();
    private final JComboBox<String> comboAlgoritmo = new JComboBox<>(new String[]{"FCFS","SJF","SRTF","Round Robin"});
    private final JTextField txtVelocidad = new JTextField("1000"); // ms por unidad

    private final DefaultTableModel modeloCola = new DefaultTableModel(new String[]{"PID","Nombre","CPU","Llegada","Restante"}, 0);
    private final JTable tablaCola = new JTable(modeloCola);

    private final DefaultTableModel modeloHistorial = new DefaultTableModel(new String[]{"PID","Nombre","CPU","Llegada","FinishTime"}, 0);
    private final JTable tablaHistorial = new JTable(modeloHistorial);

    private final JLabel lblCPU = new JLabel("CPU libre", SwingConstants.CENTER);
    private final JLabel lblTiempo = new JLabel("Tiempo: 0", SwingConstants.CENTER);

    private Simulador simulador;

    public VentanaPrincipal() {
        setTitle("Simulador de Planificación de Procesos");
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout(8,8));

        // Panel izquierdo: formulario y botones
        JPanel panelLeft = new JPanel(new GridBagLayout());
        panelLeft.setBorder(BorderFactory.createTitledBorder("Crear Proceso / Configuración"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; panelLeft.add(new JLabel("Nombre:"), c);
        c.gridx = 1; panelLeft.add(txtNombre, c);

        c.gridx = 0; c.gridy = 1; panelLeft.add(new JLabel("Tiempo CPU (u):"), c);
        c.gridx = 1; panelLeft.add(txtCPU, c);

        c.gridx = 0; c.gridy = 2; panelLeft.add(new JLabel("Llegada (u):"), c);
        c.gridx = 1; panelLeft.add(txtLlegada, c);

        c.gridx = 0; c.gridy = 3; panelLeft.add(new JLabel("Quantum (RR):"), c);
        c.gridx = 1; panelLeft.add(txtQuantum, c);

        c.gridx = 0; c.gridy = 4; panelLeft.add(new JLabel("Algoritmo:"), c);
        c.gridx = 1; panelLeft.add(comboAlgoritmo, c);

        c.gridx = 0; c.gridy = 5; panelLeft.add(new JLabel("Velocidad ms/unidad:"), c);
        c.gridx = 1; panelLeft.add(txtVelocidad, c);

        JButton btnAgregar = new JButton("Agregar Proceso");
        JButton btnIniciar = new JButton("Iniciar Simulación");
        JButton btnDetener = new JButton("Detener");
        JButton btnReiniciar = new JButton("Reiniciar");

        JPanel panelBtns = new JPanel(new GridLayout(4,1,6,6));
        panelBtns.add(btnAgregar); panelBtns.add(btnIniciar); panelBtns.add(btnDetener); panelBtns.add(btnReiniciar);

        c.gridx = 0; c.gridy = 6; c.gridwidth = 2; panelLeft.add(panelBtns, c);

        add(panelLeft, BorderLayout.WEST);

        // Panel central: tablas
        JPanel panelCenter = new JPanel(new BorderLayout(6,6));
        panelCenter.setBorder(BorderFactory.createTitledBorder("Procesos"));

        JPanel tablas = new JPanel(new GridLayout(2,1,6,6));
        tablas.add(new JScrollPane(tablaCola));
        tablas.add(new JScrollPane(tablaHistorial));
        panelCenter.add(tablas, BorderLayout.CENTER);

        add(panelCenter, BorderLayout.CENTER);

        // Panel inferior: CPU y tiempo
        JPanel panelBottom = new JPanel(new BorderLayout(6,6));
        lblCPU.setFont(new Font("SansSerif", Font.BOLD, 18));
        panelBottom.add(lblCPU, BorderLayout.CENTER);
        panelBottom.add(lblTiempo, BorderLayout.EAST);

        add(panelBottom, BorderLayout.SOUTH);

        // Inicializar simulador (sin iniciar timer todavía)
        simulador = new Simulador(this);

        // ActionListeners
        btnAgregar.addActionListener(e -> onAgregar());
        btnIniciar.addActionListener(e -> onIniciar());
        btnDetener.addActionListener(e -> onDetener());
        btnReiniciar.addActionListener(e -> onReiniciar());

        // Mejora UX: cuando seleccionen otro algoritmo, habilitar/deshabilitar campo quantum
        comboAlgoritmo.addActionListener(e -> {
            String alg = (String) comboAlgoritmo.getSelectedItem();
            txtQuantum.setEnabled("Round Robin".equals(alg));
        });
        txtQuantum.setEnabled(false);
    }

    private void onAgregar() {
        try {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) nombre = "Proc";

            int cpu = Integer.parseInt(txtCPU.getText().trim());
            int llegada = Integer.parseInt(txtLlegada.getText().trim());
            int quantum = txtQuantum.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtQuantum.getText().trim());

            Proceso p = new Proceso(nombre, cpu, llegada, quantum);
            simulador.agregarProceso(p);

            // mostrar en tabla de cola (si llega en tiempo futuro, aparecerá cuando llegue también; pero
            // agregamos ahora para que el usuario vea que existe)
            modeloCola.addRow(new Object[]{p.getPid(), p.getNombre(), p.getTiempoCPU(), p.getLlegada(), p.getTiempoRestante()});

            // limpiar campos útiles para nueva entrada
            txtNombre.setText("");
            txtCPU.setText("");
            txtLlegada.setText("");
            // no limpiar quantum ni algoritmo, por UX
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifica los campos numéricos (CPU, Llegada, Quantum).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onIniciar() {
        try {
            int ms = Integer.parseInt(txtVelocidad.getText().trim());
            if (ms < 50) ms = 50; // límite inferior razonable
            simulador.setMilisegundosPorUnidad(ms);

            String alg = (String) comboAlgoritmo.getSelectedItem();
            int quantum = txtQuantum.getText().trim().isEmpty() ? 1 : Integer.parseInt(txtQuantum.getText().trim());
            simulador.configurarPlanificador(alg, quantum);

            simulador.iniciar();

            // deshabilitar edición mientras corre para evitar conflictos
            txtVelocidad.setEnabled(false);
            comboAlgoritmo.setEnabled(false);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Velocidad debe ser un número (ms por unidad).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDetener() {
        simulador.detener();
        txtVelocidad.setEnabled(true);
        comboAlgoritmo.setEnabled(true);
    }

    private void onReiniciar() {
        simulador.detener();
        modeloCola.setRowCount(0);
        modeloHistorial.setRowCount(0);
        lblCPU.setText("CPU libre");
        lblTiempo.setText("Tiempo: 0");

        // reconstruir simulador nuevo
        simulador = new Simulador(this);
        txtVelocidad.setEnabled(true);
        comboAlgoritmo.setEnabled(true);
    }

    // Métodos llamados desde Simulador para actualizar UI en cada tick
    public void agregarFilaCola(Proceso p) {
        // si ya existe pid en tabla, no añadir (evita duplicados)
        boolean existe = false;
        for (int r = 0; r < modeloCola.getRowCount(); r++) {
            if (((Integer)modeloCola.getValueAt(r,0)) == p.getPid()) { existe = true; break; }
        }
        if (!existe) modeloCola.addRow(new Object[]{p.getPid(), p.getNombre(), p.getTiempoCPU(), p.getLlegada(), p.getTiempoRestante()});
    }

    public void agregarFilaHistorial(Proceso p, int finishTime) {
        modeloHistorial.addRow(new Object[]{p.getPid(), p.getNombre(), p.getTiempoCPU(), p.getLlegada(), finishTime});
        // remover de cola si seguía allí
        refrescarTablaColaSimRemove(p.getPid());
    }

    public void actualizarProcesoEnCPU(Proceso p) {
        lblCPU.setText(String.format("Ejecutando: P%d - %s (R:%d)", p.getPid(), p.getNombre(), p.getTiempoRestante()));
    }

    public void limpiarCPU() {
        lblCPU.setText("CPU libre");
    }

    public void refrescarTablaCola(java.util.List<Proceso> lista) {
        // actualiza toda la tabla de cola con la lista proporcionada
        SwingUtilities.invokeLater(() -> {
            modeloCola.setRowCount(0);
            for (Proceso p : lista) {
                modeloCola.addRow(new Object[]{p.getPid(), p.getNombre(), p.getTiempoCPU(), p.getLlegada(), p.getTiempoRestante()});
            }
        });
    }

    private void refrescarTablaColaSimRemove(int pid) {
        SwingUtilities.invokeLater(() -> {
            for (int r = 0; r < modeloCola.getRowCount(); r++) {
                Integer val = (Integer) modeloCola.getValueAt(r,0);
                if (val == pid) { modeloCola.removeRow(r); break; }
            }
        });
    }

    public void actualizarTiempo(int tiempo) {
        SwingUtilities.invokeLater(() -> lblTiempo.setText("Tiempo: " + tiempo));
    }
}
