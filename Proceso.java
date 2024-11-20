
public class Proceso {
    private int id;
    private String nombre;
    private double cpu;
    private double memoria;
    private double disco;
    private int tiempo;
    private String estado; 
    private int tiempoLlegada;  
    private int tiempoSalida;   
    private int prioridad;      

    // Constructor modificado para incluir los nuevos campos a futuro
    public Proceso(int id, String nombre, double cpu, double memoria, double disco, int tiempo, 
                   int tiempoLlegada, int prioridad) {
        this.id = id;
        this.nombre = nombre;
        this.cpu = cpu;
        this.memoria = memoria;
        this.disco = disco;
        this.tiempo = tiempo;
        this.estado = "En línea"; // Estado inicial
        this.tiempoLlegada = tiempoLlegada;
        this.tiempoSalida = -1;  // Inicialmente, no tiene tiempo de salida
        this.prioridad = prioridad;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getCpu() {
        return cpu;
    }

    public double getMemoria() {
        return memoria;
    }

    public double getDisco() {
        return disco;
    }

    public int getTiempo() {
        return tiempo;
    }

    public String getEstado() {
        return estado; 
    }

    // Método para cambiar el estado del proceso
    public void setEstado(String estado) {
        this.estado = estado; 
    }

    public int getTiempoLlegada() {
        return tiempoLlegada;
    }

    public int getTiempoSalida() {
        return tiempoSalida;
    }

    // Establece el tiempo de salida
    public void setTiempoSalida(int tiempoSalida) {
        this.tiempoSalida = tiempoSalida; 
    }

    public int getPrioridad() {
        return prioridad;
    }

    //De parte visual se ve esto
    @Override
    public String toString() {
        return "Proceso " + id + ", Aplicación = " + nombre + ", CPU = " + cpu + 
               ", Memoria = " + memoria + ", Disco = " + disco + ", Tiempo = " + tiempo + "s, Estado = " + estado +
               ", Tiempo Llegada = " + tiempoLlegada + ", Tiempo Salida = " + (tiempoSalida == -1 ? "En ejecución" : tiempoSalida) + 
               ", Prioridad = " + prioridad;
    }
}
