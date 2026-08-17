/**
 * Precompiled [htmlib.repositories.gradle.kts][Htmlib_repositories_gradle] script plugin.
 *
 * @see Htmlib_repositories_gradle
 */
public
class Htmlib_repositoriesPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Htmlib_repositories_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
