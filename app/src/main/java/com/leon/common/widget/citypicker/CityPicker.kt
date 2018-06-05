package com.example.citylib

import android.content.Context
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.leon.common.R
import com.leon.common.R.id.*
import com.leon.common.widget.citypicker.CityBean
import kotlinx.android.synthetic.main.layout_city.view.*
import kotlinx.android.synthetic.main.layout_city_item.view.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * 地址选择
 */
class CityPicker : RelativeLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val mCityData = mutableListOf<CityBean>()
    private val mData = mutableListOf<CityBean>()
    private val mCheckIndexMap = mutableMapOf<Int, Int>()
    private var mTabIndex = 0

    private val mAdapter: MyAdapter by lazy { MyAdapter(mData) }
    private var onCancel: (() -> Unit) = {}
    private var onAffirm: ((content: String) -> Unit) = {}

    init {
        View.inflate(context, R.layout.layout_city, this)
        addTab("请选择")
        getCityData()
        city_RecyclerView.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
        setListener()
    }

    fun setOnCancelListener(on: () -> Unit) {
        this.onCancel = on
    }

    fun setOnAffirmListener(on: (content: String) -> Unit) {
        this.onAffirm = on
    }

    private fun addTab(str: String?) {
        val newTab = city_TabLayout.newTab()
        newTab.run {
            text = str ?: ""
        }
        city_TabLayout.addTab(city_TabLayout.newTab().setText(str))
    }

    private fun setTabText(str: String?) {
        city_TabLayout.getTabAt(mTabIndex)?.text = str ?: ""
    }

    private fun getTabText(index: Int = mTabIndex) = city_TabLayout.getTabAt(index)?.text.toString()

    private fun getCityData() {
        val jsonSB = StringBuilder()
        try {
            val inputStream = context.assets.open("city.json")
            val reader = BufferedReader(InputStreamReader(inputStream, "utf-8"))
            while (true) {
                val line = reader.readLine()
                if (line == null) {
                    break
                } else {
                    jsonSB.append(line).append("\n")
                }
            }
            reader.close()
            inputStream.close()
            //Json的解析类对象
            val parser = JsonParser()
            //将JSON的String 转成一个JsonArray对象
            val jsonArray = parser.parse(jsonSB.toString()).asJsonArray
            val gson = Gson()
            val list = mutableListOf<CityBean>()
            //加强for循环遍历JsonArray
            for (bean in jsonArray) {
                //使用GSON，直接转成Bean对象
                val userBean = gson.fromJson(bean, CityBean::class.java)
                list.add(userBean)
            }
            mData.addAll(list)
            mCityData.addAll(list)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun setListener() {
        tv_cancel.setOnClickListener { onCancel.invoke() }
        tv_affirm.setOnClickListener {
            val stringBuilder = StringBuilder()
            (0 until city_TabLayout.tabCount).forEach {
                val tabText = getTabText(it)
                when {
                    tabText == "请选择" -> return@forEach
                    it == city_TabLayout.tabCount - 1 -> stringBuilder.append(tabText)
                    else -> stringBuilder.append("$tabText-")
                }
            }
            if (stringBuilder.toString().isBlank()) onCancel.invoke()
            else onAffirm.invoke(stringBuilder.toString())
        }
        mAdapter.setOnItemOnClickListener { position ->
            restData()
            mCheckIndexMap[mTabIndex] = position
            val bean = mData[position]
            setTabText(bean.areaName)
            if (bean.cities != null && bean.cities?.isNotEmpty()!!) {
                mData.clear()
                mData.addAll(bean.cities!!)
                addTab("请选择")
                mTabIndex++
            } else {
                mAdapter.checkedCity = getTabText()
                mAdapter.notifyDataSetChanged()//tab的index不变时调用select（）不会刷新数据 so 主动刷新
                scrollToCheckedPosition()
            }
            city_TabLayout.getTabAt(mTabIndex)?.select()
        }
        city_TabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                mTabIndex = tab?.position!!
                mAdapter.checkedCity = getTabText()
                setAdapterData()
                mAdapter.notifyDataSetChanged()
                scrollToCheckedPosition()
            }
        })
    }

    /**
     * 设置adapter数据
     */
    private fun setAdapterData() {
        when (mTabIndex) {
            0 -> {
                mData.clear()
                mData.addAll(mCityData)
            }
            1 -> {
                kotlin.run {
                    val p0 = mCheckIndexMap[0] ?: return@run
                    val bean = mCityData[p0].cities as MutableList
                    if (bean.isNotEmpty()) {
                        mData.clear()
                        mData.addAll(bean)
                    }
                }
            }
            2 -> {
                kotlin.run {
                    val p0 = mCheckIndexMap[0] ?: return@run
                    val cityP0 = mCityData[p0].cities as MutableList
                    if (cityP0.isNotEmpty()) {
                        mData.clear()
                        mData.addAll(cityP0)
                    }
                    val p1 = mCheckIndexMap[1] ?: return@run
                    val list = cityP0[p1].cities
                    if (list != null && list.isNotEmpty()) {
                        mData.clear()
                        mData.addAll(list)
                    }
                }
            }
        }
    }

    /**
     * 重置选中数据
     */
    private fun restData() {
        when (mTabIndex) {
            0 -> {
                (city_TabLayout.tabCount - 1 downTo 0).filter { it > 0 }.forEach {
                    city_TabLayout.removeTabAt(it)
                }
                mCheckIndexMap.clear()
            }
            1 -> {
                (city_TabLayout.tabCount - 1 downTo 0).filter { it > 1 }.forEach {
                    city_TabLayout.removeTabAt(it)
                }
                (1..3).forEach {
                    val i = mCheckIndexMap[it]
                    if (i != null) mCheckIndexMap.remove(it)
                }

            }
            2 -> {
                (city_TabLayout.tabCount - 1 downTo 0).filter { it > 2 }.forEach {
                    city_TabLayout.removeTabAt(it)
                }
                (2..3).forEach {
                    val i = mCheckIndexMap[it]
                    if (i != null) mCheckIndexMap.remove(it)
                }
            }
        }
    }

    /**
     * 滚动到选中的位置
     */
    private fun scrollToCheckedPosition() {
        mData.forEachIndexed { index, cityBean ->
            if (cityBean.areaName == mAdapter.checkedCity) {
                city_RecyclerView.smoothScrollToPosition(index)
                (city_RecyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(index, 0)
            }
        }

    }

    class MyAdapter(val list: MutableList<CityBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var onItem: ((position: Int) -> Unit) = {}

        var checkedCity = ""
        fun setOnItemOnClickListener(on: (position: Int) -> Unit) {
            this.onItem = on
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return LayoutInflater.from(parent.context).inflate(R.layout.layout_city_item, parent, false)
                    .let { MyViewHolder(it) }
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val bean = list[position]
            holder.itemView.run {
                tv_content.text = bean.areaName
                val color = if (bean.areaName == checkedCity) {
                    ContextCompat.getColor(context, R.color.blue)
                } else ContextCompat.getColor(context, android.R.color.black)
                tv_content.setTextColor(color)
                setOnClickListener { onItem.invoke(position) }
            }
        }

        class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    }
}