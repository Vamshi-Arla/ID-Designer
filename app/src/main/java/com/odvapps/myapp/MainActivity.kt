package com.odvapps.myapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var bsBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bsBehavior1: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bsBehavior2: BottomSheetBehavior<ConstraintLayout>

    //Our variables
    private var mImageView: ImageView? = null
    private var mImageView1: ImageView? = null
    private var name:EditText? = null
    private var mail:EditText? = null
    //Our widgets
    private lateinit var btnCapture: Button
    private lateinit var btnChoose : Button
    private lateinit var b1: Button
    private lateinit var b2 : Button
    private lateinit var c1: CardView
    private lateinit var frontCard:View
    private lateinit var viewIdCard:LinearLayout
    private lateinit var generateIdCard:LinearLayout
    //private lateinit var viewIdCard:LinearLayout
    //Our constants
    private lateinit var filePhoto: File
    private  val FILE_NAME = "photo.jpg"
    //activity Result for Choose from Gallery
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
           val data = result.data
            mImageView?.setImageURI(data?.data)
            mImageView1?.setImageURI(data?.data)
            viewIdCard.visibility = View.VISIBLE
        }

    }
    //activity Result for Camera intent
    private val startForResultone = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val takenPhoto = BitmapFactory.decodeFile(filePhoto.absolutePath)
            mImageView?.setImageBitmap(takenPhoto)
            mImageView1?.setImageBitmap(takenPhoto)
            viewIdCard.visibility=View.VISIBLE
        }

    }

    @SuppressLint("CutPasteId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeWidgets()
        //View the Details For ID Card Designer
        findViewById<Button>(R.id.B1).setOnClickListener {
            // bsBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            frontCard.visibility = View.VISIBLE
            if (bsBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bsBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        findViewById<Button>(R.id.B2).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // start your next activity
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.s1).setOnClickListener {
            // bsBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            val coordinatorLayout:CoordinatorLayout = findViewById(R.id.l1)
            coordinatorLayout.visibility=View.VISIBLE
            val coordinatorLayout1:CoordinatorLayout = findViewById(R.id.l2)
            val button:Button = findViewById(R.id.s1)
            button.visibility = View.GONE

            bsBehavior1.state = BottomSheetBehavior.STATE_EXPANDED
            if (bsBehavior1.state == BottomSheetBehavior.STATE_COLLAPSED)
                coordinatorLayout1.visibility=View.GONE
            else
                coordinatorLayout.visibility=View.VISIBLE

        }

        findViewById<LinearLayout>(R.id.s2).setOnClickListener {
            if (name?.text == null){
                show("Please Enter Your Name")
            }else if (mail?.text == null){
                show("Please Enter Your Email Address")
            }else {
                val constraintLayout: LinearLayout = findViewById(R.id.l3)
                constraintLayout.visibility = View.GONE
                val coordinatorLayout1: CoordinatorLayout = findViewById(R.id.l2)
                coordinatorLayout1.visibility = View.VISIBLE
                bsBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        findViewById<LinearLayout>(R.id.s3).setOnClickListener {
            val constraintLayout:LinearLayout = findViewById(R.id.l4)
            constraintLayout.visibility=View.GONE
            val textView:TextView = findViewById(R.id.headerTxt)
            textView.visibility=View.GONE
            val textView1:TextView = findViewById(R.id.detailsText)
            textView1.visibility = View.VISIBLE
            textView1.setText("Name: "+name?.text+"\n"+"Email: "+mail?.text+"\n"+
                    "Company: ID Designer"+"\n"+"Location: India")
            viewIdCard.visibility=View.GONE
            generateIdCard.visibility=View.VISIBLE
            //bsBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
        }
        findViewById<LinearLayout>(R.id.s4).setOnClickListener {

            val textView1:TextView = findViewById(R.id.detailsText1)
            textView1.visibility=View.VISIBLE
            textView1.setText("Name: "+name?.text+"\n"+"Email: "+mail?.text+"\n"+
                    "Company: ID Designer"+"\n"+"Location: India"+
                    "\n"+"Note: Terms & Conditions Apply"+
                    "\n"+"Please Scan Your ID Card")
            viewIdCard.visibility=View.GONE
            frontCard.visibility = View.GONE
            b1.visibility = View.GONE
            c1.visibility = View.VISIBLE
            b2.visibility = View.VISIBLE
            //bsBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
        }


        btnCapture.setOnClickListener{
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            filePhoto = getPhotoFile(FILE_NAME)

           val providerFile =FileProvider.getUriForFile(this,"com.example.androidcamera.fileprovider", filePhoto)
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
            if (takePhotoIntent.resolveActivity(this.packageManager) != null){
                startForResultone.launch(takePhotoIntent)
            }else {
                Toast.makeText(this,"Camera could not open", Toast.LENGTH_SHORT).show()
            }
        }
        btnChoose.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else{
                    chooseImageGallery();
                }
            }else{
                chooseImageGallery();

            }
        }

    }

    @SuppressLint("CutPasteId")
    private fun initializeWidgets() {
        bsBehavior  = BottomSheetBehavior.from(findViewById(R.id.listBottomSheet))
        bsBehavior1  = BottomSheetBehavior.from(findViewById(R.id.listBottomSheet1))
        bsBehavior2  = BottomSheetBehavior.from(findViewById(R.id.listBottomSheet2))
        frontCard = findViewById(R.id.listBottomSheet)
        viewIdCard = findViewById(R.id.s3)
        generateIdCard = findViewById(R.id.s4)
        btnCapture = findViewById(R.id.btnCapture)
        btnChoose = findViewById(R.id.btnChoose)
        b1 = findViewById(R.id.B1)
        b2 = findViewById(R.id.B2)
        c1 = findViewById(R.id.c1)
        mImageView = findViewById(R.id.mImageView)
        mImageView1 = findViewById(R.id.mImageView1)
        name = findViewById(R.id.firstName)
        mail = findViewById(R.id.emailAddress)
    }

    private fun stackSheet(behavior: BottomSheetBehavior<ConstraintLayout>){
        behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })

    }
    private fun show(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }
    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", directoryStorage)
    }
    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startForResult.launch(intent)
    }

    companion object {
        private val PERMISSION_CODE = 1001;
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    chooseImageGallery()
                }else{
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
