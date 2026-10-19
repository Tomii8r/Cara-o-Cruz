

### 🎨 Concepto de UI (Súper Minimalista)

Imagina una sola pantalla con fondo degradado suave:

1. **Arriba:** El título o marcador (*ej. "¿Qué dice el destino?"*).
2. **Centro:** Una imagen grande de la moneda (un emoji 🪙 o un gráfico vectorizado).
3. **Abajo:** Un botón grande que diga "Tirar" o "Agitar".
4. **Resultado (Texto):** Aparece suavemente bajo la moneda después de la animación.

---

### 💻 El Plan de Desarrollo (Stack Moderno)

#### 1. Estructura del Proyecto

Usa arquitectura **MVVM** (Model-View-ViewModel) con Jetpack Compose. Esto la hará ver muy profesional.

* **View (Pantalla):** Código de UI de Compose. Muestra la moneda y el botón.
* **ViewModel:** Contiene la lógica. "Gira la moneda" y expone el estado (si está girando, si salió Cara o Cruz).

#### 2. Lógica de Aleatoriedad (The Business Logic xd)

En Kotlin es extremadamente sencillo. Dentro de tu ViewModel:

```kotlin
class CoinViewModel : ViewModel() {
    // Usamos StateFlow para manejar el estado de la UI de forma reactiva
    private val _coinState = MutableStateFlow<CoinResult?>(null)
    val coinState = _coinState.asStateFlow()

    fun flipCoin() {
        viewModelScope.launch {
            // 1. Simular que está girando (puedes setear un estado temporal)
            _coinState.value = CoinResult.Flipping

            // 2. Esperar un tiempo (ej. la duración de la animación)
            delay(1500)

            // 3. Generar resultado aleatorio
            val result = if (Random.nextBoolean()) CoinResult.Cara else CoinResult.Cruz
            _coinState.value = result
        }
    }
}

sealed class CoinResult {
    object Flipping : CoinResult()
    object Cara : CoinResult()
    object Cruz : CoinResult()
}

```

#### 3. ¡LA ANIMACIÓN

En Jetpack Compose, hacer que la moneda "salte y gire" es mucho más fácil que antes.

**Ideas de Animación Sencillas:**

* **Rotación en Y:** Haces que la moneda gire como un disco. Usas `animateFloatAsState` para cambiar `rotationY` de 0 a 1800 grados (varias vueltas).
* **Escala y Desplazamiento:** Mientras gira, haces que se haga un poco más pequeña y suba (simulando un salto) y luego baje y se agrande. Usas `animateDpAsState` para `offsetY`.
* **Transición de Imagen:** Cuando la animación de giro está a mitad (90 o 270 grados, cuando no se ve la cara), cambias la imagen de "intermedia" a la imagen de Cara o Cruz final.

#### 4. Toques de Pulido 

* **Añade Sonido 🔊:** Un pequeño sonido de moneda girando y un "clink" al caer le da 1000 puntos de calidad. Usa `MediaPlayer` o `SoundPool` (más rápido).
* **Haptics (Vibración) 📳:** Haz que el teléfono vibre ligeramente durante el giro.
* **Permitir Agitar 🤝:** Usa el `SensorManager` (acelerómetro) para que si el usuario agita el teléfono, la moneda se tire automáticamente. ¡Esto une tu idea inicial de sensores con esta simplicidad!

