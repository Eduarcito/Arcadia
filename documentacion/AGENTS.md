# Juegos que estimulan el pensamiento

Estas reglas se aplican al desarrollo de una aplicación Android con seis juegos: **2048, Buscaminas, Petdoku, Snake, Number Connect y KenKen**, catálogo y ajustes. Al incorporar estas guías al proyecto Android, copiar la carpeta `documentacion/` completa a la raíz del proyecto. Pedir al agente que lea `documentacion/AGENTS.md` antes de trabajar.

## Reglas obligatorias

1. Usar un solo módulo `app`, Kotlin, vistas XML y View Binding. No usar Compose, WebView, HTML, JavaScript, Data Binding ni un motor de juegos.
2. Organizar el código por funcionalidad. `MainActivity` es el catálogo; la `Activity`, el `ViewModel` y las reglas de cada juego se mantienen en su propio paquete.
3. La `Activity` recibe pulsaciones y muestra el estado. El `ViewModel` conserva la partida y aplica sus reglas sin acceder a `Context`, vistas, recursos ni View Binding.
4. Usar `ConstraintLayout` para la estructura de las pantallas, directamente o mediante una vista compuesta que herede de esa clase. Construir los tableros de casillas con un `LinearLayout` vertical y filas de `LinearLayout` horizontal. Declarar las plantillas de fila y casilla en XML e inflarlas con View Binding. No usar `GridLayout`. Snake conserva su dibujo en `Canvas` para mantener la interpolación visual de la serpiente.
5. Usar nombres descriptivos en español y sin tildes en el código. Los textos visibles deben respetar la ortografía del español y declararse en recursos cuando sean fijos.
6. Preferir funciones cortas, `if`, `when`, bucles y colecciones sencillas. No introducir capas, dependencias ni características sin una necesidad del proyecto y sin acordarlo con el equipo.
7. La aplicación debe funcionar sin Internet. No agregar cuentas ni base de datos.
8. Mantener `documentacion/` en el repositorio mientras se comparte la guía con la persona encargada de continuar el proyecto.
9. Usar Material Components de Google para vistas XML: tema Material 3, `MaterialCardView` para las tarjetas, `MaterialButton` para los controles y `MaterialAlertDialogBuilder` para el diálogo de 2048. Esto no sustituye `ConstraintLayout`, `LinearLayout` ni View Binding.
10. Conservar los controles actuales: 2048 se maneja con gestos y acciones de accesibilidad, sin botones direccionales ni selector. Buscaminas usa toque, pulsación larga y selector para descubrir o marcar; Number Connect usa arrastre sobre números iguales; Petdoku usa toque simple, doble y arrastre; Snake usa deslizamientos continuos y toque para pausar. No cambiar estas interacciones al adaptar la estructura.
11. Construir las pantallas de **todos los juegos** según la [guía de componentes](docs/componentes.md). Encapsular los bloques visuales reutilizables en vistas compuestas y declararlas por su nombre de clase en XML. Configurar el contenido fijo mediante atributos XML, comunicar las acciones a la `Activity` y mantener las reglas de la partida en el `ViewModel` y en funciones Kotlin. No usar `<include>` en layouts.
12. Conservar el guardado automático existente de 2048, Buscaminas, Petdoku y Snake: JSON en `SharedPreferences` privadas `arcade_games`, con las mismas claves y formato. Number Connect utiliza la clave adicional `number_connect`. Al salir o cerrar la aplicación, las partidas se recuperan al volver. Reiniciar conserva los récords. KenKen conserva solo el estado durante la recreación de su pantalla, sin guardado persistente. Los ajustes se guardan por separado en `arcade_settings`; el modo claro u oscuro se aplica también a KenKen, sin añadirle audio ni vibración.
13. KenKen permite elegir desde **Controles** entre los modos **Incrementar**, **Decrementar** y **Borrar** (opción inicial), o el teclado numérico. Guardar solo esta preferencia como `kenken_controls` en `arcade_settings`; cambiarla no reinicia ni modifica la partida. No eliminar ninguno de los dos tipos de entrada.

## Guías

| Archivo | Consultar antes de |
|---|---|
| [Componentes](docs/componentes.md) | Crear la interfaz de un juego o reutilizar una parte de otra pantalla. |
| [Estructura](docs/estructura.md) | Crear o mover paquetes, clases, pantallas y recursos. |
| [Diseño](docs/diseno.md) | Cambiar tarjetas, tableros, estilos o controles. |
| [Juegos](docs/juegos.md) | Implementar o modificar las reglas de una partida. |
| [Sintaxis y ejemplos](docs/sintaxis.md) | Escribir Kotlin, conectar eventos o reutilizar XML. |
| [Pruebas](docs/pruebas.md) | Dar por terminado un juego o preparar su exposición. |
