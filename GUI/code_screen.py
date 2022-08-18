from venv import create
from PyQt5.QtWidgets import *
from PyQt5.QtGui import *
from PyQt5.QtCore import *

from utils.qt_gui import center_widget, set_label_design

from GUI.Fonts import fonts
from GUI.code_buttons import create_buttons
from GUI.code_editor import CodeEditor
from GUI.path_drawer import PathViewer
from wrapper.code_wrapper import wrap_commands_to_java
from validators.code_validator import getCommands

def create_status_bar(main_widget: QWidget):
    # status bar
    layout = QHBoxLayout()
    layout.setContentsMargins(10, 10, 10, 10)
    
    # status text
    status_text = QLabel("Status")
    set_label_design(status_text)
    status_text.setStyleSheet("color: red;")
    main_widget.status_text = status_text
    
    # run button
    run_button = QPushButton()
    run_button.setIcon(QIcon("icons/run.png"))
    run_button.setIconSize(QSize(64, 64))
    run_button.setFixedSize(64, 64)
    run_button.setStyleSheet("border: none")
    run_button.clicked.connect(main_widget.run_button_clicked)
    
    layout.insertSpacing(0, 64)
    layout.addWidget(status_text)
    layout.addWidget(run_button)
    return layout

class CodeScreen(QWidget):

    def __init__(self):
        super(CodeScreen, self).__init__()
        fonts.setup()
        
        # window properties
        # TODO: self.setWindowIcon
        self.setWindowTitle("Robot Coding")
        self.setGeometry(0, 0, 1380, 820)
        self.setStyleSheet("background-color: #b3e5fc;")
        center_widget(self)
        
        # code editor layout
        code_layout = QVBoxLayout()
        code_editor = CodeEditor(self)
        self.code_editor = code_editor
        code_buttons_layout = create_buttons(self, code_editor)
        code_layout.addWidget(code_editor)
        code_layout.addLayout(code_buttons_layout)
        
        # path viewer layout 
        path_layout = QVBoxLayout()
        path_viewer = PathViewer(self)
        self.path_viewer = path_viewer
        path_viewer.setFixedWidth(self.width() // 2)
        path_layout.addWidget(path_viewer)
        
        
        layout = QVBoxLayout()
        status_layout = create_status_bar(self)
        main_layout = QHBoxLayout()
        
        main_layout.addLayout(path_layout)
        main_layout.addLayout(code_layout)
        
        layout.setContentsMargins(0, 0, 0, 0)
        layout.setSpacing(0)
        layout.addLayout(status_layout)
        layout.addLayout(main_layout)
        self.setLayout(layout)
        
        self.show()
    
    
    def resizeEvent(self, event: QResizeEvent) -> None:
        self.path_viewer.setFixedWidth(self.width() // 2)
        
    def run_button_clicked(self):
        if self.code_editor.code_is_valid:
            lines = self.code_editor.toPlainText().split('\n')
            commands = getCommands(lines)
            wrap_commands_to_java(commands)
                
                