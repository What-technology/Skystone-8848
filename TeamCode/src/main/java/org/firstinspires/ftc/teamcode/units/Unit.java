package org.firstinspires.ftc.teamcode.units;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.BaseOpMode;
import org.firstinspires.ftc.teamcode.config.Config;

/**
 * 这是单元类，是一个重要的抽象类。
 * 每个机器人组件都继承自这个类，使得一些重要的变量或方法可以无需从Config里调用。
 */
public abstract class Unit {
    public Unit(){}
    //获取正在运行的LinearOpMode
    public BaseOpMode opMode(){
        return Config.opMode;
    }

    //opMode是否在运行
    public boolean opModeIsActive(){
        return Config.opMode.opModeIsActive();
    }

    //硬件库
    public HardwareMap hardwareMap(){
        return Config.opMode.hardwareMap;
    }

    //返回数据
    public Telemetry telemetry(){
        return Config.opMode.telemetry;
    }

    //主线程休眠
    public void sleep(long mill) {
        Config.opMode.sleep(mill);
    }

    //游戏手柄
    public Gamepad gamepad1() {
        return Config.opMode.gamepad1;
    }
    public Gamepad gamepad2() {
        return Config.opMode.gamepad2;
    }

    //配置类
    public Config config(){
        return Config.getInstance();
    }
}
