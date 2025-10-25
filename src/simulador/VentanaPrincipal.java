package simulador;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import proceso.Proceso;

public class VentanaPrincipal extends JFrame {

    // ================== Componentes de entrada ==================
    private final JTextField txtNombre = new JTextField();
    private final JSpinner spinnerCPU = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    private final JSpinner spinnerLlegada = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
    private final JSpinner spinnerQuantum = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    private final JComboBox<String> comboAlgoritmo = new JComboBox<>(new String[]{"FCFS", "SJF", "SRTF", "Round Robin"});
    private final JSpinner spinnerVelocidad = new JSpinner(new SpinnerNumberModel(1000, 50, 5000, 50));

    // ================== Tablas ==================
    private final DefaultTableModel modeloCola = new DefaultTableModel(new String[]{"PID", "Nombre", "CPU", "Llegada", "Restante"}, 0);
    private final JTable tablaCola = new JTable(modeloCola);

    // Modelo y tabla específica para Round Robin (muestra quantum de cada proceso)
    private final DefaultTableModel modeloColaRR = new DefaultTableModel(new String[]{"PID", "Nombre", "CPU", "Llegada", "Restante", "Quantum"}, 0);
    private final JTable tablaColaRR = new JTable(modeloColaRR);
    private final JPanel panelColaWrapper = new JPanel(new CardLayout());
    // Panel de barras resumidas para RR (nombre + progreso)
    private final JPanel rrBarPanel = new JPanel();

    private final DefaultTableModel modeloHistorial = new DefaultTableModel(new String[]{"PID", "Nombre", "CPU", "Llegada", "FinishTime"}, 0);
    private final JTable tablaHistorial = new JTable(modeloHistorial);

    private final DefaultTableModel modeloMetricas = new DefaultTableModel(
            new String[]{"PID", "Nombre", "tf", "Tr", "Te", "Is"}, 0);
    private final JTable tablaMetricas = new JTable(modeloMetricas);

    // ================== Label de promedio ==================
    private final JLabel lblPromedioIS = new JLabel("Promedio IS: 0.00", SwingConstants.RIGHT);

    // ================== Panel inferior ==================
    private final JLabel lblCPU = new JLabel("CPU libre", SwingConstants.CENTER);
    private final JLabel lblTiempo = new JLabel("Tiempo: 0", SwingConstants.CENTER);

    private Simulador simulador;

    public VentanaPrincipal() {
        setTitle("Simulador de Planificación de Procesos");
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // ================== Panel izquierdo ==================
        JPanel panelLeft = new JPanel(new GridBagLayout());
        panelLeft.setBackground(new Color(255, 255, 250));
        panelLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Crear Proceso / Configuración"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; panelLeft.add(new JLabel("Nombre:"), c);
        c.gridx = 1; panelLeft.add(txtNombre, c);

        c.gridx = 0; c.gridy = 1; panelLeft.add(new JLabel("Tiempo CPU (u):"), c);
        c.gridx = 1; panelLeft.add(spinnerCPU, c);

        c.gridx = 0; c.gridy = 2; panelLeft.add(new JLabel("Llegada (u):"), c);
        c.gridx = 1; panelLeft.add(spinnerLlegada, c);

        c.gridx = 0; c.gridy = 3; panelLeft.add(new JLabel("Quantum (RR):"), c);
        c.gridx = 1; panelLeft.add(spinnerQuantum, c);

        c.gridx = 0; c.gridy = 4; panelLeft.add(new JLabel("Algoritmo:"), c);
        c.gridx = 1; panelLeft.add(comboAlgoritmo, c);

        c.gridx = 0; c.gridy = 5; panelLeft.add(new JLabel("Velocidad ms/unidad:"), c);
        c.gridx = 1; panelLeft.add(spinnerVelocidad, c);

        JButton btnAgregar = new JButton("Agregar Proceso");
        JButton btnIniciar = new JButton("Iniciar Simulación");
        JButton btnDetener = new JButton("Detener");
        JButton btnReiniciar = new JButton("Reiniciar");
        JButton btnReutilizar = new JButton("Reutilizar Procesos");

        JPanel panelBtns = new JPanel(new GridLayout(5, 1, 6, 6));
        panelBtns.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelBtns.add(btnAgregar);
        panelBtns.add(btnIniciar);
        panelBtns.add(btnDetener);
        panelBtns.add(btnReiniciar);
        panelBtns.add(btnReutilizar);

        c.gridx = 0; c.gridy = 6; c.gridwidth = 2;
        panelLeft.add(panelBtns, c);
        add(panelLeft, BorderLayout.WEST);

        // ================== Panel central ==================
        JPanel panelCenter = new JPanel(new BorderLayout(6, 6));
        panelCenter.setBackground(new Color(255, 255, 255));
        panelCenter.setBorder(BorderFactory.createTitledBorder("Procesos"));

    estiloTabla(tablaCola);
    estiloTabla(tablaColaRR);
    estiloTabla(tablaHistorial);
    estiloTabla(tablaMetricas);

        JPanel panelMetricas = new JPanel(new BorderLayout());
        panelMetricas.add(new JScrollPane(tablaMetricas), BorderLayout.CENTER);
        panelMetricas.add(lblPromedioIS, BorderLayout.SOUTH);
        panelMetricas.setBorder(BorderFactory.createTitledBorder("Tabla de eficiencia"));

    JPanel tablas = new JPanel(new GridLayout(3, 1, 6, 6));
    // envolver las vistas de cola en un CardLayout para alternar entre NORMAL y RR
    panelColaWrapper.add(new JScrollPane(tablaCola), "NORMAL");
    // crear vista compuesta para RR: tabla + barras resumen
    JPanel rrView = new JPanel(new BorderLayout(4,4));
    rrView.add(new JScrollPane(tablaColaRR), BorderLayout.CENTER);
    rrBarPanel.setLayout(new BoxLayout(rrBarPanel, BoxLayout.Y_AXIS));
    rrBarPanel.setBackground(new Color(250,250,245));
    JScrollPane rrBarsScroll = new JScrollPane(rrBarPanel);
    // barra más compacta: altura reducida para mostrar más procesos
    rrBarsScroll.setPreferredSize(new Dimension(100, 80));
    rrView.add(rrBarsScroll, BorderLayout.SOUTH);
    panelColaWrapper.add(rrView, "RR");
    tablas.add(panelColaWrapper);
    tablas.add(new JScrollPane(tablaHistorial));
    tablas.add(panelMetricas);

        panelCenter.add(tablas, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        // ================== Panel inferior ==================
        JPanel panelBottom = new JPanel(new BorderLayout(6, 6));
        panelBottom.setBackground(new Color(230, 230, 250));
        lblCPU.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblCPU.setForeground(new Color(34, 139, 34));
        panelBottom.add(lblCPU, BorderLayout.CENTER);
        lblTiempo.setFont(new Font("SansSerif", Font.BOLD, 16));
        panelBottom.add(lblTiempo, BorderLayout.EAST);
        add(panelBottom, BorderLayout.SOUTH);

        simulador = new Simulador(this);

        // ================== Listeners ==================
        btnAgregar.addActionListener(e -> onAgregar());
        btnIniciar.addActionListener(e -> onIniciar());
        btnDetener.addActionListener(e -> onDetener());
        btnReiniciar.addActionListener(e -> onReiniciar());
        btnReutilizar.addActionListener(e -> onReutilizarProcesos());

        comboAlgoritmo.addActionListener(e -> {
            String alg = (String) comboAlgoritmo.getSelectedItem();
            spinnerQuantum.setEnabled("Round Robin".equals(alg));
            // alternar vista de cola según algoritmo
            CardLayout cl = (CardLayout) panelColaWrapper.getLayout();
            if ("Round Robin".equals(alg)) {
                cl.show(panelColaWrapper, "RR");
            } else {
                cl.show(panelColaWrapper, "NORMAL");
            }
        });
        spinnerQuantum.setEnabled(false);

        for (JButton btn : new JButton[]{btnAgregar, btnIniciar, btnDetener, btnReiniciar, btnReutilizar}) {
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        }
    }

    private void estiloTabla(JTable tabla) {
        tabla.setFillsViewportHeight(true);
        tabla.setBackground(new Color(255, 255, 250));
        tabla.setForeground(Color.DARK_GRAY);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabla.getTableHeader().setBackground(new Color(100, 149, 237));
        tabla.getTableHeader().setForeground(Color.WHITE);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                // fila alternada
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(230, 230, 250));
                // si está seleccionada (resaltada por resaltarMasEficiente), usar verde claro
                if (isSelected) {
                    setBackground(new Color(144, 238, 144));
                }
                return this;
            }
        });
    }

    // ================== Métodos de simulación ==================
    private void onAgregar() {
        try {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) nombre = "Proc";

            int cpu = (Integer) spinnerCPU.getValue();
            int llegada = (Integer) spinnerLlegada.getValue();
            int quantum = spinnerQuantum.isEnabled() ? (Integer) spinnerQuantum.getValue() : 0;

            Proceso p = new Proceso(nombre, cpu, llegada, quantum);
            simulador.agregarProceso(p);
            // agregar fila según el algoritmo seleccionado (si es RR mostrará la columna Quantum)
            agregarFilaCola(p);

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
        modeloColaRR.setRowCount(0);
        reiniciarTablas();
        lblCPU.setText("CPU libre");
        lblCPU.setForeground(new Color(34, 139, 34));
        lblTiempo.setText("Tiempo: 0");

        simulador = new Simulador(this);
        spinnerVelocidad.setEnabled(true);
        comboAlgoritmo.setEnabled(true);
    }

    private void onReutilizarProcesos() {
        if (simulador.getColaListos().isEmpty() && simulador.getHistorial().isEmpty() && simulador.getTiempo() == 0) {
            JOptionPane.showMessageDialog(this, "No hay procesos para reutilizar. Agrega procesos primero.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int ms = (Integer) spinnerVelocidad.getValue();
            simulador.setMilisegundosPorUnidad(ms);

            String alg = (String) comboAlgoritmo.getSelectedItem();
            int quantum = spinnerQuantum.isEnabled() ? (Integer) spinnerQuantum.getValue() : 1;

            simulador.reiniciarConMismosProcesos(alg, quantum);
            reiniciarTablas();
            simulador.iniciar();

            spinnerVelocidad.setEnabled(false);
            comboAlgoritmo.setEnabled(false);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifica los campos numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void reiniciarTablas() {
        modeloHistorial.setRowCount(0);
        modeloMetricas.setRowCount(0);
        lblPromedioIS.setText("Promedio IS: 0.00");
    }

    // ---------- Mostrar diálogo para seleccionar nuevo algoritmo (NO muestra mensaje del proceso) ----------
    public void mostrarDialogoNuevoAlgoritmo() {
        // solo resaltamos el más eficiente en la tabla
        resaltarMasEficiente();

        JDialog dialogo = new JDialog(this, "Seleccionar Nuevo Algoritmo", true);
        dialogo.setSize(300, 150);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblAlgoritmo = new JLabel("Seleccionar algoritmo:");
        JComboBox<String> comboNuevoAlgoritmo = new JComboBox<>(new String[]{"FCFS", "SJF", "SRTF", "Round Robin"});
        JSpinner spinnerNuevoQuantum = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        JLabel lblQuantum = new JLabel("Quantum (RR):");
        JButton btnEjecutar = new JButton("Ejecutar");
        JButton btnSalir = new JButton("Salir");

        comboNuevoAlgoritmo.setSelectedItem(comboAlgoritmo.getSelectedItem());
        spinnerNuevoQuantum.setEnabled("Round Robin".equals(comboNuevoAlgoritmo.getSelectedItem()));

        comboNuevoAlgoritmo.addActionListener(e -> {
            spinnerNuevoQuantum.setEnabled("Round Robin".equals(comboNuevoAlgoritmo.getSelectedItem()));
        });

        c.gridx = 0; c.gridy = 0; dialogo.add(lblAlgoritmo, c);
        c.gridx = 1; dialogo.add(comboNuevoAlgoritmo, c);
        c.gridx = 0; c.gridy = 1; dialogo.add(lblQuantum, c);
        c.gridx = 1; dialogo.add(spinnerNuevoQuantum, c);

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnEjecutar);
        panelBotones.add(btnSalir);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        dialogo.add(panelBotones, c);

        for (JButton btn : new JButton[]{btnEjecutar, btnSalir}) {
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        }

        btnEjecutar.addActionListener(e -> {
            String nuevoAlgoritmo = (String) comboNuevoAlgoritmo.getSelectedItem();
            int quantum = spinnerNuevoQuantum.isEnabled() ? (Integer) spinnerNuevoQuantum.getValue() : 1;

            comboAlgoritmo.setSelectedItem(nuevoAlgoritmo);
            spinnerQuantum.setValue(quantum);
            simulador.reiniciarConMismosProcesos(nuevoAlgoritmo, quantum);
            reiniciarTablas();
            simulador.iniciar();
            dialogo.dispose();
        });

        btnSalir.addActionListener(e -> dialogo.dispose());
        dialogo.setVisible(true);
    }

    // ---------- Resalta la fila con mayor Is ----------
    public void resaltarMasEficiente() {
        if (modeloMetricas.getRowCount() == 0) return;

        int filaEficiente = -1;
        double maxIs = -1.0;

        for (int i = 0; i < modeloMetricas.getRowCount(); i++) {
            try {
                double is = Double.parseDouble(modeloMetricas.getValueAt(i, 5).toString());
                if (is > maxIs) {
                    maxIs = is;
                    filaEficiente = i;
                }
            } catch (NumberFormatException ignored) {}
        }

        if (filaEficiente != -1) {
            final int filaFinal = filaEficiente;
            SwingUtilities.invokeLater(() -> {
                tablaMetricas.clearSelection();
                tablaMetricas.setRowSelectionInterval(filaFinal, filaFinal);
                tablaMetricas.setSelectionBackground(new Color(144, 238, 144)); // verde claro
                tablaMetricas.repaint();
            });
        }
    }

    public void agregarFilaCola(Proceso p) {
        String alg = (String) comboAlgoritmo.getSelectedItem();
        if ("Round Robin".equals(alg)) {
            boolean existe = false;
            for (int r = 0; r < modeloColaRR.getRowCount(); r++) {
                Integer v = (Integer) modeloColaRR.getValueAt(r, 0);
                if (v != null && v == p.getPid()) { existe = true; break; }
            }
            if (!existe) {
                modeloColaRR.addRow(new Object[]{p.getPid(), p.getNombre(), p.getTiempoCPU(), p.getLlegada(), p.getTiempoRestante(), p.getQuantum()});
            }
        } else {
            boolean existe = false;
            for (int r = 0; r < modeloCola.getRowCount(); r++) {
                Integer v = (Integer) modeloCola.getValueAt(r, 0);
                if (v != null && v == p.getPid()) { existe = true; break; }
            }
            if (!existe) {
                modeloCola.addRow(new Object[]{p.getPid(), p.getNombre(), p.getTiempoCPU(), p.getLlegada(), p.getTiempoRestante()});
            }
        }
    }

    public void agregarFilaHistorial(Proceso p, int finishTime) {
        modeloHistorial.addRow(new Object[]{p.getPid(), p.getNombre(), p.getTiempoCPU(), p.getLlegada(), finishTime});
        refrescarTablaColaSimRemove(p.getPid());
    }

    public void agregarFilaMetricas(Proceso p, int finishTime) {
        int tf = finishTime;
        int ti = p.getLlegada();
        int t = p.getTiempoCPU();
        int Tr = tf - ti;
        int Te = Tr - t;
        double Is = (double) t / Tr;

        modeloMetricas.addRow(new Object[]{
                p.getPid(), p.getNombre(), tf, Tr, Te, String.format("%.2f", Is)
        });
        actualizarPromedioIS();
    }

    private void actualizarPromedioIS() {
        double suma = 0.0;
        int filas = modeloMetricas.getRowCount();

        for (int i = 0; i < filas; i++) {
            try {
                suma += Double.parseDouble(modeloMetricas.getValueAt(i, 5).toString());
            } catch (NumberFormatException e) {}
        }

        double promedio = filas > 0 ? suma / filas : 0.0;
        lblPromedioIS.setText(String.format("Promedio IS: %.2f", promedio));
    }

    public void actualizarProcesoEnCPU(Proceso p) {
        lblCPU.setText(String.format("Ejecutando: P%d - %s (R:%d)", p.getPid(), p.getNombre(), p.getTiempoRestante()));
        lblCPU.setForeground(new Color(220, 20, 60));
    }

    public void limpiarCPU() {
        lblCPU.setText("CPU libre");
        lblCPU.setForeground(new Color(34, 139, 34));
    }

    public void refrescarTablaCola(java.util.List<Proceso> lista) {
        SwingUtilities.invokeLater(() -> {
            String alg = (String) comboAlgoritmo.getSelectedItem();
            if ("Round Robin".equals(alg)) {
                modeloColaRR.setRowCount(0);
                for (Proceso p : lista) {
                    modeloColaRR.addRow(new Object[]{p.getPid(), p.getNombre(), p.getTiempoCPU(), p.getLlegada(), p.getTiempoRestante(), p.getQuantum()});
                }
                // actualizar vista de barras resumidas para RR
                updateRrBars(lista);
            } else {
                modeloCola.setRowCount(0);
                for (Proceso p : lista) {
                    modeloCola.addRow(new Object[]{p.getPid(), p.getNombre(), p.getTiempoCPU(), p.getLlegada(), p.getTiempoRestante()});
                }
                // limpiar barras RR cuando no es RR
                clearRrBars();
            }
        });
    }

    // MÉTODO QUE FALTABA (lo añadimos)
    public void refrescarTablaColaSimRemove(int pid) {
        // eliminar de la tabla normal
        for (int i = 0; i < modeloCola.getRowCount(); i++) {
            Integer val = (Integer) modeloCola.getValueAt(i, 0);
            if (val != null && val == pid) {
                modeloCola.removeRow(i);
                break;
            }
        }
        // eliminar de la tabla RR (si existe)
        for (int i = 0; i < modeloColaRR.getRowCount(); i++) {
            Integer val = (Integer) modeloColaRR.getValueAt(i, 0);
            if (val != null && val == pid) {
                modeloColaRR.removeRow(i);
                break;
            }
        }
    }

    // Actualiza el panel de barras resumen para RR (nombre + progreso completado)
    private void updateRrBars(java.util.List<Proceso> lista) {
        rrBarPanel.removeAll();
        if (lista == null || lista.isEmpty()) {
            rrBarPanel.revalidate();
            rrBarPanel.repaint();
            return;
        }

        for (Proceso p : lista) {
            // fila compacta para mostrar muchas entradas simultáneamente
            JPanel row = new JPanel(new BorderLayout(4, 4));
            row.setBackground(new Color(250,250,245));
            String title = String.format("P%d - %s", p.getPid(), p.getNombre());
            JLabel lbl = new JLabel(title);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lbl.setBorder(BorderFactory.createEmptyBorder(1,4,1,4));

            int total = Math.max(1, p.getTiempoCPU());
            int completed = Math.max(0, total - p.getTiempoRestante());
            JProgressBar bar = new JProgressBar(0, total);
            bar.setValue(completed);
            // barra más delgada
            bar.setPreferredSize(new Dimension(100, 12));
            bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
            bar.setStringPainted(true);
            bar.setString(String.format("%d/%d", completed, total));

            row.add(lbl, BorderLayout.WEST);
            row.add(bar, BorderLayout.CENTER);
            row.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
            // forzar tamaño máximo para que filas queden compactas
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            rrBarPanel.add(row);
        }

        rrBarPanel.revalidate();
        rrBarPanel.repaint();
    }

    private void clearRrBars() {
        rrBarPanel.removeAll();
        rrBarPanel.revalidate();
        rrBarPanel.repaint();
    }

    public void actualizarTiempo(int tiempo) {
        SwingUtilities.invokeLater(() -> lblTiempo.setText("Tiempo: " + tiempo));
    }
}
