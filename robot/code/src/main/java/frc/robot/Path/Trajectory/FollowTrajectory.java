// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Path.Trajectory;

import java.util.ArrayList;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Path.Utils.PathPoint;
import frc.robot.chassis.subsystems.Chassis;
import frc.robot.utils.Utils;

public class FollowTrajectory extends Command {
  private Chassis chassis;
  private DemaciaTrajectory trajectory;
  private ArrayList<PathPoint> points;
  private Rotation2d wantedAngle;
  private boolean usePoints;

  public FollowTrajectory(Chassis chassis, ArrayList<PathPoint> points, Rotation2d wantedAngle) {
    this.chassis = chassis;
    this.points = points;
    this.wantedAngle = wantedAngle;
    this.usePoints = true;
    addRequirements(chassis);
  }

  @Override
  public void initialize() {
    this.trajectory = new DemaciaTrajectory(points, false, wantedAngle, chassis.getPose());
  }

  @Override
  public void execute() {
    ChassisSpeeds speeds = chassis.getChassisSpeedsFieldRel();
    chassis.setVelocitiesWithAccel(trajectory.calculate(chassis.getPose(), Utils.hypot(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond)));
    // double disFromReef = chassis.getPose().getTranslation().getDistance(RobotContainer.isRed() ? AutoUtils.redReefCenter : AutoUtils.blueReefCenter);
    // if (target != null && (disFromReef >= 1.4 && disFromReef <= 4 || target.level == LEVEL.FEEDER)) {
    //   RobotContainer.arm.setState(Arm.levelStateToArmState(target.level));
    // }
  }

  @Override
  public void end(boolean interrupted) {
    if (!interrupted && !usePoints) {

    } else if (!DriverStation.isAutonomous())
      chassis.stop();
  }

  @Override
  public boolean isFinished() {
    return trajectory.isFinishedTrajectory();
  }
}
