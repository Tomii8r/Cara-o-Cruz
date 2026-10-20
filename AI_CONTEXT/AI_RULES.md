# 🪙 Reglas del Proyecto: Cara o Cruz

## 🛠️ Tech Stack & Herramientas
- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose (100% Declarativo, NADA de XML)
- **Arquitectura:** MVVM (Model-View-ViewModel) básico
- **Librerías externas:** Ninguna por ahora (Sin Hilt, sin Room, sin Retrofit). Mantener el proyecto liviano.

## 📐 Reglas de Código y Estilo
- **Simplicidad ante todo (KISS):** Prefiere soluciones simples y código directo sobre abstracciones complejas.
- **Compose Best Practices:**
    - Usa `StateFlow` o `State` en Compose para el manejo del estado.
    - Eleva el estado (*State Hoisting*) en los Composables cuando sea necesario.
    - Sigue las convenciones de nombres de Material 3.
- **Manejo de ViewModel:** Accede a los ViewModels usando el estándar `viewModel()` de Compose.

## 🎨 Enfoque Visual y UX
- Priorizar animaciones fluidas (`animateFloatAsState`, `Transition`, etc.).
- Incluir feedback háptico (vibración) y efectos de sonido en las acciones principales.

## 🚫 Lo que NO se debe hacer
- NO sugerir ni usar XML layouts (`R.layout`).
- NO agregar inyección de dependencias (Hilt/Koin) a menos que se pida explícitamente.
- NO agregar librerías de terceros si el SDK nativo de Android lo puede resolver.