package tw.edu.pu.csim.tcyang.posedetection

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //將drawable的圖片轉換成Bitmap
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.miss)
        img.setImageBitmap(bitmap)

        /*
        // Base pose detector with streaming frames, when depending on the pose-detection sdk
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
         */

        // Accurate pose detector on static images, when depending on the pose-detection-accurate sdk
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build()
        val poseDetector = PoseDetection.getClient(options)

        //Prepare the input image
        val image = InputImage.fromBitmap(bitmap, 0)

        btn.setOnClickListener(object:View.OnClickListener{
            override fun onClick(p0: View?) {
                poseDetector.process(image)
                    .addOnSuccessListener { pose ->
                        // Task completed successfully
                        val dstBitmap = Bitmap.createBitmap(
                            bitmap.width, bitmap.height,
                            Bitmap.Config.ARGB_8888
                        )
                        var canvas = Canvas(dstBitmap)

                        //繪製原始圖片
                        canvas.drawBitmap(bitmap, 0f, 0f, null)

                        //設定畫筆
                        val paint = Paint()
                        paint.color = Color.RED
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth = 50f

                        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)

                        var xPos = leftShoulder.position.x
                        var yPos = leftShoulder.position.y
                        //canvas.drawCircle(xPos, yPos, 20f, paint)

                        val allPoseLandmarks = pose.getAllPoseLandmarks()
                        for (item in allPoseLandmarks) {
                            xPos = item.position.x
                            yPos = item.position.y
                            canvas.drawCircle(xPos, yPos, 20f, paint)
                        }

                        img.setImageBitmap(dstBitmap)

                        Toast.makeText(baseContext, "偵測成功", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        Toast.makeText(baseContext, "抱歉，發生錯誤！",
                            Toast.LENGTH_SHORT).show()
                    }
            }
        })



    }
}