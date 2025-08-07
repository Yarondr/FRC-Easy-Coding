package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.chassis.subsystems.Chassis;

public class RobotCoastOrBrake extends Command {
    private static boolean isNowBrake = true;

    private final Chassis chassis;

    public RobotCoastOrBrake(Chassis chassis) {
        this.chassis = chassis;
    }

    @Override
    public boolean runsWhenDisabled() {
        return true;
    }

    @Override
    public void initialize() {

        chassis.setNeutralMode(!isNowBrake);

        isNowBrake = !isNowBrake;
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
