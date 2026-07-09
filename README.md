# Hearts2Hearts (H2H) - Platform Pendamping Komunitas Fanbase Terlengkap 💖

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.x-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-green.svg?style=flat&logo=android)](https://developer.android.com/jetpack/compose)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg?style=flat&logo=android)](https://developer.android.com)
[![Database](https://img.shields.io/badge/Database-Room--SQLite-orange.svg?style=flat&logo=sqlite)](https://developer.android.com/training/data-storage/room)
[![AI](https://img.shields.io/badge/AI%20Integration-Gemini%20API-purple.svg?style=flat&logo=google-gemini)](https://ai.google.dev/)

**Hearts2Hearts (H2H) Fandom Companion** adalah aplikasi Android modern berbasis **Jetpack Compose** dan **Material 3** yang dirancang khusus untuk memanjakan seluruh fans setia (Hearts) dalam mendukung idola mereka, **Hearts2Hearts**. 

Aplikasi ini menggabungkan berbagai fitur interaktif mulai dari *streaming room* interaktif, obrolan dengan asisten AI pintar (Gemini AI), koordinasi event fandom, hingga pusat informasi lengkap dengan latar visual mewah bertemakan Hearts2Hearts.

---

## 🎨 Cuplikan Desain & Identitas Visual
*   **Edge-to-Edge Experience:** Pemanfaatan ruang layar penuh secara modern menggunakan window insets terbaru.
*   **Custom Adaptive Theme:** Pilihan tema dinamis premium terinspirasi dari iOS yang dapat diubah secara instan lewat profil pengguna:
    *   🖤 **iOS Dark Mode** (Default - Hitam pekat elegan dengan aksen Hearts Pink)
    *   🤍 **iOS Light Mode** (Bersih, minimalis, dan kontras tinggi)
    *   💜 **iOS Indigo** (Tema futuristik bernuansa deep space violet)
    *   💚 **iOS Forest** (Tema hijau rindang mewah)
*   **Interactive Info Center:** Halaman ensiklopedia H2H dengan visual latar belakang logo transparan (opacity 8%) memberikan kesan premium tanpa mengganggu keterbacaan teks.

---

## 🚀 Fitur Unggulan

### 1. 📺 Live Room & Streaming Party
*   **Native Player Integration:** Nonton video klip, konser, atau live stream resmi YouTube/HLS secara langsung di dalam aplikasi menggunakan pemutar terintegrasi.
*   **Interactive Live Chat & Stickers:** Obrolan langsung antarsesama fans lengkap dengan fitur pengiriman stiker fandom custom secara *real-time*.
*   **Live Metrics:** Indikator penonton aktif (*Live Viewer Count*) dan tombol pengiriman cinta (*Love Hearts Streamer*) interaktif.

### 2. 🧠 AI Fan Companion (Powered by Gemini API)
Bebas mengobrol dengan asisten kecerdasan buatan Gemini yang dilengkapi berbagai kepribadian khusus:
*   🎓 **Lore Scholar:** Menganalisis teori fiksi ilmiah, misteri video klip, dan jalan cerita MV Hearts2Hearts.
*   📝 **Songwriter:** Membantu menafsirkan lirik lagu, menyusun yel-yel (*fan chants*), dan membuat bait puisi semangat.
*   💖 **H2H Companion:** Teman curhat yang ramah, hangat, dan selalu mendukung hari-hari Anda.
*   *Failsafe Offline Mode:* Jika API Key belum dikonfigurasi atau kuota habis, AI secara cerdas beralih ke asisten tanggap lokal (*mock response*) sehingga aplikasi tidak pernah mengalami crash.

### 3. 📅 Koordinasi Event & RSVP Google Forms
*   **Interactive Timeline:** Pantau seluruh acara kumpul bareng (*fandom gathering*), proyek sosial, konser, hingga *streaming party* mendatang.
*   **Google Form RSVP Integration:** Tombol tindakan cepat untuk langsung mendaftar atau memberikan respons kehadiran melalui formulir Google RSVP terintegrasi.
*   **User Event Submission:** Anggota komunitas dapat mengajukan event lokal baru melalui tombol melayang **(+) Add Event** yang meluncurkan dialog formulir interaktif di dalam aplikasi.

### 4. 🗂️ Pusat Informasi Terlengkap (Information Center)
*   **Group Profile:** Sejarah pembentukan, filosofi nama, dan perjalanan karir Hearts2Hearts.
*   **Members Profile:** Foto definisi tinggi, fakta unik, golongan darah, zodiak, dan posisi masing-masing anggota.
*   **Discography:** Daftar album lengkap disertai lirik lagu dan tracklist interaktif.
*   **Fanguide:** Aturan resmi fanbase, chant konser, panduan warna lightstick, dan etika menonton pertunjukan.

### 5. 💬 Forum Komunitas & Diskusi Interaktif
*   **Topic Categories:** Obrolan terbagi rapi dalam kategori (Umum, Teori, Pembelian Album, Merchandise, dll).
*   **Upvoting System:** Naikkan diskusi terhangat agar terlihat oleh seluruh anggota fanbase di halaman terdepan.
*   **Thread Commenting:** Berinteraksi secara terstruktur pada setiap postingan forum.

### 6. 🏆 Leaderboard & Ranks
*   **User Ranks:** Tingkatkan level loyalitas Anda dari **Bronze Fan** hingga **Legendary Heart** dengan berpartisipasi aktif dalam komunitas.
*   **Daily Tasks:** Kerjakan misi harian sederhana seperti menonton siaran, berkomentar di forum, atau mengecek info terbaru untuk mendapatkan poin.

### 7. 🛠️ Admin Panel Khusus
*   **Console Manajemen:** Menu tersembunyi bagi admin untuk mengonfigurasi tautan live streaming aktif, menyebarkan pengumuman kilat, mensimulasikan status siaran langsung (*active streaming*), dan memoderasi konten secara instan.

---

## 🛠️ Arsitektur & Tech Stack

Aplikasi ini dibangun menggunakan arsitektur modern berbasis **MVVM (Model-View-ViewModel)** demi performa yang cepat, stabil, dan mudah dirawat:

*   **UI Framework:** Jetpack Compose (Kotlin DSL) dengan Material Design 3.
*   **Local Caching:** Room Database SQLite untuk penyimpanan data luring (offline-first).
*   **Async Processing:** Kotlin Coroutines & Flow (`MutableStateFlow`) untuk manajemen state yang responsif.
*   **JSON Engine:** Moshi (dengan pemetaan reflektif kustom untuk performa terbaik).
*   **Network Client:** Retrofit & OkHttp untuk konsumsi API eksternal yang cepat dan aman.
*   **Video Playback:** Google ExoPlayer (HLS Streaming) & YouTube Player Android SDK.
*   **Google Cloud Integration:** Gemini API (menggunakan JSON REST call terspesifikasi).

---

## ⚙️ Cara Menjalankan Project

### Prasyarat
*   Android Studio Jellyfish (atau yang lebih baru)
*   Android SDK 34 (Android 14)
*   Gradle 8.x+

### Langkah Instalasi

1.  **Clone Repositori:**
    ```bash
    git clone https://github.com/username/H2H-FandomApp.git
    cd H2H-FandomApp
    ```

2.  **Konfigurasi API Key (Gemini AI):**
    Aplikasi ini menggunakan API Key rahasia untuk terhubung ke kecerdasan buatan Gemini secara aman.
    *   Buka panel **Secrets** di AI Studio atau buat file `.env` di direktori utama proyek Anda:
        ```env
        GEMINI_API_KEY=AIzaSyYourActualAPIKeyHere
        ```
    *   Sistem build akan secara otomatis menyuntikkan kunci ini ke dalam `BuildConfig.GEMINI_API_KEY` saat proses kompilasi berjalan tanpa mengekspos kunci Anda ke repositori publik.

3.  **Buka & Jalankan:**
    *   Buka proyek melalui Android Studio dengan memilih file `settings.gradle.kts`.
    *   Lakukan sinkronisasi Gradle (*Sync Project with Gradle Files*).
    *   Tekan tombol **Run** untuk menginstal aplikasi di perangkat Android Anda atau emulator.

---

## 🤝 Kontribusi

Kami sangat menyambut kontribusi dari sesama Hearts untuk memperkaya fitur aplikasi ini!
1. Fork repositori ini.
2. Buat branch fitur baru (`git checkout -b fitur/NamaFitur`).
3. Commit perubahan Anda (`git commit -m 'Menambahkan fitur baru'`).
4. Push ke branch Anda (`git push origin fitur/NamaFitur`).
5. Buat Pull Request baru untuk ditinjau.

---

## 📄 Lisensi

Proyek ini dilisensikan di bawah **MIT License** - lihat file `LICENSE` untuk rincian lebih lanjut.

💖 *Dibuat dengan cinta oleh fanbase Hearts2Hearts untuk seluruh Hearts di dunia!*
