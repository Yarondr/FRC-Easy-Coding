package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.chassis.subsystems.Chassis;

public class MoveY extends Command {

  Chassis chassis;
  double dis;
  double startingPos;
  Timer timer;

  /**
   * move the chassis relative angle
   * @param chassis
   * @param dis in meter
   */
  public MoveY(Chassis chassis, double dis) {
    this.chassis = chassis;
    this.dis = dis;
    timer = new Timer();
  }

  @Override
  public void initialize() {
    timer.start();
    // startingPos = chassis.getPoseY();
  }

  @Override
  public void execute() {
    chassis.setVelocities(new ChassisSpeeds(0, dis >= 0 ? 0.25 : -0.25 , 0));
  }

  @Override
  public void end(boolean interrupted) {
    chassis.setVelocities(new ChassisSpeeds());
    timer.stop();
    timer.reset();
  }

  @Override
  public boolean isFinished() {
    return timer.hasElapsed(Math.abs(dis) * 2);
    // return Math.abs(chassis.getPoseY() - startingPos) >= Math.abs(dis);
  }
}
