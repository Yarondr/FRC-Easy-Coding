// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Path.Trajectory.FollowTrajectory;
import frc.robot.Path.Utils.PathPoint;
import frc.robot.chassis.commands.Drive;
import frc.robot.chassis.subsystems.Chassis;
import frc.robot.utils.CommandController;
import frc.robot.utils.CommandController.ControllerType;
import frc.robot.utils.Elastic;
import frc.robot.utils.Elastic.Notification;
import frc.robot.utils.Elastic.Notification.NotificationLevel;
import frc.robot.utils.LogManager;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer implements Sendable {

  public static RobotContainer robotContainer;
  public static CommandController driverController;
  public static CommandController operatorController;
  public static boolean isComp = DriverStation.isFMSAttached();
  private static boolean hasRemovedFromLog = false;
  public static boolean isRed;
  public static Trigger allianceTrigger;

  public static Chassis chassis;

  public SendableChooser<AutoMode> autoChooser;

  public enum AutoMode {
    LEFT, MIDDLE, RIGHT
  }

  public static Command leftAuto;
  public static Command middleAuto;
  public static Command rightAuto;
  public final Timer timer = new Timer();

  private Trigger userButtonTrigger;

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // WebServer.start(5800, Filesystem.getDeployDirectory().getPath());
    robotContainer = this;
    new LogManager();
    driverController = new CommandController(OperatorConstants.DRIVER_CONTROLLER_PORT, ControllerType.kXbox);
    operatorController = new CommandController(OperatorConstants.OPERATOR_CONTROLLER_PORT, ControllerType.kXbox);
    allianceTrigger = new Trigger(() -> isRed);

    SmartDashboard.putData("Command Scheduler", CommandScheduler.getInstance());
    SmartDashboard.putData("RC", this);
    LogManager.addEntry("Timer", () -> DriverStation.getMatchTime());
    Elastic.sendNotification(new Notification(NotificationLevel.INFO, "Start Robot Code", ""));

    configureSubsytems();
    configureDefaultCommands();
    configureBindings();
    configureAuto();

    allianceTrigger.onChange(new InstantCommand(() -> {
    }).ignoringDisable(true));
  }

  /**
   * This function start all the subsytems.
   * Put here all the subsystems you want to use.
   * This function is called at the robot container constractor.
   */
  private void configureSubsytems() {
    Ultrasonic.setAutomaticMode(true);

    chassis = new Chassis();
  }

  /**
   * This function set all the default commands to the subsystems.
   * set all the default commands of the subsytems.
   * This function is called at the robot container constractor
   */
  private void configureDefaultCommands() {
    chassis.setDefaultCommand(new Drive(chassis, driverController));
  }

  private void configureBindings() {
    userButtonTrigger = new Trigger(() -> RobotController.getUserButton() && !DriverStation.isEnabled());
    userButtonTrigger.onTrue(new RobotCoastOrBrake(chassis));

    driverController.getLeftStickMove().onTrue(new Drive(chassis, driverController));

    driverController.rightButton().onTrue(new InstantCommand(() -> Drive.invertPrecisionMode()));

    driverController.leftBumper().onTrue(new InstantCommand(() -> {
      chassis.stop();
    }, chassis).ignoringDisable(true));

    operatorController.leftBumper().onTrue(new InstantCommand(() -> {
      chassis.stop();
    }, chassis).ignoringDisable(true));

    operatorController.povDown().onTrue(new InstantCommand(chassis::stop, chassis).ignoringDisable(true));

    operatorController.leftSettings()
        .onTrue(new InstantCommand(() -> chassis.setYaw(Rotation2d.kPi)).ignoringDisable(true));
  }

  private void configureAuto() {
  }

  public static boolean isRed() {
    return isRed;
  }

  public static void setIsRed(boolean isRed) {
    RobotContainer.isRed = isRed;
  }

  public static boolean isComp() {
    return isComp;
  }

  public static void setIsComp(boolean isComp) {
    RobotContainer.isComp = isComp;
    if (!hasRemovedFromLog && isComp) {
      hasRemovedFromLog = true;
      LogManager.removeInComp();
    }
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addBooleanProperty("isRed", RobotContainer::isRed, RobotContainer::setIsRed);
    builder.addBooleanProperty("isComp", RobotContainer::isComp, RobotContainer::setIsComp);
  }

  /**
   * This command is schedules at the start of teleop.
   * Look in {@link Robot} for more details.
   * 
   * @return the ommand that start at the start at enable
   */
  public Command getEnableInitCommand() {
    return null;
  }

  /**
   * This command is schedules at the start of disable.
   * Put here all the stop functions of all the subsytems and then add them to the
   * requirments
   * This insures that the motors do not keep their last control mode earlier and
   * moves uncontrollably.
   * Look in {@link Robot} for more details.
   * 
   * @return the command that runs at disable
   */
  public Command getDisableInitCommand() {
    return new InstantCommand(() -> {
      chassis.stop();
      timer.stop();
    }, chassis).withName("initDisableCommand").ignoringDisable(true);
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   *         {@code
   *  public Command getAutonomousCommand() {
   *    timer.reset();
   *    timer.start();
   * 
   *    return new FollowTrajectory(chassis, new ArrayList<>() {
   *      {
   *        add(PathPoint.kZero);
   *        add(new PathPoint(new Pose2d(chassis.getPose().getTranslation().plus(new Translation2d(1, 1)), chassis.getGyroAngle())));
   *         }
   *         }, Rotation2d.kZero);
   *         }
   */
  public Command getAutonomousCommand() {
    timer.reset();
    timer.start();

    return new FollowTrajectory(chassis, new ArrayList<>() {
      {
        add(PathPoint.kZero);
      }
    }, Rotation2d.kZero);
  }

  PathPoint makePointX(double x) {
    return new PathPoint(
        new Pose2d(chassis.getPose().getTranslation().plus(new Translation2d(x, 0)), chassis.getGyroAngle()));
  }

  PathPoint makePointTurn(double radAngle) {
    return new PathPoint(
      new Pose2d(chassis.getPose().getTranslation(), Rotation2d.fromRadians(radAngle))
    );
  }
}
