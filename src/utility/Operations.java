package utility;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SerialPort;

public class Operations {
	int encoderPPR = 1024; //NOTE: Only useful for relative mode
	int encoderDPR = calcDPR(encoderPPR);
	int encoderPPR_PWM = 1; //TODO: Configure with encoder PPR when in absolute mode
	int encoderDPR_PWM = calcDPR(encoderPPR_PWM);
	
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
	}
	
	public void drive(double value) {
		value *= 1;  //Sets soft limit for speed
		
		frontLeft.set(ControlMode.PercentOutput, value);
		frontRight.set(ControlMode.PercentOutput, value);
		backLeft.set(ControlMode.PercentOutput, value);
		backRight.set(ControlMode.PercentOutput, value);
	}
	
	public void driveDistance(double dist) {
		int ticks = calcTicks(dist);
		frontLeft.set(ControlMode.Position, ticks);
		frontRight.set(ControlMode.Position, ticks);
	}
	
	public void turnTo(double angle) {
		double curAngle = navx.getAngle();
	}
	
	private int calcTicks(double dist) {
		return (int) (dist / encoderDPR);
	}
	
	private int calcDPR(int PPR) {
		int wheelDiameter = 4; //Inches
		return (int) (Math.PI*wheelDiameter / PPR);
	}
}
