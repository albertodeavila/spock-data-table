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

package utils
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange

class ContentUtils {

    /**
     * Get the line where the caret is located
     * @param caretModel the caret
     * @param document the file open
     * @return the line found
     */
    static String getCurrentLine(CaretModel caretModel, Document document) {
        int lineNumber = document.getLineNumber(caretModel.offset)
        document.getText(new TextRange(document.getLineStartOffset(lineNumber),
                document.getLineEndOffset(lineNumber)))
    }

    /**
     * Calculate the column number to remove
     * For example, if you have: var1 | var2 | var3 | var4 and you select var3,
     * this method will return 3.
     * @param currentLine the line where the caret is
     * @param selectedText text selected by the user
     * @return number of column to remove
     */
    static int getColumn(String currentLine, String selectedText) {
        int columnToRemovePos = currentLine.indexOf(selectedText)
        int previousStickPos = currentLine.indexOf('|')
        int nextStickPos = currentLine.indexOf('|', columnToRemovePos)
        if (previousStickPos == -1) {
            1 //First column doesn't have previous bar
        } else if (nextStickPos == -1) {
            currentLine.count( '|' ) + 1
        } else {
            currentLine.substring(0, columnToRemovePos).count( '|' ) + 1
        }
    }

    static boolean endOfFile(Document document, CaretModel caretModel) {
        caretModel.offset >= document.getLineEndOffset(document.lineCount - 1)
    }

    /**
     * Check if the given line is the test end
     * It ensures that this line only contains an end brace
     * @param line the line to check
     */
    static boolean isTestFinalLine(String line) {
        line.replaceAll('\\r|\\n| ', '') == '}'
    }
}
