// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    private static final String kDefaultAuto = "Default";
    private static final String kCustomAuto = "My Auto";
    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();

    CANSparkMax frontRightTurn;
    CANSparkMax frontLeftTurn;
    CANSparkMax backRightTurn;
    CANSparkMax backLeftTurn;

    CANSparkMax frontRightDrive;
    CANSparkMax frontLeftDrive;
    CANSparkMax backRightDrive;
    CANSparkMax backLeftDrive;

    AnalogInput frontRightEnc;
    AnalogInput frontLeftEnc;
    AnalogInput backRightEnc;
    AnalogInput backLeftEnc;

    XboxController controller1 = new XboxController(0);

    Timer autoTimer = new Timer();

    /**
     * This function is run when the robot is first started up and should be used for any
     * initialization code.
    */
    @Override
    public void robotInit() {
        m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
        m_chooser.addOption("My Auto", kCustomAuto);
        SmartDashboard.putData("Auto choices", m_chooser);

        frontRightTurn = new CANSparkMax(1, MotorType.kBrushed);
        frontLeftTurn = new CANSparkMax(2, MotorType.kBrushed);
        backRightTurn = new CANSparkMax(3, MotorType.kBrushed);
        backLeftTurn = new CANSparkMax(4, MotorType.kBrushed);

        frontRightDrive = new CANSparkMax(5, MotorType.kBrushed);
        frontLeftDrive = new CANSparkMax(6, MotorType.kBrushed);
        backRightDrive = new CANSparkMax(7, MotorType.kBrushed);
        backLeftDrive = new CANSparkMax(8, MotorType.kBrushed);

        frontRightEnc = new AnalogInput(0);
        frontLeftEnc = new AnalogInput(1);
        backRightEnc = new AnalogInput(2);
        backLeftEnc = new AnalogInput(3);
    }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
    @Override
    public void robotPeriodic() {}

    /**
     * This autonomous (along with the chooser code above) shows how to select between different
     * autonomous modes using the dashboard. The sendable chooser code works with the Java
     * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
     * uncomment the getString line to get the auto name from the text box below the Gyro
     *
     * <p>You can add additional auto modes by adding additional comparisons to the switch structure
     * below with additional strings. If using the SendableChooser make sure to add them to the
     * chooser code above as well.
    */
    @Override
    public void autonomousInit() {
        m_autoSelected = m_chooser.getSelected();
        // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
        System.out.println("Auto selected: " + m_autoSelected);
        
        //Reset Timer before use
        autoTimer.reset();

        //Starts Timer for Auto mode
        autoTimer.start();
    }

  /** This function is called periodically during autonomous. */
    @Override
    public void autonomousPeriodic() {
        switch (m_autoSelected) {
            case kCustomAuto:
                // Put custom auto code here
            break;
            case kDefaultAuto:
            default:

                double autoDriveSpeed;
                double autoSteerAngle;
                double autoDiffSpeed;
                boolean autoSpinMode;


                //Drive Forward for 3 Seconds at .25 speed
                if(autoTimer.get() < 3.0){
                    autoDriveSpeed = 0.25;
                    autoSteerAngle = 0.0;
                    autoDiffSpeed = 0.0;
                    autoSpinMode = false;
                    SwerveDrive(autoDriveSpeed, autoSteerAngle, autoDiffSpeed, autoSpinMode);
                } 
                //Spin at .25 Speed for .5 Seconds
                else if(autoTimer.get() < 3.5){
                    autoDriveSpeed = 0.25;
                    autoSteerAngle = 0.0;
                    autoDiffSpeed = 0.0;
                    autoSpinMode = true;
                    SwerveDrive(autoDriveSpeed, autoSteerAngle, autoDiffSpeed, autoSpinMode);
                } 
                //stop
                else {
                    autoDriveSpeed = 0.0;
                    autoSteerAngle = 0.0;
                    autoDiffSpeed = 0.0;
                    autoSpinMode = false;
                    SwerveDrive(autoDriveSpeed, autoSteerAngle, autoDiffSpeed, autoSpinMode);
                }

            break;
        }
    }

  /** This function is called once when teleop is enabled. */
    @Override
    public void teleopInit() {}

  /** This function is called periodically during operator control. */
    @Override
    public void teleopPeriodic() {

        //Get values from Xbox Controller (Left Stick X and Y axis, Right Stick X Axis, and Right Bumper)
        double leftStickX = controller1.getLeftX();
        double leftStickY = controller1.getLeftY();

        double rightStickX = controller1.getRightX();

        boolean modeButton = controller1.getRightBumper();

        //Shaping the Left Y Axis for smoother Control

        double leftStickY_shaped;

        if(leftStickY > 0) {
        leftStickY_shaped = leftStickY*leftStickY;
        } else {
        leftStickY_shaped = -leftStickY*leftStickY;
        }

        //Calling the Swerve Drive Function and Feeding it the Values from our Controller
        //Left Stick Y Shaped is being assigned to the Drive Speed, Right Stick X is being assigned to Wheel Turning, left X is being assigned to diff speed and Right Bumper selects mode

        SwerveDrive(leftStickY_shaped, rightStickX, leftStickX, modeButton);

    }


    //Creation of Swerve Drive Function
    public void SwerveDrive(double driveSpeed, double steer, double diffSpeed, boolean mode){

        //Doubles that will be used in equations later
        double frontRightEq;
        double frontLeftEq;
        double backRightEq;
        double backLeftEq;

        double gainFront;
        double gainBack;

        double frontRightError;
        double frontLeftError;
        double backRightError;
        double backLeftError;

        //Average Voltages from our Analog encoders
        double frontRightAverage = frontRightEnc.getAverageVoltage();
        double frontLeftAverage = frontLeftEnc.getAverageVoltage();
        double backRightAverage = backRightEnc.getAverageVoltage();
        double backLeftAverage = backLeftEnc.getAverageVoltage();

        //Mode Selector
        //if True, Sets wheels to 45deg angles and allows robot to spin in place, otherwise just drive normal.

        if(mode){
            //45deg and set the back to the opposite of the front
            gainFront = .5*1.25;
            gainBack = -.5*1.25;
        } else {
            // 90deg wheels all alligned
            gainFront = 1.25;
            gainBack = 1.25;
        }

        //Equation that requires information about wheel allignment taking the mode and right stick value multiplying them and adding the base voltage
        frontRightEq = gainFront*(steer) + 1.44;
        frontLeftEq = gainFront*(steer) + 2.94;
        backRightEq = gainBack*(steer) + 1.94;
        backLeftEq = gainBack*(steer) + 0.73;

        //Compute Error Signals from each Encoder and Adjusts if command/feedback flips from 0 to 5 or vise versa

        frontRightError = frontRightEq - frontRightAverage;
        if(frontRightError > 3.5){
            frontRightError = frontRightError - 5.0;
        }
        if(frontRightError < -3.5){
            frontRightError = frontRightError + 5.0;
        }

        frontLeftError = frontLeftEq - frontLeftAverage;
        if(frontLeftError > 3.5){
            frontLeftError = frontLeftError - 5.0;
        }
        if(frontLeftError < -3.5){
            frontLeftError = frontLeftError + 5.0;
        }

        backRightError = backRightEq - backRightAverage;
        if(backRightError > 3.5){
            backRightError = backRightError - 5.0;
        }
        if(backRightError < -3.5){
            backRightError = backRightError + 5.0;
        }

        backLeftError = backLeftEq - backLeftAverage;
        if(backLeftError > 3.5){
            backLeftError = backLeftError - 5.0;
        }
        if(backLeftError < -3.5){
            backLeftError = backLeftError + 5.0;
        }

        //Set steering motor commands to drive the error to 0

        frontRightTurn.set(frontRightError*1.5);
        frontLeftTurn.set(frontLeftError*1.5);
        backRightTurn.set(backRightError*1.5);
        backLeftTurn.set(backLeftError*1.5);

        //Sets the Driving Motors power to drive

        frontRightDrive.set(Math.max(-1, Math.min(1, (driveSpeed+diffSpeed))));
        frontLeftDrive.set(Math.max(-1, Math.min(1, (driveSpeed+diffSpeed))));
        backRightDrive.set(Math.max(-1, Math.min(1, (driveSpeed+diffSpeed))));
        backLeftDrive.set(Math.max(-1, Math.min(1, (driveSpeed+diffSpeed))));

    }




    /** This function is called once when the robot is disabled. */
    @Override
    public void disabledInit() {}

    /** This function is called periodically when disabled. */
    @Override
    public void disabledPeriodic() {}

    /** This function is called once when test mode is enabled. */
    @Override
    public void testInit() {}

    /** This function is called periodically during test mode. */
    @Override
    public void testPeriodic() {}

    /** This function is called once when the robot is first started up. */
    @Override
    public void simulationInit() {}

    /** This function is called periodically whilst in simulation. */
    @Override
    public void simulationPeriodic() {}
}