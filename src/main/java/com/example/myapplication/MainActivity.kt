package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.io.*
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import java.util.*

/*import android.widget.Toast
import android.view.View*/

class MainActivity : AppCompatActivity() {
    private var btn1: Button? = null
    private var btn2: Button? = null
    private var btn3: Button? = null
    private var txtv1: TextView? = null
    private var txt1: EditText? = null
    private var txt2: EditText? = null
    private var location = ""
    private val alpha = listOf(1, 4, 7)
    private val beta = listOf(2, 5, 8)
    private val gamma = listOf(3, 6, 9)
    private var sector = listOf(alpha, beta, gamma)
    private val requestcode = 111
    private var nettextdata = ""
    private var radioGroup: RadioGroup? = null
    private var radioGroup2: RadioGroup? = null
    private var radioGroup3: RadioGroup? = null
    private var radioGroup4: RadioGroup? = null
    private var radioGroup5: RadioGroup? = null
    private var radioButton: RadioButton? = null
    private var mcc = ""
    private var provider_list = listOf("(10)Telkomsel","(10)Simpati","(21)INDOSAT", "(01)ISAT", "(01)INDOSAT",
        "(11)XL", "(11)AXIS", "(89)THREE", "(89)TRI")
    private var netid = listOf("10","01","11","89","09","21")
    private var netlist = listOf("4G", "3G", "2G")
    private lateinit var separator: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        radioGroup = findViewById(R.id.radiogroup1)
        radioGroup2 = findViewById(R.id.radiogroup2)
        radioGroup3 = findViewById(R.id.radiogroup3)
        radioGroup4 = findViewById(R.id.radiogroup4)
        radioGroup5 = findViewById(R.id.radiogroup5)

        txtv1 = findViewById(R.id.textInputEditText)
        txt1 = findViewById(R.id.editTextTextMultiLine)
        txt2 = findViewById(R.id.editTextTextMultiLine2)
        btn1 = findViewById(R.id.button)
        btn1!!.setOnClickListener {
            val intent = Intent()
                .setType("text/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select a file"), requestcode)
        }
        btn2 = findViewById(R.id.button2)
        btn2!!.setOnClickListener{
            readcsv()
        }
        btn3 = findViewById(R.id.button3)
        btn3!!.setOnClickListener{
            convertmc()
        }
    }

    /*fun checkradio(v: View?) {
        val radioId = radioGroup!!.checkedRadioButtonId
        radioButton = findViewById(radioId)
        Toast.makeText(
            this, "Selected Radio Button: " + radioId.toString(),
            Toast.LENGTH_SHORT
        ).show()
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestcode && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            Log.d("load", selectedFile.toString())
            if (!selectedFile.toString().endsWith(".csv")) {
                return
            }
            if (isExternalStorageDocument(selectedFile!!)){
                val docId = DocumentsContract.getDocumentId(selectedFile)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                val storageDefinition: String
                if ("primary".equals(type, ignoreCase = true)) {
                    location = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else {
                    storageDefinition = if (Environment.isExternalStorageRemovable()) {
                        "EXTERNAL_STORAGE"
                    } else {
                        "SECONDARY_STORAGE"
                    }
                    location = System.getenv(storageDefinition)!! + "/" + split[1]
                }
            }
            txtv1!!.text = location
        }
    }

    private fun readcsv() {
        if (this.location != "") {
            // If you have access to the external storage, do whatever you need
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                if(!Environment.isExternalStorageManager()){
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    val uri = Uri.fromParts("package", this.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            }

            val allline = File(location).readLines()
            var str4g1 = ""
            var str4g2 = ""
            var str4g3 = ""
            var str4g4 = ""

            var str3g1 = ""
            var str3g2 = ""
            var str3g3 = ""
            var str3g4 = ""

            var str2g1 = ""
            var str2g2 = ""
            var str2g3 = ""
            var str2g4 = ""

            var mnc1 = ""
            var mnc2 = ""
            var mnc3 = ""
            var mnc4 = ""

            var net_type: String
            var lac: String
            var cid: String

            var last_lac = ""
            var last_cid = ""

            var strsim1: String
            var strsim2: String
            var strsim3: String
            var strsim4: String
            var strout = ""

            val radioId = radioGroup!!.checkedRadioButtonId
            val radioId2 = radioGroup2!!.checkedRadioButtonId
            val radioId3 = radioGroup3!!.checkedRadioButtonId
            val radioId4 = radioGroup4!!.checkedRadioButtonId

            var t4gformat: Int
            var t2gband: Int
            var t3gband: Int

            when(radioId){
                R.id.radio_slash -> separator = "/"
                R.id.radio_comma -> separator = ","
                else -> {
                    separator = "/"
                    radioButton = findViewById(R.id.radio_slash)
                    radioButton!!.isChecked = true
                }
            }

            when(radioId2){
                R.id.full4g -> t4gformat = 0
                R.id.lacenb4g -> t4gformat = 1
                R.id.enb4g -> t4gformat = 2
                else -> {
                    t4gformat = 2
                    radioButton = findViewById(R.id.enb4g)
                    radioButton!!.isChecked = true
                }
            }

            when(radioId3){
                R.id.r3gdef -> t3gband = 0
                R.id.r3gall -> t3gband = 1
                else -> {
                    t3gband = 0
                    radioButton = findViewById(R.id.r3gdef)
                    radioButton!!.isChecked = true
                }
            }

            when(radioId4){
                R.id.r2gdef -> t2gband = 0
                R.id.r2gall -> t2gband = 1
                else -> {
                    t2gband = 0
                    radioButton = findViewById(R.id.r2gdef)
                    radioButton!!.isChecked = true
                }
            }

            var duplicate = false
            var count = 0 //dev only
            val alllinez = allline.subList(1,allline.lastIndex)
            for (perline in alllinez) {
                count += 1 //dev only
                var simnum: Int
                if (perline.isEmpty()) {
                    continue
                }
                if (isLetters(perline)) {
                    continue
                }

                val splitted = perline.split(",").toTypedArray()
                mcc = splitted[0]
                net_type = splitted[9]

                if (mnc1.isEmpty() || (mnc1.isNotEmpty() and (mnc1 == splitted[1]))) {
                    //println("SIM 1 EMPTY")
                    mnc1 = splitted[1]
                    simnum = 1
                }else if (mnc2.isEmpty() || (mnc2.isNotEmpty() and (mnc2 == splitted[1]))) {
                    mnc2 = splitted[1]
                    simnum = 2
                }else if (mnc3.isEmpty() || (mnc3.isNotEmpty() and (mnc3 == splitted[1]))) {
                    mnc3 = splitted[1]
                    simnum = 3
                }else if (mnc4.isEmpty() || (mnc4.isNotEmpty() and (mnc4 == splitted[1]))) {
                    mnc4 = splitted[1]
                    simnum = 4
                }else{
                    println("Out of Memory Allocation")
                    continue
                }

                if ((last_lac == splitted[2]) and (last_cid == splitted[3])) {
                    duplicate = true
                }

                if (net_type == "1") {
                    var strbuff: String
                    strbuff = when(simnum){
                        1 -> str4g1
                        2 -> str4g2
                        3 -> str4g3
                        4 -> str4g4
                        else -> continue
                    }

                    if(t4gformat==0){
                        lac = splitted[2]
                        cid = splitted[3]
                        if (lac in strbuff) {
                            var find1 = strbuff.indexOf(lac)
                            var strbackupf = strbuff.subSequence(0, find1) // [:find1]
                            var strtemp = strbuff.subSequence(find1, strbuff.length) // [find1:]
                            var find2 = strtemp.indexOf("\\")
                            var strtemp2 = strtemp.subSequence(0, find2) // [:find2]
                            // strbackupe = strtemp[find2:]
                            strtemp2 = strtemp2.toString() + separator + cid + "\\"
                            strbuff = strbackupf.toString() + strtemp2 // + strbackupe
                        } else {
                            strbuff = if (strbuff.isNotEmpty()) {
                                "$strbuff\n   $lac-$cid\\"
                            } else {
                                strbuff + "4G:" + lac + "-" + cid + "\\"
                            }
                        }
                    }
                    else if (t4gformat==1){
                        var enb = Integer.toHexString((splitted[3].toInt()))
                        var ci = enb.toString().takeLast(2).toLong(radix = 16).toString() // int(ci[-2:],16)
                        cid = enb.toString().take(enb.toString().length - 2).toLong(radix = 16).toString() // lac=int(ci[:-2],16)enb.toString().takeLast(2).toLong(radix = 16).toString()
                        lac = splitted[2]
                        /*if (strbuff.indexOf(lac)>-1) {*/
                        if (lac in strbuff){
                            if (cid in strbuff){
                                var find1 = strbuff.indexOf(cid)
                                var strbackupf = strbuff.subSequence(0,find1) // strbuff.takeLast(find1+lac.length).toString()//strbuff[:find1]
                                var strtemp = strbuff.subSequence(find1, strbuff.length)
                                var find2 = strtemp.indexOf("\\")
                                var strtemp2 = strtemp.subSequence(0, find2) // [:find2]
                                strtemp2 = strtemp2.toString() + separator + ci + "\\"
                                strbuff = strbackupf.toString() + strtemp2
                            }else{
                                var find1 = strbuff.indexOf(lac)
                                var strbackupf = strbuff.subSequence(0,find1) // strbuff.takeLast(find1+lac.length).toString()//strbuff[:find1]
                                var strtemp = strbuff.subSequence(find1, strbuff.length)
                                var find2 = strtemp.indexOf("\\")
                                var strtemp2 = strtemp.subSequence(0, find2) // [:find2]
                                strtemp2 =
                                    "$strtemp2$separator$cid-$ci\\"//strtemp2.toString() + "||" + cid + "-" + ci + "\\"
                                strbuff = strbackupf.toString() + strtemp2
                            }
                        }else{
                            strbuff = if (strbuff.isNotEmpty()) {
                                "$strbuff\n   $lac-$cid-$ci\\"
                            } else {
                                "4G:$lac-$cid-$ci\\"
                            }
                        }
                    }
                    else if (t4gformat==2){
                        var enb = Integer.toHexString((splitted[3].toInt()))
                        cid = enb.toString().takeLast(2).toLong(radix = 16).toString() // int(ci[-2:],16)
                        lac = enb.toString().take(enb.toString().length - 2).toLong(radix = 16).toString() // lac=int(ci[:-2],16)
                        /*if (strbuff.indexOf(lac)>-1) {*/
                        if (lac in strbuff){
                            var find1 = strbuff.indexOf(lac)
                            var strbackupf = strbuff.subSequence(0,find1) // strbuff.takeLast(find1+lac.length).toString()//strbuff[:find1]
                            var strtemp = strbuff.subSequence(find1, strbuff.length)
                            var find2 = strtemp.indexOf("\\")
                            var strtemp2 = strtemp.subSequence(0, find2) // [:find2]
                            strtemp2 = strtemp2.toString() + separator + cid + "\\"
                            strbuff = strbackupf.toString() + strtemp2
                        } else {
                            strbuff = if (strbuff.isNotEmpty()) {
                                "$strbuff\n   $lac-$cid\\"
                            } else {
                                "4G:$lac-$cid\\"
                            }
                        }
                    }
                    else{
                        continue
                    }

                    when(simnum){
                        1 -> str4g1 = strbuff
                        2 -> str4g2 = strbuff
                        3 -> str4g3 = strbuff
                        4 -> str4g4 = strbuff
                        else -> continue
                    }

                    last_lac = splitted[2]
                    last_cid = splitted[3]
                } else if (net_type == "2") {
                    var strbuff: String?
                    strbuff = when(simnum){
                        1 -> str3g1
                        2 -> str3g2
                        3 -> str3g3
                        4 -> str3g4
                        else -> continue
                    }

                    lac = splitted[2]
                    cid = (splitted[3].toInt() % 65536).toString()
                    if(t3gband==1){
                        var cidsecband = getSector(cid)
                        var cid1: String
                        var cid2: String
                        when(cidsecband[1]){
                            0->{
                                cid1 = cid.take(cid.length-1) + sector[cidsecband[0]][1]
                                cid2 = cid.take(cid.length-1) + sector[cidsecband[0]][2]
                            }
                            1->{
                                cid1 = cid.take(cid.length-1) + sector[cidsecband[0]][0]
                                cid2 = cid.take(cid.length-1) + sector[cidsecband[0]][2]
                            }
                            2->{
                                cid1 = cid.take(cid.length-1) + sector[cidsecband[0]][0]
                                cid2 = cid.take(cid.length-1) + sector[cidsecband[0]][1]
                            }
                            else -> {
                                continue
                            }
                        }
                        if (lac in strbuff) {
                            if (cid in strbuff || cid1 in strbuff || cid2 in strbuff){
                                continue
                            }
                            var find1 = strbuff.indexOf(lac)
                            var strbackupf = strbuff.subSequence(0, find1) // strbuff[:find1]
                            var strtemp = strbuff.subSequence(find1, strbuff.length) // [find1:]
                            var find2 = strtemp.indexOf("\\")
                            var strtemp2 = strtemp.subSequence(0, find2) // [:find2]
                            strtemp2 = when(cidsecband[1]){
                                0->{
                                    strtemp2.toString() + separator + cid + separator + cid1 + separator + cid2 + "\\"
                                }
                                1->{
                                    strtemp2.toString() + separator + cid1 + separator + cid + separator + cid2 + "\\"
                                }
                                2->{
                                    strtemp2.toString() + separator + cid1 + separator + cid2 + separator + cid + "\\"
                                }
                                else -> {
                                    continue
                                }
                            }
                            strbuff = strbackupf.toString() + strtemp2
                        } else {
                            strbuff = if (strbuff.isNotEmpty()) {
                                when(cidsecband[1]){
                                    0->{
                                        "$strbuff\n   $lac-$cid$separator$cid1$separator$cid2\\"
                                    }
                                    1->{
                                        "$strbuff\n   $lac-$cid1$separator$cid$separator$cid2\\"
                                    }
                                    2->{
                                        "$strbuff\n   $lac-$cid1$separator$cid2$separator$cid\\"
                                    }
                                    else ->{
                                        continue
                                    }
                                }
                            } else {
                                when(cidsecband[1]){
                                    0->{
                                        strbuff + "3G:" + lac + "-" + cid + separator + cid1 + separator + cid2 + "\\"
                                    }
                                    1->{
                                        strbuff + "3G:" + lac + "-" + cid1 + separator + cid + separator + cid2 + "\\"
                                    }
                                    2->{
                                        strbuff + "3G:" + lac + "-" + cid1 + separator + cid2 + separator + cid + "\\"
                                    }
                                    else ->{
                                        continue
                                    }
                                }
                            }
                        }
                    }else{
                        if (lac in strbuff) {
                            var find1 = strbuff.indexOf(lac)
                            var strbackupf = strbuff.subSequence(0, find1) // strbuff[:find1]
                            var strtemp = strbuff.subSequence(find1, strbuff.length) // [find1:]
                            var find2 = strtemp.indexOf("\\")
                            var strtemp2 = strtemp.subSequence(0, find2) // [:find2]
                            strtemp2 = strtemp2.toString() + separator + cid + "\\"
                            strbuff = strbackupf.toString() + strtemp2
                        } else {
                            strbuff = if (strbuff.isNotEmpty()) {
                                "$strbuff\n   $lac-$cid\\"
                            } else {
                                strbuff + "3G:" + lac + "-" + cid + "\\"
                            }
                        }
                    }


                    when(simnum){
                        1 -> str3g1 = strbuff
                        2 -> str3g2 = strbuff
                        3 -> str3g3 = strbuff
                        4 -> str3g4 = strbuff
                        else -> continue
                    }

                    last_lac = splitted[2]
                    last_cid = splitted[3]
                } else if (net_type == "3") {
                    var strbuff: String?

                    strbuff = when(simnum){
                        1 -> str2g1
                        2 -> str2g2
                        3 -> str2g3
                        4 -> str2g4
                        else -> continue
                    }

                    lac = splitted[2]
                    cid = splitted[3]
                    if (t2gband==1) {
                        var cidsecband = getSector(cid)
                        var cid1: String
                        var cid2: String
                        when(cidsecband[1]){
                            0->{
                                cid1 = cid.take(cid.length-1) + sector[cidsecband[0]][1]
                                cid2 = cid.take(cid.length-1) + sector[cidsecband[0]][2]
                            }
                            1->{
                                cid1 = cid.take(cid.length-1) + sector[cidsecband[0]][0]
                                cid2 = cid.take(cid.length-1) + sector[cidsecband[0]][2]
                            }
                            2->{
                                cid1 = cid.take(cid.length-1) + sector[cidsecband[0]][0]
                                cid2 = cid.take(cid.length-1) + sector[cidsecband[0]][1]
                            }
                            else -> {
                                continue
                            }
                        }
                        if (lac in strbuff) {
                            if (cid in strbuff || cid1 in strbuff || cid2 in strbuff){
                                continue
                            }
                            var find1 = strbuff.indexOf(lac)
                            var strbackupf = strbuff.subSequence(0, find1) // [:find1]
                            var strtemp = strbuff.subSequence(find1, strbuff.length) // [find1:]
                            var find2 = strtemp.indexOf("\\")
                            var strtemp2 = strtemp.subSequence(0, find2) // [:find2]
                            // strbackupe = strtemp[find2:]
                            strtemp2 = when(cidsecband[1]){
                                0->{
                                    strtemp2.toString() + separator + cid + separator + cid1 + separator + cid2 + "\\"
                                }
                                1->{
                                    strtemp2.toString() + separator + cid1 + separator + cid + separator + cid2 + "\\"
                                }
                                2->{
                                    strtemp2.toString() + separator + cid1 + separator + cid2 + separator + cid + "\\"
                                }
                                else -> {
                                    continue
                                }
                            }
                            strbuff = strbackupf.toString() + strtemp2 // + strbackupe
                        } else {
                            strbuff = if (strbuff.isNotEmpty()) {
                                when(cidsecband[1]){
                                    0->{
                                        "$strbuff\n   $lac-$cid$separator$cid1$separator$cid2\\"
                                    }
                                    1->{
                                        "$strbuff\n   $lac-$cid1$separator$cid$separator$cid2\\"
                                    }
                                    2->{
                                        "$strbuff\n   $lac-$cid1$separator$cid2$separator$cid\\"
                                    }
                                    else ->{
                                        continue
                                    }
                                }
                            } else {
                                when(cidsecband[1]){
                                    0->{
                                        strbuff + "2G:" + lac + "-" + cid + separator + cid1 + separator + cid2 + "\\"
                                    }
                                    1->{
                                        strbuff + "2G:" + lac + "-" + cid1 + separator + cid + separator + cid2 + "\\"
                                    }
                                    2->{
                                        strbuff + "2G:" + lac + "-" + cid1 + separator + cid2 + separator + cid + "\\"
                                    }
                                    else ->{
                                        continue
                                    }
                                }
                            }
                        }
                    }else{
                        if (lac in strbuff) {
                            var find1 = strbuff.indexOf(lac)
                            var strbackupf = strbuff.subSequence(0, find1) // [:find1]
                            var strtemp = strbuff.subSequence(find1, strbuff.length) // [find1:]
                            var find2 = strtemp.indexOf("\\")
                            var strtemp2 = strtemp.subSequence(0, find2) // [:find2]
                            // strbackupe = strtemp[find2:]
                            strtemp2 = strtemp2.toString() + separator + cid + "\\"
                            strbuff = strbackupf.toString() + strtemp2 // + strbackupe
                        } else {
                            strbuff = if (strbuff.isNotEmpty()) {
                                "$strbuff\n   $lac-$cid\\"
                            } else {
                                strbuff + "2G:" + lac + "-" + cid + "\\"
                            }
                        }
                    }

                    when(simnum){
                        1 -> str2g1 = strbuff
                        2 -> str2g2 = strbuff
                        3 -> str2g3 = strbuff
                        4 -> str2g4 = strbuff
                        else -> continue
                    }

                    last_lac = splitted[2]
                    last_cid = splitted[3]
                }

            }

            if (mnc1.isNotEmpty()) {
                strsim1 = str4g1 + "\n" + str3g1 + "\n" + str2g1 + "\n"
                strsim1 = strsim1.replace("\\", "")
                when (mnc1) {
                    "01" -> {
                        strout += "(01)INDOSAT\n"
                    }
                    "10" -> {
                        strout += "(10)TELKOMSEL\n"
                    }
                    "11" -> {
                        strout += "(11)XL\n"
                    }
                    "89" -> {
                        strout += "(89)THREE\n"
                    }
                    "21"->{
                        strout += "(21)INDOSAT\n"
                    }
                }
                strout += strsim1
            }
            if (mnc2.isNotEmpty()) {
                strsim2 = str4g2 + "\n" + str3g2 + "\n" + str2g2 + "\n"
                strsim2 = strsim2.replace("\\", "")
                when (mnc2) {
                    "01" -> {
                        strout += "(01)INDOSAT\n"
                    }
                    "10" -> {
                        strout += "(10)TELKOMSEL\n"
                    }
                    "11" -> {
                        strout += "(11)XL\n"
                    }
                    "89" -> {
                        strout += "(89)THREE\n"
                    }
                    "21"->{
                        strout += "(21)INDOSAT\n"
                    }
                }
                strout += strsim2
            }
            if (mnc3.isNotEmpty()) {
                strsim3 = str4g3 + "\n" + str3g3 + "\n" + str2g3 + "\n"
                strsim3 = strsim3.replace("\\", "")
                when (mnc3) {
                    "01" -> {
                        strout += "(01)INDOSAT\n"
                    }
                    "10" -> {
                        strout += "(10)TELKOMSEL\n"
                    }
                    "11" -> {
                        strout += "(11)XL\n"
                    }
                    "89" -> {
                        strout += "(89)THREE\n"
                    }
                    "21"->{
                        strout += "(21)INDOSAT\n"
                    }
                }
                strout += strsim3
            }
            if (mnc4.isNotEmpty()) {
                strsim4 = str4g4 + "\n" + str3g4 + "\n" + str2g4 + "\n"
                strsim4 = strsim4.replace("\\", "")
                when (mnc4) {
                    "01" -> {
                        strout += "(01)INDOSAT\n"
                    }
                    "10" -> {
                        strout += "(10)TELKOMSEL\n"
                    }
                    "11" -> {
                        strout += "(11)XL\n"
                    }
                    "89" -> {
                        strout += "(89)THREE\n"
                    }
                    "21"->{
                        strout += "(21)INDOSAT\n"
                    }
                }
                strout += strsim4
            }
            if (duplicate) {
                strout += "!!!Ada duplikasi data!!!"
            }
            strout = strout.trim()
            nettextdata = strout
            txt1?.setText(strout)
        }
    }

    private fun convertmc(){
        if (nettextdata.isEmpty() || txt1!!.text.isNullOrEmpty() || mcc.isEmpty()){
            return
        }
        if (nettextdata != txt1!!.text.toString()){
            nettextdata = txt1!!.text.toString()
        }
        val radioId5 = radioGroup5!!.checkedRadioButtonId
        var skip_4g: Boolean
        when(radioId5){
            R.id.r4gmcno -> skip_4g = true
            R.id.r4gmcyes -> skip_4g = false
            else -> {
                skip_4g = true
                radioButton = findViewById(R.id.r4gmcno)
                radioButton!!.isChecked = true
            }
        }
        var stringout = ""
        var provider: String
        var net_id = ""
        var net_type = ""
        var nettextdatalower = nettextdata.lowercase()
        var nettextdatalist = nettextdatalower.lineSequence()
        var count = 0
        mainloop@for (line in nettextdatalist){
            if (line.isEmpty()){
                continue
            }
            if ("!" in line){
                continue
            }
            if ("tkp" in line){
                stringout=stringout+line.uppercase()+"\n";
                continue
            }
            count += 1
            for (pl in provider_list){
                var pl_regex = Regex("\\(\\d*?\\)")
                var pl_sval = pl_regex.find(pl)?.value.toString()
                if(pl_sval in line){
                    var pl_index = provider_list.indexOf(pl)
                    when(pl_index){
                        0 -> {
                            provider = provider_list[0].uppercase()
                            net_id = netid[0]
                            stringout += "$provider\n"
                        }
                        1 -> {
                            provider = provider_list[0].uppercase()
                            net_id = netid[0]
                            stringout += "$provider\n"
                        }
                        2 -> {
                            provider = provider_list[2].uppercase()
                            net_id = netid[5]
                            stringout += "$provider\n"
                        }
                        3 -> {
                            provider = provider_list[4].uppercase()
                            net_id = netid[1]
                            stringout += "$provider\n"
                        }
                        4 -> {
                            provider =  provider_list[4].uppercase()
                            net_id = netid[1]
                            stringout += "$provider\n"
                        }
                        5 -> {
                            provider = provider_list[5].uppercase()
                            net_id = netid[2]
                            stringout += "$provider\n"
                        }
                        6 -> {
                            provider = provider_list[5].uppercase()
                            net_id = netid[2]
                            stringout += "$provider\n"
                        }
                        7 -> {
                            provider = provider_list[7].uppercase()
                            net_id = netid[3]
                            stringout += "$provider\n"
                        }
                        8 -> {
                            provider = provider_list[7].uppercase()
                            net_id = netid[3]
                            stringout += "$provider\n"
                        }
                    }
                    continue@mainloop
                }else{
                    pl_regex = Regex("[^()]*\$")
                    var pl_sstring = pl_regex.find(pl)?.value.toString()
                    if ( pl_sstring.lowercase() in line) {
                        var pl_index = provider_list.indexOf(pl)
                        when (pl_index) {
                            0 -> {
                                provider = provider_list[0].uppercase()
                                net_id = netid[0]
                                stringout += "$provider\n"
                            }
                            1 -> {
                                provider = provider_list[0].uppercase()
                                net_id = netid[0]
                                stringout += "$provider\n"
                            }
                            2 -> {
                                provider = provider_list[4].uppercase()
                                net_id = netid[1]
                                stringout += "$provider\n"
                            }
                            3 -> {
                                provider = provider_list[4].uppercase()
                                net_id = netid[1]
                                stringout += "$provider\n"
                            }
                            4 -> {
                                provider = provider_list[4].uppercase()
                                net_id = netid[1]
                                stringout += "$provider\n"
                            }
                            5 -> {
                                provider = provider_list[5].uppercase()
                                net_id = netid[2]
                                stringout += "$provider\n"
                            }
                            6 -> {
                                provider = provider_list[5].uppercase()
                                net_id = netid[2]
                                stringout += "$provider\n"
                            }
                            7 -> {
                                provider = provider_list[7].uppercase()
                                net_id = netid[3]
                                stringout += "$provider\n"
                            }
                            8 -> {
                                provider = provider_list[7].uppercase()
                                net_id = netid[3]
                                stringout += "$provider\n"
                            }
                        }
                        continue@mainloop
                    }
                }
            }

            for (nl in netlist){
                if (nl.lowercase() in line){
                    var nl_index = netlist.indexOf(nl)
                    when(nl_index){
                        0 -> {
                            net_type = netlist[nl_index]
                            break
                        }
                        1 -> {
                            net_type = netlist[nl_index]
                            break
                        }
                        2 -> {
                            net_type = netlist[nl_index]
                            break
                        }
                        else -> {
                            break
                        }
                    }
                }
            }
            if (net_type == "4G" && skip_4g) {
                continue
            }
            var s_tmp = line
            if(":" in line){
                var a_idx = line.indexOf(":")
                s_tmp = s_tmp.substring(a_idx+1)
            }
            s_tmp = s_tmp.replace(" ","")
            if(separator in s_tmp || "|" in s_tmp){
                var stripped = s_tmp.split('-')
                var splitted = stripped[1].split(separator)
                var last_cid = ""
                for (n in splitted){
                    var nd = n.replace("\n","")
                    //stringout = stringout + nd
                    stringout = if (nd.length==1){
                        var idx_lastcid = last_cid.length
                        stringout + mcc + "-" + net_id + "-" + stripped[0] + "-" + last_cid.substring(0, idx_lastcid-1) + nd + "\n"
                    }else{
                        stringout + mcc + "-" + net_id + "-" + stripped[0] + "-" + nd + "\n"
                    }
                    last_cid = nd
                }
            }else{
                stringout = "$stringout$mcc-$net_id-$s_tmp\n"
            }
            //stringout += "$line\n"

            /*if (count > 2){
                break
            }*/
        }

        if (stringout.isNotEmpty()){
            txt2?.setText(stringout)
        }
    }

    private fun getSector(cid: String): Array<Int> {
        var index = -1
        var index2 = -1
        for (i in sector) {
            for (j in i) {
                if (cid.takeLast(1) == j.toString()) {
                    index = sector.indexOf(i)
                    index2 = i.indexOf(j)
                }
            }
        }
        return arrayOf(index, index2)
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isLetters(string: String): Boolean {
        return string.matches("^[a-zA-Z]*$".toRegex())
    }


}
