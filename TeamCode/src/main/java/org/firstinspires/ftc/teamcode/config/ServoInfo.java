package org.firstinspires.ftc.teamcode.config;

import com.qualcomm.robotcore.hardware.Servo;

/**
 * 这个类的功能是帮助实现伺服信息统一储存和构造，设计的初衷还是懒而简洁。
 */
public class ServoInfo {
    public String getName;
    public Servo.Direction direction;
    public double[] scale;
    public ServoInfo(String getName, Servo.Direction direction, double[] scale){
        this.getName=getName;
        this.direction=direction;
        this.scale=scale;
    }
    public ServoInfo(String getName, Servo.Direction direction){
        this.getName=getName;
        this.direction=direction;
        this.scale= new double[]{0.0, 1.0};
    }
    public ServoInfo(){}

    //配置伺服
    public static Servo buildServo(ServoInfo info) {
        Servo servo = Config.opMode.hardwareMap.servo.get(info.getName);
        servo.setDirection(info.direction);
        servo.scaleRange(info.scale[0], info.scale[1]);
        return servo;
    }
}
