package jp.techacademy.norihiro.nakano.autoslideshowapp

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val PERMISSION_REQUEST_CODE = 100
    private var cursor:Cursor? = null

    private var timer: Timer? = null
    //タイマーの中（サブスレッドの中）でUIの描画指示があるのでHandlerクラスのインスタンスHandler()を必ず作成すること！！
    //サブスレッドでUIの描画を直接変更はできないため。（サブからメインスレッドにUI描画の変更を依頼するという形を取ることで、初めてUIの描画が変わる）
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Log.d("ANDROID", "許可されている")
                val resolver = contentResolver
                cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                )
                getContentsInfo()
                button5.setOnClickListener(this)
                button6.setOnClickListener(this)
                button7.setOnClickListener(this)

            }else{
                Log.d("ANDROID", "許可されていない")
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),PERMISSION_REQUEST_CODE)
            }
        }else{
            val resolver = contentResolver
            cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
            )
            getContentsInfo()
            button5.setOnClickListener(this)
            button6.setOnClickListener(this)
            button7.setOnClickListener(this)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cursor!!.close()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("ANDROID", "許可された")
                    val resolver = contentResolver
                    cursor = resolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                    )
                    getContentsInfo()
                    button5.setOnClickListener(this)
                    button6.setOnClickListener(this)
                    button7.setOnClickListener(this)
                }else{
                    Log.d("ANDROID", "許可されなかった")
                }
        }
    }

    private fun getContentsInfo(){
        if (cursor!!.moveToFirst()){
            do {
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                Log.d("ANDROID", "URI :" + imageUri.toString())
                imageView.setImageURI(imageUri)
            }while (cursor!!.moveToNext())
        }

    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.button5 -> getNextInfo()
            R.id.button6 -> slideStartButton()
            R.id.button7 -> getPreviousInfo()
        }
    }

    private fun getNextInfo(){
        if (!cursor!!.moveToNext()){
            cursor!!.moveToFirst()
            val fieldIndexTop = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndexTop)
            val imageUriTop = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUriTop)
        }else{
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        }
    }

    private fun getPreviousInfo(){
        if (!cursor!!.moveToPrevious()){
            cursor!!.moveToLast()
            val fieldIndexLast = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndexLast)
            val imageUriLast = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUriLast)
        }else{
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        }
    }

    private fun slideStartButton(){
        if (timer == null){
            timer = Timer()

            timer!!.schedule(object :TimerTask(){
                override fun run() {
                    Log.d("timer", "タイマー起動")
                    mHandler.post {
                        if (!cursor!!.moveToNext()){
                            cursor!!.moveToFirst()
                            val fieldIndexTop = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor!!.getLong(fieldIndexTop)
                            val imageUriTop = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                            imageView.setImageURI(imageUriTop)
                        }else{
                            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor!!.getLong(fieldIndex)
                            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                            imageView.setImageURI(imageUri)
                        }
                    }
                }
            },2000,2000)

            button6.text = "停止"
            button5.isEnabled = false
            button7.isEnabled = false
        }else{
            timer!!.cancel()
            timer = null
            button6.text = "再生"
            button5.isEnabled = true
            button7.isEnabled = true
        }

    }

}