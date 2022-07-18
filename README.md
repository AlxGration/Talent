# Android demo* project
Пополняемая база вопросов для проведения интервью. Вопросы можно 
группировать и создавать Вакансии.

- Вкладка **Вопросы**
    - Создавать, удалять вопросы
    - Проводить поиск по названию, фильтровать по тегам
- Вкладка **Вакансии**
    - Создавать вакансии
    - Проводить поиск по названию

![Image](https://github.com/AlxGration/Talent/blob/main/img/screenshot.png)

## Особенности
- SingleActivity app
- Автоподгрузка списка
- RecyclerView:
    - DiffUtils 
    - ItemDecorator: Удаление по свайпу
    - LinearLayoutManager, GridLayoutManager
- Фрагменты
    - Переиспользование
    - BottomSheetDialogFragment
    - Обмен данными (bundle, setFragmentResult)
- Все строки в ресурсах

## Стек
```
Clean architecture, MVVM
Retrofit
Hilt
Coroutines, Flow
Kotlin
```

*демонстрационный проект ограниченной функциональности существующего 
веб-приложения

