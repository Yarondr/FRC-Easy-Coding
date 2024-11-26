# FRC Easy Coding

a python hebrew gui that connects to robot code
currently using the code in [demacia off season robot code](https://github.com/Demacia5635/offseason24-25/tree/DayC)

## requernments

- pyinstaller = 5.13.1
- pyinstaller-hooks-contrib = 2021.4
- PyQt5 = 5.15.6
- PyQt5-Qt5 = 5.15.2
- PyQt5-sip = 12.9.0
- wpilib = 2024.3.2
- java = 17

## how to use

firstly, to run the code run the [main file](main.py)

press the buttons based upon the text on the button and see the trajectory map where the robot will go

currently only support move forwards, backwards and turn right and left

then press the green button to build and deploy the code to the robot

## how to maintenance to newer versions and years

1. replace the code in [robot folder](robot/code/) to the newest robot code and change the commands in [commands folder](robot/code/java/frc/robot/commands) to the newest version of moving the chassis to the wanted direction

2. change the import and update the [default robot container](robot/default%20files/RobotContainer.java) (don't forget the put an unused imports of the commands)

3. change the return line in [robot files](utils/robot/robot_files.py?plain=1#L16)

4. update in the [code wrapper](wrapper/code_wrapper.py) the java commands

5. update the year in the [project deployer](utils/robot/project_deployer.py?pl;ain=1#L38)

6. if the robot is not demacia, change the team number in [proeject deployer](utils/robot/project_deployer.py) where the code deploy and build the robot code
