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

package es.spockdatatable.idea.action

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class CommentSpockCasesActionTest extends BasePlatformTestCase {

    void testShowNumberCases() {
        given: 'a test with data table '
        myFixture.configureByText('myTest.groovy', """ 
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)

        when: 'comment number cases'
        myFixture.testAction(new CommentSpockCasesAction())

        then:
        myFixture.checkResult(""" 
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                /*___#___*/a | b | c
                /*___0___*/0 | 0 | 0
                /*___1___*/1 | 1 | 2
                /*___2___*/0 | 1 | 1
                /*___3___*/1 | 0 | 1
                /*___4___*/2 | 2 | 4
                /*___5___*/4 | 5 | 9
            }
        """)
    }

    void testShowNumberCasesWithBreakLines() {
        given: 'a test with break lines in data date'
        myFixture.configureByText('myTest.groovy', """ 
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c
                0 | 0 | 0
                1 | 1 | 2
                
                0 | 1 | 1
                1 | 0 | 1
                
                2 | 2 | 4
                4 | 5 | 9
            }
        """)

        when: 'comment number cases'
        myFixture.testAction(new CommentSpockCasesAction())

        then:
        myFixture.checkResult(""" 
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                /*___#___*/a | b | c
                /*___0___*/0 | 0 | 0
                /*___1___*/1 | 1 | 2
                
                /*___2___*/0 | 1 | 1
                /*___3___*/1 | 0 | 1
                
                /*___4___*/2 | 2 | 4
                /*___5___*/4 | 5 | 9
            }
        """)
    }

    void testShowNumberCasesWithLinesWithComments() {
        given: 'a test with comments '
        myFixture.configureByText('myTest.groovy', """ 
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c
                0 | 0 | 0 // comment
                1 | 1 | 2 /* coment */
                0 | 2 | 2 /* comment
                */
                2 | 0/*comment*/ | 2 
            }
        """)

        when: 'comment number cases'
        myFixture.testAction(new CommentSpockCasesAction())

        then:
        myFixture.checkResult(""" 
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                /*___#___*/a | b | c
                /*___0___*/0 | 0 | 0 // comment
                /*___1___*/1 | 1 | 2 /* coment */
                /*___2___*/0 | 2 | 2 /* comment
                */
                /*___3___*/2 | 0/*comment*/ | 2 
            }
        """)
    }

    void testShowNumberCasesWithClosures() {
        given: 'a test with closures'
        myFixture.configureByText('myTest.groovy', """ 
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c
                0 | 0 | { -> 0}
                1 | 1 | { -> 2}
                0 | 1 | { -> 1}
                1 | 0 | { -> 1}
                2 | 2 | { -> 4}
                4 | 5 | { -> 9}
            }
        """)

        when: 'comment number cases'
        myFixture.testAction(new CommentSpockCasesAction())

        then:
        myFixture.checkResult(""" 
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                /*___#___*/a | b | c
                /*___0___*/0 | 0 | { -> 0}
                /*___1___*/1 | 1 | { -> 2}
                /*___2___*/0 | 1 | { -> 1}
                /*___3___*/1 | 0 | { -> 1}
                /*___4___*/2 | 2 | { -> 4}
                /*___5___*/4 | 5 | { -> 9}
            }
        """)
    }

    void testShowNumberCasesWithLotOfCases() {
        given: 'a test with data table '
        myFixture.configureByFile('SampleSpockTestWithLotOfCases.groovy')

        when: 'comment number cases'
        myFixture.testAction(new CommentSpockCasesAction())

        then:
        myFixture.checkResultByFile('SampleSpockTestWithLotOfCasesCommented.groovy')
    }

    @Override
    protected String getTestDataPath() {
        "testdata/commentCases"
    }
}
