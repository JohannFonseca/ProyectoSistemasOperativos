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
    private JButton botonVerProcesosEjecutados;
    private JLabel labelMemoriaDisponible;
    private JProgressBar barraMemoria;

    @SuppressWarnings("unused")
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
        String[] columnas = {"Nombre", "CPU (%)", "Bloque de Memoria (MB)", "Disco (%)", "Tiempo (s)", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaProcesos = new JTable(modeloTabla);
        tablaProcesos.setRowHeight(25);

        // Añadir renderizador personalizado al sistema, en parte visual
        tablaProcesos.setDefaultRenderer(Object.class, new CustomCellRenderer());

        cargarDatosTabla();
        JScrollPane scrollPane = new JScrollPane(tablaProcesos);

        // Botón de ejecutar proceso
        botonEjecutar = new JButton("Ejecutar");
        botonEjecutar.setBackground(new Color(34, 139, 34)); // Verde
        botonEjecutar.setForeground(Color.WHITE); // Letras blancas
        botonEjecutar.addActionListener(e -> ejecutarProceso());

        // Botón para ver procesos ejecutados
        botonVerProcesosEjecutados = new JButton("Ver Procesos Ejecutados");
        botonVerProcesosEjecutados.setBackground(new Color(34, 139, 34)); // Verde
        botonVerProcesosEjecutados.setForeground(Color.WHITE); // Letras blancas
        botonVerProcesosEjecutados.addActionListener(e -> mostrarProcesosEjecutados());

        // Barra de memoria disponible
        barraMemoria = new JProgressBar(0, 100);
        barraMemoria.setForeground(Color.RED); // Barra de color naranja
        actualizarBarraMemoria();

        // Etiqueta de memoria disponible
        labelMemoriaDisponible = new JLabel("Memoria disponible: " + gestor.getRamDisponible() + " GB");
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(labelMemoriaDisponible, BorderLayout.WEST);
        panelInferior.add(barraMemoria, BorderLayout.CENTER);

        // Agregar los botones al panel inferior
        JPanel panelBotones = new JPanel();
        panelBotones.add(botonVerProcesosEjecutados);
        panelBotones.add(botonEjecutar);

        panelInferior.add(panelBotones, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    //Parte visual de la tabla
    private void cargarDatosTabla() {
        for (Proceso proceso : gestor.getProcesos()) {
            modeloTabla.addRow(new Object[]{
                    proceso.getNombre(),
                    proceso.getCpu(),
                    proceso.getMemoria(),
                    proceso.getDisco(),
                    proceso.getTiempo(),
                    "En línea"
            });
        }
    }

    //En caso de no seleccionar un proceso
    private void ejecutarProceso() {
        int[] filasSeleccionadas = tablaProcesos.getSelectedRows();
        if (filasSeleccionadas.length == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione al menos un proceso para ejecutar");
            return;
        }

        //En casos que esten muchos, falten recursos, etc
        for (int fila : filasSeleccionadas) {
            Proceso proceso = gestor.getProcesos().get(fila);

            if (proceso.getEstado().equals("Ejecutando") || proceso.getEstado().equals("Finalizado")) {
                JOptionPane.showMessageDialog(this, "El proceso '" + proceso.getNombre() + "' ya está ejecutándose o finalizado.");
                continue;
            }

            if (proceso.getMemoria() > gestor.getRamDisponible()) {
                JOptionPane.showMessageDialog(this, "Recursos insuficientes para ejecutar el proceso: " + proceso.getNombre());
                continue;
            }

            if (gestor.ejecutarProceso(proceso)) {
                modeloTabla.setValueAt("Ejecutando", fila, 5);
                proceso.setEstado("Ejecutando");
                actualizarEstadoMemoria();

                new Timer(proceso.getTiempo() * 1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        gestor.liberarRecursos(proceso);
                        modeloTabla.setValueAt("Finalizado", fila, 5);
                        proceso.setEstado("Finalizado");
                        actualizarEstadoMemoria();
                        verificarProcesosFinalizados();
                    }
                }).start();
            }
        }
    }

    //Panel visual de procesos ejecutados
    private void mostrarProcesosEjecutados() {
        StringBuilder mensaje = new StringBuilder("Procesos ejecutados:\n");
        for (Proceso proceso : gestor.getProcesosEjecutados()) {
            // Mostrar los detalles del proceso
            mensaje.append("ID: ").append(proceso.getId()).append("\n")
                   .append("Aplicación: ").append(proceso.getNombre()).append("\n")
                   .append("Tiempo de llegada: ").append(proceso.getTiempoLlegada()).append("\n")
                   .append("Tiempo de salida: ").append(proceso.getTiempoSalida()).append("\n")
                   .append("Prioridad: ").append(proceso.getPrioridad()).append("\n\n");
        }
        JOptionPane.showMessageDialog(this, mensaje.toString(), "Procesos Ejecutados", JOptionPane.INFORMATION_MESSAGE);
    }

    //Panel de memoria disponible
    private void actualizarEstadoMemoria() {
        labelMemoriaDisponible.setText("Memoria disponible: " + gestor.getRamDisponible() + " GB");
        actualizarBarraMemoria();
    }

    //Actualizar memoria disponible en la barra
    private void actualizarBarraMemoria() {
        double ramDisponible = Math.max(0, gestor.getRamDisponible());
        double ramTotal = gestor.getRamTotal();
        int porcentajeUsado = (int) ((1 - (ramDisponible / ramTotal)) * 100);

        barraMemoria.setValue(porcentajeUsado);
        barraMemoria.setString("Uso de memoria: " + porcentajeUsado + "%");
        barraMemoria.setStringPainted(true);
    }

    //Verificar los procesos
    private void verificarProcesosFinalizados() {
        boolean todosFinalizados = true;
        for (Proceso proceso : gestor.getProcesos()) {
            if (!proceso.getEstado().equals("Finalizado")) {
                todosFinalizados = false;
                break;
            }
        }

        if (todosFinalizados) {
            restaurarMemoria();
        }
    }

    private void restaurarMemoria() {
        gestor.setRamDisponible(gestor.getRamTotal());
        SwingUtilities.invokeLater(() -> {
            actualizarEstadoMemoria();
        });
    }

    //CORRER EL PROGRAMA

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdministradorTareas frame = new AdministradorTareas();
            frame.setVisible(true);
        });
    }
}

//Modifcador de colores y cosas visuales de la tabla
class CustomCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (column == 5) {
            String estado = (String) value;
            switch (estado) {
                case "En línea":
                    c.setBackground(new Color(200, 255, 200));
                    break;
                case "Ejecutando":
                    c.setBackground(new Color(255, 255, 200));
                    break;
                case "Finalizado":
                    c.setBackground(new Color(255, 200, 200));
                    break;
                default:
                    c.setBackground(Color.WHITE);
                    break;
            }
        } else if (isSelected) {
            c.setBackground(new Color(173, 216, 230));
        } else {
            c.setBackground(Color.WHITE);
        }

        setForeground(Color.BLACK);
        return c;
    }
}