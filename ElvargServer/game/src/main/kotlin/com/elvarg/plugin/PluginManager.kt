package com.elvarg.plugin

import com.elvarg.utils.ProgressbarUtils
import io.github.classgraph.ClassGraph

object PluginManager {

    private val pkg = "com.elvarg.plugin"

    /**
     * Loads all plugins and executes the kotlin scripts
     */
	fun init() {
        ClassGraph().acceptPackages(pkg).enableClassInfo().scan().use { scanResult ->
            val pluginClassList = scanResult.getSubclasses(Script::class.java.name).directOnly()

            val pb = ProgressbarUtils.progress("Loading Plugins", pluginClassList.size.toLong())
            pluginClassList.forEach {
				try {
					it.loadClass(Script::class.java).getDeclaredConstructor().newInstance()
					pb.step()
				} catch (ex : Exception) {
					println("Error with plugin ${it.name}")
					ex.printStackTrace()
				}
            }

            pb.close()
        }
    }
}
