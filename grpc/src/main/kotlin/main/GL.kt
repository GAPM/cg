package main

import java.io.File

fun findExec(name: String): String? {
    val path = System.getenv("PATH")
    val dirs = path.split(File.pathSeparator)

    var f: File
    for (dir in dirs) {
        f = File(dir, name)
        if (f.exists() && f.isFile && f.canExecute()) {
            return f.absolutePath
        }
    }
    return null
}

fun bothSideCrop(s: String): String {
    try {
        return s.substring(1, s.length - 1)
    } catch (e: IndexOutOfBoundsException) {
        return ""
    }
}