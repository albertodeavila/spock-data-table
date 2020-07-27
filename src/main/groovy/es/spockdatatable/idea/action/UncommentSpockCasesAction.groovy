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
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import utils.ContentUtils

import javax.swing.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Remove number of cases in all data tables present in a test file
 */
class UncommentSpockCasesAction extends AnAction {

    UncommentSpockCasesAction() {
        super()
    }

    UncommentSpockCasesAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon)
    }

    @Override
    void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR)
        Document document = editor.document
        Project project = event.getRequiredData(CommonDataKeys.PROJECT)
        CaretModel caretModel = editor.caretModel

        Integer lineNumber = document.getLineNumber(0)

        caretModel.moveToOffset(0)
        while (lineNumber < document.lineCount) {
            String line = ContentUtils.getCurrentLine(caretModel, document)
            if (lineContainsNumberCase(line)) {
                removeComment(document, project, lineNumber, line)
            }
            lineNumber++
            if(lineNumber < document.lineCount) caretModel.moveToOffset(document.getLineStartOffset(lineNumber))
        }
    }

    /**
     * Remove number comment
     * @param document the current open file
     * @param project the current project
     * @param lineNumber current line number
     * @param line current line
     */
    private void removeComment(Document document, Project project, Integer lineNumber, String line) {
        WriteCommandAction.runWriteCommandAction(project, { ->
            int beginLine = document.getLineStartOffset(lineNumber)
            int endLine = document.getLineEndOffset(lineNumber)
            String lineWithoutNumberCase = generateLineWithoutNumberCase(line)
            document.deleteString(beginLine, endLine)
            document.insertString(beginLine, lineWithoutNumberCase)
        })
    }

    /**
     * Generate the line without the comment that contains the number case
     * @param line current line
     * @return the line without the comment
     */
    private String generateLineWithoutNumberCase(String line) {
        line.substring(0, line.indexOf('/*___')) + line.substring(line.indexOf('/*___') + 11)
    }

    /**
     * Check if the current line contains a comment with the case number
     */
    private boolean lineContainsNumberCase(String line) {
        line.contains('/*___') && line.contains('*/')
    }

    @Override
    void update(AnActionEvent e) {
        Project project = e.getProject()
        e.getPresentation().setEnabledAndVisible(project != null)
    }
}
