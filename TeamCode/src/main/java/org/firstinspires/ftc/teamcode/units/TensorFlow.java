package org.firstinspires.ftc.teamcode.units;


import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import static org.firstinspires.ftc.robotcore.external.tfod.TfodRoverRuckus.LABEL_GOLD_MINERAL;
import static org.firstinspires.ftc.robotcore.external.tfod.TfodRoverRuckus.LABEL_SILVER_MINERAL;

public class TensorFlow extends Unit {
    private VuforiaLocalizer vuforia;
    public TFObjectDetector tfod;

    private static final String TFOD_MODEL_ASSET = "Skystone.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Stone";
    private static final String LABEL_SECOND_ELEMENT = "Skystone";

    /**
     * 启动
     */
    public void activate() {
        tfod.activate();
    }

    /**
     * 停止
     */
    public void deactivate() {
        tfod.deactivate();
    }

    /**
     * 初始化Vu
     */
    private void initVuforia() {
//        val parameters = VuforiaLocalizer.Parameters()
//        parameters.vuforiaLicenseKey = config().VUFORIA_KEY
//        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK
//        vuforia = ClassFactory.getInstance().createVuforia(parameters)
        vuforia = config().vuforia;
    }

    /**
     * 初始化TF
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap().appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap().appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }

}
