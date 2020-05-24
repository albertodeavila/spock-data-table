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

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import utils.ContentUtils

import javax.swing.*
import java.util.LinkedList
import java.util.List

/**
 * Generate a data table given a line with varNames and possible values for each var
 * It should have the following format: varName => [val1#val2#val3] | varName2 => [val1#val2#val3]
 */
class GenerateDataTableAction extends AnAction {

    GenerateDataTableAction() {
        super()
    }

    GenerateDataTableAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon)
    }

    @Override
    void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR)
        Project project = event.getRequiredData(CommonDataKeys.PROJECT)
        Document document = editor.document
        CaretModel caretModel = editor.caretModel

        String currentLine = ContentUtils.getCurrentLine(caretModel, document)

        List<String> charsMustContains = ['=>', '[', ']']
        if(charsMustContains.every{currentLine.contains(it)}) {
            List<String> differentCases = currentLine.split('\\|')
            List<String> varNames = []
            List<List<String>> varValues = []
            boolean syntaxError = false
            differentCases.each { String differentCase ->
                if(charsMustContains.any{!differentCase.contains(it)}) {
                    syntaxError = true
                } else {
                    String varName = differentCase.substring(0, differentCase.indexOf('=>')).trim()
                    String varValueString = differentCase.substring(differentCase.indexOf('[') + 1, differentCase.lastIndexOf(']'))
                    List<String> varValue = varValueString.split('#')
                    if(varName && varValueString && varValue) {
                        varNames << varName
                        varValues << varValue
                    } else syntaxError = true
                }
            }

            if(syntaxError) {
                Messages.showMessageDialog(project, 'There is a sintax error. Ensure that it has the following format: varName => [val1#val2#val3] | varName2 => [val1#val2#val3]', 'Can not generate data table', Messages.getErrorIcon())
                return
            }

            boolean oneColumn = varNames.size() == 1
            String prefix = currentLine.substring(0, currentLine.indexOf(varNames.first()))
            removeCaseDefinitions(document, project, varNames, caretModel, prefix)

            List<String> spockCases = []
            generateCombinations(varValues, spockCases, 0, '')

            if(spockCases.size() > 1){
                writeOtherSpockCases(document, project, caretModel, spockCases, prefix, oneColumn)
            }
        }else {
            Messages.showMessageDialog(project, 'Please, place caret on line that defines data table and ensure that it has the following format: varName => [val1#val2#val3] | varName2 => [val1#val2#val3]', 'Can not generate data table', Messages.getErrorIcon())
        }
    }

    /**
     * Write in the test the same lines with new values
     * @param document current open file
     * @param project current project
     * @param caretModel the caret
     * @param spockLines lines to repeat with another value
     * @param prefix spaces and tabs to add at the begining of line
     * @param oneColumn boolean to create a table just with one column, to add _ |
     */
    private void writeOtherSpockCases(Document document, Project project, CaretModel caretModel, List<String> spockLines, String prefix, boolean oneColumn) {
        WriteCommandAction.runWriteCommandAction(project, { ->
            caretModel.moveCaretRelatively(0, 1, false, false, false)
            spockLines.each { String spockCase ->
                String lineWithNewCase = "$prefix${oneColumn ? '_ | ' : ''}$spockCase\n"
                document.insertString(caretModel.getOffset(), lineWithNewCase)
                caretModel.moveCaretRelatively(0, 1, false, false, false)
            }
        })
    }

    /**
     * Clean the varNames line, removing the values for each column
     * @param document current open file
     * @param project current project
     * @param varNames list of var names
     * @param caretModel the caret
     * @param prefix spaces and tabs to add at the begining of line
     */
    private void removeCaseDefinitions(Document document, Project project, List<String> varNames, CaretModel caretModel, String prefix) {
        int lineNumber = document.getLineNumber(caretModel.offset)
        int beginFirstLine = document.getLineStartOffset(lineNumber)
        int endFirstLine = document.getLineEndOffset(lineNumber)
        WriteCommandAction.runWriteCommandAction(project, { ->
            document.deleteString(beginFirstLine, endFirstLine)
            document.insertString(beginFirstLine, "$prefix${varNames.size() == 1 ? '_ | ' : ''}${varNames.join(' | ')}")
        })
    }

    /**
     * Generate all posible combinations in the given result
     * @param values values to generate combinations
     * @param result the list that contains all the combinations
     * @param depth
     * @param current
     */
    void generateCombinations(List<String[]> values, List<String> result, int depth, String current) {
        if (depth == values.size()) {
            result << current
            return
        }

        values.get(depth).size().times { int index ->
            generateCombinations(values, result, depth + 1, current ? "${current} | ${values.get(depth)[index]}" : values.get(depth)[index])
        }
    }

    @Override
    void update(AnActionEvent e) {
        Project project = e.getProject()
        e.getPresentation().setEnabledAndVisible(project != null)
    }
}
