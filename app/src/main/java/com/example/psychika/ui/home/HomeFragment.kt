package com.example.psychika.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.psychika.R
import com.example.psychika.adapter.ArticleAdapter
import com.example.psychika.adapter.FeelAdapter
import com.example.psychika.data.entity.Article
import com.example.psychika.data.entity.Feel
import com.example.psychika.data.local.preference.feel.FeelPreference
import com.example.psychika.data.local.preference.user.User
import com.example.psychika.data.local.preference.user.UserPreference
import com.example.psychika.data.network.Result
import com.example.psychika.data.network.firebase.UserGoogleAuth
import com.example.psychika.databinding.FragmentHomeBinding
import com.example.psychika.ui.ViewModelFactory
import com.example.psychika.ui.article.detail.DetailArticleActivity
import com.example.psychika.ui.auth.login.LoginActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val listFeel = ArrayList<Feel>()
    private val listArticle = ArrayList<Article>()

    private lateinit var userModel: User
    private lateinit var userPreference: UserPreference
    private lateinit var userGoogleAuth: UserGoogleAuth

    private lateinit var feelPreference: FeelPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        userPreference = UserPreference(requireContext())
        userModel = userPreference.getUser()

        feelPreference = FeelPreference(requireContext())

        if (!userModel.googleAuth) {
            getCurrentUserApi()
        } else {
            getCurrentUserGoogleAuth()
        }

        listFeel.addAll(getListFeel())
        showListFeel()

        listArticle.addAll(getListArticle())
        showListArticle()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listFeel.clear()
        listArticle.clear()
        feelPreference.setInitialSelected(true)
    }

    private fun getCurrentUserApi() {
        viewModel.getCurrentUserApi("Bearer ${userModel.id}").observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        val response = result.data
                        binding.tvHiUser.text = getString(R.string.hi_user, response.firstName)
                    }

                    is Result.Error -> {
                        val errorMessage = result.error.message

                        if (errorMessage == "timeout") {
                            userModel.id = ""
                            userModel.rememberMe = false
                            userModel.googleAuth = false
                            userPreference.setUser(userModel)

                            showToast(getString(R.string.session_expired))

                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            showToast(result.error.message)
                        }
                    }
                }
            }
        }
    }

    private fun getCurrentUserGoogleAuth() {
        viewModel.getCurrentUserGoogleAuth().observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        userGoogleAuth = result.data

                        getCurrentFirebaseUser(userGoogleAuth.id!!)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE

                        showToast(getString(R.string.cant_get_user_data_google))
                    }
                }
            }
        }
    }

    private fun getCurrentFirebaseUser(userId: String) {
        viewModel.getCurrentFirebaseUser(userId).observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        val response = result.data
                        if (response.firstName.isNullOrEmpty()) {
                            registerCurrentUserGoogleAuth()
                        }

                        getCurrentFirebaseUser(userGoogleAuth.id!!)

                        binding.tvHiUser.text = getString(R.string.hi_user, response.firstName)
                    }
                    is Result.Error -> {
                        Log.e(TAG, "${getString(R.string.failed_get_account)} : ${result.error.message}")
                        showToast(getString(R.string.failed_get_account))
                    }
                }
            }
        }
    }

    private fun registerCurrentUserGoogleAuth() {
        val user = hashMapOf(
            "id" to userModel.id,
            "profilePic" to userGoogleAuth.profilePic.toString(),
            "firstName" to userGoogleAuth.firstName,
            "lastName" to userGoogleAuth.lastName,
            "email" to userGoogleAuth.email,
        )

        viewModel.registerWithGogleAuth(userGoogleAuth.id!!, user).observe(requireActivity()) {}
    }

    private fun showListFeel() {
        val layoutManager = GridLayoutManager(requireContext(), listFeel.size)
        val listFeelAdapter = FeelAdapter(listFeel, feelPreference)

        binding.rvListFeel.apply {
            setLayoutManager(layoutManager)
            isNestedScrollingEnabled = false
            adapter = listFeelAdapter
        }
    }

    private fun getListFeel(): ArrayList<Feel> {
        val dataIcon = resources.obtainTypedArray(R.array.feel_icon)
        val dataDesc = resources.getStringArray(R.array.feel_desc)

        val listFeel = ArrayList<Feel>()
        for (i in dataDesc.indices) {
            val isSelected = feelPreference.getFeel(i).isSelected
            val feel = Feel(dataIcon.getResourceId(i, -1), dataDesc[i], isSelected)
            Log.i(TAG, feel.toString())
            listFeel.add(feel)
        }
        return listFeel
    }

    private fun showListArticle() {
        val layoutManager = LinearLayoutManager(requireContext())
        val listArticleAdapter = ArticleAdapter(listArticle)

        binding.rvListArticle.apply {
            setLayoutManager(layoutManager)
            adapter = listArticleAdapter
        }

        listArticleAdapter.setOnItemClickCallBack(object : ArticleAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: Article) {
                showSelectedArticle(data)
            }
        })
    }

    private fun getListArticle(): ArrayList<Article> {
        val dataProfilePic = resources.obtainTypedArray(R.array.article_publisher_profile_pic)
        val dataPublisher = resources.getStringArray(R.array.article_publisher)
        val dataTitle = resources.getStringArray(R.array.article_title)
        val dataDate = resources.getStringArray(R.array.article_date_publish)
        val dataPhoto = resources.obtainTypedArray(R.array.article_photo)
        val dataDesc = resources.getStringArray(R.array.article_desc)

        val listArticle = ArrayList<Article>()
        for (i in 0 until 4) {
            val article = Article(
                profilePic = dataProfilePic.getResourceId(i, -1),
                publisher = dataPublisher[i],
                title = dataTitle[i],
                date = dataDate[i],
                photo = dataPhoto.getResourceId(i, -1),
                desc = dataDesc[i]
            )
            listArticle.add(article)
        }
        return listArticle
    }

    private fun showSelectedArticle(data: Article) {
        val moveWithParcelableIntent = Intent(requireContext(), DetailArticleActivity::class.java)
        moveWithParcelableIntent.apply {
            putExtra(DetailArticleActivity.EXTRA_ARTICLE_PUBLISHER, data.publisher)
            putExtra(DetailArticleActivity.EXTRA_ARTICLE_TITLE, data.title)
            putExtra(DetailArticleActivity.EXTRA_ARTICLE_DATE, data.date)
            putExtra(DetailArticleActivity.EXTRA_ARTICLE_PHOTO, data.photo)
            putExtra(DetailArticleActivity.EXTRA_ARTICLE_DESC, data.desc)
        }
        startActivity(moveWithParcelableIntent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}