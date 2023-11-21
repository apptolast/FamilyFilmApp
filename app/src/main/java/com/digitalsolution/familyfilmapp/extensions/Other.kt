import com.digitalsolution.familyfilmapp.BaseUiState
import kotlinx.coroutines.flow.MutableStateFlow

@Suppress("UNCHECKED_CAST")
fun <T : BaseUiState> MutableStateFlow<T>.showProgressIndicator(value: Boolean) {
    this.value = this.value.copyWithLoading(value) as T
}
