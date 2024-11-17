import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdministradorTareas extends JFrame {
    private JTable tablaProcesos;
    private DefaultTableModel modeloTabla;
    private GestorProcesos gestor;
    private JButton botonEjecutar;
    private JLabel labelMemoriaDisponible;

    public AdministradorTareas() {
        setTitle("Administrador de Tareas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLayout(new BorderLayout());

        // Instanciar el gestor de procesos
        gestor = new GestorProcesos();
        try {
            gestor.cargarProcesos("procesos.txt");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los procesos: " + e.getMessage());
        }

        // Crear modelo de tabla
        String[] columnas = {"Nombre", "CPU (%)", "Memoria (MB)", "Disco (%)", "Tiempo (s)", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaProcesos = new JTable(modeloTabla);
        tablaProcesos.setRowHeight(25);

        // Añadir renderizador personalizado
        tablaProcesos.setDefaultRenderer(Object.class, new CustomCellRenderer());

        cargarDatosTabla();

        JScrollPane scrollPane = new JScrollPane(tablaProcesos);

        // Botón de ejecutar
        botonEjecutar = new JButton("Ejecutar");
        botonEjecutar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarProceso();
            }
        });

        // Etiqueta de memoria disponible
        labelMemoriaDisponible = new JLabel("Memoria disponible: " + gestor.getRamDisponible() + " GB");
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(labelMemoriaDisponible, BorderLayout.WEST);
        panelInferior.add(botonEjecutar, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void cargarDatosTabla() {
        for (Proceso proceso : gestor.getProcesos()) {
            modeloTabla.addRow(new Object[]{
                proceso.getNombre(),
                proceso.getCpu(),
                proceso.getMemoria(),
                proceso.getDisco(),
                proceso.getTiempo(),
                "En línea" // Estado inicial
            });
        }
    }

    private void ejecutarProceso() {
        int filaSeleccionada = tablaProcesos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proceso para ejecutar");
            return;
        }

        Proceso proceso = gestor.getProcesos().get(filaSeleccionada);
        if (gestor.ejecutarProceso(proceso)) {
            modeloTabla.setValueAt("Ejecutando", filaSeleccionada, 5);
            labelMemoriaDisponible.setText("Memoria disponible: " + gestor.getRamDisponible() + " GB");

            // Simular tiempo de ejecución
            new Timer(proceso.getTiempo() * 1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gestor.liberarRecursos(proceso);
                    modeloTabla.setValueAt("Finalizado", filaSeleccionada, 5);
                    labelMemoriaDisponible.setText("Memoria disponible: " + gestor.getRamDisponible() + " GB");
                }
            }).start();
        } else {
            JOptionPane.showMessageDialog(this, "Recursos insuficientes para ejecutar este proceso");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdministradorTareas frame = new AdministradorTareas();
            frame.setVisible(true);
        });
    }
}

class CustomCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Limitar colores únicamente a la columna "Estado" (índice 5)
        if (column == 5) {
            String estado = (String) value;
            switch (estado) {
                case "En línea":
                    c.setBackground(new Color(200, 255, 200)); // Verde claro
                    break;
                case "Ejecutando":
                    c.setBackground(new Color(255, 255, 200)); // Amarillo claro
                    break;
                case "Finalizado":
                    c.setBackground(new Color(255, 200, 200)); // Rojo claro
                    break;
                default:
                    c.setBackground(Color.WHITE); // Fondo blanco por defecto
                    break;
            }
        } else if (isSelected) {
            c.setBackground(new Color(173, 216, 230)); // Celeste pastel claro si está seleccionada
        } else {
            c.setBackground(Color.WHITE); // Fondo blanco para otras celdas
        }

        setForeground(Color.BLACK); // Texto negro para mejor contraste
        return c;
    }
}



