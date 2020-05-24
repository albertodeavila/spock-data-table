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

class RemoveSpockCaseColumnActionTest extends BasePlatformTestCase{

    void testFormatFailNoSelection(){
        given: 'a test to remove a column'
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
        and: 'override the showMessageDialog method to get the string message to show'
        String messageShown
        String titleShown
        Messages.metaClass.static.showMessageDialog = { Project project, String message, String title, Icon icon ->
            messageShown = message
            titleShown = title
        }

        when: 'try to remove the column'
        myFixture.testAction(new RemoveSpockCaseColumnAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not Remove Column'
        assert messageShown == 'Please, select a data table column'
    }

    void testFormatFailMoreThanOneColSelection(){
        given: 'a test to remove a column'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                a | <selection>b | c<caret></selection>
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

        when: 'try to remove the column'
        myFixture.testAction(new RemoveSpockCaseColumnAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not Remove Column'
        assert messageShown == 'Please, select a data table column'
    }

    void testFormatFailWrongSelection(){
        given: 'a test to remove a column'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                myVar1 | m<selection>yvar2<caret></selection> | c
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

        when: 'try to remove the column'
        myFixture.testAction(new RemoveSpockCaseColumnAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not Remove Column'
        assert messageShown == 'Please, select a data table column'
    }

    void testFormatFailSelectMoreThanAColumnName(){
        given: 'a test to remove a column'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                myVar1 <selection>| myvar2<caret></selection> | c
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

        when: 'try to remove the column'
        myFixture.testAction(new RemoveSpockCaseColumnAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not Remove Column'
        assert messageShown == 'Please, select a data table column'
    }

    void testFormatFailSelectAColumnNameAndASpace(){
        given: 'a test to remove a column'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c
        
                where:
                myVar1 |<selection> myvar2<caret></selection> | c
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

        when: 'try to remove the column'
        myFixture.testAction(new RemoveSpockCaseColumnAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not Remove Column'
        assert messageShown == 'Please, select a data table column'
    }

    void testRemoveFirstColum() {
        given: 'a test to remove a column'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c + d
        
                where:
                <selection>a<caret></selection> | b | c | d
                0 | 0 | 0 | 0
                1 | 1 | 2 | 0
                0 | 1 | 1 | 0
                1 | 0 | 1 | 0
                2 | 2 | 4 | 0
                4 | 5 | 9 | 0
            }
        """)

        when: 'try to remove the column'
        myFixture.testAction(new RemoveSpockCaseColumnAction())

        then:
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c + d
        
                where:
                b | c | d
                0 | 0 | 0
                1 | 2 | 0
                1 | 1 | 0
                0 | 1 | 0
                2 | 4 | 0
                5 | 9 | 0
            }
        """)
    }

    void testRemoveSecondColum() {
        given: 'a test to remove a column'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c + d
        
                where:
                a | <selection>b<caret></selection> | c | d
                0 | 0 | 0 | 0
                1 | 1 | 2 | 0
                0 | 1 | 1 | 0
                1 | 0 | 1 | 0
                2 | 2 | 4 | 0
                4 | 5 | 9 | 0
            }
        """)

        when: 'try to remove the column'
        myFixture.testAction(new RemoveSpockCaseColumnAction())

        then:
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c + d
        
                where:
                a | c | d
                0 | 0 | 0
                1 | 2 | 0
                0 | 1 | 0
                1 | 1 | 0
                2 | 4 | 0
                4 | 9 | 0
            }
        """)
    }

    void testRemoveThirdColum() {
        given: 'a test to remove a column'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c + d
        
                where:
                a | b | <selection>c<caret></selection> | d
                0 | 0 | 0 | 0
                1 | 1 | 2 | 0
                0 | 1 | 1 | 0
                1 | 0 | 1 | 0
                2 | 2 | 4 | 0
                4 | 5 | 9 | 0
            }
        """)

        when: 'try to remove the column'
        myFixture.testAction(new RemoveSpockCaseColumnAction())

        then:
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c + d
        
                where:
                a | b | d
                0 | 0 | 0
                1 | 1 | 0
                0 | 1 | 0
                1 | 0 | 0
                2 | 2 | 0
                4 | 5 | 0
            }
        """)
    }

    void testRemoveLastColum() {
        given: 'a test to remove a column'
        myFixture.configureByText('myTest.groovy', """
            void "Sample test"() {
                expect:
                a + b == c + d
        
                where:
                a | b | c | <selection>d<caret></selection>
                0 | 0 | 0 | 0
                1 | 1 | 2 | 0
                0 | 1 | 1 | 0
                1 | 0 | 1 | 0
                2 | 2 | 4 | 0
                4 | 5 | 9 | 0
            }
        """)

        when: 'try to remove the column'
        myFixture.testAction(new RemoveSpockCaseColumnAction())

        then:
        myFixture.checkResult("""
            void "Sample test"() {
                expect:
                a + b == c + d
        
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
}
