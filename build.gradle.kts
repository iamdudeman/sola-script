plugins {
  id("idea")
  id("java-library")
  id("maven-publish")
}

version = "0.1.0"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }

  withSourcesJar()
  withJavadocJar()
}

repositories {
  mavenCentral()
}

dependencies {
  // nullability annotations
  api("org.jspecify:jspecify:1.0.0")

  // unit testing
  testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
  testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    events("passed", "skipped", "failed")
  }
}

tasks.javadoc {
  options.memberLevel = JavadocMemberLevel.PROTECTED
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = "technology.sola.script"
      artifactId = "sola-script"
      version = version

      from(components["java"])
    }
  }
}

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

tasks.register("distFatJar", Jar::class) {
  group = "distribution"
  description = "Bundles fat JAR for distribution"
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  manifest {
    attributes["Main-Class"] = "technology.sola.script.SolaScriptMain"
  }

  val dependencies = configurations.getByName("runtimeClasspath").map(::zipTree)

  from(dependencies)
  with(project.tasks.getByName("jar", CopySpec::class))
  destinationDirectory.set(file("${project.rootDir}/dist"))
  dependsOn(configurations.getByName("runtimeClasspath"))
}
