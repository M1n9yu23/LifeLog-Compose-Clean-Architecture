# Custom Gradle Build Logic 

## 디렉토리 구조

```text
root/
├── build-logic/
│   ├── settings.gradle.kts
│   └── convention/
│       ├── build.gradle.kts
│       └── src/main/kotlin/
│           ├── ProjectExt.kt             ← 공통 확장 프로퍼티 (libs 등)
│           ├── AndroidExt.kt             ← Android 공통 설정
│           ├── AndroidApplicationConventionPlugin.kt
│           ├── AndroidLibraryConventionPlugin.kt
│           └── kts/                      ← Precompiled Script 예제 (선택)
├── gradle/
│   └── libs.versions.toml               
├── app/
│   └── build.gradle.kts                 
└── settings.gradle.kts                  
```

## 1단계: 루트 `settings.gradle.kts` 설정

루트 프로젝트에서 `build-logic`을 composite build로 포함합니다.

```kotlin
// root/settings.gradle.kts
pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
```

## 2단계: `build-logic/settings.gradle.kts` 설정

루트의 Version Catalog(`libs.versions.toml`)를 `build-logic`에서도 사용할 수 있도록 연결합니다.

```kotlin
// build-logic/settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
```

## 3단계: `libs.versions.toml` 플러그인 의존성 추가

Convention Plugin 작성에 필요한 Gradle 플러그인(Android, Kotlin 등)을 정의합니다.

```toml
# gradle/libs.versions.toml
[versions]
agp = "8.13.0"
kotlin = "2.2.21"

[libraries]
android-gradlePlugin = { group = "com.android.tools.build", name = "gradle", version.ref = "agp" }
kotlin-gradlePlugin = { group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version.ref = "kotlin" }
# 기타 필요한 플러그인 추가 (예: KSP, Hilt 등)
```

## 4단계: `build-logic/convention/build.gradle.kts` 설정

플러그인 의존성을 추가하고, 구현할 커스텀 플러그인을 등록합니다.

```kotlin
// build-logic/convention/build.gradle.kts
plugins {
    `kotlin-dsl`
}

group = "com.bossmg.android.buildlogic"

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    // 기타 의존성 추가
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// 플러그인 등록
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "bossmg.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "bossmg.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        // 기타 필요한 플러그인 등록
    }
}
```

## 5단계: 확장 프로퍼티 및 공통 설정 함수 작성

여러 플러그인에서 중복되는 로직을 분리합니다.

### `ProjectExt.kt` (libs 참조용)

```kotlin
// build-logic/convention/src/main/kotlin/ProjectExt.kt
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
```

### `AndroidExt.kt` (Android 공통 설정)

```kotlin
// build-logic/convention/src/main/kotlin/AndroidExt.kt
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = 36

        defaultConfig {
            minSdk = 26
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
```

## 6단계: 커스텀 플러그인 클래스 작성

`Plugin<Project>` 인터페이스를 구현하여 모듈 형태에 맞는 설정을 적용합니다.

### `AndroidApplicationConventionPlugin.kt`

```kotlin
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 36
            }
        }
    }
}
```

### `AndroidLibraryConventionPlugin.kt`

```kotlin
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 36
            }
        }
    }
}
```

## 7단계: `libs.versions.toml` 커스텀 플러그인 등록

프로젝트 모듈에서 커스텀 플러그인을 쉽게 적용할 수 있도록 등록합니다.

```toml
# gradle/libs.versions.toml
[plugins]
# 커스텀 플러그인
bossmg-android-application = { id = "bossmg.android.application", version = "unspecified" }
bossmg-android-library = { id = "bossmg.android.library", version = "unspecified" }
# 기타 생성한 플러그인 추가
```

## 8단계: 모듈에서 플러그인 사용

각 모듈별로 필요한 커스텀 플러그인만 선언하여 복잡한 빌드 설정을 대체합니다.

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.bossmg.android.application)
}

android {
    namespace = "com.bossmg.android.lifelog"
    defaultConfig {
        applicationId = "com.bossmg.android.lifelog"
        versionCode = 1
        versionName = "1.0"
    }
}
```

```kotlin
// core/data/build.gradle.kts
plugins {
    alias(libs.plugins.bossmg.android.library)
}

android {
    namespace = "com.bossmg.android.data"
}
```

---

## (참고) Precompiled Script (`.gradle.kts`) 방식

순수 Kotlin 클래스 외에도 `.gradle.kts` 스크립트 파일 자체를 플러그인으로 사용할 수 있습니다. 파일명이 곧 플러그인 ID가 됩니다. 

파일 위치 예: `build-logic/convention/src/main/kotlin/bossmg.android.application.gradle.kts`

### `bossmg.android.application.gradle.kts`

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

configure<ApplicationExtension> {
    configureKotlinAndroid(this)
    defaultConfig.targetSdk = 36
}
```

### `bossmg.android.library.gradle.kts`

```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

configure<LibraryExtension> {
    configureKotlinAndroid(this)
    defaultConfig.targetSdk = 36
}
```

### 두 방식 비교

| 항목 | Precompiled Script (`.gradle.kts`) | 순수 Kotlin (`Plugin<Project>`) |
|---|---|---|
| **파일 위치** | `src/main/kotlin/{id}.gradle.kts` | `src/main/kotlin/XxxPlugin.kt` |
| **플러그인 ID** | 파일명으로 자동 지정 (`{id}` 부분이 ID가 됨) | `build.gradle.kts`의 `gradlePlugin` 블록에서 수동 등록 |
| **Version Catalog (`libs`) 접근**| **자동 생성된 접근자**로 별도 설정 없이 바로 사용 가능 | `Project`에 대한 **확장 프로퍼티**를 수동으로 만들어야 함 |
| **Android DSL 가독성** | 기존 `build.gradle.kts`와 유사 | `extensions.configure<...>{}` 등 Gradle API 사용 필요 |
| **마이그레이션 난이도** | 쉬움  | Gradle API 및 Plugin 구조에 대한 이해가 조금 필요함 |
| **구조화 및 확장성** | 스크립트가 커질수록 로직이 방대해져 관리가 어려움 | 객체지향 설계로 복잡한 로직 분리에 유리함 |
