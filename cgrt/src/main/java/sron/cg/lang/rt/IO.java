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

package sron.cg.lang.rt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IO {
    public static void print(int i) {
        System.out.println(i);
    }

    public static void print(float f) {
        System.out.println(f);
    }

    public static void print(boolean b) {
        System.out.println(b);
    }

    public static void print(Object o) {
        System.out.println(o.toString());
    }

    public static String read() {
        String result;
        try (InputStreamReader isr = new InputStreamReader(System.in)) {
            try (BufferedReader br = new BufferedReader(isr)) {
                result = br.readLine();
            } catch (IOException e) {
                result = "";
            }
        } catch (IOException e) {
            result = "";
        }

        return result;
    }
}