package com.moneyassist.app.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moneyassist.app.R
import com.moneyassist.app.ui.adapter.Article
import com.moneyassist.app.ui.adapter.ArticleAdapter

class LearnFragment : Fragment() {

    private lateinit var articleAdapter: ArticleAdapter
    private var allArticles = listOf(
        Article(1, "Building an Emergency Fund", "Savings", 3, "An emergency fund is your financial safety net. Start with R1,000 and build up to 3-6 months of expenses."),
        Article(2, "How Credit Scores Work", "Credit", 2, "Your credit score is a number between 300-850 that represents your creditworthiness."),
        Article(3, "Paying Off Debt Faster", "Debt", 3, "Use the avalanche method: pay off high-interest debt first while maintaining minimum payments."),
        Article(4, "Investing for Beginners", "Investing", 4, "Start investing early — even R200/month at a 10% annual return grows significantly over time."),
        Article(5, "The 50/30/20 Budget Rule", "Budgeting", 3, "Allocate 50% to needs, 30% to wants, and 20% to savings and debt repayment."),
        Article(6, "Tax-Free Savings Accounts", "Savings", 5, "A TFSA allows you to save up to R36,000 per year tax-free. Learn how to maximise your allowance.")
    ).toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_learn, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        articleAdapter = ArticleAdapter(
            onSave = { article ->
                article.saved = !article.saved
                val msg = if (article.saved) "🔖 Article saved!" else "Removed from saved"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                filterAndSubmit(view.findViewById<EditText>(R.id.etLearnSearch).text.toString())
            },
            onRead = { article ->
                Toast.makeText(requireContext(), "📖 ${article.title} — coming soon!", Toast.LENGTH_SHORT).show()
            }
        )

        view.findViewById<RecyclerView>(R.id.rvArticles).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = articleAdapter
        }

        articleAdapter.submitList(allArticles.toList())

        view.findViewById<EditText>(R.id.etLearnSearch).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAndSubmit(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        view.findViewById<View>(R.id.btnCloseTipLearn).setOnClickListener {
            view.findViewById<View>(R.id.layoutTipLearn).visibility = View.GONE
        }
    }

    private fun filterAndSubmit(query: String) {
        val filtered = if (query.isBlank()) allArticles.toList()
        else allArticles.filter { it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
        articleAdapter.submitList(filtered)
    }
}
