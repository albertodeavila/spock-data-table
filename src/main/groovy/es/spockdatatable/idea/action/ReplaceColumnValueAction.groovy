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
 * Replace a value in a column for a new value
 * It's needed to have a selected text with the following format varName [oldValue=>newValue]
 */
class ReplaceColumnValueAction extends AnAction {

    ReplaceColumnValueAction() {
        super()
    }

    ReplaceColumnValueAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon)
    }

    @Override
    void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR)
        Project project = event.getRequiredData(CommonDataKeys.PROJECT)
        Document document = editor.document
        CaretModel caretModel = editor.caretModel

        String selectedText = editor.selectionModel.selectedText
        String currentLine = ContentUtils.getCurrentLine(caretModel, document)

        List<String> charsMustContains = ['=>', '[', ']']
        if(selectedText && charsMustContains.every{ currentLine.contains(it) } && currentLine.contains('|')) {
            int totalColumns = currentLine.count( '|' ) + 1
            int column = ContentUtils.getColumn(currentLine, selectedText)

            String oldValue = selectedText.substring(selectedText.indexOf('[') + 1, selectedText.indexOf('=>')).trim()
            String newValue = selectedText.substring(selectedText.indexOf('=>') + 2, selectedText.lastIndexOf(']')).trim()
            if(oldValue && newValue) {
                Integer lineNumber = document.getLineNumber(caretModel.offset)
                String prefix = currentLine.substring(0, currentLine.indexOf(selectedText))

                removeCaseDefinitions(document, project, selectedText, currentLine, caretModel, prefix)

                while (lineNumber <= document.lineCount) {
                    String line = ContentUtils.getCurrentLine(caretModel, document)
                    if (line.contains('|')) {
                        replaceValueInLine(document, project, caretModel, column, totalColumns, oldValue, newValue, lineNumber, line, prefix)
                        lineNumber++
                    } else if (ContentUtils.isTestFinalLine(line) || ContentUtils.endOfFile(document, caretModel)) {
                        lineNumber = document.lineCount + 1
                    } else {
                        lineNumber++
                        if(lineNumber < document.lineCount) caretModel.moveToOffset(document.getLineStartOffset(lineNumber))
                    }
                }
            } else {
                Messages.showMessageDialog(project, 'Please, you must specify and old value and a new value in the following format: varName [oldVal=>newVal]', 'Can not replace column value', Messages.getErrorIcon())
            }
        } else {
            Messages.showMessageDialog(project, 'Please, select a data table column and ensure that it has the following format: varName [oldVal=>newVal]', 'Can not replace column value', Messages.getErrorIcon())
        }
    }

    /**
     * Clean the varNames line, removing the values for each column
     * @param document current open file
     * @param project current project
     * @param selectedText selected text with the column name and the values
     * @param line the line with var names
     * @param caretModel the caret
     * @param prefix spaces and tabs to add at the begining of line
     */
    private void removeCaseDefinitions(Document document, Project project, String selectedText, String line, CaretModel caretModel, String prefix) {
        int lineNumber = document.getLineNumber(caretModel.offset)
        int beginFirstLine = document.getLineStartOffset(lineNumber)
        int endFirstLine = document.getLineEndOffset(lineNumber)

        String lineWithoutValues = line.substring(0, line.indexOf(selectedText) + selectedText.indexOf('[')) +
                line.substring(line.indexOf(selectedText) + selectedText.size())

        WriteCommandAction.runWriteCommandAction(project, { ->
            document.deleteString(beginFirstLine, endFirstLine)
            document.insertString(beginFirstLine, lineWithoutValues)
        })
    }

    /**
     * Replace the line with the new value
     * @param document current open file
     * @param project current project
     * @param caretModel the caret
     * @param column column number to replace value
     * @param totalColumns column total number
     * @param oldValue old value to replace
     * @param newValue new value to replace the old one
     * @param lineNumber current number line
     * @param line current line
     */
    private void replaceValueInLine(Document document, Project project, CaretModel caretModel, int column, int totalColumns, String oldValue, String newValue, Integer lineNumber, String line, String prefix) {
        int beginLine = document.getLineStartOffset(lineNumber)
        int endLine = document.getLineEndOffset(lineNumber)
        String lineReplaced = generateLineWithReplacedValue(line, column, totalColumns, prefix, oldValue, newValue)
        WriteCommandAction.runWriteCommandAction(project, { ->
            document.deleteString(beginLine, endLine)
            document.insertString(beginLine, lineReplaced)
        })
        caretModel.moveToOffset(document.getLineStartOffset(lineNumber + 1))
    }

    /**
     * Generate a new line with the replaced value for the specified column
     * @param line current line
     * @param column column number to replace value
     * @param totalColumns column total number
     * @param prefix prefix to maintain tabs and spaces
     * @param oldValue old value to replace
     * @param newValue new value to replace the old one
     * @return the line with replaced value
     */
    String generateLineWithReplacedValue(String line, int column, int totalColumns, String prefix, String oldValue, String newValue){
        String columnValue, newLine = ''
        if(column == 1) {
            columnValue = line.substring(0, line.indexOf('|')).trim()
            newLine = "$prefix$newValue ${line.substring(line.indexOf('|'))}"
        } else if(column == totalColumns) {
            columnValue = line.substring(line.lastIndexOf('|') + 1).trim()
            newLine = "${line.substring(0, line.lastIndexOf('|') +1)} $newValue"
        } else {
            String[] splittedLineValues = line.split('\\|')
            columnValue = splittedLineValues[column -1].trim()
            if(columnValue == oldValue) {
                String leftValues = splittedLineValues[0..(column - 2)].join('|')
                String rightValues = splittedLineValues[column..-1].join('|')
                newLine = (leftValues ? "$leftValues| " : '') + newValue + (rightValues ? " |$rightValues" : '')
            }
        }
        columnValue == oldValue ? newLine : line
    }

    @Override
    void update(AnActionEvent e) {
        Project project = e.getProject()
        e.getPresentation().setEnabledAndVisible(project != null)
    }
}
