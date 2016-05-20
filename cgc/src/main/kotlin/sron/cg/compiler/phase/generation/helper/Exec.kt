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

import java.io.File

private val root = "out"

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
    val file = File("$root${File.separator}$fileName")
    file.parentFile.mkdirs()
    file.outputStream().use {
        it.write(bytes)
    }
}

fun createExec(ba: ByteArray) {
    File(root).mkdir()

    dumpClass("sron.cg.runtime.collections.BitArray")
    dumpClass("sron.cg.runtime.collections.BitMatrix")
    dumpClass("sron.cg.runtime.graph.IGraph")
    dumpClass("sron.cg.runtime.graph.Graph")
    dumpClass("sron.cg.runtime.graph.DiGraph")
    dumpClass("sron.cg.runtime.rt.Print")
    dumpClass("sron.cg.runtime.rt.Str")

    File("out${File.separator}EntryPoint.class").outputStream().use {
        it.write(ba)
    }
}
