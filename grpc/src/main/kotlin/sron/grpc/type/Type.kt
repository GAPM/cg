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

package sron.grpc.type

enum class Type {
    byte,
    short,
    int,
    long,
    float,
    double,
    bool,
    void,
    string,
    char,
    error
}

fun Type.isIntegral(): Boolean {
    return this == Type.byte || this == Type.short || this == Type.int || this == Type.long
}

fun Type.isFP(): Boolean {
    return this == Type.float || this == Type.double
}

infix fun Type.lowerOrEqual(other: Type): Boolean {
    if (this == Type.error || other == Type.error) {
        return false
    }

    if ((!this.isIntegral() && !this.isFP()) ||
            (!other.isIntegral() && !other.isFP())) {
        throw UnsupportedOperationException("Type must be integral or floating point")
    }

    if ((this.isFP() && other.isIntegral()) ||
            (this.isIntegral() && other.isFP())) {
        throw UnsupportedOperationException("Types must be from the same class")
    }

    return this <= other
}

infix fun Type.equivalent(other: Type): Boolean {
    if (this == other) {
        return true
    }

    if (this.isIntegral() && this lowerOrEqual other) {
        return true
    }

    if (this.isFP() && this lowerOrEqual other) {
        return true
    }

    return false
}

fun Type.toJVMDescriptor() = when(this) {
    Type.byte -> "B"
    Type.short -> "S"
    Type.int -> "I"
    Type.long -> "J"
    Type.float -> "F"
    Type.double -> "D"
    Type.bool -> "Z"
    Type.void -> "V"
    Type.string -> "Ljava/lang/String"
    Type.char -> "C"
    Type.error -> ""
}
