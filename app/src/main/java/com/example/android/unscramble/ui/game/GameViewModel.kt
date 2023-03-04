package com.example.android.unscramble.ui.game

import androidx.lifecycle.ViewModel
import android.util.Log

class GameViewModel : ViewModel() {//Thêm viewModel
    // ********7.ĐIỀN VIEWMODEL********
    //giữ danh sách các từ mà bạn sử dụng trong trò chơi nhằm tránh việc lặp lại.
    private var wordsList: MutableList<String> = mutableListOf()//7
    //chứa từ mà người chơi đang cố gắng xếp. Hãy sử dụng từ khoá lateinit vì bạn sẽ khởi tạo thuộc tính này sau
    private lateinit var currentWord: String//7

    //****5.DI CHUYỂN DỮ LIỆU SANG ViewModel****
    //****8.HỘP THOẠI****
    private var _score = 0//5,8. thêm thuộc tính sao lưu vào biến score
    val score: Int
        get() = _score

    private var currentWordCount = 0//5
    //Thêm thuộc tính sao lưu vào currentScrambledWord
    private lateinit var _currentScrambledWord: String//5
    val currentScrambledWord: String //5
        get() = _currentScrambledWord

    //phương thức getNextWord() để lấy từ được xáo trộn tiếp theo
    private fun getNextWord() {//7
        //Lấy một từ ngẫu nhiên trên allWordsList và chỉ định từ đó cho currentWord
        currentWord = allWordsList.random()
        //chuyển đổi chuỗi currentWord thành mảng ký tự và gán mảng ký tự đó cho val mới có tên là tempWord
        //Để xáo trộn từ, hãy đảo các ký tự trong mảng này bằng phương thức shuffle() trong Kotlin.
        val tempWord = currentWord.toCharArray()
        while (String(tempWord).equals(currentWord, false)) {
            //Đôi khi, thứ tự ngẫu nhiên của các ký tự sẽ giống với từ gốc. Thêm vòng lặp while dưới đây
            // xung quanh lệnh gọi để xáo trộn nhằm tiếp tục vòng lặp cho đến khi từ được xáo trộn khác với từ ban đầu.
            tempWord.shuffle()
        }
        //Thêm khối if-else để kiểm tra xem một từ đã được dùng hay chưa. Nếu wordsList chứa currentWord, hãy
        //gọi getNextWord(). Nếu không, hãy cập nhật giá trị của _currentScrambledWord bằng từ mới được xáo trộn,
        //tăng số từ và thêm từ mới vào wordsList.
        if (wordsList.contains(currentWord)){
            getNextWord()
        } else{
            _currentScrambledWord = String(tempWord)
            ++currentWordCount
            wordsList.add(currentWord)
        }
    }

    //6. ***Vòng đời của viewModel***
    init {//6
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()//7
    }
    //Ngay trước khi ViewModel bị huỷ, hệ thống sẽ thực hiện lệnh gọi lại onCleared()
    override fun onCleared() {//6
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }
}