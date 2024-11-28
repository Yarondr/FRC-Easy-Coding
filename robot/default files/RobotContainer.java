// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.chassis.commands.DriveCommand;
import frc.robot.chassis.subsystems.Chassis;
import frc.robot.utils.LogManager;
import frc.robot.commands.MoveY;
import frc.robot.commands.MoveX;
import frc.robot.commands.Turn;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer implements Sendable{
  public static boolean isRed;
  public static RobotContainer robotContainer;

  public LogManager logManager = new LogManager();

  public static Chassis chassis;
  
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    robotContainer = this;
    
    DataLogManager.start();
    DriverStation.startDataLog(DataLogManager.getLog());

    chassis = new Chassis();
    // chassis.setDefaultCommand(new DriveCommand(chassis, new CommandXboxController(0)));
    // Configure the button bindings

    SmartDashboard.putData(this);
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {}

  public static void isRed(boolean isRed) {
    RobotContainer.isRed = isRed;
  }
  public static boolean isRed() {
    return isRed;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addBooleanProperty("is Red", RobotContainer::isRed, RobotContainer::isRed);
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
			return new MoveX(chassis, 1.0);   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
		
  }
}
