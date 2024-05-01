package pers.xyj.accountkeeper.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentDashboardBinding
import pers.xyj.accountkeeper.databinding.FragmentUserBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.BookApi
import pers.xyj.accountkeeper.repository.model.BookStatisticsVo
import pers.xyj.accountkeeper.repository.model.TypeNameAndCountVo
import pers.xyj.accountkeeper.ui.book.adapter.BookAdapter
import pers.xyj.accountkeeper.ui.dashboard.adapter.StatisticItemAdapter
import pers.xyj.accountkeeper.util.LogUtil

class DashboardFragment : BaseFragment<FragmentDashboardBinding, ViewModel>(
    FragmentDashboardBinding::inflate,
    null,
    true
), StatisticItemAdapter.OnItemClickListener {
    lateinit var pieChart: PieChart
    var bookId: Int = 0
    var bookName: String = ""
    var bookDescription: String = ""
    var typeList: ArrayList<TypeNameAndCountVo> = ArrayList<TypeNameAndCountVo>()
    var recordCountTotal: Int = 0
    var recordAmountTotal: Double = 0.0
    val statisticAdapter: StatisticItemAdapter = StatisticItemAdapter(typeList)

    override fun initFragment(
        binding: FragmentDashboardBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {
        var bundle = arguments
        if (bundle != null){
            bookId = bundle.getInt("bookId")
            bookName = bundle.getString("bookName").toString()
            bookDescription = bundle.getString("bookDescription").toString()
        }
        binding.bookTitle.text = bookName + "报表"
        binding.statisticItemList.adapter = statisticAdapter
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        statisticAdapter.setOnItemClickListener(this)
        pieChart = binding.pieChart
        initPieChart()
    }
    fun initPieChart(){
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(45f); //半径
        pieChart.setTransparentCircleRadius(55f); // 半透明圈
        var description = Description()
        description.text = "各类支出饼状图"
        pieChart.setDescription(description);
        pieChart.setDrawCenterText(true); //饼状图中间可以添加文字
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationAngle(90f); // 初始旋转角度
        pieChart.setRotationEnabled(true); // 可以手动旋转
        pieChart.setUsePercentValues(true); //显示成百分比
        pieChart.setCenterText("支出一览"); //饼状图中间的文字
        var mLegend: Legend = pieChart.getLegend(); //设置比例图
        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);
        pieChart.animateXY(1000, 1000); //设置动画
        getPieData()
    }

    fun getPieData(){
        publicViewModel?.apply {
            request(BookApi::class.java).getBookStatistics(bookId).getResponse {
                it.collect {
                    when (it) {
                        is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                        ApiResponse.Loading -> LogUtil.e("Loading")
                        is ApiResponse.Success -> {
                            LogUtil.e("${it.data.toString()}")
                            var statistics: BookStatisticsVo = it.data?.data as BookStatisticsVo
                            recordCountTotal = statistics.recordCountTotal
                            recordAmountTotal = statistics.recordAmountTotal
//                            statisticAdapter.setTotalAmount(recordAmountTotal)
                            typeList.addAll(statistics.typeList)
                            var entries : ArrayList<PieEntry> = ArrayList<PieEntry>()
                            for (item in typeList){
                                var entry: PieEntry = PieEntry((item.amount/recordAmountTotal).toFloat() * 100, item.typeName)
                                entries.add(entry)
                            }
                            val dataSet = PieDataSet(entries, "支出种类")
                            dataSet.colors = listOf(Color.rgb(255, 123, 124), Color.rgb(114, 188, 223), Color.rgb(242, 195, 139), Color.rgb(255, 105, 180), Color.rgb(205, 205, 205))
                            dataSet.valueTextColor = Color.BLACK

                            val data = PieData(dataSet)
                            pieChart.data = data
                            withContext(Dispatchers.Main){
                                pieChart.invalidate()
                                statisticAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }

    }

    override fun onItemClick(position: Int) {

    }
}