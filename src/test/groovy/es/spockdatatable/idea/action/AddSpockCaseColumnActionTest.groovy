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

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.testFramework.fixtures.BasePlatformTestCase

import javax.swing.Icon

class AddSpockCaseColumnActionTest extends BasePlatformTestCase{

    void testFormatFailNoValues(){
        given: 'a test to generate a new column without values'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | <selection>d=>[]</selection>
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)

        and: 'override the showMessageDialog method to get the string message to show'
        String messageShown
        String titleShown
        Messages.metaClass.static.showMessageDialog = { Project project, String message, String title, Icon icon ->
            messageShown = message
            titleShown = title
        }

        when: 'try generate the column'
        myFixture.testAction(new AddSpockCaseColumnAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not Add Column'
        assert messageShown == 'You must specify values to add the column. The format should be: varName => [val1#val2#val3]'
    }

    void testFormatFailNoStartBracket(){
        given: 'a test to generate a new column without start bracket'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | <selection>d=>1#2]</selection>
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)
        and: 'override the showMessageDialog method to get the string message to show'
        String messageShown
        String titleShown
        Messages.metaClass.static.showMessageDialog = { Project project, String message, String title, Icon icon ->
            messageShown = message
            titleShown = title
        }

        when: 'try generate the column'
        myFixture.testAction(new AddSpockCaseColumnAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not Add Column'
        assert messageShown == 'Please, select a data table column and ensure that it has the following format: varName => [val1#val2#val3]'
    }

    void testFormatFailNoEndBracket(){
        given: 'a test to generate a new column without end bracket'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | <selection>d=>[1#2</selection>
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)
        and: 'override the showMessageDialog method to get the string message to show'
        String messageShown
        String titleShown
        Messages.metaClass.static.showMessageDialog = { Project project, String message, String title, Icon icon ->
            messageShown = message
            titleShown = title
        }

        when: 'try generate the column'
        myFixture.testAction(new AddSpockCaseColumnAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not Add Column'
        assert messageShown == 'Please, select a data table column and ensure that it has the following format: varName => [val1#val2#val3]'
    }

    void testFormatFailNoSelection(){
        given: 'a test to generate a new column without selection'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | d=>[1#2]
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)
        and: 'override the showMessageDialog method to get the string message to show'
        String messageShown
        String titleShown
        Messages.metaClass.static.showMessageDialog = { Project project, String message, String title, Icon icon ->
            messageShown = message
            titleShown = title
        }

        when: 'try generate the column'
        myFixture.testAction(new AddSpockCaseColumnAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not Add Column'
        assert messageShown == 'Please, select a data table column and ensure that it has the following format: varName => [val1#val2#val3]'
    }

    void testAddColumnWithOneValue() {
        given: 'a test to generate a new column with one value'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | <selection><caret>d=>[1]</selection>
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)

        when: 'generate the table'
        myFixture.testAction(new AddSpockCaseColumnAction())

        then: 'check the data table generated'
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | d
                0 | 0 | 0 | 1
                1 | 1 | 2 | 1
                0 | 1 | 1 | 1
                1 | 0 | 1 | 1
                2 | 2 | 4 | 1
                4 | 5 | 9 | 1
            }
        """)
    }

    void testAddColumnWithTwoValues() {
        given: 'a test to generate a new column wit 2 values'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | <selection><caret>d=>[1#2]</selection>
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)

        when: 'generate the table'
        myFixture.testAction(new AddSpockCaseColumnAction())

        then: 'check the data table generated'
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | d
                0 | 0 | 0 | 1
                1 | 1 | 2 | 1
                0 | 1 | 1 | 1
                1 | 0 | 1 | 1
                2 | 2 | 4 | 1
                4 | 5 | 9 | 1

                0 | 0 | 0 | 2
                1 | 1 | 2 | 2
                0 | 1 | 1 | 2
                1 | 0 | 1 | 2
                2 | 2 | 4 | 2
                4 | 5 | 9 | 2
            }
        """)
    }

    void testAddColumnWithOneValueWithBreaklines() {
        given: 'a test to generate a new column with one value and break lines'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | <selection><caret>d=>[1]</selection>
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)

        when: 'generate the table'
        myFixture.testAction(new AddSpockCaseColumnAction())

        then: 'check the data table generated'
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | d
                0 | 0 | 0 | 1
                1 | 1 | 2 | 1
                0 | 1 | 1 | 1
                
                1 | 0 | 1 | 1
                2 | 2 | 4 | 1
                4 | 5 | 9 | 1
            }
        """)
    }

    void testAddColumnWithTwoValuesWithBreaklines() {
        given: 'a test to generate a new column wit 2 values and break lines'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | <selection><caret>d=>[1#2]</selection>
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)

        when: 'generate the table'
        myFixture.testAction(new AddSpockCaseColumnAction())

        then: 'check the data table generated'
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | d
                0 | 0 | 0 | 1
                1 | 1 | 2 | 1
                0 | 1 | 1 | 1
                
                1 | 0 | 1 | 1
                2 | 2 | 4 | 1
                4 | 5 | 9 | 1

                0 | 0 | 0 | 2
                1 | 1 | 2 | 2
                0 | 1 | 1 | 2
                1 | 0 | 1 | 2
                2 | 2 | 4 | 2
                4 | 5 | 9 | 2
            }
        """)
    }

    void testAddColumnWithTwoValuesOneSpace() {
        given: 'a test to generate a new column wit 2 values'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | <selection><caret>d=>[1# ]</selection>
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)

        when: 'generate the table'
        myFixture.testAction(new AddSpockCaseColumnAction())

        then: 'check the data table generated'
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c | d
                0 | 0 | 0 | 1
                1 | 1 | 2 | 1
                0 | 1 | 1 | 1
                1 | 0 | 1 | 1
                2 | 2 | 4 | 1
                4 | 5 | 9 | 1

                0 | 0 | 0 |  
                1 | 1 | 2 |  
                0 | 1 | 1 |  
                1 | 0 | 1 |  
                2 | 2 | 4 |  
                4 | 5 | 9 |  
            }
        """)
    }
}
