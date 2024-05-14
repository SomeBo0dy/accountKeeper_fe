package pers.xyj.accountkeeper.ui.smartKeep

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentSmartEditBinding
import pers.xyj.accountkeeper.databinding.FragmentSmartKeepBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.BookApi
import pers.xyj.accountkeeper.repository.entity.BookVo
import pers.xyj.accountkeeper.repository.model.PageVo
import pers.xyj.accountkeeper.ui.book.adapter.BookAdapter
import pers.xyj.accountkeeper.ui.smartKeep.adapter.SelectBookAdapter
import pers.xyj.accountkeeper.util.LogUtil
import java.text.SimpleDateFormat
import java.util.Date

class SmartEditFragment: BaseFragment<FragmentSmartEditBinding, ViewModel>(
    FragmentSmartEditBinding::inflate,
    null,
    true
) , SelectBookAdapter.OnItemClickListener{
    val bookList: ArrayList<BookVo> = ArrayList<BookVo>()
    val selectBookAdapter: SelectBookAdapter = SelectBookAdapter(bookList)
    var amount: Double = 0.0
    lateinit var description: String
    var timeInMillis: Long = 0L
    var bookId: Int = 1
    var isSelected: Boolean = false
    lateinit var selectCalendarView: CalendarView

    override fun initFragment(
        binding: FragmentSmartEditBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {
        var bundle = arguments
        if (bundle != null){
            amount = bundle.getDouble("amount")
            description = bundle.getString("description")!!
            timeInMillis = bundle.getLong("timeInMillis")
        }
        selectCalendarView = binding.selectCalendarView
        var date = Date(timeInMillis)
        selectCalendarView.selectedCalendar.year = date.year
        selectCalendarView.selectedCalendar.month = date.month
        selectCalendarView.selectedCalendar.day = date.day
        selectCalendarView.setOnCalendarSelectListener(object : CalendarView.OnCalendarSelectListener{
            override fun onCalendarOutOfRange(calendar: Calendar?) {

            }

            override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                if (isClick){
                    timeInMillis = calendar!!.timeInMillis
                }

            }

        })
        binding.selectBookRecycleList.adapter = selectBookAdapter
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.selectSmartBookButton.setOnClickListener {
            if (!isSelected){
                Toast.makeText(requireContext(), "请选择账本", Toast.LENGTH_SHORT)
                    .show()
            }else{
                var bundle: Bundle = Bundle()
                bundle.putBoolean("isSmartAdded", true)
                bundle.putInt("bookId", bookId)
                bundle.putDouble("amount", amount)
                bundle.putString("description", description)
                bundle.putLong("timeInMillis", timeInMillis)
                requireActivity().findNavController(R.id.app_navigation).navigate(R.id.action_smartEditFragment_to_addRecordFragment, bundle)
            }

        }
        initBookFromDB()
        selectBookAdapter.setOnItemClickListener(this)
    }
    fun initBookFromDB() {
        bookList.clear()
        publicViewModel?.apply {
            request(BookApi::class.java).getBooks(1, 100).getResponse {
                it.collect {
                    when (it) {
                        is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                        ApiResponse.Loading -> LogUtil.e("Loading")
                        is ApiResponse.Success -> {
//                            LogUtil.e("${it.data.toString()}")
                            var page: PageVo = it.data?.data as PageVo
                            var rows: List<Any> = page.rows
                            spUtil.toBeanList(rows, bookList)
                            withContext(Dispatchers.Main) {
                                selectBookAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onItemClick(position: Int) {
        bookId = bookList[position].id
        isSelected = true
    }
}