# PLAN — Cara o Cruz

> Documento operativo para agentes LLM. Arquitectura simplificada según `AI_RULES.md`: MVVM básico + Compose + KISS.

---

## 0. METADATA

```yaml
project: Cara o Cruz
package: com.example.caraocruz
language: Kotlin
ui: Jetpack Compose + Material3
architecture: MVVM básico
di: ninguna (ViewModel sin dependencias)
tests: no obligatorios
estado: UI mínima funcional; falta diseño visual y animación
```

---

## 1. REGLAS DEL PROYECTO

Leer `AI_CONTEXT/AI_RULES.md` antes de tocar código. Resumen:

- Kotlin + Compose 100% declarativo. Sin XML layouts ni `R.layout`.
- MVVM básico. Sin capas domain/data/usecase.
- Sin Hilt, Koin, Room, Retrofit ni librerías de terceros.
- `StateFlow` en el ViewModel, `viewModel()` estándar en Compose.
- State hoisting en los composables.
- KISS: código directo, sin abstracciones innecesarias.
- Animaciones fluidas + hápticos + sonido (pendientes de implementar).

---

## 2. ARQUITECTURA ACTUAL

```text
MainActivity
  └── CaraOCruzTheme
        └── CoinFlipScreen(uiState, onFlipClick)
              ↑
        CoinFlipViewModel
              └── Random.Default
```

- `MainActivity` obtiene el ViewModel con `viewModel()`.
- `MainActivity` recolecta el `StateFlow` con `collectAsState()`.
- `CoinFlipScreen` es stateless y solo recibe estado + evento.
- `CoinFlipViewModel` decide Cara/Cruz y maneja los tiempos.
- `CoinSide` y `CoinFlipUiState` viven en `presentation/coin`.

---

## 3. ÁRBOL DE ARCHIVOS

```text
app/src/main/java/com/example/caraocruz/
├── MainActivity.kt
├── presentation/
│   └── coin/
│       ├── CoinFlipUiState.kt
│       ├── CoinFlipViewModel.kt
│       └── CoinFlipScreen.kt
└── ui/theme/
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt

app/src/main/res/
├── values/strings.xml
└── drawable/coin_cara.xml        (pendiente)
    drawable/coin_cruz.xml        (pendiente)
```

No crear carpetas `domain`, `data`, `di` ni factories.

---

## 4. CONTRATOS

### 4.1 Estado

```kotlin
enum class CoinSide { CARA, CRUZ }

sealed interface CoinFlipUiState {
    data object Idle : CoinFlipUiState
    data class Flipping(val target: CoinSide, val flipId: Int) : CoinFlipUiState
    data class Result(val side: CoinSide, val flipId: Int) : CoinFlipUiState
}
```

### 4.2 ViewModel

```kotlin
const val FLIP_DURATION_MS = 1500L

class CoinFlipViewModel : ViewModel() {
    private val random = Random.Default
    private val _uiState = MutableStateFlow<CoinFlipUiState>(CoinFlipUiState.Idle)
    val uiState: StateFlow<CoinFlipUiState> = _uiState.asStateFlow()

    fun flipCoin() {
        if (_uiState.value is CoinFlipUiState.Flipping) return
        val target = if (random.nextBoolean()) CoinSide.CARA else CoinSide.CRUZ
        val flipId = ++nextFlipId
        viewModelScope.launch {
            _uiState.value = CoinFlipUiState.Flipping(target, flipId)
            delay(FLIP_DURATION_MS)
            _uiState.value = CoinFlipUiState.Result(target, flipId)
        }
    }
}
```

Invariantes:
- `flipId` creciente.
- Un solo `Flipping` a la vez.
- `Result.side == Flipping.target` para el mismo `flipId`.
- `Idle` solo antes de la primera tirada.

### 4.3 MainActivity

```kotlin
setContent {
    CaraOCruzTheme {
        val viewModel: CoinFlipViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsState()
        CoinFlipScreen(
            uiState = uiState,
            onFlipClick = viewModel::flipCoin
        )
    }
}
```

---

## 5. DISEÑO VISUAL PENDIENTE

Referencia: `AI_CONTEXT/UI.png`.

### 5.1 Tokens

```kotlin
PurplePrimary      = 0xFF6C4AE2
PurpleButtonStart  = 0xFF7B4DFF
PurpleButtonEnd    = 0xFF6338F0
BackgroundTop      = 0xFFF7F4FF
BackgroundMiddle   = 0xFFF3EDFF
BackgroundBottom   = 0xFFFFF4F0
TextPrimary        = 0xFF241C3B
TextSecondary      = 0xFF8A8299
```

- Tema claro fijo, `dynamicColor = false`.
- Título con `FontFamily.Serif`, bold, ~38sp.
- Botón degradado morado, alto 64dp, radio 20dp.

### 5.2 Estructura

```text
Box (fondo degradado + glows)
└── Column (safeDrawing, 24dp horizontal)
    ├── Badge ✦ + "UNA PEQUEÑA SEÑAL"
    ├── Título "¿Qué dice el destino?"
    ├── Subtítulo
    ├── Moneda (~190dp, centrada, weight 1f)
    ├── Resultado: "LA MONEDA HA HABLADO" + Cara/Cruz + frase
    ├── Botón "Tirar la moneda" / "Girando..." / "Volver a tirar"
    └── Footer "CONFÍA EN TU INTUICIÓN"
```

### 5.3 Resultado

- CARA → `Cara` + `El destino dice que sí.`
- CRUZ → `Cruz` + `Mejor espera un poco.`
- Visible solo en `Result`, con fade + slide-up.
- Altura reservada para evitar saltos.

### 5.4 Moneda

- Dos imágenes: `coin_cara` y `coin_cruz`.
- Mientras el usuario no las entregue, crear placeholders vectoriales con esos nombres.
- Al recibir PNG/WEBP: eliminar el XML placeholder y agregar la imagen con el mismo nombre base.

---

## 6. ANIMACIÓN PENDIENTE

Implementar en `CoinFlipScreen` o `CoinView`:

- `Animatable` para `rotationY`, `offsetY`, `scale`.
- Disparar con `LaunchedEffect(flipId)` al entrar en `Flipping`.
- Ángulo acumulado:
  - `targetAngle = actual + 360f * 5 + (if (target == CRUZ) 180f else 0f)`
- Duración `FLIP_DURATION_MS`.
- Salto: `0 → -60dp → 0`; escala `1f → 1.08f → 0.98f → 1f`.
- Cara visible según ángulo normalizado (`< 90` o `> 270` → CARA).
- Cara trasera con `rotationY = 180f` para evitar espejado.
- No reiniciar la animación al pasar de `Flipping` a `Result`.

---

## 7. HÁPTICOS Y SONIDO (PENDIENTE)

Según `AI_RULES.md`, incluir en las acciones principales:

- Háptico al tocar el botón y al obtener resultado.
- Sonido de giro y aterrizaje.
- Usar APIs nativas (`HapticFeedback`, `SoundPool`). Sin librerías externas.

---

## 8. DEPENDENCIAS

Actuales y permitidas:

```kotlin
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.activity.compose)
implementation(libs.androidx.compose.material3)
implementation(libs.androidx.compose.ui)
implementation(libs.androidx.compose.ui.graphics)
implementation(libs.androidx.compose.ui.tooling.preview)
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.lifecycle.viewmodel.compose)
```

No agregar nada más sin justificarlo en este archivo.

---

## 9. FASES RESTANTES

### Fase A — Recursos y tema
- [ ] Actualizar `Color.kt`, `Theme.kt`, `Type.kt`.
- [ ] Agregar strings faltantes.
- [ ] Crear placeholders `coin_cara.xml` y `coin_cruz.xml`.

### Fase B — UI visual
- [ ] Fondo degradado + glows.
- [ ] Header (badge, título, subtítulo).
- [ ] Moneda y sección de resultado.
- [ ] Botón degradado + footer.
- [ ] Previews `Idle`, `Flipping`, `Result`.

### Fase C — Animación
- [ ] Giro 3D sincronizado con `FLIP_DURATION_MS`.
- [ ] Salto, escala y sombra.
- [ ] Cara final correcta según `target`.

### Fase D — Pulido
- [ ] Hápticos.
- [ ] Sonido.
- [ ] QA en pantallas pequeñas.

---

## 10. VERIFICACIÓN

```powershell
.\gradlew.bat :app:assembleDebug
```

QA manual:
- [ ] Primer arranque en `Idle`.
- [ ] Tap → animación → resultado correcto.
- [ ] Taps repetidos durante el giro se ignoran.
- [ ] `Volver a tirar` funciona.
- [ ] Sin overflow en 360x640dp.

---

## 11. DEUDA TÉCNICA ACEPTADA

- `collectAsState()` en lugar de `collectAsStateWithLifecycle()` para evitar una dependencia extra. Reevaluar si la app crece.
- Sin tests unitarios por decisión del proyecto.
- Sin DI: el ViewModel crea `Random.Default` internamente.

---

## 12. DEFINICIÓN DE TERMINADO

- [ ] UI replica `UI.png`.
- [ ] Moneda gira y muestra Cara/Cruz.
- [ ] Resultado textual correcto.
- [ ] Animaciones fluidas.
- [ ] Hápticos y sonido.
- [ ] `assembleDebug` exitoso.
