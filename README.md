# Woofie Android MVP

Woofie es una app movil para aprender ingles con enfoque profesional.

## Flujo implementado (v1)

1. Onboarding: seleccion de profesion y nivel.
2. Home: saludo, foco profesional, racha y XP.
3. Leccion: quiz corto de 3 preguntas segun profesion.
4. Progreso: resumen de resultados y acciones para continuar.

## Profesiones iniciales

- Tecnologia e IT
- Salud
- Ventas y atencion

## Recursos visuales

- Paleta Woofie: `#8ECAE6`, `#219EBC`, `#023047`, `#FFB703`, `#FB8500`
- Mascota inicial: drawable `woofie_wolf`

## Notas tecnicas

- Stack UI: Kotlin + XML + Material Components
- Navegacion v1: `MainActivity` con reemplazo de `Fragment`
- Datos: repositorio en memoria (`WoofieRepository`)

## Siguiente iteracion sugerida

- Guardar perfil en `SharedPreferences`
- Mejorar mascota con ilustracion SVG
- Agregar animaciones de feedback (correcto/incorrecto)
- Agregar progreso diario real por fecha

