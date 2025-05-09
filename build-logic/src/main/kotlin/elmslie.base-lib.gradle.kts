import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

tasks.withType<JavaCompile> {
  targetCompatibility = JvmVersion.toString()
  sourceCompatibility = JvmVersion.toString()
}

tasks.withType<KotlinCompile> { compilerOptions { jvmTarget.set(JvmTarget) } }
