from typing import Tuple
from validators.code_validator import Commands, isMoveCommand

java_commands = {
    Commands.FORWARD: "add(makePointX(%s));",
    Commands.BACKWARD: "add(makePointX(-%s));",
    Commands.TURN_LEFT: "add(makePointTurn(-%s * Math.PI / 180));",
    Commands.TURN_RIGHT: "add(makePointTurn(%s * Math.PI / 180));",
}

def wrap_commands_to_java(commands: list[Tuple[Commands, int]]):
    
    java_commands_list = tuple((java_commands[command] % value) 
                          for command, value in commands)
    java_code : str = ""
    for i in java_commands_list:
        java_code += "\t\t\t\t" + java_commands_list + "\n"

    # match len(java_commands_list):
    #     case 0:
    #         return
    #     case 1:
    #         java_code = "\t\t\treturn " + java_commands_list[0] + ";"
    #     case 2:
    #         java_code = "\t\t\treturn %s\n\t\t\t.andThen(%s);" % java_commands_list
    #     case _:
    #         java_code = (
    # """
    #     /*Auto genereted*/
    #     return %s
    #     .andThen(%s)
    #     .andThen(""" % java_commands_list[:2]
    # ) + ")\n\t\t\t\t.andThen(".join(java_commands_list[2:]) + ");"
    return java_code
        