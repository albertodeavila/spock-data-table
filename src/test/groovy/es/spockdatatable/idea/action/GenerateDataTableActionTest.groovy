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

class GenerateDataTableActionTest extends BasePlatformTestCase {

    void testGenerateDataTableWithOneColumn() {
        given: 'a line with format to generate a table with just one column'
        myFixture.configureByText('myTest.groovy', """ 
            <caret>a => [true#false]
        """)

        when: 'generate the table'
        myFixture.testAction(new GenerateDataTableAction())

        then: 'check data table generated is what expected'
        myFixture.checkResult(""" 
            _ | a
            _ | false
            _ | true
        """)
    }

    void testGenerateDataTableWithTwoColumns() {
        given: 'a line with format to generate a table with two columns'
        myFixture.configureByText('myTest.groovy', """ 
            <caret>a => [true#false] | b => [1#2]
        """)

        when: 'generate the table'
        myFixture.testAction(new GenerateDataTableAction())

        then: 'check data table generated is what expected'
        myFixture.checkResult(""" 
            a | b
            false | 1
            false | 2
            true | 1
            true | 2
        """)
    }

    void testGenerateDataTableWithThreeColumns() {
        given: 'a line with format to generate a table with three columns'
        myFixture.configureByText('myTest.groovy', """ 
            <caret>a => [true#false] | b =>[1#2] | c=>[[1,2]#[3,4]] 
        """)

        when: 'generate the table'
        myFixture.testAction(new GenerateDataTableAction())

        then: 'check data table generated is what expected'
        myFixture.checkResult(""" 
            a | b | c
            false | 1 | [1,2]
            false | 1 | [3,4]
            false | 2 | [1,2]
            false | 2 | [3,4]
            true | 1 | [1,2]
            true | 1 | [3,4]
            true | 2 | [1,2]
            true | 2 | [3,4]
        """)
    }

    void testGenerateDataTableWithClosureValues() {
        given: 'a line with format to generate a table with three columns'
        myFixture.configureByText('myTest.groovy', """ 
            <caret>a => [true#false] | b =>[1#2] | c=>[{ -> "foo"}#{ -> "var"}] 
        """)

        when: 'generate the table'
        myFixture.testAction(new GenerateDataTableAction())

        then: 'check data table generated is what expected'
        myFixture.checkResult(""" 
            a | b | c
            false | 1 | { -> "foo"}
            false | 1 | { -> "var"}
            false | 2 | { -> "foo"}
            false | 2 | { -> "var"}
            true | 1 | { -> "foo"}
            true | 1 | { -> "var"}
            true | 2 | { -> "foo"}
            true | 2 | { -> "var"}
        """)
    }

    void testFormatFailNoArrow(){
        given: 'a line with format to generate a table with three columns'
        myFixture.configureByText('myTest.groovy', '<caret>a [true#false] | b [1#2]')

        and: 'override the showMessageDialog method to get the string message to show'
        String messageShown
        String titleShown
        Messages.metaClass.static.showMessageDialog = { Project project, String message, String title, Icon icon ->
            messageShown = message
            titleShown = title
        }

        when: 'generate the table'
        myFixture.testAction(new GenerateDataTableAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not generate data table'
        assert messageShown == 'Please, place caret on line that defines data table and ensure that it has the following format: varName => [val1#val2#val3] | varName2 => [val1#val2#val3]'
    }

    void testFormatFailNoName(){
        given: 'a line with format to generate a table with three columns'
        myFixture.configureByText('myTest.groovy', '<caret>=>[true#false] | b =>[1#2] | c=>[{ -> "foo"}#{ -> "var"}]')

        and: 'override the showMessageDialog method to get the string message to show'
        String messageShown
        String titleShown
        Messages.metaClass.static.showMessageDialog = { Project project, String message, String title, Icon icon ->
            messageShown = message
            titleShown = title
        }

        when: 'generate the table'
        myFixture.testAction(new GenerateDataTableAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not generate data table'
        assert messageShown == 'There is a sintax error. Ensure that it has the following format: varName => [val1#val2#val3] | varName2 => [val1#val2#val3]'
    }

    void testFormatFailNoValues(){
        given: 'a line with format to generate a table with three columns'
        myFixture.configureByText('myTest.groovy', '<caret>a=>[] | b =>[1#2] | c=>[{ -> "foo"}#{ -> "var"}]')

        and: 'override the showMessageDialog method to get the string message to show'
        String messageShown
        String titleShown
        Messages.metaClass.static.showMessageDialog = { Project project, String message, String title, Icon icon ->
            messageShown = message
            titleShown = title
        }

        when: 'generate the table'
        myFixture.testAction(new GenerateDataTableAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not generate data table'
        assert messageShown == 'There is a sintax error. Ensure that it has the following format: varName => [val1#val2#val3] | varName2 => [val1#val2#val3]'
    }

    void testFormatFailNoStartBracket(){
        given: 'a line with format to generate a table with three columns'
        myFixture.configureByText('myTest.groovy', '<caret>a=>1#2] | b =>1#2] ')

        and: 'override the showMessageDialog method to get the string message to show'
        String messageShown
        String titleShown
        Messages.metaClass.static.showMessageDialog = { Project project, String message, String title, Icon icon ->
            messageShown = message
            titleShown = title
        }

        when: 'generate the table'
        myFixture.testAction(new GenerateDataTableAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not generate data table'
        assert messageShown == 'Please, place caret on line that defines data table and ensure that it has the following format: varName => [val1#val2#val3] | varName2 => [val1#val2#val3]'
    }

    void testFormatFailNoEndBracket(){
        given: 'a line with format to generate a table with three columns'
        myFixture.configureByText('myTest.groovy', '<caret>a=>[1#2 | b =>[1#2 | c=>[{ -> "foo"}#{ -> "var"}')

        and: 'override the showMessageDialog method to get the string message to show'
        String messageShown
        String titleShown
        Messages.metaClass.static.showMessageDialog = { Project project, String message, String title, Icon icon ->
            messageShown = message
            titleShown = title
        }

        when: 'generate the table'
        myFixture.testAction(new GenerateDataTableAction())

        then: 'check a message dialog appears and has the following title and message'
        assert titleShown == 'Can not generate data table'
        assert messageShown == 'Please, place caret on line that defines data table and ensure that it has the following format: varName => [val1#val2#val3] | varName2 => [val1#val2#val3]'
    }
}
