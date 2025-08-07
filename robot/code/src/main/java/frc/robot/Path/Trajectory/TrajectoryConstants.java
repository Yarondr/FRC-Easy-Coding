// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Path.Trajectory;

/** Add your docs here. */
public class TrajectoryConstants {
    public static final double FIELD_LENGTH = -1; // X in field based
    public static final double FIELD_HEIGHT = -1; // y in field based

    public static final double MAX__POSITION_THRESHOLD = 0.05; // in meters;
    public static final double MAX_ROTATION_THRESHOLD = Math.toRadians(1);
    
   


    public class PathsConstraints {
        public static final double MAX_VELOCITY = 2;
        public static final double MAX_ACCEL = 4;

        public static final double FINISH_MAX_VELOCITY = 1;
        public static final double FINISH_ACCEL = 1;

        public static final double APPROACH_MAX_VEL = 1.5;
        public static final double APPROACH_ACCEL = 3;

        public static final double DISTANCE_TO_SLOWER_VELOCITY = 1;
        public static final double MAX_ROTATIONAL_VELOCITY = Math.toRadians(360); // in radians;
        public static final double MAX_ROTATIONAL_ACCEL = Math.toRadians(720); // in radians^2

    }

}
