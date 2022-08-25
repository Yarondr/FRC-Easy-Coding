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
        paths = calculate_path(commands, self)
        if paths:
            draw_path(paths, self)
        
def calculate_path(commands: list[Tuple[Commands, float]], draw_area: QLabel, biggest_length: float = 6) -> list[Tuple[int, int, int, int]]:
    if len(commands) == 0:
        return
    x = draw_area.width() // 2
    y = draw_area.height() // 2
    current_degree = -90
    distance_per_one = draw_area.width() // biggest_length
    paths: list[Tuple[int, int, int, int]] = []
    
    for command, value in commands:
        if isMoveCommand(command):
            direction = 1 if command == Commands.FORWARD else -1
            temp_x = int(x + math.cos(math.radians(current_degree)) * value * direction * distance_per_one)
            temp_y = int(y + math.sin(math.radians(current_degree)) * value * direction * distance_per_one)
            
            # print(x, y, temp_x, temp_y, draw_area.width(), draw_area.height())
            if temp_x > draw_area.width() or temp_x < 0:
                return calculate_path(commands, draw_area, biggest_length * 1.5)
            if temp_y > draw_area.height() or temp_y < 0:
                return calculate_path(commands, draw_area, biggest_length * 1.5)
            
            paths.append((x, y, temp_x, temp_y))
            
            x = temp_x
            y = temp_y
        else:
            direction = 1 if command == Commands.TURN_RIGHT else -1
            current_degree += direction * value
    return paths

def draw_path(paths: list[Tuple[int, int, int, int]], draw_area: QLabel):
    painter = QPainter()
    painter.begin(draw_area)
    painter.setPen(QPen(QColor(0, 0, 0), 5))
    for x, y, new_x, new_y in paths:
        painter.drawLine(x, y, new_x, new_y)    