package pers.xyj.accountkeeper.ui.smartKeep

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentSmartResultBinding
import pers.xyj.accountkeeper.repository.model.SmartKeepRecordVo
import pers.xyj.accountkeeper.ui.record.adapter.RecordAdapter
import pers.xyj.accountkeeper.ui.smartKeep.adapter.SmartResultAdapter
import pers.xyj.accountkeeper.util.LogUtil
import java.text.SimpleDateFormat

class SmartResultFragment : BaseFragment<FragmentSmartResultBinding, ViewModel>(
    FragmentSmartResultBinding::inflate,
    null,
    true
), SmartResultAdapter.OnItemClickListener {
    var smartRecordList: ArrayList<SmartKeepRecordVo> = ArrayList()
    val smartResultAdapter: SmartResultAdapter = SmartResultAdapter(smartRecordList)
    lateinit var noSmartRecordLayout: LinearLayout
    var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    var count: Int = -1;
    override fun initFragment(
        binding: FragmentSmartResultBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {
        noSmartRecordLayout= binding.noSmartRecordLayout
        var bundle = arguments
        if (bundle != null){
            count = bundle.getInt("recordCount")
            smartRecordList.addAll(bundle.getSerializable("recordList") as ArrayList<SmartKeepRecordVo>)
            if (count == 0){
                noSmartRecordLayout.visibility = View.VISIBLE
            }else{
                noSmartRecordLayout.visibility = View.GONE
            }
            binding.smartRecordCount.text = count.toString()
        }
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.smartRecordsRecyclerView.adapter = smartResultAdapter
        smartResultAdapter.setOnItemClickListener(this)
    }

    override fun onItemClick(position: Int) {
        var  smartKeepRecord: SmartKeepRecordVo = smartRecordList[position]
        var bundle: Bundle = Bundle()
        bundle.putDouble("amount", smartKeepRecord.amount)
        bundle.putString("description", smartKeepRecord.description)
        bundle.putLong("timeInMillis", smartKeepRecord.createDate.time)
        requireActivity().findNavController(R.id.app_navigation)
            .navigate(
                R.id.action_smartKeepResultFragment_to_smartEditFragment,
                bundle
            )
    }
}