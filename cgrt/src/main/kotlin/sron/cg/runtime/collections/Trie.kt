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

package sron.cg.runtime.collections

import java.util.*

class Trie {
    private data class TrieNode(val char: Char, var value: Int? = null) {
        val children = ArrayList<TrieNode>()

        fun getChild(char: Char): TrieNode? {
            children.forEach {
                if (char == it.char) {
                    return it
                }
            }
            return null
        }
    }

    private val root = TrieNode('\\')

    operator fun set(key: String, value: Int) {
        require(key.length != 0, { "The empty string is not a valid key" })

        var k = key;
        var node: TrieNode = root
        var child: TrieNode?

        while (k.length > 0) {
            child = node.getChild(k[0])
            if (child == null) {
                child = TrieNode(k[0])
                node.children.add(child)
            }
            node = child
            k = k.drop(1)
        }

        node.value = value
    }

    operator fun get(key: String): Int? {
        require(key.length != 0, { "The empty string is not a valid key" })

        var k = key
        var node = root;
        var child: TrieNode?

        while (k.length > 0) {
            child = node.getChild(k[0])
            if (child == null) {
                return null
            }
            node = child
            k = k.drop(1)
        }

        return node.value
    }

    fun hasKey(key: String): Boolean {
        require(key.length != 0, { "The empty string is not a valid key" })

        var k = key
        var node = root
        var child: TrieNode?

        while (k.length > 0) {
            child = node.getChild(k[0])
            if (child == null) {
                return false
            }
            node = child
            k = k.drop(1)
        }

        return node.value != null
    }

    private fun key(node: TrieNode, acc: String): List<String> {
        val result = ArrayList<String>()

        if (node.value != null) {
            result.add(acc)
        }

        result += node.children.flatMap { key(it, "$acc${it.char}") }
        return result
    }

    fun keys() = root.children.flatMap { key(it, "${it.char}") }

    private fun value(node: TrieNode): List<Int> {
        val result = ArrayList<Int>()

        if (node.value != null) {
            result.add(node.value!!)
        }

        result += node.children.flatMap { value(it) }
        return result
    }

    fun values() = root.children.flatMap { value(it) }

    private fun pair(node: TrieNode, acc: String): List<Pair<String, Int>> {
        val result = ArrayList<Pair<String, Int>>()

        if (node.value != null) {
            result.add(acc to node.value!!)
        }

        result += node.children.flatMap { pair(it, "$acc${it.char}") }
        return result
    }

    fun pairs() = root.children.flatMap { pair(it, "${it.char}") }
}
