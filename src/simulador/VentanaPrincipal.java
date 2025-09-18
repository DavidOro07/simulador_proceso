package simulador;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import proceso.Proceso;

/**
 * Ventana principal con diseño visual mejorado.
 */
public class VentanaPrincipal extends JFrame {
    private final JTextField txtNombre = new JTextField();
    private final JSpinner spinnerCPU = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    private final JSpinner spinnerLlegada = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
    private final JSpinner spinnerQuantum = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    private final JComboBox<String> comboAlgoritmo = new JComboBox<>(new String[]{"FCFS","SJF","SRTF","Round Robin"});
    private final JSpinner spinnerVelocidad = new JSpinner(new SpinnerNumberModel(1000, 50, 5000, 50));

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

        // ================= Panel izquierdo =================
        JPanel panelLeft = new JPanel(new GridBagLayout());
        panelLeft.setBackground(new Color(245,245,245));
        panelLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Crear Proceso / Configuración"),
                BorderFactory.createEmptyBorder(10,10,10,10)
        ));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0; c.gridy=0; panelLeft.add(new JLabel("Nombre:"), c);
        c.gridx=1; panelLeft.add(txtNombre, c);

        c.gridx=0; c.gridy=1; panelLeft.add(new JLabel("Tiempo CPU (u):"), c);
        c.gridx=1; panelLeft.add(spinnerCPU, c);

        c.gridx=0; c.gridy=2; panelLeft.add(new JLabel("Llegada (u):"), c);
        c.gridx=1; panelLeft.add(spinnerLlegada, c);

        c.gridx=0; c.gridy=3; panelLeft.add(new JLabel("Quantum (RR):"), c);
        c.gridx=1; panelLeft.add(spinnerQuantum, c);

        c.gridx=0; c.gridy=4; panelLeft.add(new JLabel("Algoritmo:"), c);
        c.gridx=1; panelLeft.add(comboAlgoritmo, c);

        c.gridx=0; c.gridy=5; panelLeft.add(new JLabel("Velocidad ms/unidad:"), c);
        c.gridx=1; panelLeft.add(spinnerVelocidad, c);

        JButton btnAgregar = new JButton("Agregar Proceso");
        JButton btnIniciar = new JButton("Iniciar Simulación");
        JButton btnDetener = new JButton("Detener");
        JButton btnReiniciar = new JButton("Reiniciar");

        JPanel panelBtns = new JPanel(new GridLayout(4,1,6,6));
        panelBtns.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        panelBtns.add(btnAgregar); panelBtns.add(btnIniciar); panelBtns.add(btnDetener); panelBtns.add(btnReiniciar);

        c.gridx=0; c.gridy=6; c.gridwidth=2; panelLeft.add(panelBtns, c);

        add(panelLeft, BorderLayout.WEST);

        // ================= Panel central =================
        JPanel panelCenter = new JPanel(new BorderLayout(6,6));
        panelCenter.setBackground(new Color(250,250,250));
        panelCenter.setBorder(BorderFactory.createTitledBorder("Procesos"));

        JPanel tablas = new JPanel(new GridLayout(2,1,6,6));
        tablas.add(new JScrollPane(tablaCola));
        tablas.add(new JScrollPane(tablaHistorial));
        panelCenter.add(tablas, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        // ================= Panel inferior =================
        JPanel panelBottom = new JPanel(new BorderLayout(6,6));
        panelBottom.setBackground(new Color(230,230,250));
        lblCPU.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblCPU.setForeground(new Color(34,139,34)); // verde libre
        panelBottom.add(lblCPU, BorderLayout.CENTER);
        lblTiempo.setFont(new Font("SansSerif", Font.BOLD, 16));
        panelBottom.add(lblTiempo, BorderLayout.EAST);
        add(panelBottom, BorderLayout.SOUTH);

        // ================= Tablas estilo =================
        estiloTabla(tablaCola);
        estiloTabla(tablaHistorial);

        // ================= Inicializar simulador =================
        simulador = new Simulador(this);

        // ================= Listeners =================
        btnAgregar.addActionListener(e -> onAgregar());
        btnIniciar.addActionListener(e -> onIniciar());
        btnDetener.addActionListener(e -> onDetener());
        btnReiniciar.addActionListener(e -> onReiniciar());

        comboAlgoritmo.addActionListener(e -> {
            String alg = (String)comboAlgoritmo.getSelectedItem();
            spinnerQuantum.setEnabled("Round Robin".equals(alg));
        });
        spinnerQuantum.setEnabled(false);

        // ================= Estilo botones =================
        for (JButton btn : new JButton[]{btnAgregar, btnIniciar, btnDetener, btnReiniciar}) {
            btn.setBackground(new Color(70,130,180));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        }
    }

    private void estiloTabla(JTable tabla) {
        tabla.setFillsViewportHeight(true);
        tabla.setBackground(new Color(255,255,240));
        tabla.setForeground(Color.DARK_GRAY);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabla.getTableHeader().setBackground(new Color(100,149,237));
        tabla.getTableHeader().setForeground(Color.WHITE);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(230,230,250));
                return this;
            }
        });
    }

    // ================= Métodos de simulación =================
    private void onAgregar() {
        try {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) nombre = "Proc";

            int cpu = (Integer) spinnerCPU.getValue();
            int llegada = (Integer) spinnerLlegada.getValue();
            int quantum = spinnerQuantum.isEnabled() ? (Integer) spinnerQuantum.getValue() : 0;

            Proceso p = new Proceso(nombre, cpu, llegada, quantum);
            simulador.agregarProceso(p);
            modeloCola.addRow(new Object[]{p.getPid(), p.getNombre(), p.getTiempoCPU(), p.getLlegada(), p.getTiempoRestante()});

            txtNombre.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifica los campos numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onIniciar() {
        try {
            int ms = (Integer) spinnerVelocidad.getValue();
            simulador.setMilisegundosPorUnidad(ms);

            String alg = (String) comboAlgoritmo.getSelectedItem();
            int quantum = spinnerQuantum.isEnabled() ? (Integer) spinnerQuantum.getValue() : 1;
            simulador.configurarPlanificador(alg, quantum);
            simulador.iniciar();

            spinnerVelocidad.setEnabled(false);
            comboAlgoritmo.setEnabled(false);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Velocidad debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDetener() {
        simulador.detener();
        spinnerVelocidad.setEnabled(true);
        comboAlgoritmo.setEnabled(true);
    }

    private void onReiniciar() {
        simulador.detener();
        modeloCola.setRowCount(0);
        modeloHistorial.setRowCount(0);
        lblCPU.setText("CPU libre");
        lblCPU.setForeground(new Color(34,139,34));
        lblTiempo.setText("Tiempo: 0");

        simulador = new Simulador(this);
        spinnerVelocidad.setEnabled(true);
        comboAlgoritmo.setEnabled(true);
    }

    public void agregarFilaCola(Proceso p) {
        boolean existe = false;
        for (int r = 0; r < modeloCola.getRowCount(); r++) {
            if (((Integer)modeloCola.getValueAt(r,0)) == p.getPid()) { existe = true; break; }
        }
        if (!existe) modeloCola.addRow(new Object[]{p.getPid(), p.getNombre(), p.getTiempoCPU(), p.getLlegada(), p.getTiempoRestante()});
    }

    public void agregarFilaHistorial(Proceso p, int finishTime) {
        modeloHistorial.addRow(new Object[]{p.getPid(), p.getNombre(), p.getTiempoCPU(), p.getLlegada(), finishTime});
        refrescarTablaColaSimRemove(p.getPid());
    }

    public void actualizarProcesoEnCPU(Proceso p) {
        lblCPU.setText(String.format("Ejecutando: P%d - %s (R:%d)", p.getPid(), p.getNombre(), p.getTiempoRestante()));
        lblCPU.setForeground(new Color(220,20,60)); // rojo ejecutando
    }

    public void limpiarCPU() {
        lblCPU.setText("CPU libre");
        lblCPU.setForeground(new Color(34,139,34)); // verde libre
    }

    public void refrescarTablaCola(java.util.List<Proceso> lista) {
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
