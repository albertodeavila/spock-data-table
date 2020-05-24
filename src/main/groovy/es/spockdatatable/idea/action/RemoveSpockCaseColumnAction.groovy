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
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

/**
 * Remove a column in a data table. It's needed to select the column to delete
 */
class RemoveSpockCaseColumnAction extends AnAction {

    RemoveSpockCaseColumnAction() {
        super()
    }

    RemoveSpockCaseColumnAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
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
        if(selectedText && currentLine.contains('|') && currentLine.split('\\|').any{it.trim() == selectedText}) {
            int totalColumns = currentLine.count( '|' ) + 1
            int column = ContentUtils.getColumn(currentLine, selectedText)

            if(!column) {
                Messages.showMessageDialog(project, 'Please, select a data table column', 'Can not Remove Column', Messages.getWarningIcon())
                return
            }
            String prefix = currentLine.substring(0, currentLine.indexOf(selectedText))
            Integer lineNumber = document.getLineNumber(caretModel.offset)

            while (lineNumber <= document.lineCount) {
                String line = ContentUtils.getCurrentLine(caretModel, document)
                if (line.contains("|")) {
                    String lineWithoutColumn = generateLineWithoutColumnValue(line, column, totalColumns, prefix)
                    replaceLine(project, document, caretModel, lineNumber, lineWithoutColumn)
                    lineNumber++
                } else if (ContentUtils.isTestFinalLine(line) || ContentUtils.endOfFile(document, caretModel)) {
                    lineNumber = document.getLineCount() + 1
                } else {
                    lineNumber++
                    if(lineNumber < document.lineCount) caretModel.moveToOffset(document.getLineStartOffset(lineNumber))
                }
            }
        } else {
            Messages.showMessageDialog(project, 'Please, select a data table column', 'Can not Remove Column', Messages.getWarningIcon())
        }
    }

    /**
     * Replace the line for the same line without the column removed
     * @param project current project
     * @param document current open file
     * @param caretModel the caret
     * @param lineNumber the number line
     * @param lineWithoutColumn line with the column removed
     */
    private void replaceLine(Project project, Document document, CaretModel caretModel, Integer lineNumber, String lineWithoutColumn) {
        int beginLine = document.getLineStartOffset(lineNumber)
        int endLine = document.getLineEndOffset(lineNumber)
        WriteCommandAction.runWriteCommandAction(project, { ->
            document.deleteString(beginLine, endLine)
            document.insertString(beginLine, lineWithoutColumn)
        })
        caretModel.moveToOffset(document.getLineStartOffset(lineNumber + 1))
    }

    /**
     * Generate line excluding the value to remove
     * @param line current line
     * @param column numberColumn to add the value
     * @param totalColumns total number of columns
     * @param prefix string to maintain tabs and spaces when add value at first position
     * @return line generated
     */
    String generateLineWithoutColumnValue(String line, int column, int totalColumns, String prefix){
        if(column == 1) {
            "$prefix${line.substring(line.indexOf('|') + 2)}"
        } else if(column == totalColumns) {
            line.substring(0, line.lastIndexOf('|') - 1)
        } else {
            List<String> splittedLineValues = line.split('\\|')
            String leftValues = splittedLineValues[0..(column - 2)].join('|')
            String rightValues = splittedLineValues[column..-1].join('|')
            leftValues ? "$leftValues|$rightValues" : rightValues
        }
    }

    @Override
    void update(AnActionEvent e) {
        Project project = e.getProject()
        e.getPresentation().setEnabledAndVisible(project != null)
    }
}
