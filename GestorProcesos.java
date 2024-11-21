import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorProcesos {
    private static final int RAM_TOTAL_MB = 2048; // Memoria total en MB
    private static final int TAMANO_BLOQUE_MB = 32; // Tamaño mínimo de bloque en MB
    private static final double CPU_TOTAL = 16.0; // En núcleos
    private static final double DISCO_TOTAL = 500.0; // En GB

    private int ramDisponible; // Memoria disponible en MB
    private double cpuDisponible; // CPU disponible en
    private double discoDisponible; // Disco disponible en 
    private List<Proceso> procesos; // Lista para almacenar los procesos
    private List<Proceso> procesosEjecutados; // Lista para almacenar los procesos ejecutados
    private int tiempoActual; // Representa el tiempo actual del sistema, en segundos

    public GestorProcesos() {

        // Simular memoria inicial aleatoria (entre 1/4 y 3/4 de la memoria total)
        this.ramDisponible = (int) (RAM_TOTAL_MB * (0.25 + Math.random() * 0.5));
        this.cpuDisponible = CPU_TOTAL;
        this.discoDisponible = DISCO_TOTAL;
        this.procesos = new ArrayList<>();
        this.procesosEjecutados = new ArrayList<>(); // Inicializar la lista de procesos ejecutados
        this.tiempoActual = 0; // Tiempo inicial del sistema, en segundos
    }

    public void cargarProcesos(String rutaArchivo) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo));
        String linea;
        String[] nombres = {
            "Imprimir 2 hojas", "Fotocopia de cédula", "Scanner", "Cámara Web con micrófono",
            "Parlantes", "Zoom", "Discord", "Google", "Teams", "Visual Studio Code"
        };
        int index = 0;

        //variables y su tipo
        while ((linea = reader.readLine()) != null) {
            String[] partes = linea.split(",");
            int id = Integer.parseInt(partes[0].trim());
            double cpu = Double.parseDouble(partes[1].trim());
            double memoria = Double.parseDouble(partes[2].trim());
            double disco = Double.parseDouble(partes[3].trim());
            int tiempo = Integer.parseInt(partes[4].trim());

            // Redondear memoria al bloque más cercano de 32 MB
            memoria = Math.ceil(memoria / TAMANO_BLOQUE_MB) * TAMANO_BLOQUE_MB;

            int tiempoLlegada = (index % 10) * 2;  // Asignación de tiempo de llegada
            int prioridad = (index % 5) + 1; // Asignacion de prioridad entre 1 y 5

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
        return false; // En caso de recursos insuficientes
    }

    public void liberarRecursos(Proceso proceso) {

        // Restaurar recursos
        cpuDisponible = Math.min(CPU_TOTAL, cpuDisponible + proceso.getCpu());
        ramDisponible = Math.min(RAM_TOTAL_MB, ramDisponible + (int) proceso.getMemoria());
        discoDisponible = Math.min(DISCO_TOTAL, discoDisponible + proceso.getDisco());
    }

    public int getRamDisponible() {
        return Math.max(0, ramDisponible); // Evita los valores negativos
    }

    public double getCpuDisponible() {
        return Math.max(0, cpuDisponible); // Evita los valores negativos
    }

    public double getDiscoDisponible() {
        return Math.max(0, discoDisponible); // Evita los valores negativos
    }

    public List<Proceso> getProcesosEjecutados() {
        return procesosEjecutados;
    }

    public double getRamTotal() {
        return RAM_TOTAL_MB;
    }

    public void setRamDisponible(double ramDisponible) {

        // Restringe que no sea la RAM_TOTAL_MB
        this.ramDisponible = (int) Math.max(0, Math.min(RAM_TOTAL_MB, ramDisponible)); 
    }

    public int getTiempoActual() {
        return tiempoActual;
    }
    
    public void avanzarTiempo(int unidades) {
        tiempoActual += unidades;
        // Aquí se manejan procesos que deban finalizar al avanzar el tiempo
    }
    
    @Override
    public String toString() {

        //Esto va de manera visual
        return "Gestor de Procesos [RAM Disponible = " + getRamDisponible() + " MB, CPU Disponible = " + getCpuDisponible() +
                " núcleos, Disco Disponible = " + getDiscoDisponible() + " GB, Procesos cargados = " + procesos.size() + "]";
    }
    
}
