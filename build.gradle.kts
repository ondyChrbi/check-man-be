import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.10"
}

group = "cz.fei.upce"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-graphql")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("com.graphql-java:graphql-java-extended-scalars:20.0")
	implementation("org.springframework.hateoas:spring-hateoas:1.4.2")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.liquibase:liquibase-core")
	implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.8")
	implementation("org.springdoc:springdoc-openapi-hateoas:1.6.8")
	implementation("org.springdoc:springdoc-openapi-security:1.6.8")
	implementation("org.springdoc:springdoc-openapi-ui:1.6.8")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("cz.jirutka.rsql:rsql-parser:2.1.0")
	compileOnly("org.projectlombok:lombok")
	implementation("com.graphql-java:graphql-java-extended-scalars:20.0")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.postgresql:r2dbc-postgresql")
	runtimeOnly("org.springdoc:springdoc-openapi-kotlin:1.6.8")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	runtimeOnly("org.aspectj:aspectjweaver:1.9.9.1")
	testImplementation("io.r2dbc:r2dbc-h2")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springdoc:springdoc-openapi-webmvc-core:1.6.8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:3.1.2")
	implementation("io.netty:netty-resolver-dns-native-macos:4.1.75.Final") {
		artifact {
			classifier = "osx-aarch_64"
		}
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

sourceSets.getByName("test") {
	java.srcDir("src/test/kotlin/unit")
	java.srcDir("src/test/kotlin/intg")
}
