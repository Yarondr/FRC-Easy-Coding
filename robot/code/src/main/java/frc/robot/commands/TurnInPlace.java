// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Chassis;

public class TurnInPlace extends CommandBase {

  public static enum Direction {
    LEFT, RIGHT
  }

  private final Chassis chassis;
  private final Direction direction;
  private final double degrees;
  private double startAngle;
  private static final double TURN_VELOCITY = 0.3;
  private static final double ERROR = 1.5;

  /**
   * Creates a new TurnInPlace.
   * @param degrees the degrees to turn
   * @param direction the direction to turn
   * @param chassis the chassis to turn
   */
  public TurnInPlace(double degrees, Direction direction, Chassis chassis) {
    this.chassis = chassis;
    this.direction = direction;
    this.degrees = degrees;
    addRequirements(chassis);
  }

  /**
   * gets the degrees that were turned
   * @return the degrees that were turned
   */
  private double getDegreesDone() {
    return Math.abs(startAngle - chassis.getGyroAngle());
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    startAngle = chassis.getGyroAngle();
    if (direction == Direction.LEFT) {
      chassis.setVelocity(-TURN_VELOCITY, TURN_VELOCITY);
    } else {
      chassis.setVelocity(TURN_VELOCITY, -TURN_VELOCITY);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    chassis.setVelocity(0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return getDegreesDone() >= degrees - ERROR;
  }
}
