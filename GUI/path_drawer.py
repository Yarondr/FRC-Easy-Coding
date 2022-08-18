from typing import Tuple
from validators.code_validator import Commands, getCommands, isMoveCommand
from PyQt5.QtWidgets import *
from PyQt5.QtGui import *
from PyQt5.QtCore import *
import math

class PathViewer(QWidget):
    
    def __init__(self, widget):
        super().__init__(widget)
        self.main_widget = widget
        self.setAttribute(Qt.WA_StyledBackground)
        self.setStyleSheet("background-color: red")


    def sizeHint(self):
        return QSize(30, 0)

    def paintEvent(self, event: QPaintEvent) -> None:
        lines = self.main_widget.code_editor.toPlainText().split('\n')
        commands = getCommands(lines)
        draw_path(commands, self)

def draw_path(commands: list[Tuple[Commands, float]], draw_area: QLabel):
    if len(commands) == 0:
        return
    x = draw_area.width() // 2
    y = draw_area.height() // 2
    current_degree = -90
    biggest_length = biggest_meter_length(commands) * 4.5
    painter = QPainter()
    # calcuate distance per one (smaller when the biggest_length is larger)
    distance_per_one = draw_area.width() // biggest_length
    
    painter.begin(draw_area)
    painter.setPen(QPen(QColor(0, 0, 0), 5))
    for command, value in commands:
        if isMoveCommand(command):
            direction = 1 if command == Commands.FORWARD else -1
            temp_x = int(x + math.cos(math.radians(current_degree)) * value * direction * distance_per_one)
            temp_y = int(y + math.sin(math.radians(current_degree)) * value * direction * distance_per_one)
            
            painter.drawLine(x, y, temp_x, temp_y)
            
            x = temp_x
            y = temp_y
        else:
            direction = 1 if command == Commands.TURN_RIGHT else -1
            current_degree += direction * value
            
            
            
            

def biggest_meter_length(commands: list[Tuple[Commands, float]]) -> float:
    biggest_length = 0
    for command, value in commands:
        if isMoveCommand(command):
            length = value
            if length > biggest_length:
                biggest_length = length
    return biggest_length
            
                