import androidx.recyclerview.widget.DiffUtil
import com.example.example.ui.Profesor.Profesor

class ProfesorDiffCallback(
    private val oldList: List<Profesor>,
    private val newList: List<Profesor>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].idProfesor == newList[newItemPosition].idProfesor

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}