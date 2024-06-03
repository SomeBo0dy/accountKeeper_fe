package pers.xyj.accountkeeper.ui.bill

import android.Manifest
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.CalendarContract
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentAddBillBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.BillApi
import pers.xyj.accountkeeper.network.api.RecordApi
import pers.xyj.accountkeeper.repository.model.AddBillForm
import pers.xyj.accountkeeper.repository.model.AddRecordForm
import pers.xyj.accountkeeper.repository.model.EditBillForm
import pers.xyj.accountkeeper.repository.model.EditRecordForm
import pers.xyj.accountkeeper.util.LogUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class AddBillFragment  : BaseFragment<FragmentAddBillBinding, AddBillViewModel>(
    FragmentAddBillBinding::inflate,
    AddBillViewModel::class.java,
    true
){
    private val REQUEST_CODE = 100
    val CALENDER_URL: String = "content://com.android.calendar/calendars"
    val CALENDER_EVENT_URL: String = "content://com.android.calendar/events"
    val CALENDER_REMINDER_URL : String = "content://com.android.calendar/reminders"
    lateinit var timePicker: TimePicker
    var isEdit: Boolean = false
    var billId: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    lateinit var date: String
    lateinit var model: AddBillViewModel
    var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    override fun initFragment(
        binding: FragmentAddBillBinding,
        viewModel: AddBillViewModel?,
        savedInstanceState: Bundle?
    ) {
        binding.viewModel = viewModel
        model = viewModel!!
        timePicker = binding.billTime
        timePicker.setIs24HourView(true)
        var bundle = arguments
        if (bundle != null){
            isEdit = bundle.getBoolean("isEdit")
            date = bundle.getString("date")!!
            if (isEdit) {
                binding.deleteBillButton.visibility = View.VISIBLE
                binding.insertCalendarButton.visibility = View.VISIBLE
                binding.appName.text = "编辑账单提醒"
                billId = bundle.getInt("billId")
                hour = bundle.getInt("hour")
                minute = bundle.getInt("minute")
                viewModel!!.billName.value = bundle.getString("billName")
                timePicker.hour = hour
                timePicker.minute = minute
            }
        }
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.saveBillButton.setOnClickListener {
            if (viewModel!!.billName.value == "") {
                Toast.makeText(requireContext(), "账单名称不能为空", Toast.LENGTH_SHORT)
                    .show()
            }else{
                publicViewModel?.apply {
                    if (!isEdit) {
                        request(BillApi::class.java).addBill(
                            AddBillForm(
                                viewModel!!.billName.value!!,
                                format.parse(date + " " + timePicker.hour + ":" + timePicker.minute + ":00")
                            )
                        ).getResponse {
                            it.collect {
                                when (it) {
                                    is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                                    ApiResponse.Loading -> LogUtil.e("Loading")
                                    is ApiResponse.Success -> {
                                        LogUtil.e("${it.data.toString()}")
                                        withContext(Dispatchers.Main) {
                                            requireActivity().findNavController(R.id.app_navigation)
                                                .navigateUp()
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        request(BillApi::class.java).editBill(
                            EditBillForm(
                                billId,
                                viewModel!!.billName.value!!,
                                format.parse(date + " " + timePicker.hour + ":" + timePicker.minute + ":00")
                            )
                        ).getResponse {
                            it.collect {
                                when (it) {
                                    is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                                    ApiResponse.Loading -> LogUtil.e("Loading")
                                    is ApiResponse.Success -> {
                                        LogUtil.e("${it.data.toString()}")
                                        withContext(Dispatchers.Main) {
                                            requireActivity().findNavController(R.id.app_navigation)
                                                .navigateUp()
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }

        }
        binding.deleteBillButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("确认删除")
            builder.setMessage("你确认要删除这条账单吗？删除后将无法找回")
            builder.setPositiveButton("确定") { dialog, which ->
                publicViewModel?.apply {
                    request(BillApi::class.java).deleteBill(billId).getResponse {
                        it.collect {
                            when (it) {
                                is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                                ApiResponse.Loading -> LogUtil.e("Loading")
                                is ApiResponse.Success -> {
                                    dialog.dismiss()
                                    LogUtil.e("${it.data.toString()}")
                                    withContext(Dispatchers.Main) {
                                        requireActivity().findNavController(R.id.app_navigation)
                                            .navigateUp()
                                    }
                                }
                            }
                        }
                    }
                }

            }
            builder.setNegativeButton("取消") { dialog, which ->
                dialog.dismiss() // 关闭对话框
            }
            val dialog = builder.create()
            dialog.show()
        }
        binding.insertCalendarButton.setOnClickListener {
            checkCalendarPermissions()
        }
    }
    private fun checkCalendarPermissions() {
        context?.let {
            if (ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(it, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR), REQUEST_CODE)
            } else {
                addLocalCalendar()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                addLocalCalendar()
            }
        }
    }
    private fun addLocalCalendar() {
        context?.let {
            val accountName = "本地日历"
            val accountType = CalendarContract.ACCOUNT_TYPE_LOCAL

            val values = ContentValues().apply {
                put(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                put(CalendarContract.Calendars.ACCOUNT_TYPE, accountType)
                put(CalendarContract.Calendars.NAME, "本地日历")
                put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "本地日历")
                put(CalendarContract.Calendars.CALENDAR_COLOR, -0x10000)
                put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)
                put(CalendarContract.Calendars.OWNER_ACCOUNT, accountName)
                put(CalendarContract.Calendars.VISIBLE, 1)
                put(CalendarContract.Calendars.SYNC_EVENTS, 1)
            }

            val uri = CalendarContract.Calendars.CONTENT_URI.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType)
                .build()

            val calendarUri = it.contentResolver.insert(uri, values)
            val calendarId = calendarUri?.lastPathSegment?.toLong() ?: return

            // 日历创建成功后，添加事件
            addEventToCalendar(calendarId)
        }
    }

    private fun addEventToCalendar(calendarId: Long) {
        context?.let {
            var billDate = dateFormat.parse(date)
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = billDate
            calendar.add(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.add(Calendar.MINUTE, timePicker.minute)
            val startMillis: Long = calendar.timeInMillis
            calendar.add(Calendar.HOUR_OF_DAY, 1)
            val endMillis: Long = calendar.timeInMillis
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, model.billName.value)
                put(CalendarContract.Events.DESCRIPTION, model.billName.value)
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }

            val uri: Uri? = it.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventID: Long = uri?.lastPathSegment?.toLong() ?: 0

            // 添加提醒
            val reminderValues = ContentValues().apply {
                put(CalendarContract.Reminders.MINUTES, 10)
                put(CalendarContract.Reminders.EVENT_ID, eventID)
                put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
            }
            it.contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)

            Toast.makeText(
                requireContext(),
                "添加成功",
                Toast.LENGTH_SHORT
            ).show()

        }
    }
}