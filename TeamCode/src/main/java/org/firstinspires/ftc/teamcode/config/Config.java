package org.firstinspires.ftc.teamcode.config;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.BaseOpMode;

import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

public class Config {
    private Config(){}
    private static final Config instance=new Config();
    public static Config getInstance(){
        return instance;
    }
    /**
     * 实例区<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
     */
    //正在运行的LinearOpMode
    public static BaseOpMode opMode;

    //单例Vuforia
    public VuforiaLocalizer vuforia;
    //Vuforia的Key
    public final String VUFORIA_KEY="AW3BbqH/////AAAAmXvEKYeBKkQSuwntlOf9l54bTtsbZGwfTfUdVScy6zXY41HdT0Q5/RLMXDNd3jy2v4/A+fWtI8n8CGOIS///hL1WIdzmUut3jJ5XVSIBPbSMHSUxI2V+M0eYNXap743ZmLSVKCiPyWsvdiZs1VwEs8EpYxxNWJgkIT1jyXhmRrath10INBW5BCMHM4Y8YRjMo3Be1nx4nb9X0tR1Vi2lwtjqPNqML78csvzI9JsbIV/F7S+IBJXO7GHjZQdKjb6b7N0pb0Bft79IPSv08APyXduWd79LdpAY/qRBNLX45dPUVn2wBWj9ZMqkDT+fSH7c+vWKLhqeg/t2JRO1rsJMB8vXNrGJtTmltqUNpGGDx78u";
    //Vuforia摄像头方向
    public static final VuforiaLocalizer.CameraDirection CAMERA_DIRECTION = BACK;
    /**
     * 实例区>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
     */


    /**
     * 硬件信息<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
     */
    //底盘马达
    public DcMotorInfo FRONT_LEFT_MOTOR =new DcMotorInfo("m0", DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.FLOAT, DcMotor.RunMode.RUN_USING_ENCODER);//左前方马达
    public DcMotorInfo REAR_LEFT_MOTOR =new DcMotorInfo("m1", DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.FLOAT, DcMotor.RunMode.RUN_USING_ENCODER);//左后方马达
    public DcMotorInfo FRONT_RIGHT_MOTOR =new DcMotorInfo("m2", DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.FLOAT, DcMotor.RunMode.RUN_USING_ENCODER);//右前方马达
    public DcMotorInfo REAR_RIGHT_MOTOR =new DcMotorInfo("m3", DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.FLOAT, DcMotor.RunMode.RUN_USING_ENCODER);//右后方马达

    //IMU命名
    public final String IMU_GETNAME = "imu";

    //外接摄像头
    public WebcamName webcamName = opMode.hardwareMap.get(WebcamName .class, "Webcam 1");
    /**
     * 硬件信息>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
     */



    /**
     * 常量区<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
     */
    //TF图像大小
    public int ImageHeight = 1920;
    public int ImageWidth = 1080;

    //电机编码器
    public final int NeveRest40Gearmotor_ticksPerRev = 1120;
    public final int NeveRest60Gearmotor_ticksPerRev = 1680;

    //底盘马达的转向系数
    public final double CHASSIS_TURN_K = 1.0;
    //转向PID和死区
    public final double[] CHASSIS_ROTATE_PID =new double[]{0.022, 0.0, 0.025};
    public final double CHASSIS_ROTATE_BLIND = 1.0;
    //TF的PID和死区
    public final double[] CHASSIS_TF_PID = new double[]{0.0004, 0.0, 0.0001};
    public final double CHASSIS_TF_BLIND = 125.0;
    //编码器的PID
    public final double[] CHASSIS_ENCODER_PID = new double[]{0.0030, 0.0, 0.005};
    public final double CHASSIS_ENCODER_BLIND = 20.0;
    //底盘PID控制时的最大功率
    public final double CHASSIS_MAX_POWER = 1.0;

    //机械臂PID和死区
    //val ARM_PID = doubleArrayOf(0.005, 0.0, 0.001)
    //const val ARM_BLIND = 0.0
    //速度比例常数
    //const val speedK = 200.0
    //机械臂最大功率
    //const val ARM_MAX_POWER = 1.0
    //机械臂最大位置
    //const val ARM_MAX_POSITION = 2550
    //机械臂中间位置
    //const val ARM_MIDDLE_POSITION = 1000

    //弹出伺服
    //const val ARM_EJECT_OUT = 1.0

    //升降PID和死区
    //val LIFT_PID = doubleArrayOf(0.0007, 0.0001, 0.008)
    //const val LIFT_BLIND = 75.0
    //升降的最大校正功率
    //const val LIFT_CORRECT_MAXPOWER = 0.35
    //合适的高挂位置
    //const val LIFT_HIGHUP_POSITION = 10800

    //收集装置常量
    //const val COLLECT_TAKE_IN_INTO = 1.0//收入
    //const val COLLECT_TAKE_IN_OUT = 0.0//吐出
    //const val COLLECT_TAKE_IN_STOP = 0.5//停止
    //const val COLLECT_FILP_PLATE_ON = 0.40//打开
    //const val COLLECT_FILP_PLATE_OFF = 0.00//关闭

    //陀螺仪初始方向
    public double[] initialAngle = new double[]{0.0, 0.0, 0.0};
    /**
     * 常量区>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
     */



    /**
     * 按键区<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
     */

    /**
     * 按键区>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
     */
}
