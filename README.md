# UstozUz — backend

O'zbekiston uchun onlayn ta'lim platformasi (Spring Boot 4.1, Java 17, PostgreSQL).

- Frontend: alohida repo (`ustozuz-frontend`, Next.js)
- Internetga chiqarish: [DEPLOY.md](DEPLOY.md)
- Tanlov uchun demo ssenariysi: [DEMO.md](DEMO.md)

## Lokal ishga tushirish

1. PostgreSQL'da `ustozuz` bazasini yarating.
2. Loyiha ildizida `config/application.properties` yarating (git'ga kirmaydi):
   ```properties
   spring.datasource.password=<postgres paroli>
   app.jwt.secret=<kamida 32 belgili maxfiy satr>
   app.seed.admin-password=<admin paroli>
   app.seed.instructor-password=<namunaviy ustozlar paroli>
   ```
3. `BackendApplication`ni ishga tushiring (IntelliJ yoki `mvn spring-boot:run`) → http://localhost:8080
   Bo'sh bazada kategoriyalar, 10 ta ustoz, 10 ta kurs (darslari bilan) va admin avtomatik yaratiladi.

## API

| Yo'l | Kim uchun | Vazifasi |
|---|---|---|
| `POST /api/auth/register`, `/login`, `/forgot-password`, `/reset-password` | hamma | Kirish va ro'yxatdan o'tish (JWT) |
| `GET /api/categories`, `/api/courses`, `/api/courses/{slug}`, `/api/stats/public` | hamma | Katalog |
| `POST /api/contact` | hamma | Aloqa formasi |
| `GET/PUT /api/me`, `PUT /api/me/password` | kirgan foydalanuvchi | Profil |
| `POST /api/orders/checkout`, `GET /api/orders` | kirgan foydalanuvchi | Sotib olish (sinov to'lovi) |
| `GET /api/me/courses`, `/api/me/courses/{slug}`, `PUT .../lessons/{id}` | kirgan foydalanuvchi | O'qish va progress |
| `GET /api/me/certificates` | kirgan foydalanuvchi | Sertifikatlar |
| `/api/instructor/courses/**` | INSTRUCTOR | Kurs va darslarni boshqarish |
| `/api/admin/**` | ADMIN | Statistika, foydalanuvchilar, kurslar, xabarlar |

## Tuzilma

`auth` (JWT), `user`, `category`, `course`, `lesson`, `order` + `payment` (to'lov interfeysi va sinov
amalga oshirilishi), `enrollment` (yozilish, progress), `certificate`, `contact`, `admin`, `stats`, `config`.
