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

    public GestorProcesos() {
        this.ramDisponible = RAM_TOTAL;
        this.cpuDisponible = CPU_TOTAL;
        this.discoDisponible = DISCO_TOTAL;
        this.procesos = new ArrayList<>();
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

            procesos.add(new Proceso(id, nombres[index], cpu, memoria, disco, tiempo));
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
            return true;
        }
        return false; // Recursos insuficientes
    }

    public void liberarRecursos(Proceso proceso) {
        // Restaurar recursos
        cpuDisponible += proceso.getCpu();
        ramDisponible += proceso.getMemoria();
        discoDisponible += proceso.getDisco();
    }

    public double getRamDisponible() {
        return ramDisponible;
    }

    public double getCpuDisponible() {
        return cpuDisponible;
    }

    public double getDiscoDisponible() {
        return discoDisponible;
    }
}


