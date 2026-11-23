# Juegos que estimulan el pensamiento

Estas reglas se aplican al desarrollo de una aplicación Android con tres juegos: **2048, Buscaminas y KenKen**. Al incorporar estas guías al proyecto Android, copiar la carpeta `documentacion/` completa a la raíz del proyecto. Pedir al agente que lea `documentacion/AGENTS.md` antes de trabajar.

## Reglas obligatorias

1. Usar un solo módulo `app`, Kotlin, vistas XML y View Binding. No usar Compose, WebView, HTML, JavaScript, Data Binding ni un motor de juegos.
2. Organizar el código por funcionalidad. `MainActivity` es el catálogo; la `Activity`, el `ViewModel` y las reglas de cada juego se mantienen en su propio paquete.
3. La `Activity` recibe pulsaciones y muestra el estado. El `ViewModel` conserva la partida y aplica sus reglas sin acceder a `Context`, vistas, recursos ni View Binding.
4. Usar `ConstraintLayout` para la estructura de las pantallas. Construir cada tablero con un `LinearLayout` vertical y filas de `LinearLayout` horizontal. Declarar las plantillas de fila y casilla en XML e inflarlas con View Binding. No usar `GridLayout`.
5. Usar nombres descriptivos en español y sin tildes en el código. Los textos visibles deben respetar la ortografía del español y declararse en recursos cuando sean fijos.
6. Preferir funciones cortas, `if`, `when`, bucles y colecciones sencillas. No introducir capas, dependencias ni características sin una necesidad del proyecto y sin acordarlo con el equipo.
7. La aplicación debe funcionar sin Internet. No agregar cuentas ni base de datos.
8. Mantener `documentacion/` en el repositorio mientras se comparte la guía con la persona encargada de continuar el proyecto.
9. Usar Material Components de Google para vistas XML: tema Material 3, `MaterialCardView` para las tarjetas, `MaterialButton` para los controles y `MaterialAlertDialogBuilder` para el diálogo de 2048. Esto no sustituye `ConstraintLayout`, `LinearLayout` ni View Binding.
10. Entregar 2048 con gestos y botones de dirección. Mostrar el selector de controles al abrir el juego por primera vez y permitir cambiar el modo sin reiniciar la partida.
11. Construir las pantallas de **todos los juegos** según la [guía de componentes](docs/componentes.md). Encapsular los bloques visuales reutilizables en vistas compuestas y declararlas por su nombre de clase en XML. Configurar el contenido fijo mediante atributos XML, comunicar las acciones a la `Activity` y mantener las reglas de la partida en el `ViewModel` y en funciones Kotlin. No usar `<include>` en layouts.

## Guías

| Archivo | Consultar antes de |
|---|---|
| [Componentes](docs/componentes.md) | Crear la interfaz de un juego o reutilizar una parte de otra pantalla. |
| [Estructura](docs/estructura.md) | Crear o mover paquetes, clases, pantallas y recursos. |
| [Diseño](docs/diseno.md) | Cambiar tarjetas, tableros, estilos o controles. |
| [Juegos](docs/juegos.md) | Implementar o modificar las reglas de una partida. |
| [Sintaxis y ejemplos](docs/sintaxis.md) | Escribir Kotlin, conectar eventos o reutilizar XML. |
| [Pruebas](docs/pruebas.md) | Dar por terminado un juego o preparar su exposición. |
