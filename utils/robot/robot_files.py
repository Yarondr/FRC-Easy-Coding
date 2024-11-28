from PyQt5.QtWidgets import *
import shutil

old_file_path = "robot/default files/RobotContainer.java"
new_file_path = "robot/code/src/main/java/frc/robot/RobotContainer.java"

def copy_robot_container():
    shutil.copyfile(old_file_path, new_file_path)
    
def write_code(java_code):
    copy_robot_container()
    
    with open(new_file_path, "r") as file:
        data = file.readlines()
    
    data.insert(75, java_code)
    
    with open(new_file_path, "w") as file:
        file.writelines(data)
        
    