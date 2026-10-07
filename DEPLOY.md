# UstozUz — internetga chiqarish (deploy) qo'llanmasi

Sxema: **frontend → Vercel**, **backend + PostgreSQL → Railway**. Ikkalasi ham GitHub'dan oladi.

Taxminiy vaqt: 40–60 daqiqa (birinchi marta).

---

## 1. GitHub: ikkita private repo

1. https://github.com/new oching.
2. Repo nomi `ustozuz-backend`, **Private** tanlang, README/.gitignore **qo'shmang** → *Create repository*.
3. Xuddi shunday `ustozuz-frontend` yarating.
4. Ikkala repo manzilini Claude'ga yuboring — u kodni yuklaydi (`git push`).
   Agar GitHub kirish oynasi ochilsa, o'zingiz kirasiz.

---

## 2. Railway: baza + backend

1. https://railway.app → GitHub orqali kiring.
2. **New Project → Deploy PostgreSQL**. Baza yaratiladi (nomi `Postgres`).
3. Shu loyiha ichida **+ Create → GitHub Repo → `ustozuz-backend`**.
   Railway `Dockerfile`ni o'zi topadi va yig'adi.
4. Backend xizmatini oching → **Variables** → quyidagilarni qo'shing:

| O'zgaruvchi | Qiymat |
|---|---|
| `DB_URL` | `jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}` |
| `DB_USERNAME` | `${{Postgres.PGUSER}}` |
| `DB_PASSWORD` | `${{Postgres.PGPASSWORD}}` |
| `JWT_SECRET` | kamida 32 belgili tasodifiy satr (pastga qarang) |
| `ADMIN_PASSWORD` | admin uchun kuchli parol (o'zingiz o'ylab toping) |
| `INSTRUCTOR_PASSWORD` | namunaviy ustozlar uchun parol |
| `FRONTEND_URL` | hozircha bo'sh qoldiring — 3-qadamdan keyin to'ldiriladi |
| `CORS_ORIGINS` | hozircha bo'sh qoldiring — 3-qadamdan keyin to'ldiriladi |

   `JWT_SECRET` yasash uchun Git Bash'da:
   ```bash
   openssl rand -base64 48
   ```

5. **Settings → Networking → Generate Domain**. Masalan:
   `https://ustozuz-backend-production.up.railway.app`
6. Tekshirish: brauzerda `https://<railway-domen>/api/health` → `{"status":"ok"}` chiqishi kerak.

> Bo'sh bazada backend birinchi ishga tushganda kategoriyalar, 10 ta ustoz,
> 10 ta kurs va admin hisobini o'zi yaratadi.

---

## 3. Vercel: frontend

1. https://vercel.com → GitHub orqali kiring.
2. **Add New → Project → `ustozuz-frontend` → Import**.
3. **Environment Variables**:
   - `NEXT_PUBLIC_API_URL` = `https://<railway-domen>/api`
4. **Deploy**. Tugagach, manzilni oling, masalan `https://ustozuz.vercel.app`.

---

## 4. Ikkalasini bog'lash

Railway → backend → **Variables**:

- `FRONTEND_URL` = `https://ustozuz.vercel.app`
- `CORS_ORIGINS` = `https://ustozuz.vercel.app,https://*.vercel.app`

Saqlang — Railway backend'ni qayta ishga tushiradi.

---

## 5. Tekshirish

1. Vercel manzilini oching — bosh sahifada kurslar chiqishi kerak.
2. Admin bilan kiring: `admin@ustozuz.uz` + siz bergan `ADMIN_PASSWORD`.
3. Ustoz bilan kiring: masalan `aziz.karimov@ustozuz.uz` + `INSTRUCTOR_PASSWORD`.

## Tanlov kuni

- Taqdimotdan **10–15 daqiqa oldin** saytni bir marta ochib chiqing (server "uyg'onadi").
- Kodga o'zgarish kiritilsa: `git push` qilinadi → Railway va Vercel avtomatik qayta deploy qiladi.

## Lokal ishga tushirish (eslatma)

Lokal maxfiy sozlamalar `config/application.properties` faylida (git'ga kirmaydi).
IntelliJ'da `BackendApplication`ni ishga tushirganda Working directory — loyiha ildizi bo'lishi kerak.
