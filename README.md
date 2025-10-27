# Simulador de Circuitos

Proyecto Java Swing para diseñar y guardar circuitos simples de forma visual.

## Resumen
Este proyecto proporciona una interfaz gráfica para:
- Agregar componentes eléctricos (fuente de voltaje, resistencias, cables, etc.).
- Mover, rotar (tecla `R`) y eliminar (tecla `DEL`) componentes.
- Guardar y cargar circuitos mediante serialización en disco (`circuitos.dat`).

La UI principal está en [`com.wsxdev.simuladorcircuitos.vista.MainWindow`](src/main/java/com/wsxdev/simuladorcircuitos/vista/MainWindow.java) y el panel de edición en [`com.wsxdev.simuladorcircuitos.vista.PanelCircuito`](src/main/java/com/wsxdev/simuladorcircuitos/vista/PanelCircuito.java).

## Estructura del proyecto
- Código fuente: `src/main/java/com/wsxdev/simuladorcircuitos/`
  - Vista (UI)
    - [`MainWindow.java`](src/main/java/com/wsxdev/simuladorcircuitos/vista/MainWindow.java)
    - [`MainWindow.form`](src/main/java/com/wsxdev/simuladorcircuitos/vista/MainWindow.form)
    - [`PanelCircuito.java`](src/main/java/com/wsxdev/simuladorcircuitos/vista/PanelCircuito.java)
  - Controlador
    - [`CircuitoControlador.java`](src/main/java/com/wsxdev/simuladorcircuitos/controlador/CircuitoControlador.java)
  - Modelo
    - [`Circuito.java`](src/main/java/com/wsxdev/simuladorcircuitos/modelo/Circuito.java)
    - [`Componente.java`](src/main/java/com/wsxdev/simuladorcircuitos/modelo/Componente.java)
    - [`Resistencia.java`](src/main/java/com/wsxdev/simuladorcircuitos/modelo/Resistencia.java)
    - [`FuenteVoltaje.java`](src/main/java/com/wsxdev/simuladorcircuitos/modelo/FuenteVoltaje.java)
    - [`Conexion.java`](src/main/java/com/wsxdev/simuladorcircuitos/modelo/Conexion.java)
    - Enums: [`TipoComponente.java`](src/main/java/com/wsxdev/simuladorcircuitos/modelo/enums/TipoComponente.java)
  - Persistencia
    - [`ICircuitos.java`](src/main/java/com/wsxdev/simuladorcircuitos/persistencia/ICircuitos.java)
    - [`Circuitos.java`](src/main/java/com/wsxdev/simuladorcircuitos/persistencia/Circuitos.java)
- Recursos (imágenes): `src/main/resources/img/png/componentes/` y `src/main/resources/img/png/iconMenus/`
- Build: [`pom.xml`](pom.xml)
- Clase main de arranque: [`com.wsxdev.simuladorcircuitos.App`](src/main/java/com/wsxdev/simuladorcircuitos/App.java)

## Cómo ejecutar
Desde un IDE (recomendado):
- Importa el proyecto Maven y ejecuta la clase [`com.wsxdev.simuladorcircuitos.App`](src/main/java/com/wsxdev/simuladorcircuitos/App.java).

Desde línea de comandos:
1. Compilar:
    mvn compile
    mvn clean compile

2. Ejecutar (si tu classpath apunta a target/classes):
    java -cp target/classes com.wsxdev.simuladorcircuitos.App
    mvn exec:java -Dexec.mainClass="com.wsxdev.simuladorcircuitos.App"

Nota: si dependes de librerías externas, ejecuta desde el IDE o configura `maven-exec-plugin` para incluir dependencias.

## Persistencia
La persistencia está implementada en [`com.wsxdev.simuladorcircuitos.persistencia.Circuitos`](src/main/java/com/wsxdev/simuladorcircuitos/persistencia/Circuitos.java) que serializa una lista de objetos `Circuito` en `circuitos.dat`. La interfaz está en [`com.wsxdev.simuladorcircuitos.persistencia.ICircuitos`](src/main/java/com/wsxdev/simuladorcircuitos/persistencia/ICircuitos.java).

Las clases serializables principales:
- [`com.wsxdev.simuladorcircuitos.modelo.Circuito`](src/main/java/com/wsxdev/simuladorcircuitos/modelo/Circuito.java)
- [`com.wsxdev.simuladorcircuitos.modelo.Componente`](src/main/java/com/wsxdev/simuladorcircuitos/modelo/Componente.java)

## Controles y uso
- Clic derecho en el panel de edición para abrir el menú contextual y agregar componentes.
- Arrastrar para mover componentes.
- `R` rota el componente seleccionado (cuando está arrastrado).
- `DEL` elimina el componente seleccionado.
- Botón "Guardar" en la ventana principal llama a [`CircuitoControlador.guardarCircuito`](src/main/java/com/wsxdev/simuladorcircuitos/controlador/CircuitoControlador.java).

## Limitaciones conocidas / TODO
- La clase `Conexion` existe pero no está integrada en la UI ni es serializable (si se necesita persistir conexiones, hacerla Serializable).
- No hay validación de nombres al guardar. `Circuito` tiene constructor con lista de componentes y otro con nombre.
- Simulación eléctrica real (cálculos de corriente/voltaje) no implementada.
- Manejo de recursos/imágenes: asegurar que los PNG estén en `src/main/resources` para empaquetado correcto.

## Diseño y patrones
- Separación básica MVC:
  - Vista: [`MainWindow`](src/main/java/com/wsxdev/simuladorcircuitos/vista/MainWindow.java), [`PanelCircuito`](src/main/java/com/wsxdev/simuladorcircuitos/vista/PanelCircuito.java)
  - Controlador: [`CircuitoControlador`](src/main/java/com/wsxdev/simuladorcircuitos/controlador/CircuitoControlador.java)
  - Persistencia: [`ICircuitos`](src/main/java/com/wsxdev/simuladorcircuitos/persistencia/ICircuitos.java), [`Circuitos`](src/main/java/com/wsxdev/simuladorcircuitos/persistencia/Circuitos.java)

## Recursos
- Iconos de componentes: `src/main/resources/img/png/componentes/`
- Iconos del menú: `src/main/resources/img/png/iconMenus/`

## Archivos relevantes (rápido acceso)
- [`MainWindow.java`](src/main/java/com/wsxdev/simuladorcircuitos/vista/MainWindow.java)
- [`PanelCircuito.java`](src/main/java/com/wsxdev/simuladorcircuitos/vista/PanelCircuito.java)
- [`CircuitoControlador.java`](src/main/java/com/wsxdev/simuladorcircuitos/controlador/CircuitoControlador.java)
- [`Circuito.java`](src/main/java/com/wsxdev/simuladorcircuitos/modelo/Circuito.java)
- [`Componente.java`](src/main/java/com/wsxdev/simuladorcircuitos/modelo/Componente.java)
- [`Circuitos.java`](src/main/java/com/wsxdev/simuladorcircuitos/persistencia/Circuitos.java)
- [`ICircuitos.java`](src/main/java/com/wsxdev/simuladorcircuitos/persistencia/ICircuitos.java)
- [`App.java`](src/main/java/com/wsxdev/simuladorcircuitos/App.java)
- [`pom.xml`](pom.xml)