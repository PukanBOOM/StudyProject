# comparator-proj

Comparator -- это площадка, на которой пользователи могут сравнивать цены на резные товары и услуги. Задача
сервиса -- предоставить пользователю список площадок для приобретения товара или услуги с актуальными ценами и помочь определиться с наиболее выгодным предложением.

## Визуальная схема фронтенда

![Макет фронта](imgs/СхемаФронта.drawio)

## Документация

1. Маркетинг и аналитика
    1. [Целевая аудитория](docs/biz/target-audience.md)
2. Аналитика:
    1. [Функциональные требования](docs/analysis/functional-requiremens.md)
    2. [Нефункциональные требования](docs/analysis/nonfunctional-requiremens.md)
    3. [Метрики MVP](docs/analysis/mvp-metrics.md)
3. Архитектура
    1. [Сущности](imgs/entities.drawio)
    2. [Архитектурные схемы](imgs/architecture.drawio)

# Структура проекта

### Плагины Gradle сборки проекта

1. [build-plugin](build-plugin) Модуль с плагинами
2. [BuildPluginJvm](build-plugin/src/main/kotlin/BuildPluginJvm.kt) Плагин для сборки проектов JVM
2. [BuildPluginMultiplarform](build-plugin/src/main/kotlin/BuildPluginMultiplatform.kt) Плагин для сборки
   мультиплатформенных проектов

## Проектные модули

### Транспортные модели, API
