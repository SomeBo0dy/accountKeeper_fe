package pers.xyj.accountkeeper.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
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
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.BookApi
import pers.xyj.accountkeeper.repository.model.BookStatisticsVo
import pers.xyj.accountkeeper.repository.model.TypeNameAndCountVo
import pers.xyj.accountkeeper.ui.dashboard.adapter.StatisticItemAdapter
import pers.xyj.accountkeeper.util.LogUtil

class DashboardFragment : BaseFragment<FragmentDashboardBinding, ViewModel>(
    FragmentDashboardBinding::inflate,
    null,
    true
), StatisticItemAdapter.OnItemClickListener {
    lateinit var incomePieChart: PieChart
    lateinit var outcomePieChart: PieChart
    var bookId: Int = 0
    var bookName: String = ""
    var bookDescription: String = ""
    var incomeStatisticsList: ArrayList<TypeNameAndCountVo> = ArrayList<TypeNameAndCountVo>()
    var outcomeStatisticsList: ArrayList<TypeNameAndCountVo> = ArrayList<TypeNameAndCountVo>()
    var incomeCount: Int = 0
    var outcomeCount: Int = 0
    var incomeAmountSum: Double = 0.0
    var outcomeAmountSum: Double = 0.0
    val incomeStatisticAdapter: StatisticItemAdapter = StatisticItemAdapter(incomeStatisticsList, true)
    val outcomeStatisticAdapter: StatisticItemAdapter = StatisticItemAdapter(outcomeStatisticsList, false)
    lateinit var incomeStatisticCount: TextView
    lateinit var incomeStatisticAmount: TextView
    lateinit var outcomeStatisticCount: TextView
    lateinit var outcomeStatisticAmount: TextView
    lateinit var moneyState: TextView
    lateinit var money: TextView

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
        incomeStatisticCount = binding.incomeStatisticCount
        incomeStatisticAmount = binding.incomeStatisticAmount
        outcomeStatisticCount = binding.outcomeStatisticCount
        outcomeStatisticAmount = binding.outcomeStatisticAmount
        moneyState = binding.moneyState
        money = binding.money

        binding.bookTitle.text = bookName + "报表"
        binding.incomeStatisticItemList.adapter = incomeStatisticAdapter
        binding.outcomeStatisticItemList.adapter = outcomeStatisticAdapter
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
//        incomeStatisticAdapter.setOnItemClickListener(this)
        incomePieChart = binding.incomePieChart
        outcomePieChart = binding.outcomePieChart
        getPieData()
        initPieChart(incomePieChart, "各类收入饼状图" , "收入一览")
        initPieChart(outcomePieChart, "各类支出饼状图" , "支出一览")
        incomeStatisticAdapter.setOnItemClickListener(this)
        outcomeStatisticAdapter.setOnItemClickListener(this)
    }
    fun initPieChart(pieChart: PieChart, desc: String, centerText: String){
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(45f); //半径
        pieChart.setTransparentCircleRadius(55f); // 半透明圈
        var description = Description()
        description.text = desc
        pieChart.setDescription(description);
        pieChart.setDrawCenterText(true); //饼状图中间可以添加文字
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationAngle(90f); // 初始旋转角度
        pieChart.setRotationEnabled(true); // 可以手动旋转
        pieChart.setUsePercentValues(true); //显示成百分比
        pieChart.setCenterText(centerText); //饼状图中间的文字
        var mLegend: Legend = pieChart.getLegend(); //设置比例图
        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);
        pieChart.animateXY(1000, 1000); //设置动画
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
                            incomeCount = statistics.incomeCount
                            outcomeCount = statistics.outcomeCount
                            incomeAmountSum = statistics.incomeAmountSum
                            outcomeAmountSum = statistics.outcomeAmountSum
                            incomeStatisticsList.addAll(statistics.incomeStatistics)
                            outcomeStatisticsList.addAll(statistics.outcomeStatistics)
                            var incomeEntries : ArrayList<PieEntry> = ArrayList<PieEntry>()
                            var outcomeEntries : ArrayList<PieEntry> = ArrayList<PieEntry>()
                            for (item in incomeStatisticsList){
                                var entry: PieEntry = PieEntry((item.amount/incomeAmountSum).toFloat() * 100, item.typeName)
                                incomeEntries.add(entry)
                            }
                            for (item in outcomeStatisticsList){
                                var entry: PieEntry = PieEntry((item.amount/outcomeAmountSum).toFloat() * 100, item.typeName)
                                outcomeEntries.add(entry)
                            }
                            val incomeDataSet = PieDataSet(incomeEntries, "")
                            val outcomeDataSet = PieDataSet(outcomeEntries, "")
                            incomeDataSet.colors = listOf(Color.rgb(255, 123, 124), Color.rgb(114, 188, 223), Color.rgb(242, 195, 139), Color.rgb(255, 105, 180), Color.rgb(205, 205, 205))
                            incomeDataSet.valueTextColor = Color.BLACK
                            val incomeData = PieData(incomeDataSet)
                            incomePieChart.data = incomeData

                            outcomeDataSet.colors = listOf(Color.rgb(255, 123, 124), Color.rgb(114, 188, 223), Color.rgb(242, 195, 139), Color.rgb(255, 105, 180), Color.rgb(205, 205, 205))
                            outcomeDataSet.valueTextColor = Color.BLACK
                            val outcomeData = PieData(outcomeDataSet)
                            outcomePieChart.data = outcomeData
                            var moneyResult: Double = incomeAmountSum - outcomeAmountSum
                            withContext(Dispatchers.Main){
                                incomePieChart.invalidate()
                                outcomePieChart.invalidate()
                                incomeStatisticCount.text = incomeCount.toString()
                                outcomeStatisticCount.text = outcomeCount.toString()
                                incomeStatisticAmount.text = String.format("%.2f", incomeAmountSum)
                                outcomeStatisticAmount.text = String.format("%.2f", outcomeAmountSum)
                                if (moneyResult >= 0){
                                    moneyState.text = "盈余"
                                    money.text = String.format("%.2f", moneyResult)
                                }else{
                                    moneyState.text = "亏损"
                                    money.text = String.format("%.2f", -moneyResult)
                                }
                                incomeStatisticAdapter.notifyDataSetChanged()
                                outcomeStatisticAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }

    }

    override fun onItemClick(position: Int) {
        LogUtil.e(position.toString())
    }
}