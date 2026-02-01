# WordMaster - Kelime Avı Oyunu 🎮

WordMaster, kullanıcıların verilen ipuçlarından (anlamlardan) yola çıkarak doğru kelimeleri bulmaya çalıştığı, eğlenceli ve öğretici bir Android kelime bulmaca oyunudur.

## 📱 Proje Hakkında

Bu uygulama, kelime dağarcığını geliştirmek ve hafızayı tazelemek isteyen kullanıcılar için tasarlanmıştır. Kullanıcıya bir kelimenin anlamı ipucu olarak verilir ve karışık harfler veya boşluklar arasından doğru kelimeyi bulması istenir. Modern Android teknolojileri kullanılarak geliştirilen bu proje, akıcı animasyonlar ve kullanıcı dostu bir arayüz sunar.




![WhatsApp Image 2025-12-22 at 22 20 00 (1)](https://github.com/user-attachments/assets/fa1435c3-410c-4d51-b87c-2ffd8c690b30)

![WhatsApp Image 2025-12-22 at 22 20 00](https://github.com/user-attachments/assets/b021c7d4-ad60-4484-853e-38f0193adb7b)





## ✨ Özellikler

*   **Zengin Kelime Hazinesi**: JSON tabanlı geniş bir kelime ve anlam veritabanı.
*   **Seviye Sistemi**: Kolaydan zora doğru ilerleyen oyun yapısı.
*   **İpucu Sistemi**: Zorlanılan kelimelerde yardımcı olacak ipuçları.
*   **Puan ve Profil**: Kullanıcı ilerlemesini takip eden profil ekranı.
*   **Ayarlar**: Müzik ve ses efektlerini açıp kapatma imkanı.
*   **Modern Arayüz**: Jetpack Compose ile tasarlanmış şık ve dinamik ekranlar.
*   **Animasyonlar**: Lottie kütüphanesi ile güçlendirilmiş görsel geri bildirimler (Doğru/Yanlış cevap animasyonları).
*   **Reklam Entegrasyonu**: AdMob ile reklam gösterimi.

## 🛠️ Teknolojiler ve Kütüphaneler

Bu proje aşağıdaki modern Android geliştirme teknolojilerini kullanmaktadır:

*   **Dil**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetbrains/compose) - Modern, bildirime dayalı UI araç seti.
*   **Mimari**: MVVM (Model-View-ViewModel) - Temiz ve test edilebilir kod yapısı.
*   **Navigation**: Jetpack Compose Navigation - Ekranlar arası geçiş yönetimi.
*   **Animasyon**: [Lottie](https://airbnb.io/lottie/#/) - Yüksek kaliteli JSON tabanlı animasyonlar.
*   **Reklam**: Google AdMob SDK.
*   **Veri Yönetimi**: JSON Parsing (GSON veya Kotlin Serialization).
*   **Ses**: Android MediaPlayer API.

## 📂 Proje Yapısı

```
com.keremsen.wordmaster
├── MainActivity.kt      # Uygulama giriş noktası
├── model                # Veri modelleri (Kelime, Kullanıcı vb.)
├── navigation           # Navigasyon grafiği ve rotalar
├── ui                   # Tema ve ortak UI bileşenleri
├── utils                # Yardımcı sınıflar ve sabitler
├── view                 # Ekranlar (Screen)
│   ├── MainScreen.kt    # Ana Menü
│   ├── LevelScreen.kt   # Oyun Ekranı
│   ├── ProfileScreen.kt # Profil Ekranı
│   ├── ResultScreen.kt  # Sonuç Ekranı
│   └── SettingScreen.kt # Ayarlar
└── viewmodel            # İş mantığı (State yönetimi)
```

## 🚀 Kurulum

Projeyi yerel makinenizde çalıştırmak için şu adımları izleyin:

1.  **Projeyi Klonlayın**:
    ```bash
    git clone https://github.com/kullaniciadi/WordHuntApp-Android.git
    ```
2.  **Android Studio'da Açın**:
    *   Android Studio'yu başlatın ve "Open" seçeneği ile projenin kök klasörünü seçin.
3.  **Senkronizasyon**:
    *   Gradle dosyalarının senkronize olmasını bekleyin.
4.  **Çalıştırın**:
    *   Bir emülatör veya fiziksel Android cihaz bağlayın.
    *   "Run" (Yeşil oynatma butonu) tuşuna basarak uygulamayı yükleyin.

## 🎮 Nasıl Oynanır?

1.  Ana menüden **Oyna** butonuna basın.
2.  Ekranda beliren **Kelime Anlamını** okuyun.
3.  Harfleri kullanarak doğru kelimeyi tahmin etmeye çalışın.
4.  Doğru bilirseniz bir sonraki kelimeye geçersiniz ve puan kazanırsınız.
5.  Takıldığınızda ipucu butonunu kullanabilirsiniz.

---
*Geliştirici: Kerem Şen*
