package org.firstinspires.ftc.teamcode;

import android.util.Log;
import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.config.Config;
import org.firstinspires.ftc.teamcode.units.Vuforia;
import org.firstinspires.ftc.teamcode.units.Chassis;
import org.firstinspires.ftc.teamcode.units.IMU;


public abstract class BaseOpMode extends LinearOpMode{
    public boolean runArm,runLift,runChassis,runIMU,runCollect,runVuforia,runTensorFlow;

    public final String TAG="BaseOpMode";
    public Chassis chassis;
    //public Lift lift;
    //public Arm arm;
    public IMU imu;
    //public Collect collect;
    public Vuforia vuforia;
    //public TensorFlow tensorFlow;

    public BaseOpMode(boolean runArm, boolean runLift, boolean runChassis, boolean runCollect, boolean runIMU, boolean runVuforia, boolean runTensorFlow){
        this.runArm=runArm;
        this.runLift=runLift;
        this.runChassis=runChassis;
        this.runIMU=runIMU;
        this.runCollect=runCollect;
        this.runVuforia=runVuforia;
        this.runTensorFlow=runTensorFlow;
    }

    boolean gamepadMonitor = false;//手柄监测
    private ElapsedTime welcomeTime = new ElapsedTime();//计时器
    private ElapsedTime batterTime = new ElapsedTime();//计时器

    private int createVuforia;


    private class AssistThread extends Thread{
        @Override
        public void run() {
            int batterLowTimes = 0;
            Log.d(TAG, "assistThread");
            ElapsedTime time = new ElapsedTime();
            waitForStart();
            while (opModeIsActive()) {
                if (gamepadMonitor) {
                    if (gamepad1.getGamepadId() == -2 && time.seconds() >= 1) {
                        sound(R.raw.handle_disconnect);//手柄断连提示音
                        time.reset();
                    }
                }
                if (getBatteryVoltage() < 10) {
                    batterLowTimes++;
                    if (batterTime.seconds() >= 5 && batterLowTimes >= 20) {
                        batterTime.reset();
                        sound(R.raw.battery_low);
                    }
                } else batterLowTimes = 0;
                if (gamepad1.right_stick_button) welcome();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 填写机器人代码的地方
     */
    public abstract void run();

    /**
     * 因为涉及到硬件库调用等部分对象需要在runOpMode()中调用，为了照顾初始化和主程序运行，这里final了原来的runOpMode()
     */
    @Override
    public final void runOpMode() {
        Log.d(TAG, "initRobot()");
        telemetry.addData(">", "初始化中，请勿开始");
        telemetry.update();
        initRobot();//初始化机器人
        new AssistThread().start();//启动协助线程
        telemetry.addData(">", "初始化完毕，可以启动->>>>>>>>>>");
        telemetry.update();
        Log.d(TAG, "run()");
        run();//运行阶段
        Log.d(TAG, "stopRobot()");
        stopRobot();
        Log.d(TAG, "End");
    }

    /**
     * 初始化机器人
     */
    private void initRobot() {
        Config.opMode = this;//将目前启动的OpMode发送到配置类
        if (runVuforia) {
            createVuforia = 0;
        } else if (runTensorFlow && runVuforia) {
            createVuforia = 1;
        } else {
            createVuforia = 2;
        }
        //用于保持Vuforia的单例
        if (createVuforia == 0) {
            int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id",
                    hardwareMap.appContext.getPackageName());
            VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
            parameters.vuforiaLicenseKey = config().VUFORIA_KEY;
            parameters.cameraDirection = config().CAMERA_DIRECTION;
            parameters.cameraName = config().webcamName;
            config().vuforia = ClassFactory.getInstance().createVuforia(parameters);
        } else if (createVuforia == 1) {
            VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
            parameters.vuforiaLicenseKey = config().VUFORIA_KEY;
            parameters.cameraDirection = config().CAMERA_DIRECTION;
            config().vuforia = ClassFactory.getInstance().createVuforia(parameters);
        } else {
            config().vuforia = null;
        }

        //初始化组件
        if (runChassis) chassis = new Chassis();
        //if (runLift) lift = new Lift();
        if (runVuforia) vuforia = new Vuforia();
        //if (runTensorFlow) tensorFlow = new TensorFlow();
        if (runIMU) imu = new IMU();
        //if (runCollect) collect = new Collect();
        //if (runArm) arm = new Arm();
    }

    /**
     * 程序停止
     */
    private void stopRobot() {
        if (runVuforia) vuforia.deactivate();
        //if (runTensorFlow) tensorFlow.tfod.shutdown();
        if (runIMU) imu.stopMonitor();
    }

    /**
     * 获取电池电压
     */
    private double getBatteryVoltage(){
        double result = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double voltage = sensor.getVoltage();
            if (voltage > 0) {
                result = Math.min(result, voltage);
            }
        }
        return result;
    }

    /**
     * 欢迎操作手
     */
    private void welcome() {
        if (welcomeTime.seconds() >= 1) {
            welcomeTime.reset();
            new Thread() {
                @Override
                public void run(){
                    sound(R.raw.welcome);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //sound(R.raw.beginning_3);
                }
            }.start();
        }
    }

    public void waitForEnd() {
        while (opModeIsActive()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 播放声音
     */
    private void sound(int i) {
        SoundPlayer.getInstance().startPlaying(AppUtil.getInstance().getApplication().getApplicationContext(), i);
    }

    /**
     * 打印Log
     */
    public void log(Object msg) {
        Log.d("UST", msg.toString());
    }

    /**
     * 打印telemetry
     */
    public void telemetryLog(Object msg) {
        telemetry.addData("msg", msg);
        telemetry.update();
    }

    /**
     * 返回配置类单例
     */
    private Config config(){
        return Config.getInstance();
    }
}
