![Elmslie](https://user-images.githubusercontent.com/16104123/104534649-b5defa80-5625-11eb-98b6-d761623f8964.jpeg)
[![](https://jitpack.io/v/diklimchuk/test.svg)](https://jitpack.io/#diklimchuk/test)

[![Maven Central Version](https://img.shields.io/maven-central/v/money.vivid.elmslie/elmslie-core)](https://central.sonatype.com/artifact/money.vivid.elmslie/elmslie-core)
[![License badge](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Elmslie is a minimalistic reactive implementation of TEA/ELM written in kotlin with java support.  
Named after [George Grant Elmslie](https://en.wikipedia.org/wiki/George_Grant_Elmslie), a Scottish-born architect.

## Why?
- **Scalable and Reusable**: Built-in support for nesting components
- **Multiplatform**: Written with pure Kotlin and Coroutines, supports KMP (Android, iOS, JS)
- **Single immutable state**: Simplify state management
- **UDF**: Say no to spaghetti code with Unidirectional Data Flow

## Documentation
This is a visual representation of the architecture:
<p>
<img src="https://user-images.githubusercontent.com/16104123/115949827-40b27980-a4e0-11eb-85dc-03a7073e3127.png" width="500">
</p>



For more info head to the [wiki](https://github.com/vivid-money/elmslie/wiki)

## Samples
Samples are available [here](https://github.com/vivid-money/elmslie/tree/publish-elmslie-3.0/samples)
- Basic loader for android: [link](https://github.com/vivid-money/elmslie/tree/publish-elmslie-3.0/samples/coroutines-loader)
- Pure kotlin calculator: [link](https://github.com/vivid-money/elmslie/tree/publish-elmslie-3.0/samples/kotlin-calculator)

## Download
Library is distributed through Maven Central

#### Add repository in the root build.gradle
```kotlin
allprojects {
    repositories {
        mavenCentral()
    }
}
```

#### Add required modules:
- Core - for pure kotlin ELM implementation

`implementation 'money.vivid.elmslie:elmslie-core:{latest-version}'`

- Android - for android apps only, simplifies lifecycle handling  

`implementation 'money.vivid.elmslie:elmslie-android:{latest-version}'`


## Related articles
- Why did we select ELM? ([Russian](https://habr.com/ru/company/vivid_money/blog/534386/), [English](https://medium.com/@klimchuk.daniil/how-we-chose-presentation-layer-architecture-and-didnt-regret-it-bc694cab3e80))
- What is ELM architecture? ([Russian](https://habr.com/ru/company/vivid_money/blog/550932/))
- How to use our library? ([Russian](https://habr.com/ru/company/vivid_money/blog/553232/))
