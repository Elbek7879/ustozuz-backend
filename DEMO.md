# UstozUz — tanlov uchun demo ssenariysi (5–7 daqiqa)

Sayt: **https://ustozuz.vercel.app**

## Tayyorgarlik (taqdimotdan 15 daqiqa oldin)

- Saytni bir marta oching va bir-ikki sahifani aylanib chiqing.
- **Ikkita brauzer oynasi** tayyorlang: oddiy oyna va **inkognito** oyna.
  Shunda turli rollar (talaba / ustoz / admin) o'rtasida tez almashasiz.
- Hisoblar:
  - Admin: `admin@ustozuz.uz` — parol: deploy'da bergan `ADMIN_PASSWORD`
  - Ustoz: `aziz.karimov@ustozuz.uz` — parol: `INSTRUCTOR_PASSWORD`
  - Talaba: demo vaqtida **yangi ro'yxatdan o'tasiz** (bu ham ko'rsatiladigan imkoniyat)
- Zaxira: zalda Wi-Fi bo'lmasa — telefondan internet tarqating (hotspot);
  u ham bo'lmasa — oldindan olingan skrinshot / ekran videosini ko'rsating.
- **Parolni tiklashni ko'rsatmang** — email xizmati hali ulanmagan.

## 1. Bosh sahifa va katalog (1 daqiqa)

1. Bosh sahifa: pastga aylantiring — bo'limlar silliq paydo bo'ladi, statistika raqamlari **sanab chiqadi**.
   Kategoriyalar, mashhur kurslar, statistika — **hammasi bazadan**.
2. Yuqoridagi qidiruvga `dastur` deb yozing → natijalar **yozayotganda darhol** rasm va narx bilan chiqadi
   (↓ / Enter bilan tanlash mumkin).
3. Kurs sahifasini oching → muqovadagi **"Kursni bepul ko'rish"** → birinchi dars videosi
   **saytning o'zida** ochiladi (YouTube'ga o'tmaydi).
4. (Ixtiyoriy) Sayt havolasini Telegram'da yuboring → chiroyli **ulashish kartochkasi** chiqadi.

## 2. Talaba yo'li (2–3 daqiqa) — asosiy qism

1. **Ro'yxatdan o'tish** (yangi email bilan).
2. Kursni **savatga qo'shish** → Savat → **To'lash**.
3. To'lov usuli: Payme / Click / karta (sinov rejimi) → **To'lash**.
4. **Mening kurslarim**: "Salom, …! 👋", statistika, **"Davom ettiring"** bloki → kursni oching.
5. Dars sahifasi: video saytda o'ynaydi, o'ngda **"Kurs mazmuni"**, yuqorida progress doirasi.
   **"Darsni tugatdim"** → keyingi darsga o'tadi, progress oshadi.
6. Oxirgi dars → ekranga **konfetti 🎉** va "Tabriklaymiz! Sertifikat berildi".
7. **Sertifikatlarim** → Ko'rish → ismingiz yozilgan sertifikat → **Chop etish / PDF saqlash**.
8. Sertifikatdagi **QR kod**: hakamga telefoni bilan skanerlashni taklif qiling → "✅ Sertifikat haqiqiy"
   sahifasi ochiladi (ism, kurs, ustoz, sana). Kodni taxmin qilib bo'lmaydi — soxta sertifikat o'tmaydi.

## Telefonda ilova (30 soniya)

- Taqdimotdan oldin telefoningizga o'rnatib qo'ying:
  Android (Chrome): ⋮ menyu → **Ilovani o'rnatish** / **Bosh ekranga qo'shish**;
  iPhone (Safari): **Ulashish** tugmasi → **Bosh ekranga**.
- Taqdimotda bosh ekrandagi **UstozUz** ikonkasini ochib ko'rsating — sayt ilova kabi, manzil qatorisiz ochiladi.

## 3. Ustoz yo'li (1–2 daqiqa)

1. Inkognito oynada ustoz bilan kiring → **Ustoz paneli** (kurslar, talabalar, reyting).
2. **Yangi kurs** → nom, kategoriya, tavsif, narx → saqlash.
3. 2–3 ta dars qo'shing va har biriga **YouTube havolasini** qo'ying → video rasmi chiqadi.
   Tartibini o'zgartiring → **Nashr qilish**.
4. Ko'rish (ko'z belgisi) → kurs katalogda va o'z sahifasida paydo bo'ldi, birinchi dars bepul ko'rinadi.

## 4. Admin paneli (1 daqiqa)

1. Admin bilan kiring → **Admin panel**.
2. Statistika: yangi foydalanuvchi, **xarid va daromad** darhol ko'rinadi.
3. Grafiklar: **"Eng mashhur kurslar"** va **"Yo'nalishlar ulushi"**; "So'nggi harakatlar".
4. **Foydalanuvchilar**: qidiruv, rolni o'zgartirish (talabani ustoz qilish), bloklash.
5. **Kurslar**: kursni yashirish / qayta faollashtirish.
6. **Aloqa xabarlari** (saytdagi "Aloqa" formasidan kelganlar).

## Savol bo'lsa: "Keyingi rejalar"

- **Payme / Click**: to'lov qatlami interfeys orqali ajratilgan (`PaymentProvider`).
  Hozir sinov rejimi, haqiqiy integratsiya shu interfeysga ulanadi.
- **Video**: hozir YouTube orqali (Unlisted video, saytning o'zida o'ynaydi);
  keyingi bosqich — videoni to'g'ridan-to'g'ri platformaga yuklash va himoyalangan pleyer.
- **Email** (parolni tiklash xati — kod tayyor, xizmat ulanishi qoldi), **ko'p tillilik**
  (o'zbek / rus / ingliz), **sharhlar va reyting**, mobil ilova.

## Texnik ma'lumot (hakamlar so'rasa)

- Frontend: Next.js 16 (App Router, TypeScript, Tailwind CSS) → Vercel
- Backend: Spring Boot 4.1, Java 17, Spring Security + JWT (HS256), JPA/Hibernate → Railway
- Ma'lumotlar bazasi: PostgreSQL
- Xavfsizlik: rollar (STUDENT / INSTRUCTOR / ADMIN) backend'da tekshiriladi, parollar BCrypt bilan,
  maxfiy sozlamalar kodda emas (environment o'zgaruvchilar).
- GitHub'ga yuklash → sayt avtomatik yangilanadi (CI/CD).
