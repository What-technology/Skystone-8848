package org.firstinspires.ftc.teamcode.units;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.config.Config;

public class PID {
    private double[] pid;
    private double errAllowable;
    public PID(double[] pid,double errAllowable){
        this.pid = pid;
        this.errAllowable = errAllowable;
    }
    public PID(){}

    /**
     * PID参数
     */
    double kp = pid[0];
    double ki = pid[1];
    double kd = pid[2];

    private double target = 0.0;
    private double actual = 0.0;
    private double err = 0.0;
    private double lastErr = 0.0;
    private double outPut = 0.0;
    private double integral = 0.0;

    boolean isInErrAllowable = false;

    /**
     * PID控制方法
     */
    public double run(double actual, double target){
        this.actual = actual;
        this.target = target;
        err = this.target - this.actual;//计算偏差
        if (Math.abs(err) > errAllowable/*检测是否在死区*/) {
            integral += err;
            outPut = kp * err + ki * integral + kd * (err - lastErr);//PID函数运算
            isInErrAllowable = false;
        } else {
            outPut = 0.0;
            isInErrAllowable = true;
        }
        lastErr = err;//更新偏差
        return outPut;
    }

    /**
     * 调试方法
     */
    public void debug(LinearOpMode opMode) {
        if (opMode == null){
            opMode=Config.opMode;
        }
        opMode.telemetry.addLine()
                .addData("实际", "%.1f", actual)
                .addData("目标", "%.1f", target)
                .addData("偏差", "%.1f", err)
                .addData("功率", "%.4f", outPut);
        opMode.telemetry.addLine("PID :")
                .addData("P","%.5f", kp)
                .addData("I","%.5f", ki)
                .addData("D","%.5f", kd)
                .addData("允许误差", errAllowable);
    }
}
