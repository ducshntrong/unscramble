package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

class GameViewModel : ViewModel() {//Thêm viewModel
    // ********7.ĐIỀN VIEWMODEL********
    //giữ danh sách các từ mà bạn sử dụng trong trò chơi nhằm tránh việc lặp lại.
    private var wordsList: MutableList<String> = mutableListOf()//7
    //chứa từ mà người chơi đang cố gắng xếp. Hãy sử dụng từ khoá lateinit vì bạn sẽ khởi tạo thuộc tính này sau
    private lateinit var currentWord: String//7

    //****5.DI CHUYỂN DỮ LIỆU SANG ViewModel****
    //****8.HỘP THOẠI****
//    private var _score = 0//5,8. thêm thuộc tính sao lưu vào biến score
//    val score: Int
//        get() = _score
//    //11. *****Xác minh ViewModel lưu giữ dữ liệu****
//    private val _currentWordCount = 0//5,11
//    //Thêm trường sao lưu.
//    val currentWordCount: Int//11
//        get() = _currentWordCount

    //Thêm thuộc tính sao lưu vào currentScrambledWord
//    private lateinit var _currentScrambledWord: String//5
//    val currentScrambledWord: String //5
//        get() = _currentScrambledWord

    //2.6****Đính kèm đối tượng tiếp nhận dữ liệu vào điểm số và số từ****
    private val _score = MutableLiveData(0)//2.6
    val score: LiveData<Int>
        get() = _score
    private val _currentWordCount = MutableLiveData(0)//2.6
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    //2.4 ****Thêm LiveData vào từ bị xáo trộn hiện tại****
    //Thay đổi loại biến _currentScrambledWord thành val vì giá trị của đối tượng
    // LiveData/MutableLiveData sẽ giữ nguyên và chỉ có dữ liệu được lưu trữ trong đối tượng mới thay đổi.
    private val _currentScrambledWord = MutableLiveData<String>()//2.4
    //Thay đổi trường sao lưu, thay đổi loại currentScrambledWord thành LiveData<String>, vì trường này không thể thay đổi
//    val currentScrambledWord: LiveData<String>//2.4
//        get() = _currentScrambledWord
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

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
            //Để truy cập vào dữ liệu trong đối tượng LiveData, sử dụng thuộc tính value
            _currentScrambledWord.value = String(tempWord)//2.4
            //sử dụng hàm Kotlin inc() để giá trị tăng thêm một bằng giá trị rỗng an toàn
            _currentWordCount.value = (_currentWordCount.value)?.inc()//2.6
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
    //9.*****nút submit*****
    private fun increaseScore() {
        //Tăng biến score bằng SCORE_INCREASE.
//        _score += SCORE_INCREASE
        //Sử dụng hàm Kotlin plus() để tăng giá trị _score. Giá trị này bổ sung giá trị rỗng an toàn.
        _score.value = (_score.value)?.plus(SCORE_INCREASE)//2.6
    }
    //isUserWordCorrect() để trả về Boolean và lấy String (từ của người chơi) làm tham số.
    fun isUserWordCorrect(playerWord: String): Boolean {//9
        //xác thực từ của người chơi và cộng điểm nếu đoán đúng.
        // Thao tác này sẽ cập nhật điểm cuối cùng trong hộp thoại thông báo.
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    /*
    * Trả về true nếu số từ hiện tại nhỏ hơn MAX_NO_OF_WORDS.
    * Cập nhật từ tiếp theo.
    */
    fun nextWord(): Boolean {//7
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    //Tái tạo dữ liệu trò chơi để khởi động lại trò chơi
    fun reinitializeData(){
        //Thiết lập điểm và số từ thành 0. Xoá danh sách từ và gọi phương thức getNextWord().
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }
}