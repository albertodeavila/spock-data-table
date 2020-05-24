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

import javax.swing.*

class ReplaceColumnValueActionTest extends BasePlatformTestCase{

    void testFormatFailNoSelection(){
        given: 'a test to replace a value in a column'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b [0=>7] | c
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

        when: 'try to replace the column value'
        myFixture.testAction(new ReplaceColumnValueAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not replace column value'
        assert messageShown == 'Please, select a data table column and ensure that it has the following format: varName [oldVal=>newVal]'
    }

    void testFormatFailNoValues(){
        given: 'a test to try to replace a column value without values'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | <selection>c[]</selection>
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
        myFixture.testAction(new ReplaceColumnValueAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not replace column value'
        assert messageShown == 'Please, select a data table column and ensure that it has the following format: varName [oldVal=>newVal]'
    }

    void testFormatFailNoStartBracket(){
        given: 'a test to try to replace a column value without start bracket'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | <selection>c0=>5]</selection>
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

        when: 'try to replace the column value'
        myFixture.testAction(new ReplaceColumnValueAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not replace column value'
        assert messageShown == 'Please, select a data table column and ensure that it has the following format: varName [oldVal=>newVal]'
    }

    void testFormatFailNoEndBracket(){
        given: 'a test to generate a new column without end bracket'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | <selection>c=>[1#2</selection>
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

        when: 'try to replace the column value'
        myFixture.testAction(new ReplaceColumnValueAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not replace column value'
        assert messageShown == 'Please, select a data table column and ensure that it has the following format: varName [oldVal=>newVal]'
    }

    void testFormatFailNoValueToReplace(){
        given: 'a test to try to replace a column value without a value to replace'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | <selection>c[=>5]</selection>
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

        when: 'try to replace the column value'
        myFixture.testAction(new ReplaceColumnValueAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not replace column value'
        assert messageShown == 'Please, select a data table column and ensure that it has the following format: varName [oldVal=>newVal]'
    }

    void testFormatFailNoValueToReplaceWith(){
        given: 'a test to try to replace a column value without a value to replace'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | <selection>c[1=>]</selection>
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

        when: 'try to replace the column value'
        myFixture.testAction(new ReplaceColumnValueAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not replace column value'
        assert messageShown == 'Please, select a data table column and ensure that it has the following format: varName [oldVal=>newVal]'
    }

    void testReplaceValueAtFirstColumn(){
        given: 'a test to replace a column value'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                <selection>c[1=>7]<caret></selection> | b | c
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)

        when: 'replace the column value'
        myFixture.testAction(new ReplaceColumnValueAction())

        then: 'check the value has been replaced'
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                c | b | c
                0 | 0 | 0
                7 | 1 | 2
                0 | 1 | 1
                7 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)
    }

    void testReplaceValueAtSecondColumn(){
        given: 'a test to replace a column value'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | <selection>b[1=>7]<caret></selection> | c
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)

        when: 'replace the column value'
        myFixture.testAction(new ReplaceColumnValueAction())

        then: 'check the value has been replaced'
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c
                0 | 0 | 0
                1 | 7 | 2
                0 | 7 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)
    }

    void testReplaceValueAtLastColumn(){
        given: 'a test to replace a column value'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | <selection>c[1=>7]<caret></selection>
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)

        when: 'replace the column value'
        myFixture.testAction(new ReplaceColumnValueAction())

        then: 'check the value has been replaced'
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 7
                1 | 0 | 7
                2 | 2 | 4
                4 | 5 | 9
            }
        """)
    }

    void testReplaceValueThatDoesNotExist(){
        given: 'a test to replace a column value'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | <selection>c[7=>5]<caret></selection>
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)

        when: 'replace the column value'
        myFixture.testAction(new ReplaceColumnValueAction())

        then: 'check the table is the same, but without the replace value'
        myFixture.checkResult("""
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
    }

    void testReplaceValueInATableWithOneColumn(){
        given: 'a test to replace a column value'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                _ | <selection>c[1=>5]<caret></selection>
                _ | 0
                _ | 1
                _ | 0
                _ | 1
                _ | 2
                _ | 4
            }
        """)

        when: 'replace the column value'
        myFixture.testAction(new ReplaceColumnValueAction())

        then: 'check the table is the same, but without the replace value'
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                _ | c
                _ | 0
                _ | 5
                _ | 0
                _ | 5
                _ | 2
                _ | 4
            }
        """)
    }

    void testFormatFailWithWrongSelection(){
        given: 'a test to try to replace a column value with a worng selection'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | <selection>b | c[1=>7]</selection>
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
        myFixture.testAction(new ReplaceColumnValueAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not replace column value'
        assert messageShown == 'Please, select a data table column and ensure that it has the following format: varName [oldVal=>newVal]'
    }


    void testReplaceValueWithAList(){
        given: 'a test to replace a column value'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | <selection>c[1=>[1, 2]]<caret></selection>
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | 1
                1 | 0 | 1
                2 | 2 | 4
                4 | 5 | 9
            }
        """)

        when: 'replace the column value'
        myFixture.testAction(new ReplaceColumnValueAction())

        then: 'check the table is the same, but without the replace value'
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | b | c
                0 | 0 | 0
                1 | 1 | 2
                0 | 1 | [1, 2]
                1 | 0 | [1, 2]
                2 | 2 | 4
                4 | 5 | 9
            }
        """)
    }
}
