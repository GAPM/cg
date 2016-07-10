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

public class Str {
    public static String concat(String s1, String s2) {
        return s1 + s2;
    }

    public static boolean equal(String s1, String s2) {
        return s1.equals(s2);
    }

    public static int toInt(String s) {
        Error.setErr(ErrorType.NO_ERROR);
        int result = 0;

        try {
            result = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            Error.setErr(ErrorType.CAST_ERROR);
        }

        return result;
    }

    public static float toFloat(String s) {
        Error.setErr(ErrorType.NO_ERROR);
        float result = 0.0f;

        try {
            result = Float.parseFloat(s);
        } catch (NumberFormatException e) {
            Error.setErr(ErrorType.CAST_ERROR);
        }

        return result;
    }

    public static boolean toBool(String s) {
        Error.setErr(ErrorType.NO_ERROR);
        boolean result = false;

        switch (s) {
            case "true":
                result = true;
                break;
            case "false":
                result = false;
                break;
            default:
                Error.setErr(ErrorType.CAST_ERROR);
        }

        return result;
    }
}
