// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.commands.InstantCommandInDisable;

public class Chassis extends SubsystemBase {

  private final TalonFX leftFront, leftBack, rightFront, rightBack;
  private final PigeonIMU gyro;
  private static final SimpleMotorFeedforward FEED_FORWARD = new SimpleMotorFeedforward(
      Constants.KS, Constants.KV);
  private boolean isBrake;

  /**
   * Creates a new Chassis.
   */
  public Chassis() {
    leftFront = new TalonFX(Constants.LEFT_FRONT_PORT);
    leftBack = new TalonFX(Constants.LEFT_BACK_PORT);
    rightFront = new TalonFX(Constants.RIGHT_FRONT_PORT);
    rightBack = new TalonFX(Constants.RIGHT_BACK_PORT);
    leftFront.setNeutralMode(NeutralMode.Brake);
    gyro = new PigeonIMU(Constants.GYRO_PORT);

    resetConfig();

    setInverted(false, true);

    leftBack.follow(leftFront);
    rightBack.follow(rightFront);

    setKP(Constants.KP);
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    SmartDashboard.putData("NeutralMode", new InstantCommandInDisable(() -> {setNeutralMode(!isBrake);}));
    builder.addDoubleProperty("Current Angle", this::getGyroAngle, null);
    builder.addDoubleProperty("Left Sensor Position", this::getMetersLeft, null);
    builder.addDoubleProperty("Right Sensor Position", this::getMetersRight, null);
  }

  public void setNeutralMode(boolean isBrake) {
      this.isBrake = isBrake;
      NeutralMode mode = isBrake ? NeutralMode.Brake : NeutralMode.Coast;
      leftFront.setNeutralMode(mode);
      rightFront.setNeutralMode(mode);
      leftBack.setNeutralMode(mode);
      rightBack.setNeutralMode(mode);
  }

  /**
   * Resets the configuration of the TalonFX motors.
   */
  private void resetConfig() {
    leftFront.configFactoryDefault();
    leftBack.configFactoryDefault();
    rightFront.configFactoryDefault();
    rightBack.configFactoryDefault();
  }

  /**
   * Sets the inverted state of the TalonFX motors.
   * @param left whether the left motors should be inverted
   * @param right whether the right motors should be inverted
   */
  private void setInverted(boolean left, boolean right) {
    leftFront.setInverted(left);
    leftBack.setInverted(left);
    rightFront.setInverted(right);
    rightBack.setInverted(right);
  }

  /**
   * Sets the kP of the TalonFX motors.
   * @param kp the kP to set
   */
  private void setKP(double kp) {
    kp *= 1023;
    leftFront.config_kP(0, kp);
    rightFront.config_kP(0, kp);
    leftBack.config_kP(0, kp);
    rightBack.config_kP(0, kp);
  }

  /**
   * Sets the power of the TalonFX motors.
   * @param left the power to set the left motors to in [-1, 1]
   * @param right the power to set the right motors to in [-1, 1]
   */
  public void setPower(double left, double right) {
    leftFront.set(ControlMode.PercentOutput, left);
    rightFront.set(ControlMode.PercentOutput, right);
  }

  /**
   * Sets the velocity of the TalonFX motors.
   * @param left the velocity to set the left motors to in m/s
   * @param right the velocity to set the right motors to in m/s
   */
  public void setVelocity(double left, double right) {
    leftFront.set(ControlMode.Velocity, left, DemandType.ArbitraryFeedForward,
        FEED_FORWARD.calculate(left));
    rightFront.set(ControlMode.Velocity, right, DemandType.ArbitraryFeedForward,
        FEED_FORWARD.calculate(right));
  }

  /**
   * Returns the meters counted by the left encoder.
   * @return the meters counted by the left encoder
   */
  public double getMetersLeft() {
    return leftFront.getSelectedSensorPosition() / Constants.PULSES_PER_METER;
  }

  /**
   * Returns the meters counted by the right encoder.
   * @return the meters counted by the right encoder
   */
  public double getMetersRight() {
    return rightFront.getSelectedSensorPosition() / Constants.PULSES_PER_METER;
  }

  /**
   * Returns the meters counted by the encoders.
   * @return the avarage meters counted by the encoders
   */
  public double getMetersAvg() {
    return (getMetersLeft() + getMetersRight()) / 2;
  }

  /**
   * Gets the heading of the robot.
   * @return the heading of the robot
   */
  public double getGyroAngle() {
    return gyro.getFusedHeading();
  }

  public void setHeading(double angle) {
    gyro.setFusedHeading(angle);
  }

  /** Zeroes the heading of the robot. */
  public void resetGyro() {
    setHeading(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
