package utility;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SerialPort;

public class Operations implements PIDOutput {
	int encoderPPR = 1024; //NOTE: Only useful for relative mode
	int encoderDPR = calcDPR(encoderPPR);
	int encoderPPR_PWM = 1; //TODO: Configure with encoder PPR when in absolute mode
	int encoderDPR_PWM = calcDPR(encoderPPR_PWM);
	boolean useEncoderL = true;
	
	FeedbackDevice encoderAbsolute = FeedbackDevice.CTRE_MagEncoder_Absolute;
	FeedbackDevice encoderRealtive = FeedbackDevice.CTRE_MagEncoder_Relative;
	double pL = 1; //TODO: Calibrate and replace
	double iL = 0; //TODO: Calibrate and replace
	double dL = 0; //TODO: Calibrate and replace
	double pR = 1; //TODO: Calibrate and replace
	double iR = 0; //TODO: Calibrate and replace
	double dR = 0; //TODO: Calibrate and replace
	int feedbackDelay = 200;
	
	AHRS navx = new AHRS(SerialPort.Port.kMXP);
	double pN = 1; //TODO: Calibrate and replace
	double iN = 0; //TODO: Calibrate and replace
	double dN = 0; //TODO: Calibrate and replace
	PIDController pid = new PIDController(pN, iN, dN, navx, this);
	double speed = 0; //Configured by pidWrite() and only implemented by turnTo()
	
	TalonSRX frontLeft = new TalonSRX(11);  //TODO: Configure with proper deviceID
	TalonSRX frontRight = new TalonSRX(11);  //TODO: Configure with proper deviceID
	TalonSRX backLeft = new TalonSRX(11);  //TODO: Configure with proper deviceID
	TalonSRX backRight = new TalonSRX(11);  //TODO: Configure with proper deviceID
	
	TalonSRX launchLeft = new TalonSRX(11);  //TODO: Configure with proper deviceID
	TalonSRX launchRight = new TalonSRX(11);  //TODO: Configure with proper deviceID
	TalonSRX intakeLeft = new TalonSRX(11);  //TODO: Configure with proper deviceID
	TalonSRX intakeRight = new TalonSRX(11);  //TODO: Configure with proper deviceID
	
	public Operations() {
		//Configuring frontLeft PID
		frontLeft.configSelectedFeedbackSensor(encoderAbsolute, 0, feedbackDelay);
		frontLeft.config_kP(0, pL, feedbackDelay);
		frontLeft.config_kI(0, iL, feedbackDelay);
		frontLeft.config_kD(0, dL, feedbackDelay);
		
		//Configuring frontRight PID
		frontRight.configSelectedFeedbackSensor(encoderAbsolute, 0, feedbackDelay);
		frontRight.config_kP(0, pR, feedbackDelay);
		frontRight.config_kI(0, iR, feedbackDelay);
		frontRight.config_kD(0, dR, feedbackDelay);
		
		//Configuring navX PID
		pid.setOutputRange(-1, 1);
		pid.setP(pN);
		pid.setI(iN);
		pid.setD(dN);
	}
	
	public void drive(double value) {
		value *= 1;  //Sets soft limit for speed
		
		frontLeft.set(ControlMode.PercentOutput, value);
		frontRight.set(ControlMode.PercentOutput, value);
		backLeft.set(ControlMode.PercentOutput, value);
		backRight.set(ControlMode.PercentOutput, value);
	}
	
	public void drive(double left, double right) {
		left *= 1; right *= 1; //Sets soft limit for speed
		
		frontLeft.set(ControlMode.PercentOutput, left);
		frontRight.set(ControlMode.PercentOutput, right);
		backLeft.set(ControlMode.PercentOutput, left);
		backRight.set(ControlMode.PercentOutput, right);
	}
	
	public void driveDistance(double dist) {
		int ticks = calcTicks(dist);
		
		if (useEncoderL) {
			frontLeft.set(ControlMode.Position, ticks);
			frontRight.set(ControlMode.PercentOutput, frontLeft.getClosedLoopError(0));
			backLeft.set(ControlMode.PercentOutput, frontLeft.getClosedLoopError(0));
			backRight.set(ControlMode.PercentOutput, frontLeft.getClosedLoopError(0));
		}
		else {
			frontRight.set(ControlMode.Position, ticks);
			frontLeft.set(ControlMode.PercentOutput, frontLeft.getClosedLoopError(0));
			backLeft.set(ControlMode.PercentOutput, frontLeft.getClosedLoopError(0));
			backRight.set(ControlMode.PercentOutput, frontLeft.getClosedLoopError(0));
		}
	}
	
	public void turnTo(double angle) {
		//TODO: Determine if deadzone should be set
		
		pid.setSetpoint(angle);
		if (navx.getAngle() - angle > 0) { //Determines if robot needs to turn left for fastest rate
			drive(-speed, speed);
		}
		else {
			drive(speed, -speed);
		}
	}
	
	private int calcTicks(double dist) {
		return (int) (dist / encoderDPR);
	}
	
	private int calcDPR(int PPR) {
		int wheelDiameter = 4; //Inches
		return (int) (Math.PI*wheelDiameter / PPR);
	}

	@Override
	public void pidWrite(double output) {
		speed = output;
	}
}
