from typing import Tuple
from validators.code_validator import Commands, isMoveCommand

directions = {
    Commands.TURN_LEFT: "Direction.LEFT",
    Commands.TURN_RIGHT: "Direction.RIGHT",
}

java_commands = {
    Commands.FORWARD: "new MoveForward(%s, chassis)",
    Commands.BACKWARD: "new MoveForward(-%s, chassis)",
    Commands.TURN_LEFT: "new TurnInPlace(%s, %s, chassis)",
    Commands.TURN_RIGHT: "new TurnInPlace(%s, %s, chassis)",
}

def wrap_commands_to_java(commands: list[Tuple[Commands, int]]):
    
    java_commands_list = tuple((java_commands[command] % value) if isMoveCommand(command) 
                          else (java_commands[command] % (value, directions[command]))
                          for command, value in commands)
    match len(java_commands_list):
        case 0:
            return
        case 1:
            java_code = "\t\t\treturn " + java_commands_list[0] + ";"
        case 2:
            java_code = "\t\t\treturn %s\n\t\t\t.andThen(%s);" % java_commands_list
        case _:
            java_code = (
    """
        return %s
        .andThen(%s)
        .andThen(""" % java_commands_list[:2]
    ) + ")\n\t\t\t.andThen(".join(java_commands_list[2:]) + ");"
    return java_code
        