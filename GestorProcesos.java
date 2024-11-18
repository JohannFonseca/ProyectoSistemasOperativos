import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorProcesos {
    private static final double RAM_TOTAL = 32.0; // En GB
    private static final double CPU_TOTAL = 16.0; // En núcleos
    private static final double DISCO_TOTAL = 500.0; // En GB

    private double ramDisponible;
    private double cpuDisponible;
    private double discoDisponible;
    private List<Proceso> procesos;
    private List<Proceso> procesosEjecutados; // Lista para almacenar los procesos ejecutados
    private int tiempoActual; // Representa el tiempo actual del sistema

    public GestorProcesos() {
        this.ramDisponible = RAM_TOTAL;
        this.cpuDisponible = CPU_TOTAL;
        this.discoDisponible = DISCO_TOTAL;
        this.procesos = new ArrayList<>();
        this.procesosEjecutados = new ArrayList<>(); // Inicializar la lista de procesos ejecutados
        this.tiempoActual = 0; // Tiempo inicial del sistema
    }

    public void cargarProcesos(String rutaArchivo) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo));
        String linea;
        String[] nombres = {
            "Imprimir 2 hojas", "Fotocopia de cédula", "Scanner", "Cámara Web con micrófono",
            "Parlantes", "Zoom", "Discord", "Google", "Teams", "Visual Studio Code"
        };
        int index = 0;

        while ((linea = reader.readLine()) != null) {
            String[] partes = linea.split(",");
            int id = Integer.parseInt(partes[0].trim());
            double cpu = Double.parseDouble(partes[1].trim());
            double memoria = Double.parseDouble(partes[2].trim());
            double disco = Double.parseDouble(partes[3].trim());
            int tiempo = Integer.parseInt(partes[4].trim());

            // Asignar valores adicionales (tiempo de llegada y prioridad)
            int tiempoLlegada = (index % 10) * 2;  // Solo un ejemplo de asignación de tiempo de llegada
            int prioridad = (index % 5) + 1; // Prioridad entre 1 y 5

            procesos.add(new Proceso(id, nombres[index], cpu, memoria, disco, tiempo, tiempoLlegada, prioridad));
            index++;
        }

        reader.close();
    }

    public List<Proceso> getProcesos() {
        return procesos;
    }

    public boolean ejecutarProceso(Proceso proceso) {
        if (cpuDisponible >= proceso.getCpu() && 
            ramDisponible >= proceso.getMemoria() && 
            discoDisponible >= proceso.getDisco()) {
            
            // Reducir recursos disponibles
            cpuDisponible -= proceso.getCpu();
            ramDisponible -= proceso.getMemoria();
            discoDisponible -= proceso.getDisco();
            
            // Registrar el tiempo de salida del proceso
            proceso.setTiempoSalida(tiempoActual + proceso.getTiempo());
            
            // Agregar proceso a la lista de ejecutados
            procesosEjecutados.add(proceso);
            
            // Actualizar el estado del proceso
            proceso.setEstado("Ejecutando");
            return true;
        }
        return false; // Recursos insuficientes
    }

    public void liberarRecursos(Proceso proceso) {
        // Restaurar recursos
        cpuDisponible = Math.min(CPU_TOTAL, cpuDisponible + proceso.getCpu());
        ramDisponible = Math.min(RAM_TOTAL, ramDisponible + proceso.getMemoria());
        discoDisponible = Math.min(DISCO_TOTAL, discoDisponible + proceso.getDisco());
    }

    public double getRamDisponible() {
        return Math.max(0, ramDisponible); // Evitar valores negativos
    }

    public double getCpuDisponible() {
        return Math.max(0, cpuDisponible); // Evitar valores negativos
    }

    public double getDiscoDisponible() {
        return Math.max(0, discoDisponible); // Evitar valores negativos
    }

    public List<Proceso> getProcesosEjecutados() {
        return procesosEjecutados;
    }

    public double getRamTotal() {
        return RAM_TOTAL;
    }

    public void setRamDisponible(double ramDisponible) {
        this.ramDisponible = Math.max(0, Math.min(RAM_TOTAL, ramDisponible)); // Restringir entre 0 y RAM_TOTAL
    }

    public int getTiempoActual() {
        return tiempoActual;
    }

    public void avanzarTiempo() {
        tiempoActual++; // Avanzar el tiempo del sistema en 1 segundo
    }
}
