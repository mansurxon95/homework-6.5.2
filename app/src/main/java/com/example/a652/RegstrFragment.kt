package com.example.a652

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.a652.databinding.FragmentRegstrBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegstrFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegstrFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentRegstrBinding
    lateinit var myDB: MyDB
    private var imageUri: Uri?=null
    private var imageCapture:ImageCapture?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentRegstrBinding.inflate(inflater,container,false)

        myDB = MyDB(binding.root.context)

        var adapter = ArrayAdapter<String>(binding.root.context,android.R.layout.simple_spinner_item,spinner())
        binding.spinnerDavlat.adapter = adapter

        binding.btnPlus.setOnClickListener {

            startcamera()

        }

        var count = false

        binding.btnCameraFront.setOnClickListener {


            if (count){
                lifecycleScope.launch {
                    startCamera(binding.previewViewCamera,CameraSelector.DEFAULT_BACK_CAMERA)
                }
                count = false
            }else{
                lifecycleScope.launch {
                    startCamera(binding.previewViewCamera,CameraSelector.DEFAULT_FRONT_CAMERA)
                }
                count = true
            }


        }

        binding.btnCamera.setOnClickListener {
            takePhoto()
            binding.layout1.visibility = View.VISIBLE
            binding.layout2.visibility = View.GONE
        }

        binding.btnCameraGallery.setOnClickListener {
            openGallery()
            binding.layout1.visibility = View.VISIBLE
            binding.layout2.visibility = View.GONE
        }


        binding.btnRegistr.setOnClickListener {

            val name = binding.nameLastFirst.text.trim().toString()
            val tel = binding.telefonRaqam.text.trim().toString()
            val davlat:String = spinner()[binding.spinnerDavlat.selectedItemPosition]
            val viloyat = binding.manzil.text.trim().toString()
            val pass = binding.parol.text.trim().toString()
            if (name!=""&&tel!=""&&davlat!="Davlat nomini tanlang"&&viloyat!=""&&pass!=""&&imageUri!=null){
                myDB.addUser(User(null,name,tel,davlat, viloyat,pass,imageUri!!))
                Toast.makeText(
                    binding.root.context,
                    "Ro'yhatdan o'tish muafaqiyatli yakunlandi",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_regstrFragment_to_homFragment)
            }else Toast.makeText(
                binding.root.context,
                "Barcha maydonlarni to'ldiring",
                Toast.LENGTH_SHORT
            ).show()

        }



        return binding.root
    }

    private fun startcamera() {
        if (!checkReadContactsPermission(binding.root.context, android.Manifest.permission.CAMERA) &&
            !checkReadContactsPermission(binding.root.context, multiplePermissionsLisName)&&
            !checkReadContactsPermission(binding.root.context, android.Manifest.permission.CALL_PHONE)&&
            !checkReadContactsPermission(binding.root.context, android.Manifest.permission.SEND_SMS)
        ) {
            requestPermissions()
        }else{
            binding.layout1.visibility = View.GONE
            binding.layout2.visibility = View.VISIBLE
            lifecycleScope.launch {
                startCamera(binding.previewViewCamera,CameraSelector.DEFAULT_BACK_CAMERA)
            }


        }

    }


private fun openGallery() {

            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)

    }

    private val changeImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                //qaytaradigan qiymat URI
                imageUri = data?.data
                binding.imageAdd.setImageURI(imageUri)
            }
        }


    private suspend fun startCamera(previewViewCamera: PreviewView, cameraSelector: CameraSelector){
        val cameraProvider = ProcessCameraProvider.getInstance(binding.root.context).await()

        val preview = Preview.Builder().build()

        preview.setSurfaceProvider(previewViewCamera.surfaceProvider) // TODO: 111111

        imageCapture = ImageCapture.Builder().build()


        try {

            cameraProvider.unbindAll()
            var camera = cameraProvider.bindToLifecycle(
                this,cameraSelector,preview,imageCapture
            )

        } catch (e:Exception){
            Toast.makeText(binding.root.context, "UseCase-ni ulash amalga oshmadi", Toast.LENGTH_SHORT).show()
        }

    }





    fun takePhoto(){

        val name = SimpleDateFormat(FILNAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())


        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,name)
            put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg")
            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.P){
                put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/CameraX-Image")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(binding.root.context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()


        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(binding.root.context),
            object : ImageCapture.OnImageSavedCallback{

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "suratga olishdagi xatolik: ${exception.message}",exception )
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val msg = "Suratga olish muvaffaqiyatli yakunlandi${outputFileResults.savedUri}"
                    imageUri = outputFileResults.savedUri                                                // TODO: 222222
                    binding.imageAdd.setImageURI(outputFileResults.savedUri)
                    Toast.makeText(binding.root.context, "$msg", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "$msg")
                }



            }
        )

    }



    fun checkReadContactsPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private val multiplePermissionsLisName = if (Build.VERSION.SDK_INT>=33){
        android.Manifest.permission.READ_MEDIA_IMAGES
    }else android.Manifest.permission.READ_EXTERNAL_STORAGE
    private fun requestPermissions() {
        // pastdagi satr joriy faoliyatda ruxsat so'rash uchun ishlatiladi.
        // bu usul ish vaqti ruxsatnomalarida xatoliklarni hal qilish uchun ishlatiladi
        Dexter.withContext(binding.root.context)
            // pastdagi satr ilovamizda talab qilinadigan ruxsatlar sonini so'rash uchun ishlatiladi.
            .withPermissions(
                multiplePermissionsLisName,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.SEND_SMS
            )
            // ruxsatlarni qo'shgandan so'ng biz tinglovchi bilan usulni chaqiramiz.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    // bu usul barcha ruxsatlar berilganda chaqiriladi
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        // hozir ishlayapsizmi
                        Toast.makeText(binding.root.context, "Barcha ruxsatlar berilgan..", Toast.LENGTH_SHORT).show()

                        if (checkReadContactsPermission(binding.root.context,android.Manifest.permission.CAMERA)) {

                            binding.layout1.visibility = View.GONE
                            binding.layout2.visibility = View.VISIBLE
                            lifecycleScope.launch {
                                startCamera(binding.previewViewCamera,CameraSelector.DEFAULT_BACK_CAMERA)
                            }
                            // Ruxsat berilgan, kontakt ma'lumotlariga kirishingiz mumkin
                            // Kerakli harakatlar tugallanishi uchun shu yerga kod yozing
                        }

                    }
                    // har qanday ruxsatni doimiy ravishda rad etishni tekshiring
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        // ruxsat butunlay rad etilgan, biz foydalanuvchiga dialog xabarini ko'rsatamiz.
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(list: List<PermissionRequest>, permissionToken: PermissionToken) {
                    // foydalanuvchi ba'zi ruxsatlarni berib, ba'zilarini rad qilganda bu usul chaqiriladi.
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener {
                // biz xato xabari uchun tost xabarini ko'rsatmoqdamiz.
                Toast.makeText(binding.root.context, "Error occurred! ", Toast.LENGTH_SHORT).show()
            }
            // pastdagi satr bir xil mavzudagi ruxsatlarni ishga tushirish va ruxsatlarni tekshirish uchun ishlatiladi
            .onSameThread().check()

    }

    // quyida poyabzal sozlash dialog usuli
    // dialog xabarini ko'rsatish uchun ishlatiladi.
    private fun showSettingsDialog() {
        // biz ruxsatlar uchun ogohlantirish dialogini ko'rsatmoqdamiz
        val builder = AlertDialog.Builder(binding.root.context)

        // pastdagi satr ogohlantirish dialogining sarlavhasidir.
        builder.setTitle("Need Permissions")

        // pastdagi satr bizning muloqotimiz uchun xabardir
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            // bu usul musbat tugmani bosganda va shit tugmasini bosganda chaqiriladi
            // biz foydalanuvchini ilovamizdan ilovamiz sozlamalari sahifasiga yo'naltirmoqdamiz.
            dialog.cancel()
            // quyida biz foydalanuvchini qayta yo'naltirish niyatimiz.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", null, "AddFragment")
            intent.data = uri
            startActivityForResult(intent, 101)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            // bu usul foydalanuvchi salbiy tugmani bosganda chaqiriladi.
            dialog.cancel()
        }
        // dialog oynamizni ko'rsatish uchun quyidagi qatordan foydalaniladi
        builder.show()
    }

    private fun spinner(): ArrayList<String> {
        var list = ArrayList<String>()
        list.add("Davlat nomini tanlang")
        list.add("Uzbekistan")
        list.add("Russia")
        list.add("Kazakhstan")
        list.add("Bihar")
        list.add("Chhattisgarh")
        list.add("Goa")
        list.add("Gujarat")
        list.add("Haryana")
        list.add("Himachal Pradesh")
        list.add("Jammu and Kashmir")
        list.add("Jharkhand")
        list.add("Karnataka")
        list.add("Kerala")
        list.add("Madhya Pradesh")
        list.add("Maharashtra")
        list.add("Manipur")
        list.add("Meghalaya")
        list.add("Mizoram")
        list.add("Nagaland")
        list.add("Odisha")
        list.add("Punjab")
        list.add("Rajasthan")
        list.add("Sikkim")
        list.add("Tamil Nadu")
        list.add("Telangana")
        list.add("Tripura")
        list.add("Uttarakhand")
        list.add("Uttar Pradesh")
        list.add("West Bengal")
        list.add("Andaman and Nicobar Islands")
        list.add("Chandigarh")
        list.add("Dadra and Nagar Haveli")
        list.add("Daman and Diu")
        list.add("Delhi")
        list.add("Lakshadweep")
        list.add("Puducherry")


        return list
    }

    companion object {

        private const val TAG = "CameaXApp"
        private const val FILNAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS = mutableListOf(
            android.Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        }.toTypedArray()
        fun hasPermissions(context: Context) = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context,it) == PackageManager.PERMISSION_GRANTED
        }


        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegstrFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegstrFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}