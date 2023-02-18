import org.gradle.internal.impldep.org.joda.time.tz.ZoneInfoLogger.set
import org.gradle.kotlin.dsl.accessors.AccessorFormats.default

plugins {
	java
	id("org.springframework.boot") version "3.0.2-SNAPSHOT"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.cong"
version = "web"
java.sourceCompatibility = JavaVersion.VERSION_17


repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

repositories {
	mavenLocal()
	mavenCentral()
	maven(url = "https://www.jitpack.io") {
		content {
			excludeModule("cf.wayzer", "ScriptAgent")
		}
	}

	if (System.getProperty("user.timezone") != "Asia/Shanghai")//ScriptAgent
		maven("https://maven.tinylake.tk/")
	else {
		maven {
			url = uri("https://packages.aliyun.com/maven/repository/2102713-release-0NVzQH/")
			credentials {
				username = "609f6fb4aa6381038e01fdee"
				password = "h(7NRbbUWYrN"
			}
		}
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

	implementation("org.jetbrains:annotations:20.1.0")


	val mdtXVersion = "v140.405"
	val mindustryVersion = "v141.1"

	implementation("com.github.Anuken.Mindustry:core:$mindustryVersion")
	implementation("com.github.TinyLake.MindustryX:core:$mdtXVersion")
	implementation("com.github.Anuken.Arc:arc-core:$mindustryVersion")


	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
