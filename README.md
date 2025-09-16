# 🛍️ Shop App (Android + Kotlin)

## 📌 프로젝트 소개
Shop App은 **네이버 쇼핑 API**를 활용한 안드로이드 쇼핑 애플리케이션입니다.  
사용자는 상품을 검색하고, 상세 정보를 확인하며, 장바구니와 찜 기능을 통해 관리할 수 있습니다.  
또한 회원가입, 로그인, 마이페이지, 관리자(Admin) 화면까지 포함한 **e-commerce 데모 앱**입니다.  

---

## 🏗️ 아키텍처
- **언어/플랫폼**: Kotlin, Android
- **UI**: Jetpack Compose + Material3
- **DB**: Room (Entity, Dao, Paging3)
- **네트워크**: Retrofit2 + Naver Shop API
- **아키텍처 패턴**: MVVM + Repository
- **DI**: Service Locator

---

## ✨ 주요 기능
- 🔍 상품 검색 (네이버 쇼핑 API 연동)
- 📑 상품 상세 보기 (Product Detail Sheet)
- ❤️ 찜(Like) 기능
- 🛒 장바구니 기능
- 👤 회원가입 / 로그인 / 마이페이지
- 🔑 관리자(Admin) 화면
- 🌐 인앱 웹뷰 (상품 원문 보기)

---

## 🗂️ 폴더 구조
```plaintext
Shop/
├─ app/src/main/java/com/example/shop/
│  ├─ core/            # 앱 설정, Application, 네트워크 API
│  ├─ data/            # DB, Entity, Dao, Repository
│  ├─ ui/              # UI (Compose Screens)
│  │   ├─ auth/        # 로그인, 회원가입, 관리자, 마이페이지
│  │   ├─ main/        # 메인 화면
│  │   ├─ search/      # 검색, 상품 상세
│  │   ├─ web/         # 웹뷰
│  │   └─ theme/       # 테마, 스타일
│  ├─ ShopApplication.kt
│  ├─ ShopRepository.kt
│  ├─ AuthViewModel.kt / ShopViewModel.kt
│
├─ app/src/main/res/
│  ├─ layout/          # activity_main.xml
│  ├─ values/          # colors.xml, strings.xml, themes.xml
│  └─ drawable/        # 아이콘, 이미지 리소스
