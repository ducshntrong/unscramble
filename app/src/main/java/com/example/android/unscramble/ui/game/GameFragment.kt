package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Đoạn nơi trò chơi được chơi, chứa logic trò chơi.
 */
class GameFragment : Fragment() {
    //tạo thuộc tính tham chiếu (đối tượng) cho ViewModel bên trong đơn vị điều khiển giao diện người dùng.
    private val viewModel: GameViewModel by viewModels()//Thêm viewModel
    // Syntax for property delegation
    //var <property-name> : <property-type> by <delegate-class>()

    private var score = 0
    private var currentWordCount = 0
    private var currentScrambledWord = "test"


    // Thể hiện đối tượng liên kết với quyền truy cập vào các chế độ xem trong bố cục game_fragment.xml
    private lateinit var binding: GameFragmentBinding

    // Tạo một ViewModel khi đoạn đầu tiên được tạo.
    // Nếu mảnh được tạo lại, nó sẽ nhận cùng một đối tượng GameViewModel được tạo bởi
    // đoạn đầu tiên

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate  tệp XML bố cục và trả về một thể hiện đối tượng liên kết
        //thay đổi cách tạo bản sao của biến binding để sử dụng tính năng liên kết dữ liệu.
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)//2.7
        //Lệnh gọi lại onCreateView() sẽ được kích hoạt khi mảnh được tạo lần đầu tiên và cũng được
        //kích hoạt mỗi khi được tạo lại cho mọi sự kiện (chẳng hạn như thay đổi cấu hình).
//        Log.d("GameFragment", "GameFragment created/re-created!")//6
        //in dữ liệu ứng dụng, từ, điểm và số từ.
        Log.d("GameFragment", "Word: ${viewModel.currentScrambledWord} " +
                "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}")//11
        return binding.root
    }

    //Tạo và hiển thị một alertdialog với điểm số cuối cùng.
    private fun showFinalScoreDialog() {//8.Hộp thoại
        //Phương thức requireContext() trả về một Context khác rỗng.
        MaterialAlertDialogBuilder(requireContext())
            //đặt tiêu đề trên hộp thoại thông báo, sử dụng tài nguyên chuỗi qua strings.xml
            .setTitle(getString(R.string.congratulations))
            //Thiết lập thông báo để hiện điểm cuối cùng, sử dụng phiên bản chỉ có thể đọc của biến điểm (viewModel.score )
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            //Đảm bảo không huỷ được hộp thoại thông báo khi người dùng nhấn phím quay lại
            .setCancelable(false)
            //Thêm hai nút văn bản EXIT (THOÁT) và PLAY AGAIN (CHƠI LẠI) bằng cách sử dụng các phương thức
            //viết tắt của setNegativeButton(getString(R.string.exit), { _, _ -> exitGame()})
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            //tạo rồi hiện hộp thoại thông báo.
            .show()
    }

// Phương thức này sẽ được gọi khi hoạt động và mảnh tương ứng bị huỷ
    override fun onDetach() {//6
        super.onDetach()
        Log.d("GameFragment", "GameFragment destroyed!")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //khởi chạy các biến bố cục gameViewModel và maxNoOfWords
        binding.gameViewModel = viewModel//2.8
        binding.maxNoOfWords = MAX_NO_OF_WORDS//2.8

        //LiveData có thể quan sát nhận biết được vòng đời nên bạn phải chuyển chủ sở hữu vòng đời vào bố cục
        // Chỉ định chế độ xem phân đoạn là chủ sở hữu vòng đời của liên kết.
        // Điều này được sử dụng để ràng buộc có thể quan sát các cập nhật LiveData
        binding.lifecycleOwner = viewLifecycleOwner//2.8

        //2.5****Đính kèm đối tượng tiếp nhận dữ liệu vào đối tượng LiveData****
        // Quan sát ScrambledCharArray LiveData, truyền vào LifecycleOwner và the observer.
//        viewModel.currentScrambledWord.observe(viewLifecycleOwner,//2.5
//            //Trong nội dung hàm của biểu thức lambda, gán newWord cho chế độ xem văn bản từ bị xáo trộn.
//            { newWord -> binding.textViewUnscrambledWord.text = newWord
//            }) // xoá mã đối tượng tiếp nhận dữ liệu LiveData cho currentScrambledWord/2.9
                //Bố cục sẽ trực tiếp nhận được thông tin cập nhật về những thay đổi đối với LiveData
        //vào lúc này chế độ xem văn bản từ bị xáo trộn sử dụng biểu thức liên kết để cập nhật giao diện 2.9
        // người dùng chứ không phải LiveData đối tượng tiếp nhận dữ liệu.

        // Thiết lập trình nghe nhấp chuột cho các nút submit và skip.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
        // Update the UI
//        updateNextWordOnScreen()

//        //đính kèm đối tượng tiếp nhận dữ liệu cho score
//        viewModel.score.observe(viewLifecycleOwner,//2.6
//            //trong biểu thức lambda, chuyển điểm mới làm tham số và bên trong nội dung hàm,
//            // đặt điểm mới thành chế độ xem văn bản.
//            { newScore -> binding.score.text = getString(R.string.score, newScore)
//            })
//        //đính kèm đối tượng tiếp nhận dữ liệu cho currentWordCount
//        viewModel.currentWordCount.observe(viewLifecycleOwner,//2.6
//            { newWordCount ->
//                // đặt số từ mới cùng với MAX_NO_OF_WORDS thành chế độ xem văn bản.
//                binding.wordCount.text = getString(R.string.word_count, newWordCount, MAX_NO_OF_WORDS)
//            })
    }

    /*
     * Kiểm tra từ của người dùng và cập nhật điểm cho phù hợp.
     * Hiển thị từ xáo trộn tiếp theo.
     */
    private fun onSubmitWord() {
        //Lưu trữ từ của người chơi trong đó, bằng cách trích xuất từ qua trường văn bản trong biến binding
        val playerWord = binding.textInputEditText.text.toString()
        //if để kiểm tra từ của người chơi bằng phương thức isUserWordCorrect(), truyền vào playerWord
        if (viewModel.isUserWordCorrect(playerWord)){
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
               showFinalScoreDialog()
            }
        }else{
            setErrorTextField(true)
        }
    }

    /*
      * Bỏ qua từ hiện tại mà không thay đổi điểm số.
      * Tăng số lượng từ.
      */
    private fun onSkipWord() {
        //Nếu true, hãy hiện từ đó trên màn hình và thiết lập lại trường văn bản.
        // Nếu false và không còn từ nào ở vòng này, hãy hiện hộp thoại thông báo có điểm cuối cùng.
        if (viewModel.nextWord()){
            setErrorTextField(false)
//            updateNextWordOnScreen()
        }else {
            showFinalScoreDialog()
        }
    }

    /*
      * Lấy một từ ngẫu nhiên cho danh sách các từ và xáo trộn các chữ cái trong đó.
      */
    private fun getNextScrambledWord(): String {
        val tempWord = allWordsList.random().toCharArray()
        tempWord.shuffle()
        return String(tempWord)
    }

    /*
      * Khởi tạo lại dữ liệu trong ViewModel và cập nhật chế độ xem với dữ liệu mới để
      * khởi động lại trò chơi.
      */
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
//        updateNextWordOnScreen()
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    /*
     * Đặt và đặt lại trạng thái lỗi trường văn bản.
     * được xác định để giúp bạn thiết lập và thiết lập lại lỗi trong trường văn bản.
     * Hãy gọi phương thức này bằng true hoặc false dưới dạng tham số đầu vào, tuỳ theo bạn
     * có muốn hiện lỗi trong trường văn bản hay không.
     */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

    /*
      * Hiển thị từ xáo trộn tiếp theo trên màn hình.
      * phương thức updateNextWordOnScreen() để sử dụng thuộc tính viewModel chỉ có thể đọc là currentScrambledWord
      * Di chuyển dữ liệu sang ViewModel
      */
//    private fun updateNextWordOnScreen() {
//        binding.textViewUnscrambledWord.text = viewModel.currentScrambledWord
//    }
}
