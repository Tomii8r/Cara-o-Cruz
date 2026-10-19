# PLAN — Cara o Cruz (MVP iteración 1)

> Documento operativo para agentes LLM. Lenguaje imperativo, sin ambigüedad. Leer completo antes de editar código. Actualizar este archivo al cerrar cada fase.

---

## 0. METADATA

```yaml
project: Cara o Cruz
module: app
package: com.example.caraocruz
language: Kotlin
ui: Jetpack Compose + Material3
architecture: MVVM + Clean Architecture (3 capas)
di: manual factory (sin Hilt)
scope_iteration_1: UI + estado + animación de giro
out_of_scope_iteration_1: sonido, hápticos, sensor de agitado, historial, modo oscuro
asset_source: usuario proveerá imágenes reales de moneda (cara/cruz)
placeholder_policy: placeholders vectoriales con nombres finales hasta recibir assets
```

---

## 1. DECISIONES CERRADAS

| ID | Decisión | Valor |
|---|---|---|
| D1 | Texto de resultado | Título `Cara` / `Cruz` + frase corta |
| D2 | Frase para CARA | `El destino dice que sí.` |
| D3 | Frase para CRUZ | `Mejor espera un poco.` |
| D4 | Alcance iteración 1 | MVP visual + lógica + animación |
| D5 | DI | Factory manual con `viewModelFactory` |
| D6 | Imágenes | El usuario las aporta; usar placeholders vectoriales ahora |
| D7 | Tema | Claro fijo, `dynamicColor = false` |
| D8 | Idioma UI | Español |
| D9 | Orientación | Portrait (layout debe tolerar pantallas pequeñas) |
| D10 | Botón | `Tirar la moneda` → `Girando...` → `Volver a tirar` |

**NO HACER en iteración 1:**
- No agregar Hilt, Koin, Dagger ni navegación.
- No agregar sonido, vibración ni `SensorManager`.
- No agregar base de datos ni red.
- No agregar comentarios al código.
- No usar color dinámico.
- No crear `README.md` ni documentación extra.

---

## 2. BASELINE TÉCNICO ACTUAL

```yaml
agp: 9.4.1
kotlin: 2.2.10
compose_bom: 2026.02.01
min_sdk: 24
target_sdk: 37
compile_sdk: 37
java: 11
```

Archivos existentes relevantes:

```
app/build.gradle.kts
app/src/main/AndroidManifest.xml
app/src/main/java/com/example/caraocruz/MainActivity.kt
app/src/main/java/com/example/caraocruz/ui/theme/{Color,Theme,Type}.kt
app/src/main/res/values/strings.xml
gradle/libs.versions.toml
```

`MainActivity.kt` actualmente contiene el template `Greeting`. Debe ser reemplazado.

---

## 3. REGLAS DE ARQUITECTURA

```text
presentation -> domain <- data
```

- `domain` NO importa Android ni Compose.
- `data` implementa contratos de `domain`.
- `presentation` no instancia `data` directamente; usa factory/DI.
- No usar `androidx.compose.*` fuera de `presentation` y `ui/theme`.
- Modelos de UI no se filtran a `domain`.
- Repositorio inyecta `kotlin.random.Random` para tests deterministas.
- Estado de UI inmutable y expuesto como `StateFlow`.
- Eventos de UI: funciones públicas del ViewModel, sin callbacks en el estado.

---

## 4. ÁRBOL DE ARCHIVOS OBJETIVO

Crear exactamente esta estructura (pueden omitirse archivos vacíos innecesarios):

```text
app/src/main/java/com/example/caraocruz/
├── MainActivity.kt
├── di/
│   └── CoinFlipViewModelFactory.kt
├── domain/
│   ├── model/
│   │   └── CoinSide.kt
│   ├── repository/
│   │   └── CoinFlipRepository.kt
│   └── usecase/
│       └── FlipCoinUseCase.kt
├── data/
│   └── repository/
│       └── RandomCoinFlipRepository.kt
├── presentation/
│   └── coin/
│       ├── CoinFlipConfig.kt
│       ├── CoinFlipUiState.kt
│       ├── CoinFlipViewModel.kt
│       ├── CoinFlipRoute.kt
│       ├── CoinFlipScreen.kt
│       └── components/
│           ├── CoinView.kt
│           ├── FlipButton.kt
│           ├── GradientBackground.kt
│           └── ResultSection.kt
└── ui/theme/
    ├── Color.kt      (modificar)
    ├── Theme.kt      (modificar)
    └── Type.kt       (modificar)

app/src/main/res/
├── drawable/
│   ├── coin_cara.xml            (placeholder temporal)
│   └── coin_cruz.xml            (placeholder temporal)
└── values/
    └── strings.xml              (modificar)

app/src/test/java/com/example/caraocruz/
├── domain/
│   ├── RandomCoinFlipRepositoryTest.kt
│   └── FlipCoinUseCaseTest.kt
└── presentation/
    └── CoinFlipViewModelTest.kt
```

---

## 5. CONTRATOS DE CÓDIGO

### 5.1 Domain

```kotlin
package com.example.caraocruz.domain.model

enum class CoinSide { CARA, CRUZ }
```

```kotlin
package com.example.caraocruz.domain.repository

import com.example.caraocruz.domain.model.CoinSide

interface CoinFlipRepository {
    fun flip(): CoinSide
}
```

```kotlin
package com.example.caraocruz.domain.usecase

import com.example.caraocruz.domain.model.CoinSide
import com.example.caraocruz.domain.repository.CoinFlipRepository

class FlipCoinUseCase(
    private val repository: CoinFlipRepository
) {
    operator fun invoke(): CoinSide = repository.flip()
}
```

### 5.2 Data

```kotlin
package com.example.caraocruz.data.repository

import com.example.caraocruz.domain.model.CoinSide
import com.example.caraocruz.domain.repository.CoinFlipRepository
import kotlin.random.Random

class RandomCoinFlipRepository(
    private val random: Random = Random.Default
) : CoinFlipRepository {
    override fun flip(): CoinSide =
        if (random.nextBoolean()) CoinSide.CARA else CoinSide.CRUZ
}
```

### 5.3 Presentation — estado

```kotlin
package com.example.caraocruz.presentation.coin

import com.example.caraocruz.domain.model.CoinSide

sealed interface CoinFlipUiState {
    data object Idle : CoinFlipUiState
    data class Flipping(val target: CoinSide, val flipId: Int) : CoinFlipUiState
    data class Result(val side: CoinSide, val flipId: Int) : CoinFlipUiState
}
```

### 5.4 Presentation — configuración

```kotlin
package com.example.caraocruz.presentation.coin

object CoinFlipConfig {
    const val FLIP_DURATION_MS: Long = 1500L
    const val FLIP_FULL_TURNS: Int = 5
    const val FLIP_EXTRA_HALF_TURN_DEG: Float = 180f
}
```

### 5.5 Presentation — ViewModel

```kotlin
package com.example.caraocruz.presentation.coin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caraocruz.domain.model.CoinSide
import com.example.caraocruz.domain.usecase.FlipCoinUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CoinFlipViewModel(
    private val flipCoinUseCase: FlipCoinUseCase,
    private val flipDurationMs: Long = CoinFlipConfig.FLIP_DURATION_MS
) : ViewModel() {

    private val _uiState = MutableStateFlow<CoinFlipUiState>(CoinFlipUiState.Idle)
    val uiState: StateFlow<CoinFlipUiState> = _uiState.asStateFlow()

    private var flipJob: Job? = null
    private var nextFlipId: Int = 0

    fun flipCoin() {
        if (_uiState.value is CoinFlipUiState.Flipping) return
        flipJob?.cancel()
        val target = flipCoinUseCase()
        val flipId = ++nextFlipId
        flipJob = viewModelScope.launch {
            _uiState.value = CoinFlipUiState.Flipping(target, flipId)
            delay(flipDurationMs)
            _uiState.value = CoinFlipUiState.Result(target, flipId)
        }
    }

    override fun onCleared() {
        flipJob?.cancel()
        super.onCleared()
    }
}
```

Invariantes:
- `flipId` estrictamente creciente.
- Nunca dos `Flipping` simultáneos.
- `Result.side == Flipping.target` para el mismo `flipId`.
- `Idle` solo antes de la primera tirada.

### 5.6 DI / Factory

```kotlin
package com.example.caraocruz.di

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.caraocruz.data.repository.RandomCoinFlipRepository
import com.example.caraocruz.domain.usecase.FlipCoinUseCase
import com.example.caraocruz.presentation.coin.CoinFlipViewModel

object CoinFlipViewModelFactory {
    val Factory = viewModelFactory {
        initializer {
            CoinFlipViewModel(
                flipCoinUseCase = FlipCoinUseCase(RandomCoinFlipRepository())
            )
        }
    }
}
```

### 5.7 MainActivity

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaraOCruzTheme {
                CoinFlipRoute()
            }
        }
    }
}
```

Sin `Scaffold` con padding por defecto. La pantalla maneja sus propios insets.

### 5.8 Route

```kotlin
@Composable
fun CoinFlipRoute(
    viewModel: CoinFlipViewModel = viewModel(factory = CoinFlipViewModelFactory.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CoinFlipScreen(
        uiState = uiState,
        onFlipClick = viewModel::flipCoin
    )
}
```

`CoinFlipScreen` debe ser stateless y tener `@Preview` con estados falsos.

---

## 6. MÁQUINA DE ESTADOS Y SINCRONIZACIÓN

```text
Idle
  └─ flipCoin() -> Flipping(target, flipId)
Flipping
  ├─ tap ignorado
  └─ delay(1500ms) -> Result(side=target, flipId)
Result
  └─ flipCoin() -> Flipping(nuevoTarget, flipId+1)
```

- `CoinView` anima al entrar en `Flipping` con clave `flipId`.
- La animación y el `delay` del ViewModel duran lo mismo (`1500ms`).
- Al llegar `Result` no se reinicia la animación (mismo `flipId`).
- El botón se deshabilita solo en `Flipping`.

---

## 7. ESPECIFICACIÓN UI

### 7.1 Tokens de diseño

```kotlin
val PurplePrimary = Color(0xFF6C4AE2)
val PurpleButtonStart = Color(0xFF7B4DFF)
val PurpleButtonEnd = Color(0xFF6338F0)
val PurpleSoft = Color(0xFFB8A7FF)
val BackgroundTop = Color(0xFFF7F4FF)
val BackgroundMiddle = Color(0xFFF3EDFF)
val BackgroundBottom = Color(0xFFFFF4F0)
val TextPrimary = Color(0xFF241C3B)
val TextSecondary = Color(0xFF8A8299)
val SurfaceTranslucent = Color(0xCCFFFFFF)
val GoldGlow = Color(0x66F2C94C)
```

- Tema claro fijo: `darkTheme = false`, `dynamicColor = false`.
- Tipografía:
  - Título: `FontFamily.Serif`, `FontWeight.Bold`, 36–40sp, `lineHeight` ~42sp.
  - Cuerpo: `FontFamily.Default`.
  - Labels: 11sp, `letterSpacing` 2sp, `FontWeight.Medium`, mayúsculas.
  - Footer: 10sp, `letterSpacing` 2.5sp.
- Radios: botón 20dp, badge 50%, moneda 50%.
- Espaciados base: 8 / 12 / 16 / 24 / 32 dp.

### 7.2 Estructura de layout

```text
Box (fillMaxSize)
├── GradientBackground()
└── Column (fillMaxSize, safeDrawing padding, horizontal 24dp)
    ├── Spacer / header
    │   ├── Badge circular con "✦"
    │   ├── Text "UNA PEQUEÑA SEÑAL"
    │   ├── Text "¿Qué dice el destino?"
    │   └── Text "Piensa tu pregunta y deja que la suerte decida."
    ├── Box (weight 1f, contentAlignment Center)
    │   └── CoinView(...)
    ├── ResultSection(...)   // altura reservada
    ├── FlipButton(...)
    └── Text footer
```

Requisitos:
- `BoxWithConstraints` o `Modifier.weight` para adaptar la moneda: `coinSize = min(maxWidth * 0.52f, 200.dp)`.
- En pantallas de poca altura usar `verticalScroll` o reducir espaciados; no debe haber overflow.
- `ResultSection` mantiene una altura mínima fija (~112dp) para evitar saltos.
- Toda la pantalla debe respetar `WindowInsets.safeDrawing`.

### 7.3 Fondo

- `Brush.verticalGradient` con los 3 colores de fondo.
- Dos círculos radiales:
  - lavanda arriba-derecha, radio ~320dp, alpha bajo;
  - durazno abajo-izquierda, radio ~300dp, alpha bajo.
- Implementar con `Canvas` o `Modifier.drawBehind` + `Brush.radialGradient`.

### 7.4 Moneda

- Tamaño ~190dp.
- `Image(painterResource(R.drawable.coin_cara))` y `coin_cruz`, recortadas con `CircleShape`.
- Placeholders vectoriales circulares con degradado dorado y texto `CARA` / `CRUZ`.
- Contenedor con:
  - `graphicsLayer { rotationY = angle; cameraDistance = 12f * density }`
  - sombra/glow dorado detrás (círculo difuminado).
- Mostrar una sola cara según el ángulo normalizado:
  - `normalized = ((angle % 360f) + 360f) % 360f`
  - mostrar `CARA` si `normalized < 90f || normalized > 270f`
  - mostrar `CRUZ` en caso contrario
  - la cara trasera se renderiza con `rotationY = 180f` para no quedar espejada.
- `contentDescription` dinámico según la cara visible.

### 7.5 Animación

Implementar en `CoinView` con `Animatable`:

```text
rotationY: Animatable<Float>
offsetY:   Animatable<Float>
scale:     Animatable<Float>
```

Disparo: `LaunchedEffect(flipId)` cuando el estado sea `Flipping`.

```text
targetAngle = rotationY.value + (360f * FLIP_FULL_TURNS) +
              (if (target == CRUZ) 180f else 0f)

rotationY.animateTo(targetAngle, tween(FLIP_DURATION_MS, easing = FastOutSlowInEasing))
offsetY: keyframes 0 -> -60dp (40%) -> 0 (100%)
scale:   keyframes 1f -> 1.08f (40%) -> 0.98f (75%) -> 1f (100%)
```

- No usar `animateFloatAsState` para `rotationY`; usar `Animatable` para controlar el ángulo acumulado.
- Al terminar, el ángulo debe dejar visible la cara de `target`.
- Idle: flotación sutil opcional (±4dp, 2s, `infiniteRepeatable`); no bloquear tests.
- Sombra bajo la moneda escala con el salto.

### 7.6 Resultado

- Visible solo en `Result`.
- `AnimatedVisibility` con `fadeIn + slideInVertically(initialOffsetY = { it / 3 })`.
- Contenido:
  - `LA MONEDA HA HABLADO`
  - `Cara` / `Cruz` (28sp, bold, `TextPrimary`)
  - frase según `D2`/`D3`
- No mostrar el resultado durante `Flipping` ni en `Idle`.

### 7.7 Botón

- `Button`/`Box` clickable con `interactionSource` para escala `0.97` al presionar.
- Altura 64dp, ancho completo, esquinas 20dp.
- Fondo `Brush.linearGradient(PurpleButtonStart, PurpleButtonEnd)`.
- Texto + icono de flecha (`Icons.AutoMirrored.Filled.ArrowForward` o vector propio).
- Estado:
  - `Idle` → `Tirar la moneda`
  - `Flipping` → `Girando...`, `enabled = false`, alpha 0.7
  - `Result` → `Volver a tirar`
- `contentDescription` explícito.

### 7.8 Accesibilidad

- Todos los `Image`/`Icon` con `contentDescription` o `null` si son decorativos.
- Contraste de texto ≥ 4.5:1.
- Tamaño mínimo táctil 48dp.
- No depender solo del color para comunicar el resultado.

---

## 8. RECURSOS

### 8.1 strings.xml

Añadir (sin borrar `app_name`):

```xml
<string name="signal_label">UNA PEQUEÑA SEÑAL</string>
<string name="title_question">¿Qué dice el destino?</string>
<string name="subtitle_question">Piensa tu pregunta y deja que la suerte decida.</string>
<string name="coin_has_spoken">LA MONEDA HA HABLADO</string>
<string name="result_cara">Cara</string>
<string name="result_cruz">Cruz</string>
<string name="phrase_cara">El destino dice que sí.</string>
<string name="phrase_cruz">Mejor espera un poco.</string>
<string name="action_flip">Tirar la moneda</string>
<string name="action_flipping">Girando...</string>
<string name="action_flip_again">Volver a tirar</string>
<string name="footer_intuition">CONFÍA EN TU INTUICIÓN</string>
<string name="cd_coin_cara">Moneda mostrando cara</string>
<string name="cd_coin_cruz">Moneda mostrando cruz</string>
<string name="cd_sparkle">Destello decorativo</string>
<string name="cd_flip_arrow">Continuar</string>
```

### 8.2 Placeholders de moneda

- Crear `res/drawable/coin_cara.xml` y `res/drawable/coin_cruz.xml` como vectores circulares dorados con texto/forma distintiva.
- Nombres base definitivos: `coin_cara`, `coin_cruz`.
- Cuando el usuario entregue PNG/WEBP:
  1. eliminar los XML placeholder con el mismo nombre base;
  2. agregar `coin_cara.png` / `coin_cruz.png` en `res/drawable/`;
  3. no cambiar código.
- Nunca mantener `.xml` y `.png` con el mismo nombre base.

---

## 9. DEPENDENCIAS

Modificar `gradle/libs.versions.toml`:

```toml
[versions]
lifecycle = "2.11.0"
coroutines = "1.10.2"

[libraries]
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }
androidx-compose-animation = { group = "androidx.compose.animation", name = "animation" }
androidx-compose-foundation = { group = "androidx.compose.foundation", name = "foundation" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }
```

Modificar `app/build.gradle.kts`:

```kotlin
implementation(libs.androidx.lifecycle.viewmodel.compose)
implementation(libs.androidx.lifecycle.runtime.compose)
implementation(libs.androidx.compose.animation)
implementation(libs.androidx.compose.foundation)
testImplementation(libs.kotlinx.coroutines.test)
```

No agregar dependencias fuera de esta lista sin justificar en este archivo.

---

## 10. FASES DE IMPLEMENTACIÓN

### FASE 1 — Dominio y datos
**Archivos:** `CoinSide.kt`, `CoinFlipRepository.kt`, `FlipCoinUseCase.kt`, `RandomCoinFlipRepository.kt`.
**Criterios:**
- [ ] `domain` no importa Android/Compose.
- [ ] `Random` inyectable.
- [ ] Compila sin dependencias nuevas.

### FASE 2 — Dependencias y ViewModel
**Archivos:** `libs.versions.toml`, `app/build.gradle.kts`, `CoinFlipConfig.kt`, `CoinFlipUiState.kt`, `CoinFlipViewModel.kt`, `CoinFlipViewModelFactory.kt`.
**Criterios:**
- [ ] `uiState` es `StateFlow`.
- [ ] `flipCoin()` ignora taps durante `Flipping`.
- [ ] `flipId` incrementa por tirada.
- [ ] `delay` usa `FLIP_DURATION_MS`.

### FASE 3 — Tema y recursos
**Archivos:** `Color.kt`, `Theme.kt`, `Type.kt`, `strings.xml`, `coin_cara.xml`, `coin_cruz.xml`.
**Criterios:**
- [ ] `dynamicColor = false` y tema claro.
- [ ] Paleta del punto 7.1 aplicada a `MaterialTheme`.
- [ ] Strings listados en 8.1 presentes.
- [ ] Placeholders compilan.

### FASE 4 — UI estática
**Archivos:** `MainActivity.kt`, `CoinFlipRoute.kt`, `CoinFlipScreen.kt`, `GradientBackground.kt`, `CoinView.kt` (sin animación final), `ResultSection.kt`, `FlipButton.kt`.
**Criterios:**
- [ ] Pantalla coincide con `UI.png` en estructura y jerarquía.
- [ ] Sin overflow en 360x640dp.
- [ ] Insets respetados.
- [ ] `CoinFlipScreen` stateless + previews `Idle`, `Flipping`, `Result`.

### FASE 5 — Animación e integración
**Archivos:** `CoinView.kt`, `CoinFlipScreen.kt`.
**Criterios:**
- [ ] Giro 3D con ángulo acumulado correcto.
- [ ] La cara final siempre coincide con `target`/`Result`.
- [ ] Salto, escala y sombra sincronizados.
- [ ] No se reinicia la animación al pasar de `Flipping` a `Result`.

### FASE 6 — Tests y verificación
**Archivos:** tests unitarios.
**Criterios:**
- [ ] Tests de repositorio, caso de uso y ViewModel pasan.
- [ ] `assembleDebug` exitoso.
- [ ] `lintDebug` sin errores nuevos.
- [ ] QA manual completado.

---

## 11. TESTS

### 11.1 `RandomCoinFlipRepositoryTest`
- Con `Random(seed)` fijo, dos seeds/llamadas producen ambos valores.
- El resultado siempre pertenece a `CoinSide.entries`.

### 11.2 `FlipCoinUseCaseTest`
- Repositorio fake devuelve `CARA`; el caso de uso retorna `CARA`.
- Repositorio fake devuelve `CRUZ`; el caso de uso retorna `CRUZ`.
- Se invoca el repositorio exactamente una vez.

### 11.3 `CoinFlipViewModelTest`
Usar `kotlinx-coroutines-test` (`StandardTestDispatcher`, `Dispatchers.setMain`).

- [ ] Estado inicial `Idle`.
- [ ] Tras `flipCoin()` y `runCurrent()`: `Flipping` con `target` válido.
- [ ] Tras `advanceTimeBy(1500)` + `runCurrent()`: `Result` con `side == target` y mismo `flipId`.
- [ ] Segundo `flipCoin()` durante `Flipping` no cambia `flipId` ni genera otro job.
- [ ] Tras `Result`, un nuevo `flipCoin()` genera `flipId` mayor.
- [ ] Usar `FlipCoinUseCase` con repositorio fake determinista.

### 11.4 UI tests (opcional iteración 2)
No obligatorios en esta iteración. No agregar dependencias extra por ellos.

---

## 12. VERIFICACIÓN

Ejecutar en orden desde la raíz del proyecto:

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:lintDebug
```

Si falla un comando, corregir antes de continuar. No reportar una fase como completada sin estos comandos exitosos.

QA manual:
- [ ] Primer arranque muestra `Idle` y botón `Tirar la moneda`.
- [ ] Tap → animación 1.5s → resultado correcto.
- [ ] Taps repetidos durante animación no alteran el resultado.
- [ ] Botón `Volver a tirar` funciona.
- [ ] Rotación de pantalla no rompe el estado.
- [ ] Textos no se cortan en pantallas pequeñas.
- [ ] Iconos de barra de estado visibles en fondo claro.

---

## 13. RIESGOS Y MITIGACIONES

| Riesgo | Mitigación |
|---|---|
| Conflicto de recursos al agregar PNG con XML placeholder | Eliminar el XML antes de agregar el PNG |
| Desincronía animación/resultado | Usar `FLIP_DURATION_MS` compartido y `flipId` como clave |
| Cara final espejada | Renderizar cara trasera con `rotationY = 180f` |
| Ángulo acumulado incorrecto | Acumular sobre `rotationY.value`, no reiniciar en 0 |
| Layout con overflow en pantallas pequeñas | `weight`, tamaño adaptativo y scroll vertical |
| AGP/Kotlin muy nuevos | Mantener versiones gestionadas por BOM y lifecycle 2.11.0 |

---

## 14. ITERACIONES FUTURAS (NO IMPLEMENTAR AHORA)

1. Reemplazar placeholders por imágenes definitivas y ajustar medidas.
2. Sonido con `SoundPool` (giro + aterrizaje).
3. Hápticos con `HapticFeedback`/`Vibrator`.
4. Shake con `SensorManager` + acelerómetro.
5. Modo oscuro.
6. Historial de tiradas (Room o DataStore).
7. Tests de UI Compose.

---

## 15. DEFINICIÓN DE TERMINADO (ITERACIÓN 1)

- [ ] Arquitectura y archivos del punto 4 existen.
- [ ] UI replica estructura y estilo de `UI.png`.
- [ ] Moneda gira 3D y muestra Cara/Cruz con la imagen correspondiente.
- [ ] Resultado textual `Cara`/`Cruz` + frase aparece al terminar.
- [ ] Botón cambia de estado correctamente.
- [ ] Tests unitarios pasan.
- [ ] `assembleDebug` y `lintDebug` exitosos.
- [ ] Este `PLAN.md` actualizado con estado final y desviaciones (si hubo).
