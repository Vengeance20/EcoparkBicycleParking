# Ecopark Bicycle Parking

Backend Spring Boot cho hệ thống thuê xe đạp Ecopark.

## Yêu cầu môi trường

- Java JDK 21
- XAMPP có MySQL/MariaDB
- Maven Wrapper đã có sẵn trong repo: `mvnw`, `mvnw.cmd`

## Cách khởi động

1. Bật XAMPP và start MySQL.
2. Vào `http://localhost/phpmyadmin/`.
3. Create database tên `ecopark_bike`.
4. Vào tab SQL của database `ecopark_bike`.
5. Copy toàn bộ nội dung file `Dummy SQL data.txt` vào ô SQL rồi chạy.
6. Mở terminal tại thư mục project.
7. Chạy backend:

```powershell
.\mvnw spring-boot:run
```

Nếu chạy trong Linux/macOS/WSL:

```bash
./mvnw spring-boot:run
```

Backend mặc định chạy tại:

```text
http://localhost:8080
```

Database config hiện tại ở `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecopark_bike?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

Lưu ý: nếu import `Dummy SQL data.txt` báo lỗi thiếu bảng, hãy chạy backend một lần để Hibernate tạo bảng, dừng app, rồi import lại file SQL.

## API hiện có

### U4 - Tìm kiếm bãi và xe

#### Xem danh sách bãi trên bản đồ

```http
GET /apiv1/search/stations
```

Query params:

- `latitude`: vĩ độ hiện tại của user, optional.
- `longitude`: kinh độ hiện tại của user, optional.
- `radiusKm`: lọc bãi trong bán kính km, optional, chỉ dùng khi có `latitude` và `longitude`.
- `keyword`: tìm theo tên bãi, optional.
- `availableOnly`: `true` để chỉ lấy bãi còn xe `AVAILABLE`, optional.

Ví dụ test:

```powershell
curl.exe "http://localhost:8080/apiv1/search/stations"
curl.exe "http://localhost:8080/apiv1/search/stations?latitude=20.963451&longitude=105.931526&radiusKm=2"
curl.exe "http://localhost:8080/apiv1/search/stations?keyword=xe&availableOnly=true"
```

Response gồm thông tin bãi, tọa độ, sức chứa, tổng số xe, số xe khả dụng, số slot trống, khoảng cách và số xe khả dụng theo loại.

#### Xem chi tiết một bãi và danh sách xe

```http
GET /apiv1/search/stations/{stationId}
```

Query params:

- `bikeStatus`: lọc xe theo trạng thái, ví dụ `AVAILABLE`, `MAINTENANCE`, optional.

Ví dụ test:

```powershell
curl.exe "http://localhost:8080/apiv1/search/stations/1"
curl.exe "http://localhost:8080/apiv1/search/stations/1?bikeStatus=AVAILABLE"
```

#### Xem xe khả dụng tại một bãi

```http
GET /apiv1/search/stations/{stationId}/available-bikes
```

Ví dụ test:

```powershell
curl.exe "http://localhost:8080/apiv1/search/stations/1/available-bikes"
```

### U5 - Thuê / đặt / mở khóa xe

#### Thuê xe ngay

```http
POST /apiv1/rentals/rent
Content-Type: application/json
```

Body:

```json
{
  "userId": 3,
  "bikeCode": "ECO-S-001"
}
```

Ví dụ test:

```powershell
curl.exe -X POST "http://localhost:8080/apiv1/rentals/rent" -H "Content-Type: application/json" -d "{\"userId\":3,\"bikeCode\":\"ECO-S-001\"}"
```

#### Đặt xe trước

```http
POST /apiv1/rentals/reserve
Content-Type: application/json
```

Body:

```json
{
  "userId": 3,
  "bikeCode": "ECO-S-001"
}
```

Ví dụ test:

```powershell
curl.exe -X POST "http://localhost:8080/apiv1/rentals/reserve" -H "Content-Type: application/json" -d "{\"userId\":3,\"bikeCode\":\"ECO-S-001\"}"
```

#### Mở khóa xe đã đặt

```http
POST /apiv1/rentals/unlock
Content-Type: application/json
```

Body:

```json
{
  "userId": 3,
  "bikeCode": "ECO-S-001"
}
```

Ví dụ test:

```powershell
curl.exe -X POST "http://localhost:8080/apiv1/rentals/unlock" -H "Content-Type: application/json" -d "{\"userId\":3,\"bikeCode\":\"ECO-S-001\"}"
```


