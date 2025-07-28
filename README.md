# Currency Exchange API

REST API для описания валют и обменных курсов. Позволяет просматривать и редактировать списки валют и обменных курсов, а также совершать расчёт конвертации произвольных сумм из одной валюты в другую.

## 📋 Описание проекта

Проект реализован в соответствии с ТЗ: https://zhukovsd.github.io/java-backend-learning-course/Projects/CurrencyExchange/

Приложение представляет серверное REST API, построенное на архитектурном принципе MVC (Model-View-Controller):

- **Controller (Сервлеты)** - принимают REST запросы, проводят валидацию и возвращают ответы в JSON формате
- **Model (Сервисы)** - содержат бизнес-логику для расчета обменных курсов с поддержкой прямых, обратных и кросс-курсов через USD
- **DAO** - реализуют доступ к базе данных через CRUD интерфейс

### Особенности реализации:
- ✅ Автоматическое создание и инициализация базы данных -> просто запустить приложение и сделать запрос
- ✅ Стратегия расчета курсов: прямой → обратный → кросс через USD
- ✅ Валидация входящих параметров
- ✅ Централизованная обработка исключений
- ✅ Connection Pool для работы с БД

## 🛠️ Технологии

- **Java 21**
- **Jakarta Servlets**
- **SQLite** (встраиваемая БД)
- **JDBC** (нативный, без ORM)
- **Maven** (сборка проекта)
- **Tomcat 11** (сервер приложений)
- **Postman** (тестирование API)

## 🚀 Установка и запуск

### Требования:
- Java 21
- Apache Tomcat 11
- Maven 3.6+

### Локальный запуск:

1. **Клонирование проекта:**
   ```bash
   git clone https://github.com/WayneHays/currency_exchange.git
   cd currency_exchange
   ```

2. **Настройка конфигурации:**
   
   Изменить `src/main/resources/application.properties`:
   ```properties
   # Для локального запуска
   db.url=jdbc:sqlite:currency_exchange.sqlite
   db.pool.size=5
   ```
 
3. **Сборка проекта:**
   ```bash
   mvn clean package
   ```

4. **Деплой в Tomcat:**
   - Скопировать `target/currency_exchange.war` в папку `webapps` Tomcat
   - Запустить Tomcat: `./bin/startup.sh` (Linux/Mac) или `bin\startup.bat` (Windows)

5. **Проверка работы:**
   ```bash
   curl http://localhost:8080/currencies
   ```

## 📖 API Documentation

### 💰 Валюты

#### Получить все валюты
```http
GET /currencies
```

**Ответ:**
```json
[
    {
        "id": 1,
        "code": "USD",
        "name": "United States dollar",
        "sign": "$"
    },
    {
        "id": 2,
        "code": "EUR", 
        "name": "Euro",
        "sign": "€"
    }
]
```

#### Получить валюту по коду
```http
GET /currency/{code}
```

**Пример:**
```http
GET /currency/EUR
```

**Ответ:**
```json
{
    "id": 2,
    "code": "EUR",
    "name": "Euro", 
    "sign": "€"
}
```

#### Добавить новую валюту
```http
POST /currencies
Content-Type: application/x-www-form-urlencoded

name=Euro&code=EUR&sign=€

```

**Ответ:**
```json
{
    "id": 3,
    "code": "EUR",
    "name": "Euro",
    "sign": "€"
}
```

### 📈 Обменные курсы

#### Получить все обменные курсы
```http
GET /exchangeRates
```

**Ответ:**
```json
[
    {
        "id": 1,
        "baseCurrency": {
            "id": 1,
            "code": "USD",
            "name": "United States dollar",
            "sign": "$"
        },
        "targetCurrency": {
            "id": 2,
            "code": "EUR",
            "name": "Euro",
            "sign": "€"
        },
        "rate": 0.8613
    }
]
```

#### Получить обменный курс для пары валют
```http
GET /exchangeRate/{baseCurrencyCode}{targetCurrencyCode}
```

**Пример:**
```http
GET /exchangeRate/USDRUB
```

**Ответ:**
```json
{
    "id": 1,
    "baseCurrency": {
        "id": 1,
        "code": "USD",
        "name": "United States dollar", 
        "sign": "$"
    },
    "targetCurrency": {
        "id": 3,
        "code": "RUB",
        "name": "Russian Ruble",
        "sign": "₽"
    },
    "rate": 78.50
}
```

#### Добавить новый обменный курс
```http
POST /exchangeRates
Content-Type: application/x-www-form-urlencoded

baseCurrencyCode=USD&targetCurrencyCode=RUB&rate=78.50
```

#### Обновить обменный курс
```http
PATCH /exchangeRate/USDRUB
Content-Type: application/x-www-form-urlencoded

rate=79.25
```

### 💱 Обмен валюты

#### Конвертация валют
```http
GET /exchange?from=USD&to=EUR&amount=100
```

**Ответ:**
```json
{
    "baseCurrency": {
        "id": 1,
        "code": "USD",
        "name": "United States dollar",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 2,
        "code": "EUR", 
        "name": "Euro",
        "sign": "€"
    },
    "rate": 0.8613,
    "amount": 100.00,
    "convertedAmount": 86.13
}
```

## 🧪 Тестирование

### Postman Collection:
   - Скачать: currency_exchange.postman_collection.json
   - В Postman: Import → Upload Files → выбрать файл
### Примеры запросов:

```bash
# Получить все валюты
curl -X GET http://localhost:8080/currencies

# Добавить валюту
curl -X POST http://localhost:8080/currency-exchanger/currencies \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "code=GBP&name=British Pound&sign=£"

# Конвертация
curl -X GET "http://localhost:8080/exchange?from=USD&to=EUR&amount=100"
```

## 🗄️ База данных

### Схема БД:

```sql
CREATE TABLE currencies (
    id INTEGER PRIMARY KEY,
    code VARCHAR(3) NOT NULL UNIQUE,
    full_name VARCHAR NOT NULL,
    sign VARCHAR NOT NULL
);

CREATE TABLE exchange_rates (
    id INTEGER PRIMARY KEY,
    base_currency_id INTEGER NOT NULL REFERENCES currencies(id),
    target_currency_id INTEGER NOT NULL REFERENCES currencies(id),
    rate DECIMAL(6) NOT NULL,
    UNIQUE(base_currency_id, target_currency_id)
);
```

## 🏗️ Архитектура

```
├── servlet/           # REST контроллеры
├── service/           # Бизнес-логика
├── dao/              # Доступ к данным
├── entity/           # Сущности БД
├── dto/              # Объекты передачи данных
├── exception/        # Кастомные исключения
├── filter/           # Фильтры сервлетов
├── util/             # Утилиты и валидация
└── resources/        # Конфигурация
```

## 🤝 Contributing

1. Fork проекта
2. Создать feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit изменений (`git commit -m 'Add some AmazingFeature'`)
4. Push в branch (`git push origin feature/AmazingFeature`)
5. Создать Pull Request

## 📄 Лицензия

Distributed under the MIT License. See `LICENSE` for more information.

## 📞 Контакты
Telegram: @wayne_hays

