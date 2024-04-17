package pers.xyj.accountkeeper.ui.record

import android.os.Bundle
import android.widget.TextView
import androidx.navigation.findNavController
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.haibin.calendarview.CalendarView.OnCalendarSelectListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentRecordBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.RecordApi
import pers.xyj.accountkeeper.repository.entity.Record
import pers.xyj.accountkeeper.repository.model.BookAndRecordVo
import pers.xyj.accountkeeper.repository.model.PageVo
import pers.xyj.accountkeeper.ui.book.BookViewModel
import pers.xyj.accountkeeper.ui.record.adapter.RecordAdapter
import pers.xyj.accountkeeper.util.LogUtil
import java.text.SimpleDateFormat
import java.util.Date

class RecordFragment : BaseFragment<FragmentRecordBinding, BookViewModel>(
    FragmentRecordBinding::inflate,
    BookViewModel::class.java,
    true
), RecordAdapter.OnItemClickListener {
    var recordList: ArrayList<Record> = ArrayList()
    var recordAdapter: RecordAdapter = RecordAdapter(recordList)
    var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    lateinit var calendarView: CalendarView
    lateinit var bookTitle: TextView
    var bookId: Int = 0
    var timeInMillis:Long = 0L

    override fun initFragment(
        binding: FragmentRecordBinding,
        viewModel: BookViewModel?,
        savedInstanceState: Bundle?
    ) {
        calendarView = binding.calendarView
        calendarView.setOnCalendarSelectListener (object : CalendarView.OnCalendarSelectListener{
            override fun onCalendarOutOfRange(calendar: Calendar?) {

            }

            override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                if (isClick){
//                    LogUtil.e(Date(calendar!!.timeInMillis).toString())
                    viewModel!!.date.value = format.format(Date(calendar!!.timeInMillis))
                    timeInMillis = calendar!!.timeInMillis
                    binding.dateText.text = viewModel!!.date.value
                    initRecordFromDB()
                }

            }

        })
        bookTitle = binding.bookTitle
        timeInMillis = calendarView.selectedCalendar!!.timeInMillis
        viewModel!!.date.value = format.format(Date(timeInMillis))
        binding.dateText.text = viewModel!!.date.value
        binding.recordsRecyclerView.adapter = recordAdapter
        binding.addRecordButton.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putInt("bookId", bookId)
            bundle.putLong("timeInMillis", timeInMillis)
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_mainNavigationFragment_to_addRecordFragment, bundle)
        }
        recordAdapter.setOnItemClickListener(this)
    }

    fun initRecordFromDB() {
//        recordAdapter.notifyDataSetChanged()
        recordList.removeAll(recordList)
        publicViewModel?.apply {
            request(RecordApi::class.java).getTopBookRecords(
                Date(calendarView.selectedCalendar!!.timeInMillis),
                1,
                100
            ).getResponse {
                it.collect {
                    when (it) {
                        is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                        ApiResponse.Loading -> LogUtil.e("Loading")
                        is ApiResponse.Success -> {
//                            LogUtil.e("${it.data.toString()}")
                            var bookAndRecordVo: BookAndRecordVo = it.data?.data as BookAndRecordVo
                            var bId = bookAndRecordVo.bId
                            var name = bookAndRecordVo.name
                            var description = bookAndRecordVo.description
                            var records: List<Any> = bookAndRecordVo.recordPage.rows
                            bookId = bId
//                            LogUtil.e(recordList.toString())
                            spUtil.toBeanList(records, recordList)
                            withContext(Dispatchers.Main) {
                                bookTitle.text = name
                                recordAdapter.notifyDataSetChanged()
                            }
//                            LogUtil.e(recordList.toString())
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(position: Int) {

    }

    override fun onResume() {
        super.onResume()
        initRecordFromDB()
    }
}