from enum import Enum
from typing import Tuple


class Commands(Enum):
    FORWARD = "סע קדימה"
    BACKWARD = "סע אחורה"
    TURN_RIGHT = "פנה ימינה"
    TURN_LEFT = "פנה שמאלה"

class ErrorTypes(Enum):
    NUMBER_BELOW_OR_EQUAL_TO_ZERO = 1
    NOT_A_NUMBER = 2
    NUMBER_REQUIRED = 3
    NO_METER_KEYWORD = 4
    INVALID_KEYWORD_AT_END = 5
    INVALID_DEGREE = 6
    INVALID_KEYWORD_AT_BEGINNING = 7

def check_syntax(lines: list) -> Tuple[Commands, ErrorTypes, int]:
    for index, line in enumerate(lines):
        line = str(line)
        if line.strip() == "":
            continue
        line = line.strip()
        if line.startswith("סע קדימה") or line.startswith("סע אחורה"):
            command_last_index = line.find(" ", 3)
            if command_last_index == -1:
                command_last_index = len(line)
            command = Commands(line[:command_last_index])
            
            # checking number
            if (len(line.split(" ")) == 2):
                return command, ErrorTypes.NUMBER_REQUIRED, index + 1
            after = line.split(" ")[2]
            if not isNumeric(after):
                return command, ErrorTypes.NOT_A_NUMBER, index + 1
            if float(after) <= 0:
                return command, ErrorTypes.NUMBER_BELOW_OR_EQUAL_TO_ZERO, index + 1
            
            # checking keyword
            if (len(line.split(" ")) == 3):
                return command, ErrorTypes.NO_METER_KEYWORD, index + 1
            after = line.split(" ")[3]
            if after != "מטר":
                return command, ErrorTypes.NO_METER_KEYWORD, index + 1
            
            # end
            if (len(line.split(" ")) != 4):
                return command, ErrorTypes.INVALID_KEYWORD_AT_END, index + 1
        elif line.startswith("פנה ימינה") or line.startswith("פנה שמאלה"):
            command_last_index = line.find(" ", 4)
            if command_last_index == -1:
                command_last_index = len(line)
            command = Commands(line[:command_last_index])
            
            # checking number
            if (len(line.split(" ")) == 2):
                return command, ErrorTypes.NUMBER_REQUIRED, index + 1
            after = line.split(" ")[2]
            # work for negative numbers too
            if not isNumeric(after):
                return command, ErrorTypes.NOT_A_NUMBER, index + 1
            after = float(after)
            if after < 0 or after > 360:
                return command, ErrorTypes.INVALID_DEGREE, index + 1
            
            #end
            if (len(line.split(" ")) != 3):
                return command, ErrorTypes.INVALID_KEYWORD_AT_END, index + 1
        else:
            return None, ErrorTypes.INVALID_KEYWORD_AT_BEGINNING, index + 1
    return None, None, -1

def getCommands(lines: list) -> list:
    commands: list = []
    last = None
    total_commands = 0
    for line in lines:
        line = str(line).strip()
        if line == "":
            continue
        total_commands += 1
        command = Commands(line[:line.find(" ", 4)])
        value = float(line.split(" ")[2])
        if last and (command == last[0]):
            _, last_value = last
            last_value += value
            last = (command, last_value)
        elif last:
            commands.append(last)
            last = None
            commands.append((command, value))
        elif not last and (command == Commands.FORWARD or command == Commands.BACKWARD):
            last = (command, value)
        elif not last:
            commands.append((command, value))
    
    if last:
        commands.append(last)
    
    if not commands:
        return []
    if (len(commands) == 1 and total_commands > 1):
        commands.clear()
        for line in lines:
            line = str(line).strip()
            if line == "":
                continue
            command = Commands(line[:line.find(" ", 4)])
            value = float(line.split(" ")[2])
            commands.append((command, value))
    
    return commands


def isNumeric(s):
    try:
        float(s)
        return True
    except ValueError:
        return False