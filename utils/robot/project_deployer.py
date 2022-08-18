import time
from PyQt5.QtWidgets import *
import subprocess
import os

def build_code(main_widget: QWidget, jdk_path):
    status_label: QLabel = main_widget.status_text
    status_label.setText("Building...")
    status_label.repaint()
    command = '.\gradlew build -PteamNumber=5635 --offline -Dorg.gradle.java.home=' + jdk_path
    args = command.split(" ")
    output = subprocess.run(args, shell=True, capture_output=True, cwd="robot/code")
    if output.returncode == 0:
        status_label.setText("Build successful!")
        status_label.repaint()
        time.sleep(0.5)
        return True
    else:
        status_label.setText("Build failed!")
        return False
    
def deploy_code(main_widget: QWidget, jdk_path):
    status_label: QLabel = main_widget.status_text
    status_label.setText("Deploying...")
    status_label.repaint()
    command = '.\gradlew deploy -PteamNumber=5635 --offline -Dorg.gradle.java.home=' + jdk_path
    args = command.split(" ")
    output = subprocess.run(args, shell=True, capture_output=True, cwd="robot/code")
    if output.returncode == 0:
        status_label.setText("Deployment successful!")
    else:
        status_label.setText("Deployment failed! Check connection to robot.")
    
def get_wpilib_jdk_path():
    wpilib_path = r'C:\Users\Public\wpilib'
    version = -1
    for file in os.listdir(wpilib_path):
        path = os.path.join(wpilib_path, file)
        if os.path.isdir(path) and file.isdigit():
            if int(file) > version:
                version = int(file)
    if version == -1:
        return None
    return os.path.join(wpilib_path, str(version), 'jdk')