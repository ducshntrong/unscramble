package com.example.android.unscramble.ui.game

import androidx.lifecycle.ViewModel
import android.util.Log

class GameViewModel : ViewModel() {//Thêm viewModel
    //****Di chuyển dữ liệu sang ViewModel****
    private var score = 0
    private var currentWordCount = 0
    //Thêm thuộc tính sao lưu vào currentScrambledWord
    private var _currentScrambledWord = "test"
    val currentScrambledWord: String
        get() = _currentScrambledWord

    //Vòng đời của viewModel
    init {
        Log.d("GameFragment", "GameViewModel created!")
    }
    //Ngay trước khi ViewModel bị huỷ, hệ thống sẽ thực hiện lệnh gọi lại onCleared()
    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }
}