package org.firstinspires.ftc.teamcode.units;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.config.DcMotorInfo;

/**
 * 这是底盘控制类。
 * 底盘是一台机器人最重要的组件之一，因此如你所见这个类的体积非常庞大。
 */
public class Chassis extends Unit {
    /**
     * 底盘马达硬件
     */
    DcMotor frontLeftMotor = DcMotorInfo.buildMotor(config().FRONT_LEFT_MOTOR);//左前方
    DcMotor rearLeftMotor = DcMotorInfo.buildMotor(config().REAR_LEFT_MOTOR);//左后方
    DcMotor frontRightMotor = DcMotorInfo.buildMotor(config().FRONT_RIGHT_MOTOR);//右前方
    DcMotor rearRightMotor =  DcMotorInfo.buildMotor(config().REAR_RIGHT_MOTOR);//右后方

    //陀螺仪的PID
    private PID pidIMU =new PID(config().CHASSIS_ROTATE_PID, config().CHASSIS_ROTATE_BLIND);
    private double lastAngleAim = 0.0;
    //TF的PID
    private PID pidTF = new PID(config().CHASSIS_TF_PID, config().CHASSIS_TF_BLIND);
    //编码器的PID
    private PID pidEncoder = new PID(config().CHASSIS_ENCODER_PID, config().CHASSIS_ENCODER_BLIND);

    //方向是否全部改变
    boolean directionAllChanged = false;
    private ElapsedTime time = new ElapsedTime();

    //构造函数
    public Chassis(){
        time.reset();
    }

    /**
     * 编码器控制
     * @param angle 运动方向与x轴所成角
     * @param target 要走的脉冲数
     * @param outTime 限时
     */
    private void setTargetByEncoder(double angle,int target,int outTime)  {
        DcMotor.ZeroPowerBehavior zeroPowerBehavior = frontLeftMotor.getZeroPowerBehavior();
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        int aim = target + frontLeftMotor.getCurrentPosition();
//        opMode().telemetryLog("实际:${frontLeftMotor.currentPosition},目标:${aim}")
        drive(0.0, 0.0,0.0);
        int finished = 0;
        long startTime = System.currentTimeMillis();
        while (opModeIsActive() && System.currentTimeMillis()-startTime<outTime) {
            double power = pidEncoder.run((double)frontLeftMotor.getCurrentPosition(),(double) aim);
            drive(power*Math.cos(angle),power*Math.cos(angle),0.0);
            power = Math.signum(power) * Math.min(Math.abs(power), config().CHASSIS_MAX_POWER);

            if (pidEncoder.isInErrAllowable) {
                finished++;
            } else {
                finished = 0;
            }
            if (finished >= 3){
                break;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            pidEncoder.debug()
//            telemetry().update()
        }
        drive(0.0, 0.0, 0.0);
        setZeroPowerBehavior(zeroPowerBehavior);
    }

    /**
     * 设置底盘朝向（原地旋转动作），需要用到IMU
     * @param angle 目标角（与x轴所成角）
     * @param outTime 限时
     */
    public void setAngle(double angle,int outTime) {
        if (!opMode().runIMU){
            return ;//如果不启动IMU，则直接返回
        }
        int pidIMUInBlind = 0;
        DcMotor.ZeroPowerBehavior zeroPowerBehavior = frontLeftMotor.getZeroPowerBehavior();
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        IMU imu = opMode().imu;//返回陀螺仪的监测器
        long startTime = System.currentTimeMillis();
        while (opModeIsActive() && System.currentTimeMillis()-startTime<outTime) {
            double actual = imu.getAngleWithCycle(IMU.Direction.X);
            double target = imu.convertAngle(IMU.Direction.X, angle);
            double power = pidIMU.run(actual, target);//计算功率
            drive(0.0, 0.0, power);//给马达输出功率
//        pidIMU.debug(opMode());telemetry().update()//debug部分，可以注释
            if (pidIMU.isInErrAllowable) {
                pidIMUInBlind++;
            } else {
                pidIMUInBlind = 0;
            }
            if (pidIMUInBlind >= 3){
                break;

            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        drive(0.0, 0.0,0.0);
        setZeroPowerBehavior(zeroPowerBehavior);
    }

    /**
     * 延时控制，时间万岁
     */
    public void setTargetByTime(double xspeed, double yspeed, double aspeed, long milliSeconds) {
        long startMS = System.currentTimeMillis();
        while (opModeIsActive() && System.currentTimeMillis()-startMS < milliSeconds) {
            drive(xspeed, yspeed, aspeed);
        }
        drive(0.0, 0.0, 0.0);
    }

    /**
     * 通过手柄设置底盘功率
     */
    public void setPowerByGamepad() {
        Gamepad gamepad = gamepad1();//选择所使用的手柄
        drive(gamepad.left_stick_x*config().CHASSIS_TURN_K,
                -gamepad.left_stick_y*config().CHASSIS_TURN_K,
                -gamepad.left_trigger + gamepad.right_trigger);
    }

    /**
     * 统一设置马达刹车
     */
    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        frontLeftMotor.setZeroPowerBehavior(zeroPowerBehavior);
        frontRightMotor.setZeroPowerBehavior(zeroPowerBehavior);
        rearLeftMotor.setZeroPowerBehavior(zeroPowerBehavior);
        rearRightMotor.setZeroPowerBehavior(zeroPowerBehavior);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统一为麦克纳姆轮设置速度，被drive()调用
     * @param frontLeft 左前轮速度
     * @param frontRight 右前轮速度
     * @param rearLeft 左后轮速度
     * @param rearRight  右后轮速度
     */
    private void setMovementPower(double frontLeft,double frontRight,double rearLeft,double rearRight){
        frontLeftMotor.setPower(frontLeft);
        frontRightMotor.setPower(frontRight);
        rearLeftMotor.setPower(rearLeft);
        rearRightMotor.setPower(rearRight);
    }

    /**
     * 麦克纳姆轮驱动程序
     * @param xspeed 横向速度，正方向为右
     * @param yspeed 纵向速度，正方向为前
     * @param aspeed 旋转向(yaw轴)速度，正方向为逆时针
     */
    private void drive(double xspeed,double yspeed,double aspeed){
        double frontRight=yspeed-xspeed+aspeed;
        double frontLeft=yspeed+xspeed-aspeed;
        double rearRight=yspeed-xspeed-aspeed;
        double rearLeft=yspeed+xspeed+aspeed;

        double max=frontLeft; //比较出四个轮中最大速度
        if(max<frontRight){
            max=frontRight;
        }
        if(max<rearRight){
            max=rearRight;
        }
        if(max<rearLeft){
            max=rearLeft;
        }

        if(max>config().CHASSIS_MAX_POWER){   //如果最大速度超过限额，按比例缩小各个轮的速度
            frontLeft=frontLeft/max*config().CHASSIS_MAX_POWER;
            frontRight=frontRight/max*config().CHASSIS_MAX_POWER;
            rearLeft=rearLeft/max*config().CHASSIS_MAX_POWER;
            rearRight=rearRight/max*config().CHASSIS_MAX_POWER;
        }
        setMovementPower(frontLeft,frontRight,rearLeft,rearRight);//统一设置速度
    }

    /**
     * 统一重置底盘编码器
     */
    public void resetEncoder() {
        DcMotorInfo.resetEncoder(frontLeftMotor);
        DcMotorInfo.resetEncoder(frontRightMotor);
        DcMotorInfo.resetEncoder(rearLeftMotor);
        DcMotorInfo.resetEncoder(rearRightMotor);
    }

    /**
     * 回传位置
     */
    public void telemetryPosition() {
        telemetry().addData("fl", frontLeftMotor.getCurrentPosition());
        telemetry().addData("fr", frontRightMotor.getCurrentPosition());
        telemetry().addData("rl", rearLeftMotor.getCurrentPosition());
        telemetry().addData("rr", rearRightMotor.getCurrentPosition());
    }

}
