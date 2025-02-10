
## Escuela Colombiana de Ingeniería
### Arquitecturas de Software – ARSW


#### Ejercicio – programación concurrente, condiciones de carrera y sincronización de hilos. EJERCICIO INDIVIDUAL O EN PAREJAS.

##### Parte I – Antes de terminar la clase.

Control de hilos con wait/notify. Productor/consumidor.

1. Revise el funcionamiento del programa y ejecútelo. Mientras esto ocurren, ejecute jVisualVM y revise el consumo de CPU del proceso correspondiente. A qué se debe este consumo?, cual es la clase responsable?
2. Haga los ajustes necesarios para que la solución use más eficientemente la CPU, teniendo en cuenta que -por ahora- la producción es lenta y el consumo es rápido. Verifique con JVisualVM que el consumo de CPU se reduzca.
3. Haga que ahora el productor produzca muy rápido, y el consumidor consuma lento. Teniendo en cuenta que el productor conoce un límite de Stock (cuantos elementos debería tener, a lo sumo en la cola), haga que dicho límite se respete. Revise el API de la colección usada como cola para ver cómo garantizar que dicho límite no se supere. Verifique que, al poner un límite pequeño para el 'stock', no haya consumo alto de CPU ni errores.


##### Parte II. – Antes de terminar la clase.

Teniendo en cuenta los conceptos vistos de condición de carrera y sincronización, haga una nueva versión -más eficiente- del ejercicio anterior (el buscador de listas negras). En la versión actual, cada hilo se encarga de revisar el host en la totalidad del subconjunto de servidores que le corresponde, de manera que en conjunto se están explorando la totalidad de servidores. Teniendo esto en cuenta, haga que:

- La búsqueda distribuida se detenga (deje de buscar en las listas negras restantes) y retorne la respuesta apenas, en su conjunto, los hilos hayan detectado el número de ocurrencias requerido que determina si un host es confiable o no (_BLACK_LIST_ALARM_COUNT_).
- Lo anterior, garantizando que no se den condiciones de carrera.

##### Parte III. – Avance para el martes, antes de clase.

Sincronización y Dead-Locks.

![](http://files.explosm.net/comics/Matt/Bummed-forever.png)

1. Revise el programa “highlander-simulator”, dispuesto en el paquete edu.eci.arsw.highlandersim. Este es un juego en el que:

	* Se tienen N jugadores inmortales.
	* Cada jugador conoce a los N-1 jugador restantes.
	* Cada jugador, permanentemente, ataca a algún otro inmortal. El que primero ataca le resta M puntos de vida a su contrincante, y aumenta en esta misma cantidad sus propios puntos de vida.
	* El juego podría nunca tener un único ganador. Lo más probable es que al final sólo queden dos, peleando indefinidamente quitando y sumando puntos de vida.

2. Revise el código e identifique cómo se implemento la funcionalidad antes indicada. Dada la intención del juego, un invariante debería ser que la sumatoria de los puntos de vida de todos los jugadores siempre sea el mismo(claro está, en un instante de tiempo en el que no esté en proceso una operación de incremento/reducción de tiempo). Para este caso, para N jugadores, cual debería ser este valor?.

	Tenemos que en el codigo de la clase ControlFrame, que es la encargada de generar la vista de interfaz del juego se tienen 2 atributos que son constantes los cuales representan la vida y el daño por defecto de los jugadores inmortales
	
	![Image](https://github.com/user-attachments/assets/7e5158fe-1f36-4608-9635-9399e417b4ab)
	
	Entonces, para N jugadores, tendriamos que el valor del invariante (la sumatoria de los puntos de vida) es: 100*N 

3. Ejecute la aplicación y verifique cómo funcionan las opción ‘pause and check’. Se cumple el invariante?.

	Ejecutamos la aplicacion y tenemos una interfaz sencilla, donde para empezar solo crearemos 3 jugadores inmortales, el boton start nos permite iniciar el juego, tenemos una pantalla de registro de ataques en tiempo real y tenemos el boton pause and check que lo que hace es mostrar la vida actual de los jugadores y su sumatoria, sacamos 3 checkeos para ver si el invariante si se cumple.
	
	![Image](https://github.com/user-attachments/assets/8ec40fbe-0079-4c33-8e83-9337c573ef0e)
	
	![Image](https://github.com/user-attachments/assets/67ee62a0-bf84-45bc-870a-24d50cf5570d)
	
	![Image](https://github.com/user-attachments/assets/8181ed42-391b-47a4-9a5f-4bd81b8b882a)
	
	En los 3 casos tenemos que el invariante no se cumple, ya que la sumatoria de los puntos de vida seria de 300 siempre en este caso, pero tenemos que la sumatoria cambia con cada checkeo aunque si oscila entre el valor esperado.

4. Una primera hipótesis para que se presente la condición de carrera para dicha función (pause and check), es que el programa consulta la lista cuyos valores va a imprimir, a la vez que otros hilos modifican sus valores. Para corregir esto, haga lo que sea necesario para que efectivamente, antes de imprimir los resultados actuales, se pausen todos los demás hilos. Adicionalmente, implemente la opción ‘resume’.

	Para resolver la condicion carrera, primero se definio 2 variables que nos serviran para marcar el estado del hilo que involucra la clase Inmortal, sea que este en ejecucion, pausado o parado, y luego dentro del metodo `run()` se agrego un bloque sincronizado que me permita parar el hilo si la variable de pause este activa.
	
	![Image](https://github.com/user-attachments/assets/b85d99ae-ef10-4649-a266-11a103bce153)
	
	![Image](https://github.com/user-attachments/assets/ed5ddb7d-3dcc-442d-bc67-6330df0939fc)
	
	Con esto implementado, pasamos a la clase ControlFrame donde en el ActionListener del JButton "Pause And Check" agregaremos un ciclo donde por cada hilo ejecute el metodo de `pauseThread()` que solo cambia el estado de la variable pause para que se detenga, esto antes de realizar la suma de los puntos de vida, y por ultimo en el ActionListener del JButton "Resume" se hace el mismo ciclo para poner en reanudacion los hilos con el metodo `resumeThread()`
	
	![Image](https://github.com/user-attachments/assets/bf4a04cb-1d38-41fb-87c0-0797c0802168)
	
	![Image](https://github.com/user-attachments/assets/14f721cc-f2f5-440b-ab2c-d4d744f07064)

5. Verifique nuevamente el funcionamiento (haga clic muchas veces en el botón). Se cumple o no el invariante?.

	Al verificar el funcionamiento tenemos que ya muestra el valor esperado del invariante de forma mas seguida
	
	![Image](https://github.com/user-attachments/assets/600711c5-4b5c-4b7d-a71d-de678f697172)
	
	Pero al hacerlo varias veces de forma rapida tenemos que esto no siempre se cumple, ya que sigue presentadose algunas condiciones de carrera que hacen que el valor de la sumatoria aumente o disminuya depende del caso
	
	![Image](https://github.com/user-attachments/assets/9212763a-8ead-4447-9e06-d00a72f0f1e4)
	
	![Image](https://github.com/user-attachments/assets/dfe6074d-7289-40de-a3e6-6c1244baa805)

6. Identifique posibles regiones críticas en lo que respecta a la pelea de los inmortales. Implemente una estrategia de bloqueo que evite las condiciones de carrera. Recuerde que si usted requiere usar dos o más ‘locks’ simultáneamente, puede usar bloques sincronizados anidados:

	```java
	synchronized(locka){
		synchronized(lockb){
			…
		}
	}
	```

	Se identifico que la posible region critica en la ejecucion de la pelea de inmortales son las condiciones de carrera en la actualización de puntos de vida:
	
	* Si múltiples hilos (jugadores) están interactuando al mismo tiempo y modificando los puntos de vida de otros jugadores de forma concurrente sin un adecuado control de sincronización, podría ocurrir que los puntos de vida no se actualicen correctamente. Esto podría hacer que la suma total de los puntos de vida no sea precisa.
	  - **Ejemplo de condición de carrera:** Si dos jugadores atacan al mismo tiempo a dos jugadores diferentes y actualizan sus puntos de vida, y no hay una sincronización adecuada, uno de esos ataques podría perderse, lo que llevaría a una discrepancia en los cálculos de puntos de vida.



7. Tras implementar su estrategia, ponga a correr su programa, y ponga atención a si éste se llega a detener. Si es así, use los programas jps y jstack para identificar por qué el programa se detuvo.



8. Plantee una estrategia para corregir el problema antes identificado (puede revisar de nuevo las páginas 206 y 207 de _Java Concurrency in Practice_).

9. Una vez corregido el problema, rectifique que el programa siga funcionando de manera consistente cuando se ejecutan 100, 1000 o 10000 inmortales. Si en estos casos grandes se empieza a incumplir de nuevo el invariante, debe analizar lo realizado en el paso 4.

10. Un elemento molesto para la simulación es que en cierto punto de la misma hay pocos 'inmortales' vivos realizando peleas fallidas con 'inmortales' ya muertos. Es necesario ir suprimiendo los inmortales muertos de la simulación a medida que van muriendo. Para esto:
	* Analizando el esquema de funcionamiento de la simulación, esto podría crear una condición de carrera? Implemente la funcionalidad, ejecute la simulación y observe qué problema se presenta cuando hay muchos 'inmortales' en la misma. Escriba sus conclusiones al respecto en el archivo RESPUESTAS.txt.
	* Corrija el problema anterior __SIN hacer uso de sincronización__, pues volver secuencial el acceso a la lista compartida de inmortales haría extremadamente lenta la simulación.

11. Para finalizar, implemente la opción STOP.

<!--
### Criterios de evaluación

1. Parte I.
	* Funcional: La simulación de producción/consumidor se ejecuta eficientemente (sin esperas activas).

2. Parte II. (Retomando el laboratorio 1)
	* Se modificó el ejercicio anterior para que los hilos llevaran conjuntamente (compartido) el número de ocurrencias encontradas, y se finalizaran y retornaran el valor en cuanto dicho número de ocurrencias fuera el esperado.
	* Se garantiza que no se den condiciones de carrera modificando el acceso concurrente al valor compartido (número de ocurrencias).


2. Parte III.
	* Diseño:
		- Coordinación de hilos:
			* Para pausar la pelea, se debe lograr que el hilo principal induzca a los otros a que se suspendan a sí mismos. Se debe también tener en cuenta que sólo se debe mostrar la sumatoria de los puntos de vida cuando se asegure que todos los hilos han sido suspendidos.
			* Si para lo anterior se recorre a todo el conjunto de hilos para ver su estado, se evalúa como R, por ser muy ineficiente.
			* Si para lo anterior los hilos manipulan un contador concurrentemente, pero lo hacen sin tener en cuenta que el incremento de un contador no es una operación atómica -es decir, que puede causar una condición de carrera- , se evalúa como R. En este caso se debería sincronizar el acceso, o usar tipos atómicos como AtomicInteger).

		- Consistencia ante la concurrencia
			* Para garantizar la consistencia en la pelea entre dos inmortales, se debe sincronizar el acceso a cualquier otra pelea que involucre a uno, al otro, o a los dos simultáneamente:
			* En los bloques anidados de sincronización requeridos para lo anterior, se debe garantizar que si los mismos locks son usados en dos peleas simultánemante, éstos será usados en el mismo orden para evitar deadlocks.
			* En caso de sincronizar el acceso a la pelea con un LOCK común, se evaluará como M, pues esto hace secuencial todas las peleas.
			* La lista de inmortales debe reducirse en la medida que éstos mueran, pero esta operación debe realizarse SIN sincronización, sino haciendo uso de una colección concurrente (no bloqueante).

	

	* Funcionalidad:
		* Se cumple con el invariante al usar la aplicación con 10, 100 o 1000 hilos.
		* La aplicación puede reanudar y finalizar(stop) su ejecución.
		
		-->

<a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc/4.0/88x31.png" /></a><br />Este contenido hace parte del curso Arquitecturas de Software del programa de Ingeniería de Sistemas de la Escuela Colombiana de Ingeniería, y está licenciado como <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">Creative Commons Attribution-NonCommercial 4.0 International License</a>.
