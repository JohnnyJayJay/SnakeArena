plugins {
   java
   application
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

tasks {
    compileJava.get().options.encoding = "UTF-8"

}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_15
}

application {
    mainClassName = "game.Game"
}
