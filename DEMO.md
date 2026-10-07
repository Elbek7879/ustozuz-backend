# UstozUz — tanlov uchun demo ssenariysi (5–7 daqiqa)

## Tayyorgarlik (taqdimotdan 15 daqiqa oldin)

- Saytni bir marta oching — server "uyg'onadi", birinchi sahifa tez ochiladi.
- **Ikkita brauzer oynasi** tayyorlang: oddiy oyna va **inkognito** oyna.
  Shunda turli rollar (talaba / ustoz / admin) o'rtasida tez almashasiz.
- Hisoblar:
  - Admin: `admin@ustozuz.uz` — parol: deploy'da bergan `ADMIN_PASSWORD`
  - Ustoz: `aziz.karimov@ustozuz.uz` — parol: `INSTRUCTOR_PASSWORD`
  - Talaba: demo vaqtida **yangi ro'yxatdan o'tasiz** (bu ham ko'rsatiladigan imkoniyat)

## 1. Bosh sahifa va katalog (1 daqiqa)

1. Bosh sahifa: kategoriyalar, mashhur kurslar, statistika — **hammasi bazadan**.
2. Yuqoridagi qidiruvga `python` yozing → natija.
3. Kategoriyalar → "Dasturlash" → kurs sahifasi: tavsif, **darslar ro'yxati**, narx.

## 2. Talaba yo'li (2–3 daqiqa) — asosiy qism

1. **Ro'yxatdan o'tish** (yangi email bilan).
2. Kursni **savatga qo'shish** → Savat → **To'lash**.
3. To'lov usuli: Payme / Click / karta (sinov rejimi) → **To'lash**.
4. **Mening kurslarim** → kurs paydo bo'ldi → oching.
5. "Darsni tugatdim" tugmasini bosib darslarni o'ting → progress oshadi.
6. Oxirgi dars → **"Tabriklaymiz! Sertifikat berildi"**.
7. **Sertifikatlarim** → Ko'rish → ismingiz yozilgan sertifikat → **Chop etish / PDF saqlash**.

## 3. Ustoz yo'li (1–2 daqiqa)

1. Inkognito oynada ustoz bilan kiring → **Ustoz paneli**.
2. **Yangi kurs yaratish** → nom, kategoriya, tavsif, narx → saqlash.
3. 2–3 ta dars qo'shing, tartibini o'zgartiring → **Nashr qilish**.
4. "Ko'rish" → kurs katalogda va o'z sahifasida paydo bo'ldi.

## 4. Admin paneli (1 daqiqa)

1. Admin bilan kiring → **Admin panel**.
2. Statistika: yangi foydalanuvchi, **xarid va daromad** darhol ko'rinadi; "So'nggi harakatlar".
3. **Foydalanuvchilar**: qidiruv, rolni o'zgartirish (talabani ustoz qilish), bloklash.
4. **Kurslar**: kursni yashirish / qayta faollashtirish.
5. Pastda: **Aloqa xabarlari** (saytdagi "Aloqa" formasidan kelganlar).

## Savol bo'lsa: "Keyingi rejalar"

- **Payme / Click**: to'lov qatlami interfeys orqali ajratilgan (`PaymentProvider`).
  Hozir sinov rejimi, haqiqiy integratsiya shu interfeysga ulanadi.
- **Video darslar**: dars modelida `videoUrl` maydoni bor, yuklash keyingi bosqichda.
- **Email** (parolni tiklash xati), **ko'p tillilik** (o'zbek / rus / ingliz), **sharhlar va reyting**.

## Texnik ma'lumot (hakamlar so'rasa)

- Frontend: Next.js 16 (App Router, TypeScript, Tailwind CSS) → Vercel
- Backend: Spring Boot 4.1, Java 17, Spring Security + JWT (HS256), JPA/Hibernate → Railway
- Ma'lumotlar bazasi: PostgreSQL
- Xavfsizlik: rollar (STUDENT / INSTRUCTOR / ADMIN) backend'da tekshiriladi, parollar BCrypt bilan,
  maxfiy sozlamalar kodda emas (environment o'zgaruvchilar).
