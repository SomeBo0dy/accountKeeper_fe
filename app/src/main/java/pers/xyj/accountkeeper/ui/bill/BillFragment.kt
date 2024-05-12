package pers.xyj.accountkeeper.ui.bill

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentBillBinding
import pers.xyj.accountkeeper.databinding.FragmentBookBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.BillApi
import pers.xyj.accountkeeper.network.api.RecordApi
import pers.xyj.accountkeeper.repository.entity.Bill
import pers.xyj.accountkeeper.repository.entity.BookVo
import pers.xyj.accountkeeper.repository.model.BookAndRecordVo
import pers.xyj.accountkeeper.repository.model.PageVo
import pers.xyj.accountkeeper.ui.bill.adapter.BillAdapter
import pers.xyj.accountkeeper.ui.book.adapter.BookAdapter
import pers.xyj.accountkeeper.util.LogUtil
import java.text.SimpleDateFormat
import java.util.Date

class BillFragment : BaseFragment<FragmentBillBinding, BillViewModel>(
    FragmentBillBinding::inflate,
    BillViewModel::class.java,
    true
), BillAdapter.OnItemClickListener {
    val billList: ArrayList<Bill> = ArrayList<Bill>()
    val billAdapter: BillAdapter = BillAdapter(billList)
    var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    lateinit var calendarView: CalendarView
    lateinit var noRecordLayout: LinearLayout
    var timeInMillis:Long = 0L

    override fun initFragment(
        binding: FragmentBillBinding,
        viewModel: BillViewModel?,
        savedInstanceState: Bundle?
    ) {
        calendarView = binding.calendarView
        timeInMillis = calendarView.selectedCalendar!!.timeInMillis
        viewModel!!.date.value = format.format(Date(timeInMillis))
        binding.dateText.text = viewModel!!.date.value
        binding.billsRecyclerView.adapter = billAdapter
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.addBillButton.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putBoolean("isEdit", false)
            bundle.putString("date", viewModel!!.date.value)
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_billFragment_to_addBillFragment, bundle)
        }
        noRecordLayout= binding.noBillLayout
        calendarView = binding.calendarView
        calendarView.setOnCalendarSelectListener (object : CalendarView.OnCalendarSelectListener{
            override fun onCalendarOutOfRange(calendar: Calendar?) {

            }

            override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                if (isClick){
                    viewModel!!.date.value = format.format(Date(calendar!!.timeInMillis))
                    timeInMillis = calendar!!.timeInMillis
                    binding.dateText.text = viewModel!!.date.value
                    initBillFromDB()
                }

            }

        })
        billAdapter.setOnItemClickListener(this)
    }
    fun initBillFromDB() {
        billList.clear()
        publicViewModel?.apply {
            request(BillApi::class.java).getBills(
                Date(calendarView.selectedCalendar!!.timeInMillis),
                1,
                100
            ).getResponse {
                it.collect {
                    when (it) {
                        is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                        ApiResponse.Loading -> LogUtil.e("Loading")
                        is ApiResponse.Success -> {
                            LogUtil.e("${it.data.toString()}")
                            var billPage: PageVo = it.data?.data as PageVo
                            var bills: List<Any> = billPage.rows
                            spUtil.toBeanList(bills, billList)
                            withContext(Dispatchers.Main) {
                                billAdapter.notifyDataSetChanged()
                                if (billList.size == 0){
                                    noRecordLayout.visibility = View.VISIBLE
                                }else{
                                    noRecordLayout.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        initBillFromDB()
    }

    override fun onItemClick(position: Int) {
        var bill = billList[position]
        var bundle: Bundle = Bundle()
        var reminderTime = bill.reminderTime
        bundle.putBoolean("isEdit", true)
        bundle.putString("date", format.format(Date(calendarView.selectedCalendar!!.timeInMillis)))
        bundle.putInt("billId",bill.id)
        bundle.putString("billName",bill.billName)
        bundle.putInt("hour",Integer.parseInt(String.format("%tH", reminderTime)))
        bundle.putInt("minute",Integer.parseInt(String.format("%tM", reminderTime)))
        requireActivity().findNavController(R.id.app_navigation)
            .navigate(R.id.action_billFragment_to_addBillFragment, bundle)
    }
}