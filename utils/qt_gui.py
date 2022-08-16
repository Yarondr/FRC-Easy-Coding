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
    
def create_status_bar():
    # status bar
    layout = QHBoxLayout()
    layout.setContentsMargins(10, 10, 74, 10)
    
    # settings button
    settings_button = QPushButton()
    settings_button.setIcon(QIcon("icons/settings.png"))
    settings_button.setIconSize(QSize(64, 64))
    settings_button.setFixedSize(64, 64)
    # disable button border
    settings_button.setStyleSheet("border: none")
    
    # status text
    status_text = QLabel()
    set_label_design(status_text)
    status_text.setStyleSheet("color: red;")
    
    layout.addWidget(settings_button)
    layout.addWidget(status_text)
    return layout
    