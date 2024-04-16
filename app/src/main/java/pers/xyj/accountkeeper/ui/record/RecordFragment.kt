package pers.xyj.accountkeeper.ui.record

import android.os.Bundle
import android.widget.TextView
import androidx.navigation.findNavController
import com.haibin.calendarview.CalendarView
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

    override fun initFragment(
        binding: FragmentRecordBinding,
        viewModel: BookViewModel?,
        savedInstanceState: Bundle?
    ) {
        calendarView = binding.calendarView
        bookTitle = binding.bookTitle
        viewModel!!.date.value =
            calendarView.curYear.toString() + "-" + calendarView.curMonth.toString() + "-" + calendarView.curDay.toString()
        binding.dateText.text = viewModel!!.date.value
        binding.recordsRecyclerView.adapter = recordAdapter
        binding.addRecordButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_mainNavigationFragment_to_addRecordFragment)
        }
        recordAdapter.setOnItemClickListener(this)
    }

    fun initRecordFromDB() {
//        recordAdapter.notifyDataSetChanged()
        recordList.removeAll(recordList)
        publicViewModel?.apply {
            request(RecordApi::class.java).getTopBookRecords(
                format.parse(calendarView.curYear.toString() + "-" + calendarView.curMonth.toString() + "-" + calendarView.curDay.toString()),
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