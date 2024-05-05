package pers.xyj.accountkeeper.ui.book

import android.os.Bundle
import androidx.navigation.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentBookBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.BookApi
import pers.xyj.accountkeeper.repository.entity.BookVo
import pers.xyj.accountkeeper.repository.model.PageVo
import pers.xyj.accountkeeper.ui.book.adapter.BookAdapter
import pers.xyj.accountkeeper.util.LogUtil

class BookFragment : BaseFragment<FragmentBookBinding, BookRecycleViewModel>(
    FragmentBookBinding::inflate,
    BookRecycleViewModel::class.java,
    true
), BookAdapter.OnItemClickListener {
    val bookList: ArrayList<BookVo> = ArrayList<BookVo>()
    val bookAdapter: BookAdapter = BookAdapter(bookList)

    override fun initFragment(
        binding: FragmentBookBinding,
        viewModel: BookRecycleViewModel?,
        savedInstanceState: Bundle?
    ) {
        binding.viewModel = viewModel
        binding.booksRecyclerView.adapter = bookAdapter
        binding.addButton.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putBoolean("isEdit", false)
            bundle.putInt("bookCount", bookList.size)
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_mainNavigationFragment_to_addBookFragment, bundle)
        }
        bookAdapter.setOnItemClickListener(this)
    }

    fun initBookFromDB() {
        bookList.removeAll(bookList)
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
                                bookAdapter.notifyDataSetChanged()
                            }
                            LogUtil.e(bookList.toString())
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(position: Int) {
//        LogUtil.e("你点击了" + bookList[position].name + "条目")
        var bookVo: BookVo = bookList[position]
        bookList.removeAt(position)
        bookList.add(0, bookVo)
        updateBooksPriority(bookList)
        bookAdapter.notifyDataSetChanged()
        updateBooksPriorityToDB(bookList)
    }

    override fun onSettingButtonClick(position: Int) {
        var bookVo: BookVo = bookList[position]
        var bundle: Bundle = Bundle()
        bundle.putBoolean("isEdit", true)
        bundle.putInt("bookId", bookVo.id)
        bundle.putString("bookName", bookVo.name)
        bundle.putString("bookDescription", bookVo.description)
        requireActivity().findNavController(R.id.app_navigation)
            .navigate(R.id.action_mainNavigationFragment_to_addBookFragment, bundle)
    }

    override fun onReportButtonClick(position: Int) {
        var bookVo: BookVo = bookList[position]
        var bundle: Bundle = Bundle()
        bundle.putInt("bookId", bookVo.id)
        bundle.putString("bookName", bookVo.name)
        bundle.putString("bookDescription", bookVo.description)
        requireActivity().findNavController(R.id.app_navigation)
            .navigate(R.id.action_mainNavigationFragment_to_dashboardFragment, bundle)
    }

    fun updateBooksPriorityToDB(bookList: ArrayList<BookVo>) {
//        var beanList: MutableList<BookPriority> = BeanCopyUtils.copyBeanList(bookList, BookPriority::class.java)
//        var bookPriorityList: ArrayList<BookPriority>  =  beanList.toList() as ArrayList<BookPriority>
//        LogUtil.e(beanList.toString())
//        LogUtil.e(bookPriorityList.toString())
        publicViewModel?.apply {
            request(BookApi::class.java).updateBooksPriority(bookList).getResponse {
                it.collect {
                    when (it) {
                        is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                        ApiResponse.Loading -> LogUtil.e("Loading")
                        is ApiResponse.Success -> {
                            LogUtil.e("${it.data.toString()}")
                            withContext(Dispatchers.Main) {
                                requireActivity().findNavController(R.id.main_view_nav)
                                    .navigate(R.id.action_bookFragment_to_recordFragment)
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateBooksPriority(bookList: ArrayList<BookVo>) {
        var length: Int = bookList.size
        for ((index, item) in bookList.withIndex()) {
            item.priority = length - index
        }
    }

    override fun onResume() {
        super.onResume()
        initBookFromDB()
    }

}
