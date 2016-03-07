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

package sron.grpc.util

import sun.misc.Unsafe

object Unsafe {
    private val unsafeConstructor = Unsafe::class.java.getDeclaredConstructor()
    private lateinit var unsafe: Unsafe

    init {
        unsafeConstructor.isAccessible = true
        unsafe = unsafeConstructor.newInstance()
    }

    fun addressSize() = unsafe.addressSize()
}
