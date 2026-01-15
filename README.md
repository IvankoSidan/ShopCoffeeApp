# Мобильное приложение "ShopCoffeApp"

Это Android-приложение, представляющее собой мобильную кофейню. Проект демонстрирует современный подход к разработке с использованием архитектуры MVVM, принципов чистой архитектуры, а также инструментов из стека Jetpack.

## Основные возможности

*   **Главный экран:** Отображает баннеры, категории кофе и популярные товары.
*   **Каталог:** Позволяет просматривать все товары с возможностью фильтрации по категориям.
*   **Детализация товара:** Экран с подробным описанием, выбором размера, количества и добавлением в корзину.
*   **Корзина:** Управление добавленными товарами, применение промокодов.
*   **Оформление заказа:** Простой процесс оформления заказа.
*   **Поиск:** Возможность поиска товара по названию.

## Технологический стек

*   **Язык:** Kotlin
*   **Архитектура:** MVVM + Clean Architecture
*   **Асинхронность:** Kotlin Coroutines & Flow
*   **Внедрение зависимостей (DI):** Dagger 2
*   **Навигация:** Jetpack Navigation Component
*   **Работа с сетью:** Retrofit & OkHttp
*   **Жизненный цикл компонентов:** Jetpack ViewModel & Lifecycle
*   **Отображение UI:** ViewBinding, Android Views (XML), RecyclerView
*   **Загрузка изображений:** Glide

## Архитектура

Проект разделен на три основных слоя в соответствии с принципами чистой архитектуры.

### 1. Presentation Layer (Слой представления)

Отвечает за отображение UI и обработку пользовательского ввода. Не содержит бизнес-логики.

*   **UI (Fragments & Activities):** Экраны приложения.
*   **ViewModel:** Управляет состоянием UI и взаимодействует со слоем `Domain` через `UseCases`.

*Пример: `CartViewModel.kt`*
```kotlin
class CartViewModel @Inject constructor(
    private val updateQuantityUseCase: UpdateQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val applyPromoCodeUseCase: ApplyPromoCodeUseCase
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> get() = _cartItems

    private val _orderStatus = MutableStateFlow<OrderStatus>(OrderStatus.None)
    val orderStatus: StateFlow<OrderStatus> get() = _orderStatus

    init {
        loadCartItems()
    }

    private fun loadCartItems() = viewModelScope.launch {
        getCartItemsUseCase.invoke().collect { items ->
            _cartItems.value = items
        }
    }
    
    fun createOrder(customerName: String? = null) = viewModelScope.launch {
        // ... логика вызова use case для создания заказа
    }
}
```

### 2. Domain Layer (Слой бизнес-логики)

Содержит бизнес-логику приложения. Этот слой не зависит от Android Framework и других слоев.

*   **Use Cases:** Классы, инкапсулирующие одно конкретное бизнес-правило (например, добавление товара в корзину).
*   **Domain Models:** Простые классы данных, описывающие сущности (например, `Item`, `Category`).
*   **Repository Interfaces:** Абстракции для получения данных, реализация которых находится в слое `Data`.

*Пример: `CreateOrderUseCase.kt`*
```kotlin
class CreateOrderUseCase @Inject constructor(
    private val repository: CoffeeRepository
) {
    suspend operator fun invoke(
        customerName: String,
        items: List<CartItem>,
        promoCode: String? = null,
        discount: Double = 0.0,
        total: Double
    ): Result<Boolean> {
        return repository.createOrder(customerName, items, promoCode, discount, total)
    }
}
```

### 3. Data Layer (Слой данных)

Отвечает за предоставление данных для `Domain` слоя.

*   **API Service (Retrofit):** Интерфейс для взаимодействия с удаленным сервером.
*   **Repositories Implementation:** Реализация интерфейсов из `Domain` слоя. Здесь происходит маппинг DTO (Data Transfer Objects) в доменные модели.
*   **Data Models (DTO):** Классы данных, структура которых соответствует ответам от API.

*Пример: `CoffeeRepositoryImpl.kt`*
```kotlin
class CoffeeRepositoryImpl @Inject constructor(
    private val apiService: CoffeeApiService
) : CoffeeRepository {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getCategories(): Result<List<Category>> = withContext(dispatcher) {
        // runCatching для безопасной обработки ошибок сети
        runCatching { 
            // Получаем DTO из API и конвертируем (маппим) в доменную модель
            apiService.getCategories().map { it.toDomain() } 
        }
    }

    override suspend fun createOrder(
        customerName: String,
        items: List<CartItem>,
        promoCode: String?,
        discount: Double,
        total: Double
    ): Result<Boolean> = withContext(dispatcher) {
        runCatching {
            // ... подготовка запроса и вызов API
            apiService.createOrder(request)
            true
        }
    }
}
```

## Структура проекта

Проект организован по функциональным модулям (`features`), что упрощает навигацию и поддержку кода.

```text
C:.
│   App.kt
│
├───data
│   ├───api
│   │       CoffeeApiService.kt
│   ├───model
│   │       BannerDto.kt
│   │       ... (другие DTO)
│   └───repository
│           CartRepositoryImpl.kt
│           CoffeeRepositoryImpl.kt
│
├───di
│       AppComponent.kt
│       NetworkModule.kt
│       ... (другие модули Dagger)
│
├───domain
│   ├───model
│   │       Item.kt
│   │       ... (другие доменные модели)
│   ├───repository
│   │       CartRepository.kt
│   │       CoffeeRepository.kt
│   └───usecase
│           CreateOrderUseCase.kt
│           ... (другие use cases)
│
├───features
│   ├───cart
│   │   ├───di
│   │   └───viewmodel
│   │           CartViewModel.kt
│   ├───dashboard
│   │   ├───di
│   │   └───viewmodel
│   │           DashboardViewModel.kt
│   ├───itemdetail
│   │   ├───di
│   │   └───viewmodel
│   │           ItemDetailViewModel.kt
│   └───itemlist
│       ├───di
│       └───viewmodel
│               ItemListViewModel.kt
│
└───presentation
    ├───adapters
    │       CartAdapter.kt
    │       ...
    ├───fragments
    │       CartFragment.kt
    │       DashboardFragment.kt
    │       ...
    ├───ui
    │       MainActivity.kt
    └───views
            SearchEditText.kt
```

## [Смотреть работу Android-клиента](https://drive.google.com/drive/folders/11QJEQ1OogO3YZMY1gPLI4vOVwBl-yZip?usp=drive_link)
