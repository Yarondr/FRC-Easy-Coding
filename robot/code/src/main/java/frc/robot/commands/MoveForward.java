// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Chassis;

public class MoveForward extends CommandBase {
  
  private final Chassis chassis;
  private final double distance; //in meters
  private double start;
  private static final double VELOCITY = 0.5;
  private static final double ERROR = 0.05;

  /**
   * Creates a new MoveForward.
   * @param distance the distance to move in meters, negative for backwards
   * @param chassis the chassis to move
   */
  public MoveForward(double distance, Chassis chassis) {
    this.chassis = chassis;
    this.distance = distance;
    addRequirements(chassis);
  }

  /**
   * gets the distance that was moved
   * @return the distance that was moved
   */
  private double getDistanceDone() {
    return Math.abs(chassis.getMetersAvg() - start);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    start = chassis.getMetersAvg();
    if (distance > 0) {
      chassis.setVelocity(VELOCITY, VELOCITY);
    } else {
      chassis.setVelocity(-VELOCITY, -VELOCITY);
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
    return getDistanceDone() >= Math.abs(distance) - ERROR;
  }
}
