from PyQt5.QtWidgets import *
from PyQt5.QtGui import *
from PyQt5.QtCore import *
from GUI.Fonts import fonts

def center_widget(widget: QWidget):
    frame = widget.frameGeometry()
    center_point = QDesktopWidget().availableGeometry().center()
    frame.moveCenter(center_point)
    widget.move(frame.topLeft())
    
def set_label_design(label: QLabel):
    label.setFont(fonts.roboto_bold(24))
    label.setAlignment(Qt.AlignCenter)
    return label

def text_edit_right_alignment(text_edit: QTextEdit):
    # set text alignment to right
    text_edit.document().setDefaultTextOption(QTextOption(Qt.AlignRight))
    
    # text dir rtl
    text_option = text_edit.document().defaultTextOption()
    text_option.setTextDirection(Qt.RightToLeft)
    text_edit.document().setDefaultTextOption(text_option)
    
    # scrollerbar alighment to left
    text_edit.setLayoutDirection(Qt.RightToLeft)
    