
public class Proceso {
    private int id;
    private String nombre;
    private double cpu;
    private double memoria;
    private double disco;
    private int tiempo;

    public Proceso(int id, String nombre, double cpu, double memoria, double disco, int tiempo) {
        this.id = id;
        this.nombre = nombre;
        this.cpu = cpu;
        this.memoria = memoria;
        this.disco = disco;
        this.tiempo = tiempo;
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
}
