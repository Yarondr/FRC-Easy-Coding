from PyQt5.QtGui import *
from PyQt5.QtCore import *
from PyQt5.QtWidgets import *
from GUI.Fonts import fonts
from utils.qt_gui import text_edit_right_alignment

class LineNumberArea(QWidget):
    
    def __init__(self, editor):
        super().__init__(editor)
        self.setFont(fonts.varela(20))
        self.myeditor = editor


    def sizeHint(self):
        return QSize(self.editor.lineNumberAreaWidth(), 0)


    def paintEvent(self, event):
        self.myeditor.lineNumberAreaPaintEvent(event)

class CodeEditor(QPlainTextEdit):
    
    def __init__(self):
        super().__init__()
        self.lineNumberArea = LineNumberArea(self)
        
        # design the editor
        self.setFont(fonts.varela(24))
        self.setStyleSheet("background-color: white;")
        self.verticalScrollBar().setStyleSheet("background-color: lightgray; border: 0px;")
        self.setHorizontalScrollBarPolicy(Qt.ScrollBarAlwaysOff)
        
        # connect signals
        self.blockCountChanged.connect(self.updateLineNumberAreaWidth)
        self.updateRequest.connect(self.updateLineNumberArea)
        self.cursorPositionChanged.connect(self.highlightCurrentLine)
        self.updateLineNumberAreaWidth(0)
        
        # set right alignment
        text_edit_right_alignment(self)
        
        
        
    def lineNumberAreaWidth(self):
        digits = 1
        count = max(1, self.blockCount())
        while count >= 10:
            count /= 10
            digits += 1
        space = 10 + self.fontMetrics().width('9') * digits
        return space


    def updateLineNumberAreaWidth(self, _):
        self.setViewportMargins(0, 0, self.lineNumberAreaWidth(), 0)


    def updateLineNumberArea(self, rect, dy):

        if dy:
            self.lineNumberArea.scroll(0, dy)
        else:
            self.lineNumberArea.update(0, rect.y(), self.lineNumberArea.width(),
                       rect.height())

        if rect.contains(self.viewport().rect()):
            self.updateLineNumberAreaWidth(0)


    def resizeEvent(self, event):
        super().resizeEvent(event)

        cr = self.contentsRect();
        width = self.lineNumberAreaWidth()
        self.lineNumberArea.setGeometry(QRect(self.width() - width, cr.top(),
                    width, cr.height()))


    def lineNumberAreaPaintEvent(self, event):
        mypainter = QPainter(self.lineNumberArea)
        
        mypainter.fillRect(event.rect(), Qt.lightGray)

        block = self.firstVisibleBlock()
        blockNumber = block.blockNumber()
        top = self.blockBoundingGeometry(block).translated(self.contentOffset()).top()
        bottom = top + self.blockBoundingRect(block).height()

        # Just to make sure I use the right font
        height = self.fontMetrics().height()
        while block.isValid() and (top <= event.rect().bottom()):
            if block.isVisible() and (bottom >= event.rect().top()):
                number = str(blockNumber + 1)
                mypainter.setPen(Qt.black)
                mypainter.drawText(0, int(top)+5, self.lineNumberArea.width(), height, Qt.AlignRight, number)

            block = block.next()
            top = bottom
            bottom = top + self.blockBoundingRect(block).height()
            blockNumber += 1


    def highlightCurrentLine(self):
        extraSelections = []

        if not self.isReadOnly():
            selection = QTextEdit.ExtraSelection()

            lineColor = QColor(Qt.yellow).lighter(160)

            selection.format.setBackground(lineColor)
            selection.format.setProperty(QTextFormat.FullWidthSelection, True)
            selection.cursor = self.textCursor()
            selection.cursor.clearSelection()
            extraSelections.append(selection)
        self.setExtraSelections(extraSelections)