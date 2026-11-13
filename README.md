# 📚 Data Siswa — Room Database (Pertemuan 9)

Aplikasi Android sederhana untuk **menyimpan dan menampilkan data siswa secara offline** menggunakan **Room Database**, dibangun dengan **Jetpack Compose** dan arsitektur **MVVM + Repository**.

> Proyek ini merupakan tugas Pertemuan 9 yang membahas penyimpanan data lokal dengan Room (Entity, DAO, Database) yang dihubungkan ke UI Compose melalui ViewModel dan Flow.

---

## ✨ Fitur

- **Daftar Siswa** — menampilkan semua siswa dalam bentuk kartu, otomatis terurut berdasarkan nama (A–Z).
- **Tambah Siswa** — form input *Nama*, *Alamat*, dan *Telpon*.
- **Validasi Input** — tombol *Submit* hanya aktif jika ketiga field sudah terisi.
- **Update Otomatis** — daftar langsung ter-refresh setelah data baru disimpan (berkat `Flow`).
- **Tampilan Kosong** — muncul pesan petunjuk jika belum ada data siswa.
- **Penyimpanan Offline** — data tersimpan permanen di database SQLite lokal.

---

## 📸 Screenshot

| Home (Kosong) | Home (Ada Data) | Tambah Siswa |
|:---:|:---:|:---:|
| <img width="367" height="811" alt="Screenshot 2026-11-13 185838" src="https://github.com/user-attachments/assets/3f8f56ff-5210-4f9b-8ef3-62ab62411918" /> | <img width="366" height="804" alt="Screenshot 2026-11-13 185928" src="https://github.com/user-attachments/assets/ae6e566b-db70-417e-a06c-8d1db78d61b6" /> | <img width="370" height="810" alt="Screenshot 2026-11-13 185955" src="https://github.com/user-attachments/assets/0b388941-e734-41b0-9322-d38e78a3957e" /> |

---

## 🛠️ Teknologi yang Digunakan

| Komponen | Keterangan | Versi |
|---|---|---|
| Bahasa | Kotlin | 2.2.10 |
| UI | Jetpack Compose + Material 3 | BOM 2026.02.01 |
| Database | Room (`room-runtime`, `room-ktx`) | 2.8.4 |
| Annotation Processor | KSP | 2.3.6 |
| Navigasi | Navigation Compose | 2.9.0 |
| State | ViewModel + Lifecycle Compose | 2.10.0 |
| Asynchronous | Kotlin Coroutines + Flow | — |
| Build | Android Gradle Plugin | 9.3.3 |

**Konfigurasi SDK:** `minSdk 24` · `targetSdk 36` · `compileSdk 37`
**Application ID:** `com.example.activity7`

---

## 🏗️ Arsitektur

Aplikasi memakai pola **MVVM** dengan lapisan **Repository** di tengah, sehingga UI tidak pernah berbicara langsung ke database.

```
UI (Compose)  ->  ViewModel  ->  Repository  ->  DAO  ->  Room (SQLite)
HomeScreen        HomeViewModel  RepositoriSiswa SiswaDao  siswa_database
EntrySiswaScreen  EntryViewModel

          <-- data mengalir kembali ke UI lewat Flow / State -->
```

### 💡 Analogi singkat

Bayangkan **restoran**:

| Di Restoran | Di Aplikasi |
|---|---|
| Pelanggan membaca menu & memesan | **UI (Compose)** |
| Pelayan menyampaikan pesanan | **ViewModel** |
| Manajer yang mengatur dapur mana yang dipakai | **Repository** |
| Juru masak yang tahu cara mengambil bahan | **DAO** (`@Dao`) |
| Gudang bahan makanan | **Room Database** |
| Bentuk bahan yang disimpan | **Entity** (`@Entity`) |

---

## 📂 Struktur Proyek

```
app/src/main/java/com/example/activity7/
├── AplikasiSiswa.kt              # Class Application, menyimpan container dependensi
├── MainActivity.kt               # Entry point, memanggil SiswaApp()
│
├── room/                         # Lapisan data (Room)
│   ├── Siswa.kt                  #   Entity -> tabel "tblSiswa"
│   ├── SiswaDao.kt               #   DAO -> insert() & getAllSiswa()
│   └── DatabaseSiswa.kt          #   Database singleton ("siswa_database")
│
├── repositori/                   # Lapisan repository
│   ├── RepositoriSiswa.kt        #   Interface + OfflineRepositoriSiswa
│   └── ContainerApp.kt           #   Container dependensi (manual DI)
│
├── viewmodel/                    # Lapisan logika UI
│   ├── HomeViewModel.kt          #   StateFlow daftar siswa
│   ├── EntryViewModel.kt         #   State form, validasi, simpan data
│   └── PenyediaViewModel.kt      #   Factory pembuat ViewModel
│
├── view/                         # Lapisan tampilan (Compose)
│   ├── HalamanHome.kt            #   Layar daftar siswa
│   ├── HalamanEntry.kt           #   Layar form tambah siswa
│   └── AppBar.kt                 #   TopAppBar kustom
│
├── uicontroller/                 # Navigasi
│   ├── DestinasiNavigasi.kt      #   Interface route + judul
│   ├── DestinasiHome.kt          #   route = "home"
│   ├── DestinasiEntry.kt         #   route = "item_entry"
│   └── PetaNavigasi.kt           #   NavHost (SiswaApp & HostNavigasi)
│
└── ui/theme/                     # Warna, tipografi, tema Material 3
```

---

## 🗃️ Skema Database

**Nama database:** `siswa_database` (versi 1)
**Nama tabel:** `tblSiswa`

| Kolom | Tipe | Keterangan |
|---|---|---|
| `id` | `INTEGER` | Primary key, *auto generate* |
| `nama` | `TEXT` | Nama siswa |
| `alamat` | `TEXT` | Alamat siswa |
| `telpon` | `TEXT` | Nomor telpon siswa |

**Query yang tersedia (`SiswaDao`):**

```kotlin
@Insert(onConflict = OnConflictStrategy.IGNORE)
suspend fun insert(siswa: Siswa)

@Query("SELECT * from tblSiswa ORDER BY nama ASC")
fun getAllSiswa(): Flow<List<Siswa>>
```

---

## 🔄 Alur Kerja Aplikasi

**Menampilkan data**
1. `HomeScreen` mengamati `HomeViewModel.homeUiState`.
2. `HomeViewModel` mengumpulkan `Flow<List<Siswa>>` dari repository.
3. Setiap ada perubahan di tabel, Room otomatis mengirim data terbaru sehingga UI ter-update.

**Menambah data**
1. User menekan tombol **+** (FAB) lalu masuk ke `EntrySiswaScreen`.
2. Setiap ketikan memanggil `updateUIState()` dan menjalankan validasi.
3. Tombol **Submit** aktif jika `nama`, `alamat`, dan `telpon` tidak kosong.
4. `saveSiswa()` menyimpan data lewat repository, DAO, lalu Room, kemudian kembali ke Home.

---

## 🚀 Cara Menjalankan

### Prasyarat
- **Android Studio** versi terbaru (yang mendukung AGP 9.x)
- **JDK 11** atau lebih baru
- Emulator atau perangkat Android dengan **API 24+**
- Koneksi internet saat *sync* pertama (untuk mengunduh dependensi)

### Langkah-langkah

1. **Clone repository**
   ```bash
   git clone https://github.com/AhmadNawfalRamadhan/0028_RoomDatabase.git
   ```
2. **Buka di Android Studio**
   `File` → `Open` → pilih folder hasil clone.
3. **Tunggu Gradle Sync** sampai selesai (lihat progres di bagian bawah Android Studio).
4. **Siapkan device**
   Buat emulator lewat *Device Manager*, atau sambungkan HP dengan USB debugging aktif.
5. **Jalankan aplikasi**
   Klik tombol ▶️ **Run 'app'** (atau `Shift + F10`).
6. **Coba aplikasinya**
   - Tekan tombol **+** di pojok kanan bawah.
   - Isi Nama, Alamat, dan Telpon, lalu tekan **Submit**.
   - Data akan muncul di daftar dan tetap ada walau aplikasi ditutup.

---

## 🔮 Rencana Pengembangan

String resource untuk fitur berikut sudah disiapkan di `strings.xml`, tetapi logikanya belum diimplementasikan:

- [ ] Halaman **Detail Siswa**
- [ ] **Edit** data siswa
- [ ] **Hapus** data siswa (dengan dialog konfirmasi)
- [ ] Fungsi tombol *Back* di halaman Tambah Siswa

---

## 👤 Author

**Ahmad Nawfal Ramadhan**
GitHub: [@AhmadNawfalRamadhan](https://github.com/AhmadNawfalRamadhan)
