![Elmslie](https://user-images.githubusercontent.com/16104123/104534649-b5defa80-5625-11eb-98b6-d761623f8964.jpeg)
[![](https://jitpack.io/v/diklimchuk/test.svg)](https://jitpack.io/#diklimchuk/test)

[![Jitpack badge](https://jitpack.io/v/vivid-money/elmslie.svg)](https://jitpack.io/#vivid-money/elmslie)
[![Code quality badge](https://github.com/vivid-money/elmslie/actions/workflows/codequality.yml/badge.svg?branch=main&event=push)](https://github.com/vivid-money/elmslie/actions/workflows/codequality.yml)
[![License badge](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Elmslie is a minimalistic reactive implementation of TEA/ELM written in kotlin with java support.  
Named after [George Grant Elmslie](https://en.wikipedia.org/wiki/George_Grant_Elmslie), a Scottish-born architect.

## Why?
- **Scalable and Reusable**: Built-in support for nesting components
- **Reactive**: Written with RxJava3, but has compatibility mode with RxJava2
- **Single immutable state**: Simplify state management
- **UDF**: Say no to spaghetti code with Unidirectional Data Flow

## Documentation
This is a visual representation of the architecture:
<p>
<img src="https://user-images.githubusercontent.com/16104123/115949827-40b27980-a4e0-11eb-85dc-03a7073e3127.png" width="500">
</p>



For more info head to the [wiki](https://github.com/vivid-money/elmslie/wiki)

## Samples
Samples are available [here](https://github.com/vivid-money/elmslie/tree/main/elmslie-samples)
- Basic loader for android: [link](https://github.com/vivid-money/elmslie/tree/main/elmslie-samples/android-loader)
- Pure kotlin calculator: [link](https://github.com/vivid-money/elmslie/tree/main/elmslie-samples/kotlin-calculator)
- Pure java notes: [link](https://github.com/vivid-money/elmslie/tree/main/elmslie-samples/java-notes)
- Paging with compose: [link](https://github.com/vivid-money/elmslie/tree/main/elmslie-samples/compose-paging)

## Download
Library is distributed through JitPack

#### Add repository in the root build.gradle
```
allprojects {
 repositories {
    maven { url "https://jitpack.io" }
 }
}
```

#### Add required modules:
- Core - for pure kotlin ELM implementation

`implementation 'com.github.vivid-money.elmslie:elmslie-core:{latest-version}'`

- Android - for android apps only, simplifies lifecycle handling  

`implementation 'com.github.vivid-money.elmslie:elmslie-android:{latest-version}'`

- RxJava 2 - compatibility module (more info in the wiki [article](https://github.com/vivid-money/elmslie/wiki/RxJava-2-vs-3))

`implementation 'com.github.vivid-money.elmslie:elmslie-rxjava-2:{latest-version}'`  

- Jetpack Compose - for android apps only, simplifies using jetpack compose (not required)

`implementation 'com.github.vivid-money.elmslie:elmslie-compose:{latest-version}'`


## Related articles
- [Why did we select ELM? (Russian)](https://habr.com/ru/company/vivid_money/blog/534386/)
- [What is ELM architecture? (Russian)](https://habr.com/ru/company/vivid_money/blog/550932/)
- [How to use our library? (Russian)](https://habr.com/ru/company/vivid_money/blog/553232/)
- [ELM. Pt1 (English)](https://proandroiddev.com/taming-state-in-android-with-elm-architecture-and-kotlin-part-1-566caae0f706)
- [ELM. Pt2 (English)](https://proandroiddev.com/taming-state-in-android-with-elm-architecture-and-kotlin-part-2-c709f75f7596)
- [ELM. Pt3 (English)](https://proandroiddev.com/taming-state-in-android-with-elm-architecture-and-kotlin-part-2-c709f75f7596)
