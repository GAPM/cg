/*
 * Copyright 2016 Simón Oroño & La Universidad del Zulia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sron.cg.compiler.phase.generation.helper

import sron.cg.compiler.State

import java.io.File
import java.util.jar.Attributes
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

private fun classToByteArray(fullName: String): ByteArray {
    var result = ByteArray(0)
    val realName = "${fullName.replace('.', '/')}.class"
    ClassLoader.getSystemResourceAsStream(realName).use {
        result = it.readBytes()
    }
    return result
}

private fun dumpClass(className: String) {
    val bytes = classToByteArray(className)
    val fileName = "${className.replace(".", File.separator)}.class"
    val file = File(fileName)
    file.parentFile.mkdirs()
    file.createNewFile()
    file.outputStream().use {
        it.write(bytes)
    }
}

private fun JarOutputStream.add(source: File) {
    if (source.isDirectory) {
        var name = source.path.replace("\\", "/")
        if (!name.isEmpty()) {
            if (!name.endsWith("/")) {
                name += "/"
            }

            val entry = JarEntry(name)
            entry.time = source.lastModified()
            putNextEntry(entry)
            closeEntry()
        }

        source.listFiles().map { add(it) }
    } else {
        val entry = JarEntry(source.path.replace("\\", "/"))
        putNextEntry(entry)
        val bytes = source.inputStream().readBytes()
        write(bytes)
        closeEntry()
    }
}

fun createExec(ba: ByteArray, s: State) {
    dumpClass("sron.cg.lang.collections.BitArray")
    dumpClass("sron.cg.lang.collections.BitMatrix")
    dumpClass("sron.cg.lang.rt.Print")
    dumpClass("sron.cg.lang.rt.RT")
    dumpClass("sron.cg.lang.rt.Str")
    dumpClass("sron.cg.lang.IGraph")
    dumpClass("sron.cg.lang.Graph")
    dumpClass("sron.cg.lang.DiGraph")

    File("EntryPoint.class").outputStream().use {
        it.write(ba)
    }

    if (!s.parameters.justClass) {
        val manifest = Manifest()
        manifest.mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0")
        manifest.mainAttributes.put(Attributes.Name.MAIN_CLASS, "EntryPoint")
        val jar = JarOutputStream(File("${s.name}.jar").outputStream(), manifest)

        File("sron").listFiles().map { jar.add(it) }
        jar.add(File("EntryPoint.class"))
        jar.close()

        File("sron").deleteRecursively()
        File("EntryPoint.class").delete()
    }
}
