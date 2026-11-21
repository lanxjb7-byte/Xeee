# LIXXZY SECURITY

Android native Java starter project.

## Build APK dengan GitHub Actions
1. Upload seluruh isi ZIP ini ke repository GitHub.
2. Buka tab **Actions**.
3. Pilih **Build LIXXZY SECURITY APK** lalu tekan **Run workflow** (atau push ke branch `main`).
4. Setelah selesai, buka hasil workflow dan unduh artifact `LIXXZY-SECURITY-APK`.

Package: `com.lixxzy.security`

## Batasan penting
- Versi ini menerima `ACTION_PACKAGE_ADDED` dari Android lalu memeriksa metadata dan daftar permission aplikasi yang baru dipasang.
- Tidak menjalankan aplikasi yang baru dipasang, tidak melakukan dynamic malware analysis, dan tidak mengklaim bisa mendeteksi semua virus/ransomware.
- Permission sensitif bukan bukti malware; aplikasi sah juga dapat memerlukannya.
- Tombol copot membuka dialog uninstall resmi Android dan membutuhkan persetujuan pengguna.
- Versi awal ini tidak memindai isi file APK atau memeriksa reputasi hash ke layanan antivirus.


## Logo
Logo yang kamu kirim sudah dipasang sebagai ikon launcher dan gambar header aplikasi.
