package com.example.CodeClauseInternship.qrbarcodescanner.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.example.CodeClauseInternship.qrbarcodescanner.R
import com.example.CodeClauseInternship.qrbarcodescanner.databinding.ActivityScreenerDetailsBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.oned.Code128Writer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class ScreenerDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScreenerDetailsBinding

    var bitmap: Bitmap? = null
    var qrgEncoder: QRGEncoder? = null

    var value: String = ""
    var key: String = ""


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        key = intent.getStringExtra("key").toString()
        value = intent.getStringExtra("name").toString()

        if (key == "Barcode") {
            binding.toolbarText.text = "Bar Code Scanner Details"
        } else {
            binding.toolbarText.text = "QR Code Scanner Details"
        }

        binding.tvText.text = "" + value

        initView()
        init()

    }

    private fun init() {
        binding.llCopy.setOnClickListener {

            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Text", value)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(applicationContext, "Text Copied", Toast.LENGTH_SHORT).show()
        }

        binding.llShare.setOnClickListener {
            sharePalette(bitmap)
        }

        binding.llSearch.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/search?q=" + value)
                )
            )
        }
        binding.llSave.setOnClickListener {
            saveCode()
        }
    }

    private fun initView() {

        val widthPixels = resources.getDimensionPixelSize(R.dimen.width_barcode)
        val heightPixels = resources.getDimensionPixelSize(R.dimen.height_barcode)

        if (key == "Barcode") {
            binding.idIVQrcode.setImageBitmap(
                createBarcodeBitmap(
                    barcodeValue = value,
                    barcodeColor = getColor(R.color.black),
                    backgroundColor = getColor(android.R.color.white),
                    widthPixels = widthPixels,
                    heightPixels = heightPixels
                )
            )
            bitmap = (createBarcodeBitmap(
                barcodeValue = value,
                barcodeColor = getColor(R.color.black),
                backgroundColor = getColor(android.R.color.white),
                widthPixels = widthPixels,
                heightPixels = heightPixels
            ))

        } else {
            val manager = getSystemService(WINDOW_SERVICE) as WindowManager
            val display = manager.defaultDisplay
            val point = Point()
            display.getSize(point)
            val width = point.x
            val height = point.y
            var dimen = if (width < height) width else height
            dimen = dimen * 3 / 4
            qrgEncoder =
                QRGEncoder(value, null, QRGContents.Type.TEXT, dimen)
            bitmap = qrgEncoder!!.bitmap
            binding.idIVQrcode.setImageBitmap(bitmap)

        }
    }

    private fun createBarcodeBitmap(
        barcodeValue: String,
        @ColorInt barcodeColor: Int,
        @ColorInt backgroundColor: Int,
        widthPixels: Int,
        heightPixels: Int
    ): Bitmap {
        val bitMatrix = Code128Writer().encode(
            barcodeValue,
            BarcodeFormat.CODE_128,
            widthPixels,
            heightPixels
        )

        val pixels = IntArray(bitMatrix.width * bitMatrix.height)
        for (y in 0 until bitMatrix.height) {
            val offset = y * bitMatrix.width
            for (x in 0 until bitMatrix.width) {
                pixels[offset + x] =
                    if (bitMatrix.get(x, y)) barcodeColor else backgroundColor
            }
        }

        val bitmap = Bitmap.createBitmap(
            bitMatrix.width,
            bitMatrix.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels(
            pixels,
            0,
            bitMatrix.width,
            0,
            0,
            bitMatrix.width,
            bitMatrix.height
        )
        return bitmap
    }

    private fun sharePalette(bitmap: Bitmap?) {
        val bitmapPath =
            MediaStore.Images.Media.insertImage(contentResolver, bitmap, "palette", "share palette")
        val bitmapUri = Uri.parse(bitmapPath)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
        startActivity(Intent.createChooser(intent, "Share"))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun saveCode() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 11
            )
        } else {
            val dateFormat = SimpleDateFormat("dd_HH_mm_ss")
            val currentTimeStamp = dateFormat.format(Date()) + ".png"
            try {
                if (bitmap != null) {
                    val root = Environment.getExternalStorageDirectory()
                    val file = File(root.absolutePath + "/DCIM/Camera/" + currentTimeStamp)
                    val ostream = FileOutputStream(file)
                    bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, ostream)
                    Toast.makeText(
                        this@ScreenerDetailsActivity,
                        "Save Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val dateFormat = SimpleDateFormat("dd_HH_mm_ss")
        val currentTimeStamp = dateFormat.format(Date()) + ".png"
        try {
            if (bitmap != null) {
                val root = Environment.getExternalStorageDirectory()
                val file = File(root.absolutePath + "/DCIM/Camera/" + currentTimeStamp)
                val ostream = FileOutputStream(file)
                bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, ostream)
                Toast.makeText(
                    this@ScreenerDetailsActivity,
                    "Save Successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}