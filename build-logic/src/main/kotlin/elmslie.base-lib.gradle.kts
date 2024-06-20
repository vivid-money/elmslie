import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

tasks.withType<JavaCompile> {
    targetCompatibility = JvmTarget.toString()
    sourceCompatibility = JvmTarget.toString()
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = JvmTarget.toString() }
