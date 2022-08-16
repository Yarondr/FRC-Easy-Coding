from PyQt5.QtWidgets import *
from PyQt5.QtCore import *

from GUI.Fonts import fonts
from GUI.code_editor import CodeEditor

def set_button_style(button: QPushButton):
    style = 'QPushButton {' \
            'color: ' + "brown" + ';' \
            'background-color: ' + "lightgray" + ';' \
            'border-style: outset; border-width: px;' \
            'border-radius: 18px; border-color: 121212; padding: 8px;' \
            '}' \
            'QPushButton::pressed {' \
            'background-color: ' + "gray" + ';' \
            '}'
    button.setStyleSheet(style)
    button.setFont(fonts.roboto_bold(16))
    

def create_buttons(main_widget: QWidget, code_editor: CodeEditor):
    
    # create buttons layout
    layout = QHBoxLayout()
    layout.setGeometry(QRect(0, 0, main_widget.width()//2, 80))
    layout.setContentsMargins(20, 10, 20, 10)
    layout.setSpacing(20)
    
    # move forward button
    forward_button = QPushButton("סע קדימה")
    forward_button.clicked.connect(lambda: forward_button_clicked(code_editor))
    set_button_style(forward_button)
    
    # move backward button
    backward_button = QPushButton("סע אחורה")
    backward_button.clicked.connect(lambda: backward_button_clicked(code_editor))
    set_button_style(backward_button)
    
    # turn left button
    turn_left_button = QPushButton("פנה שמאלה")
    turn_left_button.clicked.connect(lambda: turn_left_button_clicked(code_editor))
    set_button_style(turn_left_button)
    
    # turn right button
    turn_right_button = QPushButton("פנה ימינה")
    turn_right_button.clicked.connect(lambda: turn_right_button_clicked(code_editor))
    set_button_style(turn_right_button)
    
    layout.addWidget(forward_button)
    layout.addWidget(backward_button)
    layout.addWidget(turn_left_button)
    layout.addWidget(turn_right_button)
    return layout


def forward_button_clicked(code_editor: CodeEditor):
    add_line_breaker(code_editor)
    code_editor.insertPlainText("סע קדימה (1 מטר)")


def backward_button_clicked(code_editor: CodeEditor):
    add_line_breaker(code_editor)
    code_editor.insertPlainText("סע אחורה (1 מטר)")


def turn_left_button_clicked(code_editor: CodeEditor):
    add_line_breaker(code_editor)
    code_editor.insertPlainText("פנה (-90)")


def turn_right_button_clicked(code_editor: CodeEditor):
    add_line_breaker(code_editor)
    code_editor.insertPlainText("פנה (90)")

    
def add_line_breaker(code_editor: CodeEditor):
    if (code_editor.textCursor().block().text().strip() != ""):
        code_editor.insertPlainText("\n")