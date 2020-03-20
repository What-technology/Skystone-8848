package org.firstinspires.ftc.teamcode.config;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * 这个类的功能是帮助实现马达信息统一储存和构造，设计的初衷还是懒而简洁。
 */
public class DcMotorInfo {
    public String getName;
    public DcMotorSimple.Direction direction;
    public DcMotor.ZeroPowerBehavior brake;
    public DcMotor.RunMode runMode;
    public DcMotorInfo(String getName, DcMotorSimple.Direction direction, DcMotor.ZeroPowerBehavior brake, DcMotor.RunMode runMode){
        this.getName=getName;
        this.direction=direction;
        this.brake=brake;
        this.runMode=runMode;
    }
    public DcMotorInfo(){}

    //配置马达
    public static DcMotor buildMotor(DcMotorInfo info)  {
        DcMotor motor = Config.opMode.hardwareMap.dcMotor.get(info.getName);
        motor.setDirection(info.direction);
        motor.setZeroPowerBehavior(info.brake);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//清空编码器
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        motor.setMode(info.runMode);//设定编码器模式
        return motor;
    }

    //改变方向
    public static DcMotorSimple.Direction directionChange(DcMotor motor) {
        if (motor.getDirection().equals(DcMotorSimple.Direction.FORWARD) ) {
            return DcMotorSimple.Direction.REVERSE;
        } else {
            return DcMotorSimple.Direction.FORWARD;
        }
    }

    //重置编码器
    public static void resetEncoder(DcMotor motor) {
        DcMotor.RunMode mode = motor.getMode();
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        motor.setMode(mode);
    }
}
