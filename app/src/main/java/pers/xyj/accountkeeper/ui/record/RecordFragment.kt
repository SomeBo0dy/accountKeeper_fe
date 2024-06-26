package pers.xyj.accountkeeper.ui.record
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
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
import pers.xyj.accountkeeper.repository.model.UserInfo
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
    val recordList: ArrayList<Record> = ArrayList()
    val recordAdapter: RecordAdapter = RecordAdapter(recordList)
    var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    lateinit var calendarView: CalendarView
    lateinit var bookTitle: TextView
    lateinit var noRecordLayout: LinearLayout
    var bookId: Int = 0
    var timeInMillis:Long = 0L
    var createBy:Long = -1L
    var userId:Long = -1L
    override fun initFragment(
        binding: FragmentRecordBinding,
        viewModel: BookViewModel?,
        savedInstanceState: Bundle?
    ) {
        var userInfo = publicViewModel!!.spUtil.getObjectItem("userInfo", UserInfo()) as UserInfo
        userId = userInfo.id
        calendarView = binding.calendarView
        calendarView.setOnCalendarSelectListener (object : CalendarView.OnCalendarSelectListener{
            override fun onCalendarOutOfRange(calendar: Calendar?) {

            }

            override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                if (isClick){
                    viewModel!!.date.value = format.format(Date(calendar!!.timeInMillis))
                    timeInMillis = calendar!!.timeInMillis
                    binding.dateText.text = viewModel!!.date.value
                    initRecordFromDB()
                }

            }

        })
        bookTitle = binding.bookTitle
        noRecordLayout= binding.noRecordLayout
        timeInMillis = calendarView.selectedCalendar!!.timeInMillis
        viewModel!!.date.value = format.format(Date(timeInMillis))
        binding.dateText.text = viewModel!!.date.value
        binding.recordsRecyclerView.adapter = recordAdapter
        binding.addRecordButton.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putBoolean("isEdit", false)
            bundle.putInt("bookId", bookId)
            bundle.putLong("timeInMillis", timeInMillis)
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_mainNavigationFragment_to_addRecordFragment, bundle)
        }
        binding.shareButton.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putBoolean("isOwner", userId == createBy)
            bundle.putInt("bookId", bookId)
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_mainNavigationFragment_to_shareCodeFragment, bundle)
        }
        recordAdapter.setOnItemClickListener(this)
    }

    fun initRecordFromDB() {
        recordList.clear()
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
                            LogUtil.e("${it.data.toString()}")
                            var bookAndRecordVo: BookAndRecordVo = it.data?.data as BookAndRecordVo
                            var bId = bookAndRecordVo.bId
                            var name = bookAndRecordVo.name
                            createBy = bookAndRecordVo.createBy
                            var description = bookAndRecordVo.description
                            var records: List<Any> = bookAndRecordVo.recordPage.rows
                            bookId = bId
                            spUtil.toBeanList(records, recordList)

                            withContext(Dispatchers.Main) {
                                recordAdapter.notifyDataSetChanged()
                                if (recordList.size == 0){
                                    noRecordLayout.visibility = View.VISIBLE
                                }else{
                                    noRecordLayout.visibility = View.GONE
                                }
                                bookTitle.text = name
                            }
//
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(position: Int) {
        var record = recordList[position]
        var bundle: Bundle = Bundle()
        bundle.putBoolean("isEdit", true)
        bundle.putLong("recordId",record.id)
        bundle.putDouble("recordAmount",record.amount)
        bundle.putInt("typeId",record.tId)
        bundle.putInt("isIncome",record.isIncome)
        bundle.putString("description",record.description)
        requireActivity().findNavController(R.id.app_navigation)
            .navigate(R.id.action_mainNavigationFragment_to_addRecordFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        initRecordFromDB()
    }
}