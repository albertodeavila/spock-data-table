/*
 * Copyright (c) 2020. Alberto De Ávila Hernández <alberto.deavila.hernandez@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package groovy

import spock.lang.Specification


/**
 * Sample test to check each plugin feature
 * To check uncomment and use the feature you want to try
 */
class SampleTest extends Specification {

    void "Sample to add a column with one value or multiple"() {
        expect:
        a + b == c + d

        where:
         a | b | c //| d => [[1, 2]#[3, 4]]
         0 | 0 | 0
         1 | 1 | 2
         0 | 1 | 1
         1 | 0 | 1
         2 | 2 | 4
         4 | 5 | 9
    }


    void "Sample to remove columns with different sizes and to show # cases ignoring blank lines"() {
        expect:
        a + b == c

        where:
         a  | b      | c
         0  | 0L     | 0
         1  | 1      | 2
         0  | [1, 2] | 1
         1  | 0      | 1L

         2  | 2      | { -> 2 }
         4L | 5      | 9
    }


    void "Sample to generate data table with a field"() {
        expect:
        1 * 2 == 2

        //where:
        //d => [true#false]
    }

    void "Sample to generate data table with multiple flields"() {
        expect:
        1 * 2 == 2

        //where:
        //d => [true#false] | asdf => [1#2] | myVar => [{ a -> a * 2 }#{ b -> b + 3 } ] | myList => [ new Integer (2)#new Integer (3) ]
    }


    void "Sample to remove a column"() {
        expect:
        a + b == c

        where:
         a | b | c | d
         0 | 0 | 3 | 7
         1 | 1 | 2 | 7
         0 | 1 | 1 | 7
         1 | 0 | 1 | 7
         2 | 2 | 4 | 7
         4 | 5 | 9 | 7
    }

    void "Sample to replace a column value"() {
        expect:
        a + b == c

        where:
         a | b      | c/*[true=>1]*/ | myClosure          | myVal
         1 | [1, 2] | true          | { e -> 2 + e }     | 1
         1 | [1, 2] | true          | { e -> 4 + e * 2 } | 1
         1 | [1, 2] | false         | { e -> 2 + e }     | 1
         1 | [1, 2] | false         | { e -> 4 + e * 2 } | 1
         1 | [2, 3] | true          | { e -> 2 + e }     | 1
         1 | [2, 3] | true          | { e -> 4 + e * 2 } | 1
         1 | [2, 3] | false         | { e -> 2 + e }     | 1
         1 | [2, 3] | false         | { e -> 4 + e * 2 } | 1
         1 | [3, 4] | true          | { e -> 2 + e }     | 1
         1 | [3, 4] | true          | { e -> 4 + e * 2 } | 1
         1 | [3, 4] | false         | { e -> 2 + e }     | 1
         1 | [3, 4] | false         | { e -> 4 + e * 2 } | 1
         2 | [1, 2] | true          | { e -> 2 + e }     | 1
         2 | [1, 2] | true          | { e -> 4 + e * 2 } | 1
         2 | [1, 2] | false         | { e -> 2 + e }     | 1
         2 | [1, 2] | false         | { e -> 4 + e * 2 } | 1
         2 | [2, 3] | true          | { e -> 2 + e }     | 1
         2 | [2, 3] | true          | { e -> 4 + e * 2 } | 1
         2 | [2, 3] | false         | { e -> 2 + e }     | 1
         2 | [2, 3] | false         | { e -> 4 + e * 2 } | 1
         2 | [3, 4] | true          | { e -> 2 + e }     | 1
         2 | [3, 4] | true          | { e -> 4 + e * 2 } | 1
         2 | [3, 4] | false         | { e -> 2 + e }     | 1
         2 | [3, 4] | false         | { e -> 4 + e * 2 } | 1
         3 | [1, 2] | true          | { e -> 2 + e }     | 1
         3 | [1, 2] | true          | { e -> 4 + e * 2 } | 1
         3 | [1, 2] | false         | { e -> 2 + e }     | 1
         3 | [1, 2] | false         | { e -> 4 + e * 2 } | 1
         3 | [2, 3] | true          | { e -> 2 + e }     | 1
         3 | [2, 3] | true          | { e -> 4 + e * 2 } | 1
         3 | [2, 3] | false         | { e -> 2 + e }     | 1
         3 | [2, 3] | false         | { e -> 4 + e * 2 } | 1
         3 | [3, 4] | true          | { e -> 2 + e }     | 1
         3 | [3, 4] | true          | { e -> 4 + e * 2 } | 1
         3 | [3, 4] | false         | { e -> 2 + e }     | 1
         3 | [3, 4] | false         | { e -> 4 + e * 2 } | 1
    }
}