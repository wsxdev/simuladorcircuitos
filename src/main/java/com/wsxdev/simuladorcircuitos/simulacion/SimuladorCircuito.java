package com.wsxdev.simuladorcircuitos.simulacion;

import com.wsxdev.simuladorcircuitos.modelo.*;
import org.apache.commons.math3.linear.*;

import java.util.*;

/**
 * Motor de simulación de circuitos usando análisis nodal
 */
public class SimuladorCircuito {
    private Circuito circuito;
    private Map<String, Integer> nodoIndices;
    private boolean simulacionActiva;
    
    public SimuladorCircuito(Circuito circuito) {
        this.circuito = circuito;
        this.nodoIndices = new HashMap<>();
        this.simulacionActiva = false;
    }
    
    // Constructor sin parámetros para compatibilidad con el controlador
    public SimuladorCircuito() {
        this.nodoIndices = new HashMap<>();
        this.simulacionActiva = false;
    }
    
    /**
     * Ejecuta la simulación del circuito
     */
    public ResultadosSimulacion simular() {
        try {
            // 1. Identificar nodos únicos
            Set<String> nodos = identificarNodos();
            if (nodos.size() < 2) {
                return new ResultadosSimulacion(false, "El circuito necesita al menos 2 nodos conectados");
            }
            
            // 2. Asignar índices a los nodos (tierra = 0)
            asignarIndicesNodos(nodos);
            
            // 3. Construir matriz de conductancias
            RealMatrix matrizConductancias = construirMatrizConductancias();
            
            // 4. Construir vector de corrientes
            RealVector vectorCorrientes = construirVectorCorrientes();
            
            // 5. Resolver sistema de ecuaciones
            DecompositionSolver solver = new LUDecomposition(matrizConductancias).getSolver();
            RealVector voltajesNodos = solver.solve(vectorCorrientes);
            
            // 6. Calcular corrientes en cada conexión
            calcularCorrientesConexiones(voltajesNodos);
            
            // 7. Actualizar estado visual de las conexiones
            actualizarEstadoVisual();
            
            simulacionActiva = true;
            return new ResultadosSimulacion(true, "Simulación exitosa", voltajesNodos, nodoIndices);
            
        } catch (Exception e) {
            simulacionActiva = false;
            return new ResultadosSimulacion(false, "Error en simulación: " + e.getMessage());
        }
    }
    
    /**
     * Simula un circuito específico (sobrecarga para el controlador)
     */
    public void simular(Circuito circuito) throws Exception {
        this.circuito = circuito;
        ResultadosSimulacion resultados = simular();
        if (!resultados.isExitoso()) {
            throw new Exception(resultados.getMensaje());
        }
    }
    
    /**
     * Valida que el circuito sea válido para simulación
     */
    public boolean validarCircuito(Circuito circuito) {
        if (circuito == null) return false;
        
        // Verificar que hay componentes
        if (circuito.getComponentes().isEmpty()) return false;
        
        // Verificar que hay al menos una fuente
        boolean tieneFuente = circuito.getComponentes().stream()
                .anyMatch(c -> c instanceof FuenteVoltaje);
        
        if (!tieneFuente) return false;
        
        // Verificar que hay conexiones
        if (circuito.getConexiones().isEmpty()) return false;
        
        return true;
    }
    
    /**
     * Obtiene los resultados de la última simulación
     */
    public String obtenerResultados() {
        if (!simulacionActiva || circuito == null) {
            return "No hay simulación activa";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESULTADOS DE SIMULACIÓN ===\n\n");
        
        // Información de componentes
        sb.append("COMPONENTES:\n");
        for (Componente comp : circuito.getComponentes()) {
            sb.append("- ").append(comp.getNombre()).append(" (").append(comp.getTipo()).append("): ");
            sb.append(comp.getValorPrincipal()).append(" ").append(comp.getUnidad()).append("\n");
        }
        
        // Información de conexiones
        sb.append("\nCONEXIONES:\n");
        for (Conexion conn : circuito.getConexiones()) {
            sb.append("- ").append(conn.toString()).append("\n");
        }
        
        return sb.toString();
    }
    
    private Set<String> identificarNodos() {
        Set<String> nodos = new HashSet<>();
        
        for (Conexion conexion : circuito.getConexiones()) {
            String nodoInicio = obtenerIdNodo(conexion.getPuntoInicio());
            String nodoFin = obtenerIdNodo(conexion.getPuntoFin());
            nodos.add(nodoInicio);
            nodos.add(nodoFin);
        }
        
        return nodos;
    }
    
    private String obtenerIdNodo(PuntoConexion punto) {
        return punto.getComponentePadre().getId() + "_" + punto.getNombre();
    }
    
    private void asignarIndicesNodos(Set<String> nodos) {
        nodoIndices.clear();
        int indice = 0;
        
        // Buscar un nodo de tierra (terminal negativo de fuente de voltaje)
        String nodoTierra = null;
        for (Componente componente : circuito.getComponentes()) {
            if (componente instanceof FuenteVoltaje) {
                FuenteVoltaje fuente = (FuenteVoltaje) componente;
                nodoTierra = componente.getId() + "_negativo";
                break;
            }
        }
        
        // Asignar tierra como índice 0
        if (nodoTierra != null && nodos.contains(nodoTierra)) {
            nodoIndices.put(nodoTierra, 0);
            nodos.remove(nodoTierra);
        }
        
        // Asignar índices al resto de nodos
        for (String nodo : nodos) {
            nodoIndices.put(nodo, ++indice);
        }
    }
    
    private RealMatrix construirMatrizConductancias() {
        int numNodos = nodoIndices.size();
        RealMatrix matriz = new Array2DRowRealMatrix(numNodos, numNodos);
        
        // Para cada conexión, agregar conductancia
        for (Conexion conexion : circuito.getConexiones()) {
            String nodoI = obtenerIdNodo(conexion.getPuntoInicio());
            String nodoJ = obtenerIdNodo(conexion.getPuntoFin());
            
            int i = nodoIndices.get(nodoI);
            int j = nodoIndices.get(nodoJ);
            
            // Calcular conductancia basada en el componente
            double conductancia = calcularConductancia(conexion);
            
            if (i > 0) { // No modificar fila de tierra
                matriz.addToEntry(i, i, conductancia);
                if (j > 0) {
                    matriz.addToEntry(i, j, -conductancia);
                }
            }
            
            if (j > 0) { // No modificar fila de tierra
                matriz.addToEntry(j, j, conductancia);
                if (i > 0) {
                    matriz.addToEntry(j, i, -conductancia);
                }
            }
        }
        
        return matriz;
    }
    
    private double calcularConductancia(Conexion conexion) {
        // Para cables, conductancia muy alta (resistencia muy baja)
        return 1000.0; // 1/0.001 Ω
    }
    
    private RealVector construirVectorCorrientes() {
        int numNodos = nodoIndices.size();
        RealVector vector = new ArrayRealVector(numNodos);
        
        // Para cada fuente de voltaje, agregar corriente
        for (Componente componente : circuito.getComponentes()) {
            if (componente instanceof FuenteVoltaje) {
                FuenteVoltaje fuente = (FuenteVoltaje) componente;
                String nodoPos = componente.getId() + "_positivo";
                String nodoNeg = componente.getId() + "_negativo";
                
                if (nodoIndices.containsKey(nodoPos)) {
                    int indicePos = nodoIndices.get(nodoPos);
                    if (indicePos > 0) {
                        // Método simplificado: inyectar corriente proporcional al voltaje
                        double corriente = fuente.getVoltaje() / 1.0; // Asumiendo resistencia interna de 1Ω
                        vector.setEntry(indicePos, corriente);
                    }
                }
            }
        }
        
        return vector;
    }
    
    private void calcularCorrientesConexiones(RealVector voltajesNodos) {
        for (Conexion conexion : circuito.getConexiones()) {
            String nodoI = obtenerIdNodo(conexion.getPuntoInicio());
            String nodoJ = obtenerIdNodo(conexion.getPuntoFin());
            
            double voltajeI = nodoIndices.containsKey(nodoI) ? 
                (nodoIndices.get(nodoI) > 0 ? voltajesNodos.getEntry(nodoIndices.get(nodoI)) : 0.0) : 0.0;
            double voltajeJ = nodoIndices.containsKey(nodoJ) ? 
                (nodoIndices.get(nodoJ) > 0 ? voltajesNodos.getEntry(nodoIndices.get(nodoJ)) : 0.0) : 0.0;
            
            double diferenciaPotencial = voltajeI - voltajeJ;
            double conductancia = calcularConductancia(conexion);
            double corriente = diferenciaPotencial * conductancia;
            
            conexion.setCorriente(corriente);
            conexion.setVoltaje(Math.abs(diferenciaPotencial));
        }
    }
    
    private void actualizarEstadoVisual() {
        for (Conexion conexion : circuito.getConexiones()) {
            conexion.setActiva(Math.abs(conexion.getCorriente()) > 0.001);
        }
    }
    
    public void detenerSimulacion() {
        simulacionActiva = false;
        // Resetear corrientes y voltajes
        for (Conexion conexion : circuito.getConexiones()) {
            conexion.setCorriente(0);
            conexion.setVoltaje(0);
            conexion.setActiva(false);
        }
    }
    
    public boolean isSimulacionActiva() {
        return simulacionActiva;
    }
    
    /**
     * Clase para encapsular los resultados de la simulación
     */
    public static class ResultadosSimulacion {
        private final boolean exitoso;
        private final String mensaje;
        private final RealVector voltajesNodos;
        private final Map<String, Integer> nodoIndices;
        
        public ResultadosSimulacion(boolean exitoso, String mensaje) {
            this(exitoso, mensaje, null, null);
        }
        
        public ResultadosSimulacion(boolean exitoso, String mensaje, RealVector voltajesNodos, Map<String, Integer> nodoIndices) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.voltajesNodos = voltajesNodos;
            this.nodoIndices = nodoIndices;
        }
        
        public boolean isExitoso() { return exitoso; }
        public String getMensaje() { return mensaje; }
        public RealVector getVoltajesNodos() { return voltajesNodos; }
        public Map<String, Integer> getNodoIndices() { return nodoIndices; }
    }
}
