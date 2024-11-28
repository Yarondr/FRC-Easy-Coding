package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.chassis.subsystems.Chassis;

public class Turn extends Command {

  frc.robot.chassis.subsystems.Chassis chassis;
  double angle;
  double startingAngle;

  /**
   * rotate to angle
   * @param chassis
   * @param angle in radians
   */
  public Turn(Chassis chassis, double angle) {
    this.chassis = chassis;
    this.angle = angle;
  }

  @Override
  public void initialize() {
    startingAngle = chassis.getGyroRotation().getRadians();
  }

  @Override
  public void execute() {
    chassis.setVelocities(new ChassisSpeeds(0, 0, angle >= 0 ? Math.PI / 4 : -1 * Math.PI / 4));
  }

  @Override
  public void end(boolean interrupted) {
    chassis.setVelocities(new ChassisSpeeds());
  }

  @Override
  public boolean isFinished() {
    return Math.abs(chassis.getGyroRotation().getRadians() - startingAngle) >= Math.abs(angle);
  }
}
