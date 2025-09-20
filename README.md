# 🖥️ Simulador de Planificación de Procesos

Este proyecto implementa un simulador de planificación de procesos en Java, utilizando una interfaz gráfica (GUI) basada en Swing. El simulador permite visualizar cómo se gestionan los procesos en una CPU 
según distintos algoritmos de planificación.

## 📦 Estructura del Proyecto

- `Simulador.java`: Clase principal que controla el flujo de la simulación.
- `Proceso.java`: Representa un proceso con atributos como tiempo de llegada, duración, etc.
- `planificacion/`: Contiene las clases que implementan los algoritmos de planificación:
  - `PlanificadorFCFS.java`
  - `PlanificadorSJF.java`
  - `PlanificadorSRTF.java`
  - `PlanificadorRR.java`
- `VentanaPrincipal.java`: Interfaz gráfica que muestra el estado de la simulación.

## ⚙️ Algoritmos Soportados

- **FCFS** (First Come First Served)
- **SJF** (Shortest Job First)
- **SRTF** (Shortest Remaining Time First)
- **Round Robin** (con quantum configurable)

## 🚀 Cómo Ejecutar

1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/tu-repositorio.git
2. Abre el proyecto en tu IDE (VS Code, IntelliJ, NetBeans).
3. Asegúrate de tener Java instalado (JDK 8 o superior).
4. Ejecuta la clase que contiene el método main, normalmente en VentanaPrincipal.java.
   
🧠 Funcionamiento del Simulador
- Cada unidad de tiempo es simulada con un javax.swing.Timer.
- Los procesos se agregan a una lista de entrada y se mueven a la cola de listos según su tiempo de llegada.
- El planificador selecciona el siguiente proceso a ejecutar.
- La interfaz gráfica se actualiza en tiempo real mostrando:
- Cola de listos
- Proceso en ejecución
- Historial de procesos terminados
- Tiempo actual

📌 Requisitos
- Java JDK 8+
- IDE con soporte para Swing
- Extensión de Java para VS Code (si usas VS Code)

  Ejemplos de Simulación:
  FCFS
  ![logo](https://github.com/DavidOro07/simulador_proceso/blob/9d9ea328fbcd3dfc9ca3514dddec2b4b87307008/fotos/FCFS0.jpeg)
  ![logo](https://github.com/DavidOro07/simulador_proceso/blob/9d9ea328fbcd3dfc9ca3514dddec2b4b87307008/fotos/FCFS.jpeg)
  ![logo](https://github.com/DavidOro07/simulador_proceso/blob/9d9ea328fbcd3dfc9ca3514dddec2b4b87307008/fotos/FCFS2.jpeg)
  ![logo](https://github.com/DavidOro07/simulador_proceso/blob/9d9ea328fbcd3dfc9ca3514dddec2b4b87307008/fotos/FCFS3.jpeg)

  SRTF
  ![logo](https://github.com/DavidOro07/simulador_proceso/blob/8fa34c7f0c3e492427b7013c6abe6faee071da3b/fotos/SRTF.jpeg)

  RR
  ![logo](https://github.com/DavidOro07/simulador_proceso/blob/8fa34c7f0c3e492427b7013c6abe6faee071da3b/fotos/RR.jpeg)
  
Contribuidores:
1. David Enrique Orozco Ajquijay     1990-23-12183
2. Sergio Josué Daniel Cúmez Pichiyá 1990-23-7837
3. Luis Francisco Aguirre Coj        1990-23-9406
4. Miguel Angel Raul Azmitia Ovalle  1990-23-8264
