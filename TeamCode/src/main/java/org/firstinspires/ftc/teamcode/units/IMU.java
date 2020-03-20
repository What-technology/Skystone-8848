package org.firstinspires.ftc.teamcode.units;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import static org.firstinspires.ftc.teamcode.units.IMU.Direction.*;

/**
 * 这是IMU（惯性测量单元）类。
 * IMU是嵌入在REV主控器中的一组芯片，内置了三轴陀螺仪、三轴加速度计、磁场传感器。
 * 合理的使用其中的陀螺仪可以在很大程度上帮助自动的实现。
 * 陀螺仪的功能不仅仅限于帮助完成转向，我们在自动阶段机器人着陆途中利用机器人倾斜角变化测控下降进度。
 */
public class IMU extends Unit {

    /**
     * IMU硬件
     */
    private BNO055IMU imu;//惯性测量单元
    private Orientation angles;
    private Acceleration gravity;

    private boolean start = false;
    int[] cycle = new int[]{0,0,0};//圈数

    /**
     * 监测线程
     */
    private class Monitor extends Thread{
        @Override
        public void run(){
            start = true;
            double[] lastAngle = getAngle();
            opMode().waitForStart();
            while (opModeIsActive()) {
                while (start && opModeIsActive()) {
                    double[] nowAngle = getAngle();//记录本次角度
                    for (int i=0;i<=2;i++) {
                        if (Math.abs(nowAngle[i] - lastAngle[i]) >= 180) {
                            cycle[i] += (int) Math.signum(lastAngle[i] - nowAngle[i]);//确定突变方向（顺时针圈数增加）
                        }//判断是否有突变
                    }//遍历XYZ三坐标
                    lastAngle = nowAngle;//更新上一次角度
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private Monitor monitor=new Monitor();

    //初始化
    public IMU() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm =new JustLoggingAccelerationIntegrator();

        imu = hardwareMap().get(BNO055IMU.class, config().IMU_GETNAME);
        imu.initialize(parameters);

        imu.startAccelerationIntegration(new Position(),new Velocity(), 100);

        startMonitor();//默认启动监测器
        initAngle();//初始化陀螺仪位置
    }

    /**
     * 初始化角度
     */
    public void initAngle() {
        resetCycle();
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        config().initialAngle = new double[]{angles.firstAngle, 0.0, 0.0};
    }

    /**
     * 读取含有圈数的角度
     */
    public double getAngleWithCycle(Direction direction) {
        return cycle[directionToArrayIndex(direction)] * 360.0 + getAngle(direction);
    }

    /**
     * 转换(-180,180]的角度为带有圈数的角度
     */
    public double convertAngle(Direction direction,double angle){
        double[] absAimReduceActul = new double[]{0.0, 0.0, 0.0};//声明及初始化数组
        double[] angle1 = getAngle();
        int directionIndex = directionToArrayIndex(direction);
        int cycle = this.cycle[directionIndex];
        for (int i=0;i<=2;i++) {
            absAimReduceActul[i] = Math.abs(angle1[i]-((cycle+(i-1)) * 360 + angle));//遍历数组
        }//读取相邻三个周期的值
        if (absAimReduceActul[0] < absAimReduceActul[1]) {
            if (absAimReduceActul[0] < absAimReduceActul[2]) {
                return (cycle-1) * 360 + angle;//T-1区间
            } else {
                return (cycle+1) * 360 + angle;//T+1区间
            }
        } else {
            if (absAimReduceActul[1] < absAimReduceActul[2]) {
                return (cycle+0) * 360 + angle;//T区间
            } else {
                return (cycle+1) * 360 + angle;//T+1区间
            }
        }//判断哪个区间有最接近的解
    }

    /**
     * 启动监测
     */
    public void startMonitor() {
        start = true;
        if (!monitor.isAlive()) {
            monitor.start();
        }
    }

    /**
     * 停止监测
     */
    public void stopMonitor() {
        start = false;
    }

    /**
     * 重置圈数
     */
    public void resetCycle() {
        cycle =new int[]{0,0,0};
    }

    /**
     * 获取角度
     */
    public double getAngle(Direction direction){
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        switch (direction) {
            case X:
                return -(angles.firstAngle - config().initialAngle[0]);
            case Y:
                return -(angles.secondAngle - config().initialAngle[1]);
            case Z:
                return  -(angles.thirdAngle - config().initialAngle[2]);
            default:
                return -(angles.firstAngle - config().initialAngle[0]);
        }
    }

    /**
     * 获取三个方向角度
     */
    public double[] getAngle() {
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return new double[]{-(angles.firstAngle - config().initialAngle[0]),
                -(angles.secondAngle - config().initialAngle[1]), -(angles.thirdAngle - config().initialAngle[2])};
    }
    /**
     * 获取加速度（不传参则为合加速度）
     */
    public double getAccel(Direction direction) {
        gravity = imu.getGravity();
        switch (direction) {
            case X : return gravity.xAccel;
            case Y : return gravity.yAccel;
            case Z : return gravity.zAccel;//xyz三个方向的加速度
            default: return gravity.xAccel;
        }
    }
    public double getAccel(){
        gravity = imu.getGravity();
        return Math.sqrt(gravity.xAccel * gravity.xAccel
                + gravity.yAccel * gravity.yAccel
                + gravity.zAccel * gravity.zAccel);//合加速度
    }

    /**
     * 方向枚举
     */
    enum Direction {
        X, Y, Z
    }

    /**
     * 转换方向枚举到数组序号
     */
    public static int directionToArrayIndex(Direction direction) {
        switch (direction){
            case X: return 0;
            case Y: return 1;
            case Z: return 2;
            default: return 0;
        }
    }

    /**
     * 转换数组序号到方向枚举
     */
    public static Direction arrayIndexToDirection(int index){
        switch (index){
            case 0: return X;
            case 1: return Y;
            case 2: return Z;
            default: return X;
        }
    }
}
