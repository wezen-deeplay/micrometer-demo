# Micrometer Demo

Демонстрационное приложение для мониторинга с использованием Micrometer, Prometheus и Grafana.

## Описание

Это приложение демонстрирует интеграцию метрик с использованием Micrometer для сбора метрик приложения и их экспорт в Prometheus. Приложение имитирует работу с удаленным сервисом, который может периодически выбрасывать исключения, и отслеживает успешные и неудачные запросы.

## Функциональность

- **Симуляция удаленного сервиса**: Имитация работы с внешним API с переменной задержкой (1-3 секунды)
- **Обработка исключений**: Случайная генерация исключений для демонстрации обработки ошибок
- **Сбор метрик**: Подсчет успешных и неудачных запросов с помощью Micrometer
- **Многопоточность**: Параллельная обработка запросов в 5 потоках
- **HTTP-эндпоинт для метрик**: Экспорт метрик в формате Prometheus на порту 8081

## Технологический стек

- **Java 21**
- **Gradle** с плагином Shadow для создания fat JAR
- **Micrometer** (1.11.5) - библиотека для сбора метрик
- **Prometheus** - система мониторинга и база данных временных рядов
- **Grafana** - платформа для визуализации метрик
- **Docker & Docker Compose** - контейнеризация и оркестрация

## Структура проекта

```
MicrometerDemo/
├── src/main/java/io/deeplay/wezzen/demo/
│   ├── consumer/
│   │   └── RemoteServiceConsumer.java  # Потребитель удаленного сервиса
│   ├── exceptions/
│   │   ├── RemoteServiceAvailableException.java
│   │   └── RemoteServiceExecutionException.java
│   ├── monitoring/
│   │   ├── Monitoring.java             # Интерфейс мониторинга
│   │   └── Micrometer.java             # Реализация с Micrometer
│   ├── runner/
│   │   └── Runner.java                 # Главный класс приложения
│   └── service/
│       └── RemoteService.java          # Симуляция удаленного сервиса
├── build.gradle.kts                    # Конфигурация сборки
├── docker-compose.yml                  # Конфигурация Docker Compose
├── Dockerfile                          # Docker образ приложения
└── prometheus.yml                      # Конфигурация Prometheus
```

## Метрики

Приложение экспортирует следующие метрики:

- `demo.success.query.total` - счетчик успешных запросов (с тегом `thread`)
- `demo.failed.query.total` - счетчик неудачных запросов (с тегами `thread` и `reason`)

## Установка и запуск

### Предварительные требования

- JDK 21
- Docker и Docker Compose
- Gradle (или использование Gradle Wrapper)

### Пошаговая инструкция для локального развертывания

#### 1. Клонирование репозитория
```bash
git clone <repository-url>
cd MicrometerDemo
```


#### 2. Подготовка директории для данных Grafana
```bash
# Создание директории для персистентного хранения данных Grafana
mkdir -p .data/grafana

# Установка прав доступа (важно для Linux/Mac)
# UID 472 используется пользователем grafana внутри контейнера
sudo chown -R 472:472 .data/grafana
```

#### 3. Сборка проекта
```bash
# Использование Gradle Wrapper (рекомендуется)
./gradlew clean build
```

#### 4. Запуск с Docker Compose
```bash
# Запуск всех сервисов в фоновом режиме
docker-compose up -d

# Или запуск с просмотром логов
docker-compose up

# Проверка статуса контейнеров
docker-compose ps
```

#### 5. Проверка работоспособности

После успешного запуска проверьте доступность сервисов:
- **Метрики приложения**: http://localhost:8081/metrics
- **Prometheus**: http://localhost:9090
  - Проверьте статус targets: http://localhost:9090/targets
  - Должны быть в статусе UP: prometheus и demo-app
- **Grafana**: http://localhost:3000
  - Логин: `Admin`
  - Пароль: `Admin`

### Запуск без Docker

```bash
# Запуск приложения напрямую
java -jar build/libs/MicrometerDemo-1.0-SNAPSHOT-all.jar

# Или через Gradle
./gradlew run
```

При запуске без Docker метрики будут доступны по адресу http://localhost:8081/metrics, но Prometheus и Grafana нужно будет настроить отдельно.

### Остановка и очистка

```bash
# Остановка всех контейнеров
docker-compose down

# Остановка с удалением volumes (осторожно, удалит данные Grafana!)
docker-compose down -v

# Полная очистка (включая образы)
docker-compose down --rmi all
```

## Конфигурация

### Prometheus

Prometheus настроен на сбор метрик с приложения каждые 15 секунд. Конфигурация находится в файле `prometheus.yml`.

### Grafana

#### Персистентное хранение данных

Grafana настроена с монтированием локальной директории `.data/grafana` для сохранения:
- Настроек и конфигурации
- Созданных дашбордов
- Источников данных
- Пользователей и их настроек
- Алертов и уведомлений

**Структура volume:**
```yaml
volumes:
  - .data/grafana:/var/lib/grafana
```

Это означает, что все данные Grafana будут сохраняться в директории `.data/grafana` на хост-машине и не потеряются при перезапуске или удалении контейнера.

#### Первоначальная настройка

После запуска Grafana:

1. **Вход в систему**
   - URL: http://localhost:3000
   - Логин: `Admin`
   - Пароль: `Admin`

2. **Добавление источника данных Prometheus**
   - Перейдите в Configuration → Data Sources
   - Нажмите "Add data source"
   - Выберите Prometheus
   - URL: `http://prometheus:9090` (используйте имя контейнера, не localhost!)
   - Нажмите "Save & Test"

3. **Создание дашборда**
   - Перейдите в Create → Dashboard
   - Добавьте новую панель
   - В поле запроса введите метрику, например: `demo_success_query_total`

#### Резервное копирование данных Grafana

```bash
# Создание резервной копии
tar -czf grafana-backup-$(date +%Y%m%d).tar.gz .data/grafana

# Восстановление из резервной копии
tar -xzf grafana-backup-20240101.tar.gz
```

## Особенности реализации

- **RemoteService**: Имитирует внешний сервис с задержкой 1-3 секунды и вероятностью ошибки ~20%
- **RemoteServiceConsumer**: Запускается в отдельном потоке и непрерывно выполняет запросы
- **Micrometer**: Регистрирует счетчики для успешных и неудачных запросов
- **Runner**: Запускает HTTP-сервер для экспорта метрик и создает потоки-потребители

## Разработка

```bash
# Запуск тестов
./gradlew test

# Сборка и запуск
./gradlew run
```

## Лицензия

Проект создан в демонстрационных целях.