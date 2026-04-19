# Lifelog-fts (Deprecated)

> [!CAUTION]
> **이 모듈은 더 이상 사용되지 않습니다.**  
> 이 기능에 관심이 있다면 오픈소스 라이브러리 **[hanfts](https://github.com/M1n9yu23/hanfts)** 를 사용하세요.
 
API와 코드 품질을 전면 개선하여 **[hanfts](https://github.com/M1n9yu23/hanfts)** 라는 이름으로 Maven Central에 오픈소스 라이브러리를 배포했습니다.  
LifeLog 프로젝트는 이제 내부 NDK 모듈 대신 Maven Central에 배포된 `hanfts` 라이브러리를 의존성으로 가져와 사용합니다.

## hanfts 사용하기

```kotlin
dependencies {
    implementation("io.github.m1n9yu23:hanfts:<version>")
}
```

`<version>`은 [최신 릴리스](https://github.com/M1n9yu23/hanfts/releases)를 확인하세요.

자세한 사용법은 **[hanfts README](https://github.com/M1n9yu23/hanfts#readme)** 를 참고하세요.

## License

```
Copyright 2026 Gyugle

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
