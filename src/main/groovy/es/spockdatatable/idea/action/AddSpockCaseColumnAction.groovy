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
import utils.ContentUtils

import javax.swing.*

/**
 * Create a button in Tools that allow to create a column in a data table with multiple values
 * It's needed to select the text to create the new column. It should has the following format: varName => [val1#val2#val3]
 */
class AddSpockCaseColumnAction extends AnAction {

    AddSpockCaseColumnAction() {
        super()
    }

    AddSpockCaseColumnAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon)
    }

    @Override
    void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR)
        Document document = editor.document
        Project project = event.getRequiredData(CommonDataKeys.PROJECT)
        CaretModel caretModel = editor.caretModel

        String selectedText = editor.selectionModel.selectedText
        String currentLine = ContentUtils.getCurrentLine(caretModel, document)
        List<String> charsMustContains = ['=>', '[', ']']
        if(selectedText && charsMustContains.every{ selectedText.contains(it) } && !selectedText.contains('|')) {
            String allValues = selectedText.substring(selectedText.indexOf('[') + 1, selectedText.lastIndexOf(']'))
            if(allValues && allValues.trim() != '') {
                List<String> values = allValues.split('#')
                Integer lineNumber = document.getLineNumber(caretModel.offset)
                removeCaseDefinitions(document, project, currentLine, lineNumber, selectedText)

                caretModel.moveCaretRelatively(0, 1, false, false, false)
                lineNumber++

                List<String> spockCases = []
                while (lineNumber < document.lineCount) {
                    String line = ContentUtils.getCurrentLine(caretModel, document)
                    if (isSpockCaseLine(line)) {
                        String lineWithNewValue = line + ' | ' + values[0]
                        addColumnValueToExistentLine(document, project, lineWithNewValue, lineNumber, spockCases, line, caretModel)
                        lineNumber++
                    } else if (ContentUtils.isTestFinalLine(line) || ContentUtils.endOfFile(document, caretModel)) { //End of method or eof, exit loop
                        lineNumber = document.lineCount + 1
                    } else { //Empty line, move to next line
                        lineNumber++
                        if(lineNumber < document.lineCount) caretModel.moveToOffset(document.getLineStartOffset(lineNumber))
                    }
                }

                if (values.size() > 1) {
                    writeOtherSpockCases(document, project, caretModel, values, spockCases)
                }
            } else {
                Messages.showMessageDialog(project, 'You must specify values to add the column. The format should be: varName => [val1#val2#val3]', 'Can not Add Column', Messages.getErrorIcon())
            }
        } else {
            Messages.showMessageDialog(project, 'Please, select a data table column and ensure that it has the following format: varName => [val1#val2#val3]', 'Can not Add Column', Messages.getErrorIcon())
        }
    }

    /**
     * Add the given lineWithNewValue replacing the existing line
     * @param document the file open
     * @param project the current project
     * @param lineWithNewValue line with the new value added
     * @param lineNumber the current line number
     * @param spockCases list of lines with the data already defined
     * @param line the current line
     * @param caretModel the caret
     */
    private void addColumnValueToExistentLine(Document document, Project project, String lineWithNewValue, Integer lineNumber, List<String> spockCases, String line, CaretModel caretModel) {
        spockCases << line
        int beginLine = document.getLineStartOffset(lineNumber)
        int endLine = document.getLineEndOffset(lineNumber)
        WriteCommandAction.runWriteCommandAction(project, { ->
            document.deleteString(beginLine, endLine)
            document.insertString(beginLine, lineWithNewValue)
        })
        caretModel.moveToOffset(document.getLineStartOffset(lineNumber + 1))
    }

    /**
     * Add other data cases when the values to add are more than one.
     * Then it copy all lines for each value (unless the first one, that it is already added)
     * @param document the file open
     * @param project the current project
     * @param caretModel the caret
     * @param values values to add
     * @param spockCases list of lines with the data already defined
     */
    private void writeOtherSpockCases(Document document, Project project, CaretModel caretModel, List<String> values, List<String> spockCases) {
        values[1..-1].each { String value->
            WriteCommandAction.runWriteCommandAction(project, { ->
                document.insertString(caretModel.offset, '\n')
                caretModel.moveCaretRelatively(0, 1, false, false, false)
                spockCases.each { String spockCase ->
                    String lineWithNewCase = "$spockCase | $value\n"
                    document.insertString(caretModel.offset, lineWithNewCase)
                    caretModel.moveCaretRelatively(0, 1, false, false, false)
                }
            })
        }
    }

    /**
     * Remove the values to add in the var names line
     * @param document the current open file
     * @param project the current project
     * @param currentLine the current line
     * @param lineNumber the current line number
     * @param selectedText the selected text
     */
    private void removeCaseDefinitions(Document document, Project project, String currentLine, int lineNumber, String selectedText) {
        int beginFirstLine = document.getLineStartOffset(lineNumber)
        int endFirstLine = document.getLineEndOffset(lineNumber)
        WriteCommandAction.runWriteCommandAction(project, { ->
            document.deleteString(beginFirstLine, endFirstLine)
            String previousVars = currentLine.substring(0, currentLine.indexOf(selectedText))
            String varName = selectedText.substring(0, selectedText.indexOf('=>')).trim()
            String nextVars = currentLine.substring(currentLine.indexOf(selectedText) + selectedText.size())
            document.insertString(beginFirstLine, "$previousVars$varName$nextVars")
        })
    }

    /**
     * Check if given line is an data table case.
     * It ensures that the line contains something more than spaces, tabs, and end brace
     * @param line the line to check
     */
    private boolean isSpockCaseLine(String line) {
        line.replaceAll('\\r|\\n| |}', '').size() > 0 && line.contains('|')
    }


    @Override
    void update(AnActionEvent e) {
        Project project = e.getProject()
        e.getPresentation().setEnabledAndVisible(project != null)
    }
}
