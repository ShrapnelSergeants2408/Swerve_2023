// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.photonvision.PhotonCamera;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
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
    private static final String kCustomAuto = "Auto left"; 
    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();

    VictorSPX frontRightTurn;
    VictorSPX frontLeftTurn;
    VictorSPX backRightTurn;
    VictorSPX backLeftTurn;
    VictorSPX wristMotor;
    VictorSPX armExtensionMotor;

    CANSparkMax frontRightDrive;
    CANSparkMax frontLeftDrive;
    CANSparkMax backRightDrive;
    CANSparkMax backLeftDrive;
    CANSparkMax armLiftMotor; 
    
    DoubleSolenoid gripperDoubleSolenoid;

    AnalogInput frontRightEnc;
    AnalogInput frontLeftEnc;
    AnalogInput backRightEnc;
    AnalogInput backLeftEnc;

    XboxController driverController = new XboxController(0);
    XboxController operatorController = new XboxController(1);

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

        frontRightTurn = new VictorSPX(32);
        frontLeftTurn = new VictorSPX(30);
        backRightTurn = new VictorSPX(33);
        backLeftTurn = new VictorSPX(31);
        armExtensionMotor = new VictorSPX(50);

        frontRightDrive = new CANSparkMax(22, MotorType.kBrushless);
        frontLeftDrive = new CANSparkMax(20, MotorType.kBrushless);
        backRightDrive = new CANSparkMax(23, MotorType.kBrushless);
        backLeftDrive = new CANSparkMax(21, MotorType.kBrushless);

        armLiftMotor = new CANSparkMax(40, MotorType.kBrushless);
        

        gripperDoubleSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, 0, 1);

        frontRightEnc = new AnalogInput(2);
        frontLeftEnc = new AnalogInput(0);
        backRightEnc = new AnalogInput(3);
        backLeftEnc = new AnalogInput(1);

        
    }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
    @Override
    public void robotPeriodic() {

        
    }

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
            break;
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
        double leftStickX = driverController.getLeftX();
        double leftStickY = driverController.getLeftY();

        double rightStickX = driverController.getRightX();

        boolean modeButton = driverController.getRightBumper();
        
        boolean grabButton = operatorController.getXButton();
        boolean releaseButton = operatorController.getBButton();

        boolean armPosUp = operatorController.getYButton();
        boolean armPosDown = operatorController.getAButton();

        //frontLeftDrive.getEncoder().

        //Shaping the Left Y Axis for smoother Control
        double leftStickY_shaped; 
        if(leftStickY > 0) {
        leftStickY_shaped = leftStickY*leftStickY;
        } else {
        leftStickY_shaped = -leftStickY*leftStickY;
        }

        // arm controls
        String armSet = "start";

        if(driverController.getYButton()){
            armExtensionMotor.set(VictorSPXControlMode.PercentOutput,0.5);
            SmartDashboard.putBoolean("working", true);
        } else {
            armExtensionMotor.set(VictorSPXControlMode.Disabled, 0);
            SmartDashboard.putBoolean("working", false);
        }
        //manual arm control 
        if(operatorController.getRightTriggerAxis() > 0){
            armLiftMotor.set(.3);
        }else if (operatorController.getLeftTriggerAxis() > 0){
            armLiftMotor.set(-.3);
        }
        //Logic for arm going up
        if(armPosUp && armSet != "high"){
            if(armSet == "start"){
                armSet = "low";
                armPosition(armSet);
            } else if(armSet == "low"){
                armSet = "med";
                armPosition("med");
            } else if(armSet == "med"){
                armSet = "high";
                armPosition(armSet);
            }
        // logic for arm going down
        } else if(armPosDown && armSet != "start"){
            if(armSet == "high"){
                armSet = "med";
                armPosition(armSet);
            } else if (armSet =="med"){
                armSet = "low";
                armPosition(armSet);
            } else if (armSet == "low"){
                armSet = "start";
                armPosition(armSet);
            }
        }


        // claw controls
        if(grabButton){
            gripperDoubleSolenoid.set(Value.kForward);
        }else if(releaseButton){
            gripperDoubleSolenoid.set(Value.kReverse);
        }else{
            gripperDoubleSolenoid.set(Value.kOff);
        }

        //Calling the Swerve Drive Function and Feeding it the Values from our Controller
        //Left Stick Y Shaped is being assigned to the Drive Speed, Right Stick X is being assigned to Wheel Turning, left X is being assigned to diff speed and Right Bumper selects mode

        SwerveDrive(leftStickY_shaped, rightStickX, leftStickX, modeButton);
    }

    public void armPosition(String position){
        double armEq;
        double armGain;
        double armError;
        RelativeEncoder armLiftEncoder = armLiftMotor.getEncoder();
        double armLiftPosition = armLiftEncoder.getPosition();

        switch(position) {
            case "start":

            break;
            
            case "low": 

            break;

            case "med":

            break;

            case "high":

            break;

        }
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
        frontRightEq = gainFront*(steer) + 3.36;
        frontLeftEq = gainFront*(steer) + 3.48;
        backRightEq = gainBack*(steer) + 1.06;
        backLeftEq = gainBack*(steer) + 2.79;

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

        SmartDashboard.putNumber("front left Voltage", frontLeftAverage);
        SmartDashboard.putNumber("back left Voltage", backLeftAverage);
        SmartDashboard.putNumber("front right Voltage", frontRightAverage);
        SmartDashboard.putNumber("back right Voltage", backRightAverage);
        //Set steering motor commands to drive the error to 0

        frontRightTurn.set(VictorSPXControlMode.PercentOutput,frontRightError*1.5);
        frontLeftTurn.set(VictorSPXControlMode.PercentOutput,frontLeftError*1.5);
        backRightTurn.set(VictorSPXControlMode.PercentOutput,backRightError*1.5);
        backLeftTurn.set(VictorSPXControlMode.PercentOutput,backLeftError*1.5);

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
