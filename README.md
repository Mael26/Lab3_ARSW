# Highlander Immortals: Multithreading & Synchronization Lab
**Presentado por:**

*Ana Gabriela Fiquitiva Poveda* y *Miguel Ángel Monroy Cárdenas*

Este proyecto implementa una simulación concurrente en Java diseñada para analizar y resolver problemas clásicos de programación paralela como condiciones de carrera, deadlocks y consistencia de datos en sistemas distribuidos.

Características Técnicas
Consistencia de Datos: Garantía del invariante de salud total mediante lógica de transferencia exacta entre hilos.

Sincronización de Barrera: Control de pausa implementado con Phaser para asegurar estados consistentes antes de lecturas globales.

Prevención de Deadlocks: Implementación de jerarquía de bloqueos (Orden Total) basada en identificadores únicos.

Gestión de Colecciones: Uso de CopyOnWriteArrayList para permitir modificaciones dinámicas (eliminación de hilos muertos) sin bloqueos de lectura.

Ciclo de Vida: Apagado ordenado del pool de hilos mediante ExecutorService
