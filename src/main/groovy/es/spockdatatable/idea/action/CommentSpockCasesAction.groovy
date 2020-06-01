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
import org.apache.commons.lang3.StringUtils
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import utils.ContentUtils

import javax.swing.*

/**
 * Add number of the cases in all truth tables present in a test file
 */
class CommentSpockCasesAction extends AnAction {

    CommentSpockCasesAction() {
        super()
    }

    CommentSpockCasesAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon)
    }

    @Override
    void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR)
        Project project = event.getRequiredData(CommonDataKeys.PROJECT)
        Document document = editor.document
        CaretModel caretModel = editor.caretModel
        String text = document.text

        List<Integer> wherePositions = findWordIndexInText(text, 'where:')
        List<Integer> bracePositions = findWordIndexInText(text, '}')
        bracePositions = filterEndMethodBraces(document, caretModel, bracePositions)

        int commentSize = 11
        Integer commentsCreated = 0
        Integer eofOffset = document.getLineEndOffset(document.lineCount - 1)
        for(Integer wherePos : wherePositions) {
            int endTestPosition = (findNextInList(wherePos, bracePositions) ?: eofOffset) + commentSize * commentsCreated
            wherePos += commentSize * commentsCreated

            Integer closeMethodOffset = endTestPosition
            caretModel.moveToOffset(wherePos)

            caretModel.moveCaretRelatively(0, 1, false, false, false)
            String line = ContentUtils.getCurrentLine(caretModel, document)
            while(!line.contains('|') && caretModel.offset < endTestPosition) {
                caretModel.moveCaretRelatively(0, 1, false, false, false)
                line = ContentUtils.getCurrentLine(caretModel, document)
            }

            if (line.contains('|')) {
                WriteCommandAction.runWriteCommandAction(project, { ->
                    addVarNamesComment(document, caretModel)
                    closeMethodOffset += commentSize
                    commentsCreated++
                    boolean foundEndOfTest = false
                    int index = 0
                    while (!foundEndOfTest) {
                        line = ContentUtils.getCurrentLine(caretModel, document)
                        if (isNotEmptyLine(line) && notCommentedLine(line) && line.contains('|')) {
                            addCaseComment(document, caretModel, commentSize, index)
                            commentsCreated++
                            closeMethodOffset += commentSize
                            index++
                        }
                        caretModel.moveCaretRelatively(0, 1, false, false, false)
                        if (caretModel.offset >= closeMethodOffset) {
                            foundEndOfTest = true
                        }
                    }
                })
            }
        }
    }

    /**
     * Add to the current line the comment with the number of test case
     * With the following format: \/*___1___*\/
     */
    private void addCaseComment(Document document, CaretModel caretModel, int commentSize, int index) {
        document.insertString(caretModel.offset, "${StringUtils.rightPad("/*___$index", commentSize - 2, '_')}*/")
    }

    /**
     * Filter the brace positions that aren't to close methods
     * @param caretModel the caret
     * @param document the file open
     * @param bracePositions all braces positions
     * @return braces positions filtered list
     */
    @NotNull
    private List<Integer> filterEndMethodBraces(Document document, CaretModel caretModel, List<Integer> bracePositions) {
        bracePositions.findAll { int bracePosition ->
            caretModel.moveToOffset(bracePosition)
            String endBraceLine = ContentUtils.getCurrentLine(caretModel, document)
            !endBraceLine.contains('|')
        }
    }

    /**
     * Add the comment \/*___#___*\/ to the var names line
     * @param caretModel the caret
     * @param document the file open
     */
    private void addVarNamesComment(Document document, CaretModel caretModel) {
        document.insertString(caretModel.offset, '/*___#___*/')
        caretModel.moveCaretRelatively(0, 1, false, false, false)
    }

    /**
     * Check if line is empty, excluding break lines, tabulations and spaces
     */
    private boolean isNotEmptyLine(String line) {
        line.replaceAll('\\r|\\n| ', '').size() != 0
    }

    /**
     * Check if line is commented (// comment)
     */
    private boolean notCommentedLine(String line) {
        !line.replaceAll('\\r|\\n| ', '').startsWith('//')
    }

    /**
     * Find the  given word in the given text
     * @param text text to search the word
     * @param word word to find
     * @return indexes list of the word in the text
     */
    List<Integer> findWordIndexInText(String text, String word) {
        List<Integer> indexes = []
        String lowerCaseTextString = text.toLowerCase()
        String lowerCaseWord = word.toLowerCase()
        int wordLength = 0

        int index = 0
        while(index != -1){
            index = lowerCaseTextString.indexOf(lowerCaseWord, index + wordLength)
            if (index != -1) {
                indexes << index
            }
            wordLength = word.size()
        }
        indexes
    }

    /**
     * Find the next number to the number given in the list
     * @param number number to search by
     * @param numbers list to search the next number
     * @return next number found or null
     */
    Integer findNextInList(Integer number, List<Integer> numbers) {
        numbers.sort().find{it > number}
    }

    @Override
    void update(AnActionEvent e) {
        Project project = e.getProject()
        e.getPresentation().setEnabledAndVisible(project != null)
    }
}
