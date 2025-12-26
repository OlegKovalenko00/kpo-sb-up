# Campus Room Booking Service

Сервис бронирования аудиторий на покровке.Java.

## Запуск
1) Поднять инфраструктуру (PostgreSQL, Zookeeper, Kafka):
```
docker-compose up -d
```
2) Запустить приложение:
- Windows: `.\gradlew.bat bootRun`
- Linux/macOS: `./gradlew bootRun`

Сервис на порте 8080, подключение к БД и Kafka задаётся в `application.yml`.

## Проверка
- Swagger UI: http://localhost:8080/swagger-ui.html
- Веб-интерфейс: http://localhost:8080/
- Health: http://localhost:8080/actuator/health

## Быстрая проверка (PowerShell)
Создать пользователя:
```
$u = @{ name="Alice"; email="alice@example.com" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/users -ContentType "application/json" -Body $u
```
Создать аудиторию:
```
$r = @{ name="Room A"; capacity=10; type="SMALL"; available=$true } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/rooms -ContentType "application/json" -Body $r
```
Забронировать:
```
$b = @{ roomId=1; userId=1; startTime="2026-01-01T10:00:00"; endTime="2026-01-01T12:00:00" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/bookings/book -ContentType "application/json" -Body $b
```
Отчёт JSON:
```
Invoke-RestMethod http://localhost:8080/api/bookings/report/json
```
Экспорт аудиторий CSV:
```
Invoke-RestMethod http://localhost:8080/api/rooms/export/csv
```

## Быстрая проверка (bash, Linux/macOS)
Создать пользователя:
```
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d '{"name":"Alice","email":"alice@example.com"}'
```
Создать аудиторию:
```
curl -X POST http://localhost:8080/api/rooms -H "Content-Type: application/json" -d '{"name":"Room A","capacity":10,"type":"SMALL","available":true}'
```
Забронировать:
```
curl -X POST http://localhost:8080/api/bookings/book -H "Content-Type: application/json" -d '{"roomId":1,"userId":1,"startTime":"2026-01-01T10:00:00","endTime":"2026-01-01T12:00:00"}'
```
Отчёт JSON:
```
curl http://localhost:8080/api/bookings/report/json
```
Экспорт аудиторий CSV:
```
curl http://localhost:8080/api/rooms/export/csv
```
