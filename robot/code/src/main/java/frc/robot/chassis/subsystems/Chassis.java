package frc.robot.chassis.subsystems;

import java.util.List;

import org.ejml.simple.SimpleMatrix;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.Pigeon2;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import static frc.robot.chassis.utils.ChassisConstants.*;

import frc.robot.chassis.utils.ChassisConstants.AccelConstants;
import frc.robot.chassis.utils.SwerveKinematics;
import frc.robot.utils.LogManager;
import frc.robot.utils.Utils;

public class Chassis extends SubsystemBase {
    private SwerveModule[] modules;
    private Pigeon2 gyro;
    private SwerveKinematics kinematicsFix;
    private SwerveDrivePoseEstimator poseEstimator;
    private Field2d field;

    private StatusSignal<Angle> gyroYawStatus;
    private Rotation2d lastGyroYaw;

    public Chassis() {
        modules = new SwerveModule[] {
                new SwerveModule(FRONT_LEFT),
                new SwerveModule(FRONT_RIGHT),
                new SwerveModule(BACK_LEFT),
                new SwerveModule(BACK_RIGHT),
        };
        gyro = new Pigeon2(GYRO_ID, GYRO_CAN_BUS);
        addStatus();
        kinematicsFix = new SwerveKinematics(
                FRONT_LEFT.POSITION,
                FRONT_RIGHT.POSITION,
                BACK_LEFT.POSITION,
                BACK_RIGHT.POSITION

        );
        poseEstimator = new SwerveDrivePoseEstimator(kinematicsFix, getGyroAngle(), getModulePositions(), new Pose2d());

        SimpleMatrix std = new SimpleMatrix(new double[] { 0.02, 0.02, 0 });
        poseEstimator.setVisionMeasurementStdDevs(new Matrix<>(std));
        field = new Field2d();

        SmartDashboard.putData("reset gyro", new InstantCommand(() -> setYaw(Rotation2d.kZero)).ignoringDisable(true));
        SmartDashboard.putData("reset gyro 180", new InstantCommand(() -> setYaw(Rotation2d.kPi)).ignoringDisable(true));
        LogManager.addEntry("gyro", () -> getGyroAngle().getRadians());
        SmartDashboard.putData("field", field);
        // SmartDashboard.putData("ultfielf", fieldTag);
        // SmartDashboard.putData("fieldTest", fieldTest);
        LogManager.addEntry("VELOCITY NORM: ", () -> Utils.hypot(getChassisSpeedsRobotRel().vxMetersPerSecond, getChassisSpeedsRobotRel().vyMetersPerSecond));
        // LogManager.addEntry("Chassis/vX", () -> getChassisSpeedsRobotRel().vxMetersPerSecond);
        // LogManager.addEntry("Chassis/vY", () -> getChassisSpeedsRobotRel().vyMetersPerSecond);
        SmartDashboard.putData("Chassis/set coast", new InstantCommand(() -> setNeutralMode(false)).ignoringDisable(true));
        SmartDashboard.putData("Chassis/set brake", new InstantCommand(() -> setNeutralMode(true)).ignoringDisable(true));
        // SmartDashboard.putData(getName() + "/Swerve Drive", getChassisWidget());
        // SmartDashboard.putData("Chassis", this);
    }

    public void checkElectronics() {
        for (SwerveModule module : modules) {
            module.checkElectronics();
        }
    }

    // private Sendable getChassisWidget() {
    //     return new Sendable() {
    //         @Override
    //         public void initSendable(SendableBuilder builder) {
    //             builder.setSmartDashboardType("SwerveDrive");

    //             builder.addDoubleProperty("Front Left Angle", () -> modules[0].getAbsoluteAngle(), null);
    //             builder.addDoubleProperty("Front Left Velocity", () -> modules[0].getDriveVel(), null);

    //             builder.addDoubleProperty("Front Right Angle", () -> modules[1].getAbsoluteAngle(), null);
    //             builder.addDoubleProperty("Front Right Velocity", () -> modules[1].getDriveVel(), null);

    //             builder.addDoubleProperty("Back Left Angle", () -> modules[2].getAbsoluteAngle(), null);
    //             builder.addDoubleProperty("Back Left Velocity", () -> modules[2].getDriveVel(), null);

    //             builder.addDoubleProperty("Back Right Angle", () -> modules[3].getAbsoluteAngle(), null);
    //             builder.addDoubleProperty("Back Right Velocity", () -> modules[3].getDriveVel(), null);

    //             builder.addDoubleProperty("Robot Angle", () -> getGyroAngle().getRadians(), null);
    //         }
    //     };
    // }

    public void setNeutralMode(boolean isBrake) {
        for (SwerveModule module : modules) {
            module.setNeutralMode(isBrake);
        }
    }

    public void resetPose(Pose2d pose) {
        poseEstimator.resetPose(pose);
    }

    private void addStatus() {
        gyroYawStatus = gyro.getYaw();
        lastGyroYaw = new Rotation2d(gyroYawStatus.getValueAsDouble());
    }

    public Pose2d getPose() {
        return poseEstimator.getEstimatedPosition();
    }
    

    Translation2d lastWantedSpeeds = new Translation2d();
    public void setVelocitiesWithAccel(ChassisSpeeds wantedSpeeds){
        ChassisSpeeds currentSpeeds = getChassisSpeedsFieldRel();
        Translation2d limitedVelocitiesVector = calculateVelocity(wantedSpeeds.vxMetersPerSecond, wantedSpeeds.vyMetersPerSecond, currentSpeeds.vxMetersPerSecond, currentSpeeds.vyMetersPerSecond);//lastWantedSpeeds);
        ChassisSpeeds limitedVelocities = new ChassisSpeeds(limitedVelocitiesVector.getX(), limitedVelocitiesVector.getY(), wantedSpeeds.omegaRadiansPerSecond);
        lastWantedSpeeds = limitedVelocitiesVector;
        setVelocities(limitedVelocities);
    }

    public void setVelocities(ChassisSpeeds speeds) {
        speeds = ChassisSpeeds.fromFieldRelativeSpeeds(speeds, getGyroAngle());
        speeds = ChassisSpeeds.discretize(speeds, CYCLE_DT);
        
        SwerveModuleState[] states = kinematicsFix.toSwerveModuleStates(speeds);
        setModuleStates(states);
    }

    private double calculateLinearVelocity(double wantedSpeeds, double currentSpeeds) {
        double deltaV = wantedSpeeds - currentSpeeds;
        double maxDelta = AccelConstants.MAX_LINEAR_ACCEL * CYCLE_DT;
        if(Math.abs(deltaV) > maxDelta){
            return currentSpeeds + (maxDelta * Math.signum(deltaV));
        }
        return wantedSpeeds;
        
    }

    public void setRobotRelSpeedsWithAccel(ChassisSpeeds speeds){
        ChassisSpeeds currentSpeeds = getChassisSpeedsRobotRel();

        Translation2d limitedVelocitiesVector = calculateVelocity(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond, currentSpeeds.vxMetersPerSecond, currentSpeeds.vyMetersPerSecond);//lastWantedSpeeds);
        ChassisSpeeds limitedVelocities = new ChassisSpeeds(limitedVelocitiesVector.getX(), limitedVelocitiesVector.getY(), speeds.omegaRadiansPerSecond);
        
        setRobotRelVelocities(limitedVelocities);
    }

    double lastAngle = 0;
    private Translation2d calculateVelocity(double wantedSpeedsX, double wantedSpeedsY, double currentSpeedsX, double currentSpeedsY) {
        double wantedSpeedsNorm = Utils.hypot(wantedSpeedsX, wantedSpeedsY);
        double currentSpeedsNorm = Utils.hypot(currentSpeedsX, currentSpeedsY);
        double wantedSpeedsAngle = Utils.angleFromTranslation2d(wantedSpeedsX, wantedSpeedsY);
        double currentSpeedsAngle = Utils.angleFromTranslation2d(currentSpeedsX, currentSpeedsY);

        if(wantedSpeedsNorm == 0 && currentSpeedsNorm == 0) return Translation2d.kZero;

        if(currentSpeedsNorm <0.1){
            // LogManager.log("SMALL VEL");
            double v = MathUtil.clamp(wantedSpeedsNorm, 0, currentSpeedsNorm + AccelConstants.MAX_DELTA_VELOCITY);
            return new Translation2d(v, Rotation2d.fromRadians(wantedSpeedsAngle));
        }

        if(wantedSpeedsNorm == 0 && currentSpeedsNorm > 0.1) return new Translation2d(calculateLinearVelocity(wantedSpeedsNorm, currentSpeedsNorm), Rotation2d.fromRadians(lastAngle));
        lastAngle = currentSpeedsAngle;
        double angleDiff = MathUtil.angleModulus(wantedSpeedsAngle - currentSpeedsAngle);
        double radius = currentSpeedsNorm / AccelConstants.MAX_OMEGA_VELOCITY;
        // LogManager.log("RADIUS: " + radius);
        if(Math.abs(angleDiff) < 0.6 || radius < AccelConstants.MAX_RADIUS){
            
            return new Translation2d(calculateLinearVelocity(wantedSpeedsNorm, currentSpeedsNorm), Rotation2d.fromRadians(wantedSpeedsAngle));
        }

        double velocity = Math.min(AccelConstants.MAX_VELOCITY_TO_IGNORE_RADIUS, Math.max(currentSpeedsNorm - (AccelConstants.MAX_DELTA_VELOCITY), AccelConstants.MIN_VELOCITY));
    //    LogManager.log("NEW VELOCITY: " + velocity);
        double radChange = Math.min(AccelConstants.MAX_OMEGA_VELOCITY, (velocity / AccelConstants.MAX_RADIUS) * CYCLE_DT);
        return new Translation2d(velocity, Rotation2d.fromRadians((radChange * Math.signum(angleDiff)) + currentSpeedsAngle));
        
    }

    public void setSteerPositions(double[] positions) {
        for (int i = 0; i < positions.length; i++) {
            modules[i].setSteerPosition(positions[i]);
        }
    }

    public void setSteerPower(double pow, int id) {
        modules[id].setSteerPower(pow);
    }

    public double getSteerVelocity(int id) {
        return modules[id].getSteerVel();
    }

    public double getSteeracceleration(int id) {
        return modules[id].getSteerAccel();
    }

    public void setSteerPositions(double position) {
        setSteerPositions(new double[] { position, position, position, position });
    }

    public ChassisSpeeds getRobotRelVelocities() {
        return ChassisSpeeds.fromFieldRelativeSpeeds(getChassisSpeedsRobotRel(), getGyroAngle());
    }

    public void setRobotRelVelocities(ChassisSpeeds speeds) {
        SwerveModuleState[] states = kinematicsFix.toSwerveModuleStates(speeds);
        setModuleStates(states);
    }

    public void setDriveVelocities(double[] velocities) {
        for (int i = 0; i < velocities.length; i++) {
            modules[i].setDriveVelocity(velocities[i]);
        }
    }

    public void setDriveVelocities(double velocity) {
        setDriveVelocities(new double[] { velocity, velocity, velocity, velocity });
    }

    public Rotation2d getGyroAngle() {
        gyroYawStatus.refresh();
        if (gyroYawStatus.getStatus() == StatusCode.OK) {
            lastGyroYaw = new Rotation2d(gyroYawStatus.getValue());
        }
        return lastGyroYaw;
    }

    private SwerveModulePosition[] getModulePositions() {
        SwerveModulePosition[] arr = new SwerveModulePosition[modules.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = modules[i].getModulePosition();
        }
        return arr;
    }

    public void setModuleStates(SwerveModuleState[] states) {
        for (int i = 0; i < states.length; i++) {
            modules[i].setState(states[i]);
        }
    }

    Rotation2d gyroAngle;

    @Override
    public void periodic() {
        gyroAngle = getGyroAngle();
        poseEstimator.update(gyroAngle, getModulePositions());

        field.setRobotPose(poseEstimator.getEstimatedPosition());

    }

    public boolean isRed() {
        return RobotContainer.isRed();
    }

    public ChassisSpeeds getChassisSpeedsRobotRel() {
        return kinematicsFix.toChassisSpeeds(getModuleStates());
    }

    public ChassisSpeeds getChassisSpeedsFieldRel() {
        return ChassisSpeeds.fromRobotRelativeSpeeds(kinematicsFix.toChassisSpeeds(getModuleStates()), getGyroAngle());
    }

    /**
     * Returns the state of every module
     * 
     * 
     * @return Velocity in m/s, angle in Rotation2d
     */
    public SwerveModuleState[] getModuleStates() {
        SwerveModuleState[] res = new SwerveModuleState[modules.length];
        for (int i = 0; i < modules.length; i++) {
            res[i] = modules[i].getState();
        }
        return res;
    }

    public void setYaw(Rotation2d angle) {
        if (angle != null) {
            gyro.setYaw(angle.getDegrees());
            poseEstimator
                    .resetPose(new Pose2d(poseEstimator.getEstimatedPosition().getTranslation(), gyro.getRotation2d()));
        }
    }

    public void setVelocitiesRotateToAngleOld(ChassisSpeeds speeds, double angle) {
        double angleError = angle - getGyroAngle().getRadians();
        double angleErrorabs = Math.abs(angleError);
        if (angleErrorabs > Math.toRadians(1.5)) {
            speeds.omegaRadiansPerSecond = angleError * 1.5;
        }

        setVelocitiesWithAccel(speeds);
    }

    PIDController drivePID = new PIDController(2, 0, 0);

    public void goTo(Pose2d pose, double threshold, boolean stopWhenFinished) {

        Translation2d diffVector = pose.getTranslation().minus(getPose().getTranslation());

        double distance = diffVector.getNorm();
        if (distance <= threshold) {
            if (stopWhenFinished)
                setVelocitiesRotateToAngleOld(new ChassisSpeeds(0, 0, 0), pose.getRotation().getRadians());
            else
                setVelocitiesRotateToAngleOld(
                        new ChassisSpeeds(0.5 * diffVector.getAngle().getCos(), 0.5 * diffVector.getAngle().getSin(),
                                0),
                        pose.getRotation().getRadians());
        }

        else {
            double vX = MathUtil.clamp(-drivePID.calculate(diffVector.getX(), 0), -3.2, 3.2);
            double vY = MathUtil.clamp(-drivePID.calculate(diffVector.getY(), 0), -3.2, 3.2);


            setVelocitiesRotateToAngleOld(new ChassisSpeeds(vX, vY, 0), pose.getRotation().getRadians());
        }

    }

    public void stop() {
        for (SwerveModule i : modules) {
            i.stop();
        }
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        super.initSendable(builder);
    }

    public Trajectory vector(Translation2d start, Translation2d end){
      return TrajectoryGenerator.generateTrajectory(
            List.of(
              new Pose2d(start, end.getAngle().minus(start.getAngle())),
              new Pose2d(end, end.getAngle().minus(start.getAngle()))),
            new TrajectoryConfig(4.0, 4.0));
    }
}
