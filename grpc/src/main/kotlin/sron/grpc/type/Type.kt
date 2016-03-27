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
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    BOOL,
    VOID,
    STRING,
    CHAR,

    ERROR
}

fun Type.isIntegral(): Boolean {
    return this == Type.BYTE || this == Type.SHORT || this == Type.INT || this == Type.LONG
}

fun Type.isFP(): Boolean {
    return this == Type.FLOAT || this == Type.DOUBLE
}

infix fun Type.lowerOrEqual(other: Type): Boolean {
    if (this == Type.ERROR || other == Type.ERROR) {
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
    Type.BYTE -> "B"
    Type.SHORT -> "S"
    Type.INT -> "I"
    Type.LONG -> "J"
    Type.FLOAT -> "F"
    Type.DOUBLE -> "D"
    Type.BOOL -> "Z"
    Type.VOID -> "V"
    Type.STRING -> "Ljava/lang/String"
    Type.CHAR -> "C"

    else -> ""
}
